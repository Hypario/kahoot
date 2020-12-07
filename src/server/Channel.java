package server;

import common.Message;

import java.util.ArrayList;
import java.util.UUID;

public class Channel {

    UUID id;
    String name;

    // connected to the channel
    private static final ArrayList<Connection> connections = new ArrayList<>();

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public synchronized void add(Connection client) {
        connections.add(client);
        Server.removeConnection(client); // client isn't waiting for a channel anymore
    }

    public synchronized void remove(Connection client) {
        connections.remove(client);
        Server.removeConnection(client); // client isn't waiting for a channel anymore
    }

    public synchronized static void broadcast(Message message) {
        for (Connection connection : connections) {
            connection.sendMessage(message);
        }
    }

    public void start() {
        // start quizz
    }

}
