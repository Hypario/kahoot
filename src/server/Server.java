package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import common.BDCreate;
import common.Message;
import common.MessageType;
import common.Quiz;

public class Server {

    // waiting to go select a channel
    private static final ArrayList<Connection> connections = new ArrayList<>();

    // all channels available
    private static final HashMap<String, Channel> channels = new HashMap<>();

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

                // add the connection to the server
                addConnection(client);

                // set handler of responses
                ResponseHandler handler = new ResponseHandler(client);
                handler.start();

                // send the list of channels
                ArrayList<String> channels_name = new ArrayList<>();
                for (Map.Entry<String, Channel> entry : channels.entrySet()) {
                    channels_name.add(entry.getValue().getChannelName());
                }
                Message message = new Message(MessageType.Channels, channels_name);
                client.sendMessage(message);
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
        System.out.println(connections.size() + " client in wainting list");
    }

    public synchronized static void removeConnection(Connection connection) {
        connections.remove(connection);
        channels.forEach((s, channel) -> channel.remove(connection)); // remove the connection to any channel
        System.out.println(connections.size() + " client in waiting list");
    }

    public synchronized static Channel getChannel(String channelName) {
        return channels.get(channelName);
    }

    public synchronized static void setChannel(Channel channel) {
        channels.put(channel.getChannelName(), channel);
    }

    public synchronized static ArrayList<Quiz> getQuizzes() {
        BDCreate bdCreate = BDCreate.getInstance();
        ArrayList<Quiz> quizzes = new ArrayList<>();

        java.sql.Connection con = bdCreate.connect();
        try {
            Statement statement = con.createStatement();
            statement.execute("SELECT quiz.* from quiz");
            ResultSet result = statement.getResultSet();

            while (result.next()) {
                String author = result.getString("quiz.author");
                String theme = result.getString("quiz.theme");
                double difficulty = result.getDouble("quiz.difficulty");

                quizzes.add(new Quiz(author, theme, difficulty));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // System.err.println("Something wrong happened");
        }

        return quizzes;
    }

}
