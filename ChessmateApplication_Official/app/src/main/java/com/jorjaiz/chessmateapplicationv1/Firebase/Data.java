package com.jorjaiz.chessmateapplicationv1.Firebase;

public class Data
{
    private String user;
    private int icon;
    private String body;
    private String title;
    private String sented;

    private int gameMode;
    private int connection;
    private String playerName;
    private String oponentName;

    private String keyNotif;

    public Data(String user, int icon, String body, String title, String sented,
                int gameMode, int connection, String playerName, String oponentName, String keyNotif)
    {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;

        this.gameMode = gameMode;
        this.connection = connection;
        this.playerName = playerName;
        this.oponentName = oponentName;

        this.keyNotif = keyNotif;
    }

    public Data(){ }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }
    public void setSented(String sented) {
        this.sented = sented;
    }


    public int getGameMode() {
        return gameMode;
    }
    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getConnection() {
        return connection;
    }
    public void setConnection(int connection) {
        this.connection = connection;
    }

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getOponentName() {
        return oponentName;
    }
    public void setOponentName(String oponentName) {
        this.oponentName = oponentName;
    }


    public String getKeyNotif() {
        return keyNotif;
    }
    public void setKeyNotif(String keyNotif) {
        this.keyNotif = keyNotif;
    }
}
