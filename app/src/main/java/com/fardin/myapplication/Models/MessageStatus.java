package com.fardin.myapplication.Models;

public class MessageStatus {
    private boolean seen;
    private boolean send;

    public MessageStatus(boolean send , boolean seen) {
        this.seen = seen;
        this.send = send;
    }

    public MessageStatus() {
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }
}
