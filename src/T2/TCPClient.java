package T2;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) {
        try (Socket s = new Socket("localhost", 7896);
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Ask for nickname
            String serverMsg = in.readUTF(); // "Please enter your nickname:"
            System.out.println(serverMsg);

            String nickname = scanner.nextLine();
            out.writeUTF(nickname);

            // Thread that listens for messages from server
            Thread listener = new Thread(() -> {
                try {
                    while (true) {
                        String msgFromServer = in.readUTF();
                        System.out.println(msgFromServer);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server: " + e.getMessage());
                }
            });
            listener.setDaemon(true);
            listener.start();

            // Main thread: only responsible for sending user input
            while (true) {
                String msg = scanner.nextLine();
                out.writeUTF(msg);

                // if the user types /quit, we exit the loop and close the socket
                if (msg.equalsIgnoreCase("/quit")) {
                    System.out.println("You left the chat.");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
