package org.airhockey.protocol;

public class Message {
    public MessageType type;
    public String sender;
    public String payload;

    public Message(MessageType type, String sender, String payload) {
        this.type = type;
        this.sender = sender;
        this.payload = payload;
    }
}