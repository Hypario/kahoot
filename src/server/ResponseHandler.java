package server;

import common.*;

import java.util.ArrayList;

public class ResponseHandler extends Thread {

    private Connection client;

    ResponseHandler(Connection client) {
        super();
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            Message object = client.getMessage();

            if (object != null) {
                switch (object.getType()) {
                    case Proposition:
                        this.handleProposition((Proposition) object.getObject());
                        break;
                    case ChannelChoice:
                        this.handleChannelChoice((String) object.getObject());
                        break;
                    case CreateChannel:
                        this.handleCreateChannel();
                        break;
                    case QuizzChoice:
                        this.handleCreateChannelQuizzChoice((CreateChannel) object.getObject());
                        break;
                }
            } else {
                client.close(); // have to close client socket
                return; // if object is null, connection got interrupted, do not consume any cycles of CPU anymore
            }
        }
    }

    // clicked on "create channel", return the list of all the quizzes
    private void handleCreateChannel() {
        ArrayList<Quiz> quizzes = Server.getQuizzes();
        client.sendMessage(new Message(MessageType.QuizzList, quizzes));
    }

    // confirmed creation, create channel in server
    private void handleCreateChannelQuizzChoice(CreateChannel createChannel) {
        Channel channel = new Channel(createChannel.getChannelName(), createChannel.getAdmin(), createChannel.getQuizz());
        Server.setChannel(channel);
        channel.add(client); // add admin
        channel.start();
    }

    private void handleProposition(Proposition proposition) {
        // handle proposition
    }

    private void handleChannelChoice(String id) {
        Channel channel = Server.getChannel(id);
        channel.add(client);
    }

}
