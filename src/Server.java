import java.io.IOException;
import java.net.*;

public class Server {
    private static final int PORT = 8080; //server listens for connections on this port num
    private static boolean running = true; //flag to control the state of server

    public static void main(String[] args) {
        SharedObject sharedObject = new SharedObject(); //store users and reports

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            //this is the main loop for accepting client connections
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept(); //accept a client
                    System.out.println("Client connected. ");

                    //create and start a new thread for each client
                    ServerThread handler = new ServerThread(clientSocket, sharedObject);

                    handler.start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    //graceful shutdown
    public static void shutdown() {
        running = false;
    }
}
