package com.jorjaiz.chessmateapplicationv1.Firebase;

public class PlayerFire
{
    private String namePlayer;
    private int connection;
    private String idPlayer;

    public PlayerFire(String connection, String idPlayer, String namePlayer)
    {
        this.namePlayer = namePlayer;
        this.connection = Integer.parseInt(connection);
        this.idPlayer = idPlayer;
    }

    public PlayerFire()
    {
    }

    public String getNamePlayer() {
        return namePlayer;
    }
    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public int getConnection() {
        return connection;
    }
    public void setConnection(int connection) {
        this.connection = connection;
    }

    public String getIdPlayer() {
        return idPlayer;
    }
    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }
}
