package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

public class Communication implements Constants
{
    private String idPlayerM;
    private String idPlayerS;
    public boolean isPlayerM;
    public boolean isPlayerS;
    private String data;

    public Communication(String idPlayerM, String idPlayerS, boolean isPlayerM, boolean isPlayerS, String data)
    {
        this.idPlayerM = idPlayerM;
        this.idPlayerS = idPlayerS;
        this.isPlayerM = isPlayerM;
        this.isPlayerS = isPlayerS;
        this.data = data;
    }

    public Communication(){ }

    public String getIdPlayerM() {
        return idPlayerM;
    }
    public void setIdPlayerM(String idPlayerM) {
        this.idPlayerM = idPlayerM;
    }

    public String getIdPlayerS() {
        return idPlayerS;
    }
    public void setIdPlayerS(String idPlayerS)
    {
        this.idPlayerS = idPlayerS;
    }

    public boolean isPlayerM()
    {
        return isPlayerM;
    }
    public void setPlayerM(boolean playerM)
    {
        isPlayerM = playerM;
    }

    public boolean isPlayerS() {
        return isPlayerS;
    }
    public void setPlayerS(boolean playerS) {
        isPlayerS = playerS;
    }

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
