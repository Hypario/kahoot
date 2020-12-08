package server;

import common.Message;
import common.Quiz;

import java.util.ArrayList;

public class Channel extends Thread {

    private String channelName, admin;

    private Quiz quizz;

    // connected to the channel
    private static final ArrayList<Connection> connections = new ArrayList<>();

    public Channel(String name, String admin, Quiz quizz) {
        this.channelName = name;
        this.admin = admin;
        this.quizz = quizz;
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

    @Override
    public void run() {
        try {
            System.out.println("waiting for 10s");
            Thread.sleep(10000); // wait for people to connect

            // start quizz
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }

    public String getChannelName() {
        return channelName;
    }

    public String getAdmin() {
        return admin;
    }
}
