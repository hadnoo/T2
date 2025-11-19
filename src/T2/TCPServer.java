package T2;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TCPServer {

    public static final int PORT = 7896;
    static List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

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
                    // if you don't want to send to yourself, uncomment this:
                    // if (c != sender) {
                    //     c.sendMessage(message);
                    // }
                    c.sendMessage(message);
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
            // Ask for nickname
            out.writeUTF("Please enter your nickname: ");
            nickname = in.readUTF();
            System.out.println(nickname + " connected!");

            // Inform others that this user joined
            TCPServer.broadcast(nickname + " joined the chat!", this);

            String message;
            while (true) {
                message = in.readUTF(); // blocks, throws on disconnect

                // user wants to log out
                if (message.equalsIgnoreCase("/quit")) {
                    System.out.println(nickname + " logged out.");
                    TCPServer.broadcast(nickname + " left the chat.", this);
                    break; // leave the loop, go to finally
                }

                // normal chat message
                TCPServer.broadcast(nickname + ": " + message, this);
            }

        } catch (IOException e) {
            // unexpected disconnect (lost connection)
            System.out.println("Client " + nickname + " disconnected: " + e.getMessage());
            TCPServer.broadcast(nickname + " lost the connection.", this);

        } finally {
            // close socket and remove from list
            try {
                clientSocket.close();
            } catch (IOException ignored) {}

            TCPServer.connections.remove(this);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Failed to send to " + nickname + ": " + e.getMessage());
        }
    }
}
