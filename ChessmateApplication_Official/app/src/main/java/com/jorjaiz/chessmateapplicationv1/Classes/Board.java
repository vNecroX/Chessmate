package com.jorjaiz.chessmateapplicationv1.Classes;

public class Board implements Constants, Cloneable
{
    Piece[][] p;
    Coord whiteKing;
    Coord blackKing;

    boolean whiteKMoved;
    boolean blackKMoved;

    boolean whiteTRMoved;
    boolean whiteTLMoved;
    boolean blackTRMoved;
    boolean blackTLMoved;

    boolean turn;

    PieceCounts pC;

    public Board()
    {
        initBoard();
    }

    public Coord getWhiteKingCoord()
    {
        return whiteKing;
    }
    public void setWhiteKingCoord(Coord whiteKing)
    {
        this.whiteKing = whiteKing;
    }

    public Coord getBlackKingCoord()
    {
        return blackKing;
    }
    public void setBlackKingCood(Coord blackKing)
    {
        this.blackKing = blackKing;
    }

    private void initBoard()
    {
        p = new Piece[8][8];

        for(byte i=0; i<8; i++)
        {
            p[i][1] = new Piece(TYPE_PAWN, true); // For White
            p[i][6] = new Piece(TYPE_PAWN, false); // For Black
        }

        p[0][0] = p[7][0] = new Piece(TYPE_ROOK, true);
        p[1][0] = p[6][0] = new Piece(TYPE_KNIGHT, true);
        p[2][0] = p[5][0] = new Piece(TYPE_BISHOP, true);
        p[3][0] = new Piece(TYPE_QUEEN, true);
        p[4][0] = new Piece(TYPE_KING, true);

        p[0][7] = p[7][7] = new Piece(TYPE_ROOK, false);
        p[1][7] = p[6][7] = new Piece(TYPE_KNIGHT, false);
        p[2][7] = p[5][7] = new Piece(TYPE_BISHOP, false);
        p[3][7] = new Piece(TYPE_QUEEN, false);
        p[4][7] = new Piece(TYPE_KING, false);

        whiteKing = new Coord(4, 0);
        blackKing = new Coord(4, 7);

        whiteKMoved = false;
        blackKMoved = false;

        whiteTRMoved = false;
        whiteTLMoved = false;
        blackTRMoved = false;
        blackTLMoved = false;

        turn = true;

        pC = new PieceCounts(this);
    }

    public static Board newClonedBoard(Board bo)
    {
        Board newClone = (Board)bo.clone();
        newClone.p = new Piece[bo.p.length][];
        for(int x=0; x<bo.p.length; x++) { newClone.p[x] = bo.p[x].clone(); }
        newClone.whiteKing = (Coord)bo.whiteKing.clone();
        newClone.blackKing = (Coord)bo.blackKing.clone();
        newClone.pC = (PieceCounts)bo.pC.clone();
        newClone.pC.bo = (Board)bo.pC.bo.clone();
        return newClone;
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
            System.out.println("BOARD CLASS cant duplicate");
        }

        return obj;
    }

}
