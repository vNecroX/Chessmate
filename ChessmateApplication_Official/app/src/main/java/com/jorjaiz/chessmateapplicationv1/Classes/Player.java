package com.jorjaiz.chessmateapplicationv1.Classes;

public class Player
{
    private int id;
    private boolean human;

    public Player(int id, boolean color)
    {
        this.id = id;
        this.human = color;
    }

    public int getId()
    {
        return id;
    }

    public boolean isHuman()
    {
        return human;
    }
}


