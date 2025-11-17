package T2;

import java.net.*;

//this is just an extra line for clarification



import java.io.*;
import java.util.*;

public class TCPServer {

    public static final int PORT = 7896;

    public static void main(String[] args) {
        try {
            // Create a server socket and listen on the specified port
            ServerSocket listenSocket = new ServerSocket(PORT);

            // Server runs continuously, accepting new clients
            while (true) {
                // Each accepted client is handled in a separate thread
                new Connection(listenSocket.accept()).start();
            }

        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }
}

class Connection extends Thread {
	int counter = 0;
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            // Store the socket representing this client's connection
            this.clientSocket = aClientSocket;

            // Create input and output streams for communication
            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            System.out.println("Connection: " + e.getMessage());
        }
    }

    public void run() {
        try {
            // Read a UTF string sent by the client
            String data = in.readUTF();

            // Echo the received string back to the client
            out.writeUTF(data);

        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());

        } finally {
            // Always close the client socket when done
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignored: close failed
            }
        }
    }
}
