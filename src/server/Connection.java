package server;

import common.Message;
import common.Proposition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private final Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private Proposition proposition;

    Connection(Socket socket) {
        this.socket = socket;
    }

    public Message getMessage() {
        Message data = null;

        try {
            if (input == null)
                input = new ObjectInputStream(socket.getInputStream());

            data = (Message) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            this.handleException();
        }

        return data;
    }

    public void sendMessage(Message message) {
        try  {
            if (output == null)
                output = new ObjectOutputStream(socket.getOutputStream());

            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            this.handleException();
        }
    }

    public void joinChannel(String id) {
        Channel channel = Server.getChannel(id);
        channel.add(this);
    }

    public void leaveChannel(String id) {
        Channel channel = Server.getChannel(id);
        channel.remove(this);
    }

    public void close() {
        try {
            socket.close();
            Server.removeConnection(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Proposition getProposition() {
        return proposition;
    }

    public synchronized void setProposition(Proposition proposition) {
        this.proposition = proposition;
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
