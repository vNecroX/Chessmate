package com.grauben98.esteb.appchessmate.POJO;

public class POJO_SavedGames {
    private String gameID;
    private String status;
    private String name;
    private String playerOneName;
    private String playerOnePieces;
    private String playerOneStatus;
    private String playerTwoName;
    private String playerTwoPieces;
    private String playerTwoStatus;
    private String mode;
    private String difficulty;

    public POJO_SavedGames() {}

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerOnePieces() {
        return playerOnePieces;
    }

    public void setPlayerOnePieces(String playerOnePieces) {
        this.playerOnePieces = playerOnePieces;
    }

    public String getPlayerOneStatus() {
        return playerOneStatus;
    }

    public void setPlayerOneStatus(String playerOneStatus) {
        this.playerOneStatus = playerOneStatus;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public String getPlayerTwoPieces() {
        return playerTwoPieces;
    }

    public void setPlayerTwoPieces(String playerTwoPieces) {
        this.playerTwoPieces = playerTwoPieces;
    }

    public String getPlayerTwoStatus() {
        return playerTwoStatus;
    }

    public void setPlayerTwoStatus(String playerTwoStatus) {
        this.playerTwoStatus = playerTwoStatus;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
