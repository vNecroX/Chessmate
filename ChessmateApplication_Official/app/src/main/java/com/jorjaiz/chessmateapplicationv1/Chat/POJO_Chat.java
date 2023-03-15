package com.jorjaiz.chessmateapplicationv1.Chat;

public class POJO_Chat
{
    private String message;
    private int messType;
    private String messageTime;

    public POJO_Chat() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getMessType() { return messType; }
    public void setMessType(int messType) { this.messType = messType; }

    public String getMessageTime() { return messageTime; }
    public void setMessageTime(String messageTime) { this.messageTime = messageTime; }
}
