package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    static final ArrayList<Connection> connections = new ArrayList<>();

    ServerSocket server;

    private static boolean started = false;
    private int port;

    Server(int port) {
        try {
            this.port = port;
            server = new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println("Couldn't initiate server, port may be occupied");
            System.exit(1);
        }
    }

    public void run() {
        try {
            started = true;
            System.out.println("Listening on port " + port);
            while (started) {
                Connection client = new Connection(server.accept());
                System.out.println("New connection");

                addConnection(client);
            }
        } catch (IOException e) {
            try {
                server.close();
            } catch (IOException ex) {
                System.exit(1); // forcefully exit the program
            }
        }
    }

    public synchronized static void addConnection(Connection connection) {
        connections.add(connection);
        System.out.println(connections.size() + " client connected");
    }

    public synchronized static void removeConnection(Connection connection) {
        connections.remove(connection);
        System.out.println(connections.size() + " client connected");
    }

}
