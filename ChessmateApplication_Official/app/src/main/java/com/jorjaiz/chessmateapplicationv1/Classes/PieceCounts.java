package com.jorjaiz.chessmateapplicationv1.Classes;

import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.MainActivity;

public class PieceCounts implements Constants, Cloneable
{
    private int whiteCount;
    private int blackCount;

    private int whitePawns;
    private int blackPawns;
    private int whiteKnights;
    private int blackKnights;
    private int whiteBishops;
    private int blackBishops;
    private int whiteRooks;
    private int blackRooks;
    private int whiteQueen;
    private int blackQueen;
    private int whiteKing;
    private int blackKing;

    public Board bo;

    public PieceCounts(Board bo)
    {
        this.bo = bo;
        refreshPieceCount();
    }

    public void refreshPieceCount()
    {
        defaultValues();
        for(byte a=0; a<8; a++)
            for(byte b=0; b<8; b++)
                if(bo.p[a][b] != null)
                    count(bo.p[a][b].type, bo.p[a][b].white);
    }

    private void defaultValues()
    {
        whiteCount = blackCount = 0;
        whitePawns = whiteKnights = whiteBishops = whiteRooks = whiteQueen = whiteKing = 0;
        blackPawns = blackKnights = blackBishops = blackRooks = blackQueen = blackKing = 0;
    }

    private void count(byte type, boolean white)
    {
        if(white)
        {
            whiteCount++;
            switch(type)
            {
                case TYPE_PAWN: whitePawns++; break;
                case TYPE_KNIGHT: whiteKnights++; break;
                case TYPE_BISHOP: whiteBishops++; break;
                case TYPE_ROOK: whiteRooks++; break;
                case TYPE_QUEEN: whiteQueen++; break;
                case TYPE_KING: whiteKing++; break;
            }
        }
        else
        {
            blackCount++;
            switch(type)
            {
                case TYPE_PAWN: blackPawns++; break;
                case TYPE_KNIGHT: blackKnights++; break;
                case TYPE_BISHOP: blackBishops++; break;
                case TYPE_ROOK: blackRooks++; break;
                case TYPE_QUEEN: blackQueen++; break;
                case TYPE_KING: blackKing++; break;
            }
        }
    }

    public int getPieceCount(byte type, boolean white)
    {
        if(white)
        {
            switch(type)
            {
                case TYPE_PAWN: return whitePawns;
                case TYPE_KNIGHT: return whiteKnights;
                case TYPE_BISHOP: return whiteBishops;
                case TYPE_ROOK: return whiteRooks;
                case TYPE_QUEEN: return whiteQueen;
                case TYPE_KING: return whiteKing;
            }
        }
        else
        {
            switch(type)
            {
                case TYPE_PAWN: return blackPawns;
                case TYPE_KNIGHT: return blackKnights;
                case TYPE_BISHOP: return blackBishops;
                case TYPE_ROOK: return blackRooks;
                case TYPE_QUEEN: return blackQueen;
                case TYPE_KING: return blackKing;
            }
        }
        return 0;
    }

    public int getColorCount(boolean white)
    {
        if(white)
            return whiteCount;
        else
            return blackCount;
    }

    public boolean isDrawWithoutMaterial()
    {
        if((whiteCount+blackCount < 5) && (whiteQueen+blackQueen == 0) &&
                (whiteRooks+blackRooks == 0) && (whitePawns+blackPawns == 0))
        {
            // K vs K
            if((whiteKnights+whiteBishops) == 0 && (blackKnights+blackBishops) == 0)
            {
                Log.w(MainActivity.TAG, "K vs K");
                return true;
            }
            // K vs KN || K vs KB
            else if((whiteKnights+whiteBishops+blackKnights+blackBishops) == 1)
            {
                Log.w(MainActivity.TAG, "K vs KN || K vs KB");
                return true;
            }
            // KB vs KB (Bishop with the same spot color)
            else if((whiteKnights == 0 && whiteBishops == 1) && (blackKnights == 0 && blackBishops == 1))
            {
                if(getBishopPosition(true) == getBishopPosition(false))
                {
                    Log.w(MainActivity.TAG, "KB vs KB");
                    return true;
                }
            }
            // KN vs KB || KN vs KN
            else if((whiteKnights+whiteBishops) == 1 && (blackKnights+blackBishops) == 1)
            {
                Log.w(MainActivity.TAG, "KN vs KB || KN vs KN");
                return true;
            }
            // KNN vs K
            else if((whiteKnights == 2 && whiteBishops == 0) || (blackKnights == 2 && blackBishops == 0))
            {
                Log.w(MainActivity.TAG, "KNN vs K");
                return true;
            }

            Log.i(MainActivity.TAG, "Nothing about draw for material");
            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean getBishopPosition(boolean white)
    {
        boolean w = false;

        for(byte a=0; a<8; a++)
        {
            for(byte b=0; b<8; b++)
            {
                if(bo.p[a][b] != null && bo.p[a][b].type == TYPE_BISHOP && bo.p[a][b].white == white)
                {
                    return w;
                }
                w = !w;
            }
            w = !w;
        }
        return false;
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
            System.out.println("PIECECOUNTS CLASS cant duplicate");
        }

        return obj;
    }
}
