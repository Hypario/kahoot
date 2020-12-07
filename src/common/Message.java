package common;

public class Message {

    private MessageType type;
    private Object object;

    public Message(MessageType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public MessageType getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}

