package server;

import common.*;

import java.util.ArrayList;

public class ResponseHandler extends Thread {

    private Connection client;
    private Channel joinedChannel;

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
                        this.handleCreateChannelEvent();
                        break;
                    case QuizzChoice:
                        this.handleCreateChannel((CreateChannel) object.getObject());
                        break;
                    case QuizzStart:
                        this.handleQuizzStart((String) object.getObject());
                        break;
                    case SetUsername:
                        client.setUsername((String) object.getObject());
                        break;
                }
            } else {
                if (joinedChannel != null)
                    joinedChannel.remove(client);
                client.close(); // have to close client socket
                return; // if object is null, connection got interrupted, do not consume any cycles of CPU anymore
            }
        }
    }

    private void handleQuizzStart(String channelName) {
        Channel channel = Server.getChannel(channelName);
        channel.startQuizz();
    }

    // clicked on "create channel", return the list of all the quizzes
    private void handleCreateChannelEvent() {
        ArrayList<Quiz> quizzes = Server.getQuizzes();
        client.sendMessage(new Message(MessageType.QuizzList, quizzes));
    }

    // confirmed creation, create channel in server
    private void handleCreateChannel(CreateChannel createChannel) {
        joinedChannel = new Channel(createChannel.getChannelName(), createChannel.getAdmin(), createChannel.getQuizz());
        Server.setChannel(joinedChannel);
        joinedChannel.add(client); // add admin
        joinedChannel.start();
    }

    private void handleProposition(Proposition proposition) {
        client.setProposition(proposition);
    }

    private void handleChannelChoice(String id) {
        Channel joinedChannel = Server.getChannel(id);
        joinedChannel.add(client);
    }

}
