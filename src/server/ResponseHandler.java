package server;

import common.Message;
import common.Proposition;

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
                }
            } else {
                client.close(); // have to close client socket
                return; // if object is null, connection got interrupted, do not consume any cycles of CPU anymore
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
