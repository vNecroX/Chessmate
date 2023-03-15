package com.jorjaiz.chessmateapplicationv1.Firebase;

public class Notification
{
    private String sender;
    private String receiver;
    public boolean isReplay;
    public boolean isAccepted;

    public Notification(String sender, String receiver, boolean isReplay, boolean isAccepted)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.isReplay = isReplay;
        this.isAccepted = isAccepted;
    }

    public Notification(){ }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isReplay() {
        return isReplay;
    }
    public void setReplay(boolean isReplay) {
        this.isReplay = isReplay;
    }

    public boolean isAccepted() {
        return isAccepted;
    }
    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
