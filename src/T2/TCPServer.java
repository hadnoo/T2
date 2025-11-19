package T2;

import java.net.*;
import java.io.*;
import java.util.ArrayList; 

public class TCPServer {

    public static final int PORT = 7896;
    static java.util.List<Connection> connections =
            java.util.Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try {
            ServerSocket listenSocket = new ServerSocket(PORT);
            while (true) {
                Socket client = listenSocket.accept();
                Connection c = new Connection(client);
                connections.add(c);
                c.start();
            }

        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }

    public static void broadcast(String message, Connection sender) {
        synchronized (connections) { // lock while iterating
            for (Connection c : connections) {
                if (c != null) {
                    c.sendMessage(message);
                    // or if you don't want to send to yourself:
                    // if (c != sender) c.sendMessage(message);
                }
            }
        }
    }
}
class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    private String nickname;

    public Connection(Socket aClientSocket) {
        try {
            this.clientSocket = aClientSocket;
            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection: " + e.getMessage());
        }
    }

    public void run() {
        try {
            out.writeUTF("Please enter your nickname: ");
            nickname = in.readUTF();
            System.out.println(nickname + " connected!");
            TCPServer.broadcast(nickname + " joined the chat!", this);

            String message;
            while (true) {
                message = in.readUTF(); // blocks, throws on disconnect
                TCPServer.broadcast(nickname + ": " + message, this);
            }

        } catch (IOException e) {
            System.out.println("Client " + nickname + " disconnected: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}

            // remove from list
            TCPServer.connections.remove(this);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            // if sending fails, you might also want to close and remove this connection
            System.out.println("Failed to send to " + nickname + ": " + e.getMessage());
        }
    }
}
