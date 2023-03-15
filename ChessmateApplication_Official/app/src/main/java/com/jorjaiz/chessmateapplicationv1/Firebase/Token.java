package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

public class Token implements Constants
{
    private String token;

    public Token(String token) {
         this.token = token;
    }
    public Token(){}

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
