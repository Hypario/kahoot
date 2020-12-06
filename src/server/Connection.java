package server;

import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private final Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    Connection(Socket socket) {
        this.socket = socket;
    }

    public Message getMessage() {
        Message message = null;

        try {
            if (input == null)
                input = new ObjectInputStream(socket.getInputStream());
            message = (Message) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            this.handleException();
        }

        return message;
    }

    public void sendMessage(Message message) {
        try {
            if (output == null)
                output = new ObjectOutputStream(socket.getOutputStream());

            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            this.handleException();
        }
    }

    private void handleException() {
        if (socket.isClosed()) {
            Server.removeConnection(this);
        } else if (!socket.isConnected()) {
            try {
                socket.close();
                Server.removeConnection(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
