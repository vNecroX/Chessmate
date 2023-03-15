package com.jorjaiz.chessmateapplicationv1.Classes;

import android.util.Log;

public class Move implements Constants, Cloneable
{
    boolean promot = false;
    Move rook;
    Piece pSaved = null;
    int type;

    byte x1, x2, y1, y2;

    public byte getX1() {
        return x1;
    }
    public byte getX2() {
        return x2;
    }
    public byte getY1() {
        return y1;
    }
    public byte getY2() {
        return y2;
    }


    boolean eat;
    public boolean isEat() {
        return eat;
    }
    public void setEat(boolean eat) {
        this.eat = eat;
    }

    Coord eatCoord;
    public Coord getEatCoord() {
        return eatCoord;
    }
    public void setEatCoord(Coord eatCoord) {
        this.eatCoord = eatCoord;
    }


    public Piece getpSaved() {
        return pSaved;
    }
    public void setpSaved(Piece pSaved) {
        this.pSaved = pSaved;
    }


    public Move getRook() {
        return rook;
    }
    public void setRook(Move rook) {
        this.rook = rook;
    }


    public Move(int x1, int y1, int x2, int y2)
    {
        this.x1 = (byte)x1;
        this.y1 = (byte)y1;
        this.x2 = (byte)x2;
        this.y2 = (byte)y2;
        rook = null;
    }

    void perform(Board bo)
    {
        try
        {
            Piece[][] p = bo.p;

            type = p[x1][y1].type;

            pSaved = bo.p[x2][y2]; // Save the original piece of spot before performing

            if(pSaved != null)
            {
                eat = true;
                eatCoord = new Coord(x2, y2);
            }
            else
            {
                eat = false;
                eatCoord = null;
            }

            p[x2][y2] = p[x1][y1];
            p[x1][y1] = null;

            // Promotion
            if(p[x2][y2].type == TYPE_PAWN && (y2 == 0 || y2 == 7))
            {
                p[x2][y2].setType(TYPE_QUEEN);
                promot = true;
            }

            // Castling
            if(p[x2][y2].type == TYPE_KING)
            {
                if(p[x2][y2].white)
                    bo.whiteKing.setCoord(x2, y2);
                else
                    bo.blackKing.setCoord(x2, y2);

                if(x1 == 4 && y1 == 0 && x2 == 6)
                    rook = new Move(7, 0, 5, 0);
                else if(x1 == 4 && y1 == 0 && x2 == 2)
                    rook = new Move(0, 0, 3, 0);
                else if(x1 == 4 && y1 == 7 && x2 == 6)
                    rook = new Move(7, 7, 5, 7);
                else if(x1 == 4 && y1 == 7 && x2 == 2)
                    rook = new Move(0, 7, 3, 7);

                if(rook != null)
                    rook.perform(bo);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "CLASS MOVE Except perform " + e.toString());
        }
    }

    void undo(Board bo)
    {
        Piece[][] p = bo.p;

        // For king and castling
        if(p[x2][y2].type == TYPE_KING)
        {
            if(p[x2][y2].white)
                bo.whiteKing.setCoord(x1, y1);
            else
                bo.blackKing.setCoord(x1, y1);

            if(rook != null)
            {
                rook.undo(bo);
                rook = null;
            }
        }

        p[x1][y1] = p[x2][y2];
        p[x2][y2] = pSaved;

        // For promotion
        if(promot)
        {
            p[x1][y1].setType(TYPE_PAWN);
            promot = false;
        }

        pSaved = null;
    }
}
