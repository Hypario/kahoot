package common;

import java.util.ArrayList;

public class Message {

    private String author;
    private String content;

    private ArrayList<Object> metadata;

    public Message(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<Object> metadata)
    {
        this.metadata = metadata;
    }
}
