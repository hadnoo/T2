package T2;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class TCPServer {

    public static final int PORT = 7896;

    public static void main(String[] args) {
        try {
            // Create a server socket and listen on the specified port
            ServerSocket listenSocket = new ServerSocket(PORT);
            
            //create an array of threads 
            ArrayList<Connection> connections = new ArrayList<>();
            Scanner s = new Scanner(System.in); 

            // Server runs continuously, accepting new clients
            while (true) {
                // Accept each client in a loop and handle it in a separate thread
            	Socket client = listenSocket.accept();
            	Connection c = new Connection(client);
            	connections.add(c); 
            	c.start(); 
            }

        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }
}

class Connection extends Thread {

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
