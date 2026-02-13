import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket connection; //socket connection to client
    private SharedObject sharedObject; //for user and report management
    private ObjectOutputStream out; //send messages to client
    private ObjectInputStream in; //receive messages from client

    private User currentUser; //current user of the app

    //constuctor initialising serverthread with a client socket and shared object
    public ServerThread(Socket connection, SharedObject sharedObject) {
        this.connection = connection;
        this.sharedObject = sharedObject;
    }

    @Override
    public void run() {
        try {
            //initialise input and output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());

            //handle menu interactions
            MenuHandler menuHandler = new MenuHandler(this, sharedObject);
            menuHandler.menuHandler();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in ServerThread: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                connection.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    //set and get the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void sendPrompt(String msg) {
        sendMessage(msg + " >> ");
    }

    //send and receive messages to and from the client
    public void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        } catch (IOException ioException) {
            System.err.println("Error sending message: " + ioException.getMessage());
        }
    }

    public String receiveMessage() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }
}
