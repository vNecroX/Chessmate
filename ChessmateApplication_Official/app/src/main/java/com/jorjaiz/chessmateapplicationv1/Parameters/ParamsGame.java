package com.jorjaiz.chessmateapplicationv1.Parameters;

import android.os.Parcel;
import android.os.Parcelable;

public class ParamsGame implements Parcelable
{
    private int pOneId;
    private int pTwoId;
    private int pOneWhite;
    private int pTwoBlack;

    private int idGame;

    private int mode;
    private int difficulty;

    private String stringMoves;
    private int rewinds;
    private long secondsLeft;

    private int idPlayer;
    private int idOponent;
    private String playerName;
    private String oponentName;

    private String NewSaved;

    private int colorPlayer;

    private int resume;

    private int kindPlayer;

    private String keyComunnic;

    private int saveGame;

    private int kindOfLocal;

    public ParamsGame() { this.idGame = 0; }

    public int getpOneId()
    {
        return pOneId;
    }
    public void setpOneId(int pOneId)
    {
        this.pOneId = pOneId;
    }

    public int getpTwoId()
    {
        return pTwoId;
    }
    public void setpTwoId(int pTwoId)
    {
        this.pTwoId = pTwoId;
    }

    public int getpOneWhite()
    {
        return pOneWhite;
    }
    public void setpOneWhite(int pOneWhite)
    {
        this.pOneWhite = pOneWhite;
    }

    public int getpTwoBlack()
    {
        return pTwoBlack;
    }
    public void setpTwoBlack(int pTwoBlack)
    {
        this.pTwoBlack = pTwoBlack;
    }


    public int getMode()
    {
        return mode;
    }
    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public int getDifficulty()
    {
        return difficulty;
    }
    public void setDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
    }


    public String getStringMoves()
    {
        return stringMoves;
    }
    public void setStringMoves(String stringMoves)
    {
        this.stringMoves = stringMoves;
    }

    public int getRewinds()
    {
        return rewinds;
    }
    public void setRewinds(int rewinds)
    {
        this.rewinds = rewinds;
    }

    public long getSecondsLeft()
    {
        return secondsLeft;
    }
    public void setSecondsLeft(long secondsLeft)
    {
        this.secondsLeft = secondsLeft;
    }


    public int getIdPlayer()
    {
        return idPlayer;
    }
    public void setIdPlayer(int idPlayer)
    {
        this.idPlayer = idPlayer;
    }

    public int getIdOponent() {
        return idOponent;
    }
    public void setIdOponent(int idOponent) {
        this.idOponent = idOponent;
    }

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getOponentName()
    {
        return oponentName;
    }
    public void setOponentName(String oponentName)
    {
        this.oponentName = oponentName;
    }

    public int getIdGame()
    {
        return idGame;
    }
    public void setIdGame(int idGame)
    {
        this.idGame = idGame;
    }

    public String getNewSaved()
    {
        return NewSaved;
    }
    public void setNewSaved(String newSaved)
    {
        NewSaved = newSaved;
    }

    public int getColorPlayer()
    {
        return this.colorPlayer;
    }
    public void setColorPlayer(int colorPlayer)
    {
        this.colorPlayer = colorPlayer;
    }

    public void setResume(int resume) {
        this.resume = resume;
    }
    public int getResume() {
        return resume;
    }

    public int getKindPlayer() {
        return kindPlayer;
    }
    public void setKindPlayer(int kindPlayer) {
        this.kindPlayer = kindPlayer;
    }

    public String getKeyComunnic() {
        return keyComunnic;
    }
    public void setKeyComunnic(String keyComunnic) {
        this.keyComunnic = keyComunnic;
    }

    public int getSaveGame() {
        return saveGame;
    }
    public void setSaveGame(int saveGame) {
        this.saveGame = saveGame;
    }

    public int getKindOfLocal() {
        return kindOfLocal;
    }
    public void setKindOfLocal(int kindOfLocal) {
        this.kindOfLocal = kindOfLocal;
    }

    ///////////////////////////////////  PARCELABLE   /////////////////////////////////

    public ParamsGame(Parcel in)
    {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in )
    {
        pOneId = in.readInt();
        pTwoId = in.readInt();

        pOneWhite = in.readInt();
        pTwoBlack = in.readInt();

        mode = in.readInt();
        difficulty = in.readInt();

        rewinds = in.readInt();
        secondsLeft = in.readLong();

        idPlayer = in.readInt();
        idOponent = in.readInt();
        playerName = in.readString();
        oponentName = in.readString();

        idGame = in.readInt();

        stringMoves = in.readString();

        NewSaved = in.readString();

        colorPlayer = in.readInt();

        resume = in.readInt();

        kindPlayer = in.readInt();

        keyComunnic = in.readString();

        saveGame = in.readInt();

        kindOfLocal = in.readInt();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(pOneId);
        dest.writeInt(pTwoId);

        dest.writeInt(pOneWhite);
        dest.writeInt(pTwoBlack);

        dest.writeInt(mode);
        dest.writeInt(difficulty);

        dest.writeInt(rewinds);

        dest.writeLong(secondsLeft);

        dest.writeInt(idPlayer);
        dest.writeInt(idOponent);

        dest.writeString(playerName);
        dest.writeString(oponentName);

        dest.writeInt(idGame);

        dest.writeString(stringMoves);

        dest.writeString(NewSaved);

        dest.writeInt(colorPlayer);

        dest.writeInt(resume);

        dest.writeInt(kindPlayer);

        dest.writeString(keyComunnic);

        dest.writeInt(saveGame);

        dest.writeInt(kindOfLocal);
    }

    public static final Parcelable.Creator<ParamsGame> CREATOR = new Parcelable.Creator<ParamsGame>()
    {
        @Override
        public ParamsGame createFromParcel(Parcel source )
        {
            return new ParamsGame( source );
        }

        @Override
        public ParamsGame[] newArray(int size)
        {
            return new ParamsGame[size];
        }
    };
}
