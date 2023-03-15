package com.jorjaiz.chessmateapplicationv1;

public class User
{
    private String idPlayer;
    private String playerName;
    private String nation;
    private int connection;

    private int notif;

    public String getPlayerName()
    {
        return playerName;
    }
    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public String getNation()
    {
        return nation;
    }
    public void setNation(String nation)
    {
        this.nation = nation;
    }

    public String getIdPlayer() {
        return idPlayer;
    }
    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public int getConnection() {
        return connection;
    }
    public void setConnection(int connection) {
        this.connection = connection;
    }

    public int getNotif() {
        return notif;
    }
    public void setNotif(int notif) {
        this.notif = notif;
    }
}

