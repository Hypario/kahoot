package server;

import common.Message;
import common.Proposition;
import common.Question;

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

            switch (object.getType()) {
                case Proposition:
                    this.handleProposition((Proposition) object.getObject());
                    break;
                case ChannelChoice:
                    this.handleChannelChoice((String) object.getObject());
                    break;
            }
        }
    }

    private void handleProposition(Proposition proposition) {
        // handle proposition
    }

    private void handleChannelChoice(String channel) {
        // handle channel choice
    }

}
