package T2;

import java.net.*;
import java.io.*;

public class TCPClient {
    public static void main(String[] args) {
        Socket s = null;

        try {
            // Connect to the server on the given host and port
            s = new Socket("localhost", TCPServer.PORT);

            // Create input and output streams for reading/writing data
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            // Send a message to the server
            out.writeUTF("Message from " + new java.util.Date());

            // Wait for and read the server's reply (blocking call)
            String data = in.readUTF();

            // Print the reply from the server
            System.out.println("Received: " + data);

        } catch (IOException e) {

            System.out.println("IO: " + e.getMessage());

        } finally {

            // Close the connection gracefully
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    // Ignored: close failed
                }
            }
        }
    }
}
