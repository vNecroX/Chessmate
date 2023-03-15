package com.jorjaiz.chessmateapplicationv1;

public class Game
{
    private int idGame;
    private String mode;
    private String difficulty;
    private String colorPlayer;

    public int getIdGame()
    {
        return idGame;
    }
    public void setIdGame(int idGame)
    {
        this.idGame = idGame;
    }

    public String getMode()
    {
        return mode;
    }
    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getDifficulty()
    {
        return difficulty;
    }
    public void setDifficulty(String difficulty)
    {
        this.difficulty = difficulty;
    }

    public String getColorPlayer() {
        return colorPlayer;
    }
    public void setColorPlayer(String colorPlayer)
    {
        this.colorPlayer = colorPlayer;
    }

    private String lastState;
    private String nameOfGame;

    public String getLastState() {
        return lastState;
    }
    public void setLastState(String lastState) {
        this.lastState = lastState;
    }

    public String getNameOfGame() {
        return nameOfGame;
    }
    public void setNameOfGame(String nameOfGame) {
        this.nameOfGame = nameOfGame;
    }

    private String playerName;
    private String playerStatus;

    private String oponentName;
    private String oponentStatus;
    private String oponentColor;

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerStatus() {
        return playerStatus;
    }
    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    public String getOponentName() {
        return oponentName;
    }
    public void setOponentName(String oponentName) {
        this.oponentName = oponentName;
    }

    public String getOponentColor() {
        return oponentColor;
    }
    public void setOponentColor(String oponentColor) {
        this.oponentColor = oponentColor;
    }

    public String getOponentStatus() {
        return oponentStatus;
    }
    public void setOponentStatus(String oponentStatus) {
        this.oponentStatus = oponentStatus;
    }
}
