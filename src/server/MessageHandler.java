package server;

import common.Message;

public class MessageHandler extends Thread {

    private Connection client;

    MessageHandler(Connection client) {
        super();
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            Message message = client.getMessage();

            if (message != null) {
                if (message.getAuthor().equals("client_socket")) {
                    this.handleSpecialCases(message);
                } else if (!message.getContent().trim().equals("") && !message.getContent().isBlank()) {
                    System.out.println("Server received : " + message.getContent());
                }
            }
        }
    }

    private void handleSpecialCases(Message message) {
        // todo : handle special cases
    }
}
