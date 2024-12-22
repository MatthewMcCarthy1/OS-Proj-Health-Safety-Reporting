import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler {
    private static final String SERVER_ADDRESS = "localhost"; //server address
    private static final int SERVER_PORT = 8080; //server port num
    private Socket socket; //socket for connecting to the server
    private ObjectOutputStream out; //send messages to the server
    private ObjectInputStream in; //receive messages from the server
    private Scanner scanner; //read user input

    //send a message to the server
    private void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
            System.out.println("client> " + message);
        } catch (IOException e) {
            System.err.println("Error sending message to server: " + e.getMessage());
        }
    }

    //receive a message to the server
    public String receiveMessage() throws IOException, ClassNotFoundException {
        String message = (String) in.readObject();
        System.out.println("server> " + message);
        return message;
    }

    /*handle server interactions (easiest way was to use .startswith(enter), obviously not a good idea but for a small
    project like this with simple prompts it saves having to write out a list of code receiving and sending responses.
    */
    private void handleServerInteraction() throws IOException, ClassNotFoundException {
        String serverMessage = receiveMessage();
        if (serverMessage.startsWith("Enter")) {
            String input = scanner.nextLine();
            sendMessage(input);
        }
    }

    //run the client
    public void run() {
        try {
            //get connection to the server
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            scanner = new Scanner(System.in);
            System.out.println("Connected to the server at " + SERVER_ADDRESS + ":" + SERVER_PORT);

            //main loop to handle server interactions
            while (true) {
                handleServerInteraction();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in communication with server: " + e.getMessage());
        } finally {
            //Closing connection
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //start the client
    public static void main(String[] args) {
        ClientHandler client = new ClientHandler();
        client.run();
    }
}
