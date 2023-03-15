package com.jorjaiz.chessmateapplicationv1.Classes;

import java.util.HashMap;
import java.util.Map;

public class Coord implements Cloneable
{
    public byte x, y;

    public static Map<Character, Integer> coordsX;

    public Coord(int x, int y)
    {
        this.x = (byte)x;
        this.y = (byte)y;

        coordsX = new HashMap<>();
        coordsX.put('A', 0);
        coordsX.put('B', 1);
        coordsX.put('C', 2);
        coordsX.put('D', 3);
        coordsX.put('E', 4);
        coordsX.put('F', 5);
        coordsX.put('G', 6);
        coordsX.put('H', 7);
    }

    public void setCoord(int x, int y)
    {
        this.x = (byte)x;
        this.y = (byte)y;
    }

    public static int getIntOfCoordByChar(char c)
    {
        return coordsX.get(c);
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
            System.out.println("COORD CLASS cant duplicate");
        }

        return obj;
    }
}
