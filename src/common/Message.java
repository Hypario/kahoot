package common;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = -3714598469244907310L;
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

