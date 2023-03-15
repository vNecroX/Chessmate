package com.jorjaiz.chessmateapplicationv1.Parameters;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

public class CP implements Constants
{
    private static CP PLAYER;

    private int idPlayer;
    private String namePlayer;
    private int conn;
    private int kindOfUser;

    private boolean autosave;
    private boolean board;
    private boolean chat;
    private boolean notif;

    private int numGamesSaved;

    private int lastKindOfPlayer;

    private CP()
    {
        idPlayer = 0;
        namePlayer = "";
        conn = 0;
        kindOfUser = NO_USER;

        autosave = false;
        board = false;
        chat = false;
        notif = false;

        numGamesSaved = 0;

        lastKindOfPlayer = NO_ONE;

    }

    public static CP get()
    {
        if(PLAYER == null)
            PLAYER = new CP();

        return PLAYER;
    }

    public static CP getPLAYER()
    {
        return PLAYER;
    }
    public static void setPLAYER(CP PLAYER)
    {
        CP.PLAYER = PLAYER;
    }

    public int getIdPlayer()
    {
        return idPlayer;
    }
    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getNamePlayer() {
        return namePlayer;
    }
    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public int getConn() {
        return conn;
    }
    public void setConn(int conn) {
        this.conn = conn;
    }

    public int getKindOfUser() {
        return kindOfUser;
    }
    public void setKindOfUser(int kindOfUser) {
        this.kindOfUser = kindOfUser;
    }



    public boolean isAutosave() {
        return autosave;
    }
    public void setAutosave(boolean autosave) {
        this.autosave = autosave;
    }

    public boolean isBoard() {
        return board;
    }
    public void setBoard(boolean board) {
        this.board = board;
    }

    public boolean isChat() {
        return chat;
    }
    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public boolean isNotif() {
        return notif;
    }
    public void setNotif(boolean notif) {
        this.notif = notif;
    }



    public int getNumGamesSaved() {
        return numGamesSaved;
    }
    public void setNumGamesSaved(int numGamesSaved) {
        this.numGamesSaved = numGamesSaved;
    }



    public int getLastKindOfPlayer() {
        return lastKindOfPlayer;
    }
    public void setLastKindOfPlayer(int lastKindOfPlayer) {
        this.lastKindOfPlayer = lastKindOfPlayer;
    }
}
