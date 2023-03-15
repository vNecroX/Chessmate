package com.jorjaiz.chessmateapplicationv1.Classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Piece implements Constants, Cloneable
{
    byte type;
    boolean white;

    public static Map<Character, Integer> valueInt;
    public static Map<Character, Byte> valueByte;

    public Piece(byte type, boolean white)
    {
        this.type = type;
        this.white = white;

        valueInt = new HashMap<>();
        valueInt.put('P', 0);
        valueInt.put('N', 1);
        valueInt.put('B', 2);
        valueInt.put('R', 3);
        valueInt.put('Q', 4);
        valueInt.put('K', 5);

        valueByte = new HashMap<>();
        valueByte.put('P', (byte)0);
        valueByte.put('N', (byte)1);
        valueByte.put('B', (byte)2);
        valueByte.put('R', (byte)3);
        valueByte.put('Q', (byte)4);
        valueByte.put('K', (byte)5);
    }

    public int getCost(boolean turn)
    {
        if(turn)
            return (white?-1:1)*cost[type]; //When IA is white
        else
            return (white?1:-1)*cost[type]; //When IA is black
    }

    public void setType(byte type)
    {
        this.type = type;
    }

    public static int getIntValueByChar(char c)
    {
        return valueInt.get(c);
    }

    public static byte getByteValueByChar(char c)
    {
        return valueByte.get(c);
    }

    public boolean isWhite() {
        return white;
    }
    public void setWhite(boolean white) {
        this.white = white;
    }

    public Object clone()
    {
        Object obj = null;

        try
        {
            obj = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            System.out.println("PIECE CLASS cant duplicate");
        }

        return obj;
    }
}
