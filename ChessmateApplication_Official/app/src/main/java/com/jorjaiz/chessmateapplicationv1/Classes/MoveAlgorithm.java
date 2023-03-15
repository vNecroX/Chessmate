package com.jorjaiz.chessmateapplicationv1.Classes;

import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MoveAlgorithm implements Constants, Cloneable
{
    private Board board;

    private byte level;
    private int stepCounter;
    private Move lastMove;
    private Date time1, time2;
    private Random rnd;

    private int estNow;
    private int estCost;
    private int estAttackCost;

    private int difficulty;
    private int easyParameter;
    private static int easyParameter2;

    private int numNodes;
    private int theNode;

    private int numThread;

    public MoveAlgorithm()
    {
        this.board = new Board();

        this.level = 0;
        this.stepCounter = 0;
        this.lastMove = null;

        this.estNow = 0;
        this.estCost = 0;
        this.estAttackCost = 0;

        this.rnd = new Random();

        this.numNodes = 0;
        this.theNode = 0;

        this.numThread = 0;
    }

    public void setBoard(Board board)
    {
        this.board = board;
    }

    public void setDepth(byte level)
    {
        this.level = level;
    }
    public byte getDepth()
    {
        return level;
    }

    public void setDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
        this.easyParameter = 0;
    }
    public int getDifficulty()
    {
        return difficulty;
    }

    public Move getLastMove()
    {
        return lastMove;
    }
    public void setLastMove(Move lastMove)
    {
        this.lastMove = lastMove;
    }

    public int getStepCounter()
    {
        return stepCounter;
    }
    public void setStepCounter(int stepCounter)
    {
        this.stepCounter = stepCounter;
    }

    public int getNumNodes() {
        return numNodes;
    }
    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public int getTheNode() {
        return theNode;
    }
    public void setTheNode(int theNode) {
        this.theNode = theNode;
    }

    public int getNumThread() {
        return numThread;
    }
    public void setNumThread(int numThread) {
        this.numThread = numThread;
    }

    public Board getBoard() {
        return board;
    }


    public int getEasyParameter() {
        return easyParameter;
    }
    public void setEasyParameter(int easyParameter) {
        this.easyParameter = easyParameter;
    }

    public int getEasyParameter2()
    {
        return easyParameter2;
    }
    public void setEasyParameter2(int easyParameter2) {
        this.easyParameter2 = easyParameter2;
    }


    // Response of Human
    public Move replyHuman(Move finalMove)
    {
        lastMove = finalMove;
        return finalMove;
    }


    public int alphaBeta(boolean white, int alpha, int beta, int depth)
    {
        if(depth == 0)
        {
            numNodes++;
            int esti = estimate();
            //Log.w(TAG, "        >>>>>  LEAF NODE #" + numNodes + "  <<<<<");
            //Log.i(TAG, "        ***TOTAL: " + esti);
            return esti;
        }

        int best = -INFINITY;

        ArrayList<Move> vv;

        if(difficulty == EASY)
        {
            if(easyParameter < easyParameter2)
            {
                vv = successorsEasy(white);
                easyParameter++;
            }
            else
            {
                vv = successors(white);
            }
        }
        else
        {
            vv = successors(white);
        }

        if(vv != null)
        {
            vv = randomize(board.p, vv);

            ArrayList<Move> v = new ArrayList<>();

            if(difficulty == EASY)
            {
                v = vv;
            }
            else if(difficulty == INTERMEDIATE)
            {
                v = vv;
            }
            else
            {
                if(depth == level)
                {
                    if(vv.size() >= 8)
                    {
                        int f = vv.size()/4;

                        switch(numThread)
                        {
                            case 1: v.addAll(vv.subList(0, f)); break;
                            case 2: v.addAll(vv.subList(f, f+f)); break;
                            case 3: v.addAll(vv.subList(f+f, f+f+f)); break;
                            case 4: v.addAll(vv.subList(f+f+f, f+f+f+f)); break;
                        }
                    }
                    else
                    {
                        if(difficulty == DIFFICULT)
                        {
                            if(vv.size() >= 4)
                                v.addAll(vv.subList(0, vv.size()/2));
                            else
                                v = vv;
                        }
                        else
                        {
                            v = vv;
                        }
                    }
                }
                else
                {
                    if(vv.size() >= 8)
                    {
                        v.addAll(vv.subList(0, ((vv.size()/2)+(vv.size()/10)+(vv.size()/10)+(vv.size()/10))));
                    }
                    else
                    {
                        if(vv.size() >= 4)
                            v.addAll(vv.subList(0, vv.size()/2));
                        else
                            v = vv;
                    }
                }
            }

            while(v.size()>0 && best<beta)
            {
                Move m = v.remove(0);
                m.perform(board);

                //Log.i(TAG, "BETA is: " + beta);
                //Log.i(TAG, "BEST is: " + best);

                if(best > alpha)
                    alpha = best;

                if(depth == level)
                {
                    //log.w(TAG, " ");
                    //Log.w(TAG, " ( ( ( ( ( ( ( ( ( ( ( ( ( ( (     NODO DE PRIMER NIVEL     ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ");
                    //Log.w(TAG, " ");
                }

                int est = -alphaBeta(!white, -beta, -alpha, depth-1);

                //Log.i(TAG, "        ESTIMATION IN ALPHA BETA: " + est);

                if(est > best)
                {
                    best = est;

                    if(depth == level)
                    {
                        lastMove = m;
                        theNode = numNodes;
                    }
                }

                m.undo(board);
            }
        }

        return best;
    }

    public int alphaBetaFather(ArrayList<Move> bestMoves, int alpha, int beta, int depth)
    {
        if(depth == 0)
        {
            numNodes++;
            return estimate();
        }

        int best = -INFINITY;

        if(bestMoves != null)
        {
            while(bestMoves.size()>0 && best<beta)
            {
                Move m = bestMoves.remove(0);
                m.perform(board);

                if(best > alpha)
                    alpha = best;

                int est = -alphaBetaFather(bestMoves, -beta, -alpha, depth-1);

                //Log.i(TAG, "        FINAL ESTIMATION IN ALPHA BETA: " + est);

                if(est > best)
                {
                    best = est;

                    if(depth == 1)
                    {
                        lastMove = m;
                        theNode = numNodes;
                        //Log.w(TAG, "        FINAL NEW BEST MOVE FOUND: " + theNode);
                    }
                }

                m.undo(board);
            }
        }

        return best;
    }

    public void testAlgorithm()
    {
        if(time1 == null)
        {
            time1 = new Date();
        }
        else
        {
            time2 = new Date();

            long milliDiff = time2.getTime() - time1.getTime();
            long seconds = (milliDiff/1000);
            long milliseconds = (milliDiff-(seconds*1000));

            time1 = time2 = null;

            Log.w(TAG," ");
            Log.w(TAG,"        < TEST REPORT >");

            if(lastMove != null)
                Log.w(MainActivity.TAG, "          Reply => THERE IS A MOVE");
            else
                Log.w(MainActivity.TAG, "       Reply =>  NULL MOVE");

            Log.w(MainActivity.TAG,"        # Seconds: " + seconds + " Milliseconds: " + milliseconds);
            Log.w(MainActivity.TAG,"        # Level of Search Tree: " + level + " (1)");
            Log.w(TAG, "        # Number of nodes analyzed in MAIN THREAD: " + stepCounter);
            Log.w(TAG, "        # THE NODE choosen #" + theNode);
            Log.w(TAG," ");
        }
    }

    public void testSubAlgorithm(int numThread)
    {
        if(time1 == null)
        {
            time1 = new Date();
        }
        else
        {
            time2 = new Date();

            long milliDiff = time2.getTime() - time1.getTime();
            long seconds = (milliDiff/1000);
            long milliseconds = (milliDiff-(seconds*1000));

            time1 = time2 = null;

            Log.w(TAG," ");
            Log.w(MainActivity.TAG,"        < SUB-TEST REPORT > THREAD #" + numThread);

            if(lastMove != null)
                Log.w(MainActivity.TAG, "          Reply => THERE IS A MOVE");
            else
                Log.w(MainActivity.TAG, "       Reply =>  NULL MOVE");

            Log.w(MainActivity.TAG,"        # Seconds: " + seconds + " Milliseconds: " + milliseconds);
            Log.w(MainActivity.TAG,"        # Level of Search Tree: " + level);
            Log.w(TAG, "        # Nodes analyzed in Thread #" + numThread + ": " + stepCounter);
            Log.w(TAG, "        # THE NODE choosen #" + theNode);
            Log.w(TAG," ");
        }
    }

    private ArrayList<Move> successors(boolean white)
    {
        ArrayList<Move> v = null;

        for(byte a=0; a<8; a++)
            for(byte b=0; b<8; b++)
                if(board.p[a][b] != null && board.p[a][b].white == white)
                    v = sumVectors(v, getRealAll(new Coord(a, b)));
        return v;
    }

    private ArrayList<Move> randomize(Piece[][] p, ArrayList<Move> v)
    {
        int s = v.size();
        int b = 0;

        // Put preference with the places which have a piece
        for(int i=0; i<s; i++)
        {
            Move m = v.get(i);

            if(p[m.x2][m.y2] != null)
            {
                v.remove(i);
                v.add(0, m);
                b++;
            }
        }

        for(int i=b; i<s-1; i++)
        {
            Move m = v.get(i);
            int j = rnd.nextInt(s-1-i)+i+1;
            v.set(i, v.get(j));
            v.set(j, m);
        }

        return v;
    }

    private ArrayList<Move> successorsEasy(boolean white)
    {
        ArrayList<Move> v = null;

        for(byte a=0; a<8; a++)
        {
            for(byte b=0; b<8; b++)
            {
                if(board.p[a][b] != null && board.p[a][b].white == white && board.p[a][b].type == TYPE_PAWN)
                {
                    v = sumVectors(v, getRealAll(new Coord(a, b)));
                }
            }
        }
        return v;
    }


    // METHODS FOR ALGORITHM

    public int estimateBase()
    {
        lastMove = null;
        estCost = 0;
        estAttackCost = 0;

        int out = estimate();

        estCost = getCost();
        estAttackCost = getAttackCost();

        return out;
    }

    private int estimate()
    {
        stepCounter++;
        return (((getCost()-estCost)*10)+(getAttackCost()-estAttackCost));
    }

    /**
     * GET COST DOC
     *
     * If OUT (+) WHITE (OPPONENT) predominates
     * If OUT (-) BLACK (IA) predominates
     *
     */

    private int getCost()
    {
        int out = 0;
        for(int a=0; a<8; a++)
            for(int b=0; b<8; b++)
                if(board.p[a][b] != null)
                    out += board.p[a][b].getCost(board.turn);

        //Log.i(TAG, "            GETCOST out = " + out);

        return out;
    }


    /**
     * GET ATTACK COST DOC
     *
     * If -OUT (-) there's more WHITE (OPPONENT pieces) eaten
     * If -OUT (+) there's more BLACK (IA pieces) eaten
     *
     */

    private int getAttackCost()
    {
        int out = 0;
        ArrayList<Move> v;
        for(int a=0; a<8; a++)
        {
            for(int b=0; b<8; b++)
            {
                if(board.p[a][b] != null)
                {
                    v = getRealAttacks(new Coord(a, b));
                    if(v != null)
                    {
                        for(int i=0; i<v.size(); i++)
                        {
                            Move m = v.get(i);
                            out += board.p[m.x2][m.y2].getCost(board.turn);
                            //Log.i(TAG, "                - attack out = " + out);
                        }
                    }
                }
            }
        }

        //Log.i(TAG, "            GETATTACKCOST out = " + -out);
        return -out;
    }

    // GET MOVES OF PIECES

    public ArrayList<Move> getRealAll(Coord c)
    {
        Piece p = board.p[c.x][c.y];

        if(p == null)
            return null;

        return sumVectors(getRealMoves(c), getRealAttacks(c));
    }

    public ArrayList<Move> getRealAttacks(Coord c)
    {
        Piece p = board.p[c.x][c.y];
        ArrayList<Move> v = getAttacks(c);

        if(v != null)
        {
            for(int i=0; i<v.size(); i++)
            {
                Move m = v.get(i);

                if(board.p[m.x2][m.y2] == null || board.p[m.x2][m.y2].white == p.white)
                {
                    v.remove(i);
                    i--;
                }
                else
                {
                    m.perform(board);

                    if(isAttacked(p.white?board.whiteKing:board.blackKing, !p.white))
                    {
                        v.remove(i);
                        i--;
                    }

                    m.undo(board);
                }
            }

            if(v.size() == 0)
                v = null;
        }

        return v;
    }

    public ArrayList<Move> getRealMoves(Coord c)
    {
        Piece p = board.p[c.x][c.y];
        ArrayList<Move> v = getMoves(c);

        if(v != null)
        {
            for(int i=0; i<v.size(); i++)
            {
                Move m = v.get(i);

                if(board.p[m.x2][m.y2] != null)
                {
                    v.remove(i);
                    i--;
                }
                else
                {
                    m.perform(board);

                    if(isAttacked(p.white?board.whiteKing:board.blackKing, !p.white))
                    {
                        v.remove(i);
                        i--;
                    }

                    m.undo(board);
                }
            }

            if(v.size() == 0)
                v = null;
        }

        return v;
    }

    private ArrayList<Move> getAttacks(Coord c)
    {
        Piece p = board.p[c.x][c.y];
        if(p == null)
            return null;

        switch(p.type)
        {
            case TYPE_PAWN: return getPawnAttacks(c);
            case TYPE_KNIGHT: return getKnightMoves(c);
            case TYPE_BISHOP: return getBishopMoves(c);
            case TYPE_ROOK: return getRookMoves(c);
            case TYPE_QUEEN: return getQueenMoves(c);
            case TYPE_KING: return getBasicKingMoves(c);
        }

        return null;
    }

    private ArrayList<Move> getMoves(Coord c)
    {
        Piece p = board.p[c.x][c.y];
        if(p==null)
            return null;

        switch(p.type)
        {
            case TYPE_PAWN: return getPawnMoves(c);
            case TYPE_KNIGHT: return getKnightMoves(c);
            case TYPE_BISHOP: return getBishopMoves(c);
            case TYPE_ROOK: return getRookMoves(c);
            case TYPE_QUEEN: return getQueenMoves(c);
            case TYPE_KING: return getKingMoves(c);
        }

        return null;
    }

    // MOVES OF PIECES | LEGAL MOVES OF EACH PIECE

    private ArrayList<Move> getPawnMoves(Coord c)
    {
        ArrayList<Move> v = new ArrayList<>();

        if(board.p[c.x][c.y].white)
        {
            v.add(new Move(c.x, c.y, c.x, c.y+1));
            if(c.y == 1)
                if(board.p[c.x][c.y+1] == null)
                    v.add(new Move(c.x, c.y, c.x, c.y+2));
        }
        else
        {
            v.add(new Move(c.x, c.y, c.x, c.y-1));
            if(c.y == 6)
                if(board.p[c.x][c.y-1] == null)
                    v.add(new Move(c.x, c.y, c.x, c.y-2));
        }

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> getPawnAttacks(Coord c)
    {
        ArrayList<Move> v = new ArrayList<>();

        if(board.p[c.x][c.y].white)
        {
            if(c.x+1 <= 7)
                v.add(new Move(c.x, c.y, c.x+1, c.y+1));
            if(c.x-1 >= 0)
                v.add(new Move(c.x, c.y, c.x-1, c.y+1));
        }
        else
        {
            if(c.x+1 <= 7)
                v.add(new Move(c.x, c.y, c.x+1, c.y-1));
            if(c.x-1 >= 0)
                v.add(new Move(c.x, c.y, c.x-1, c.y-1));
        }

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> getKnightMoves(Coord c)
    {
        ArrayList<Move> v = new ArrayList<>();

        if(c.x+1 <= 7)
        {
            if(c.y+2 <= 7)
                v.add(new Move(c.x, c.y, c.x+1, c.y+2));
            if(c.y-2 >= 0)
                v.add(new Move(c.x, c.y, c.x+1, c.y-2));
            if(c.x+2 <= 7)
            {
                if(c.y+1 <= 7)
                    v.add(new Move(c.x, c.y, c.x+2, c.y+1));
                if(c.y-1 >= 0)
                    v.add(new Move(c.x, c.y, c.x+2, c.y-1));
            }
        }
        if(c.x-1 >= 0)
        {
            if(c.y+2 <= 7)
                v.add(new Move(c.x, c.y, c.x-1, c.y+2));
            if(c.y-2 >= 0)
                v.add(new Move(c.x, c.y, c.x-1, c.y-2));
            if(c.x-2 >= 0)
            {
                if(c.y+1 <= 7)
                    v.add(new Move(c.x, c.y, c.x-2, c.y+1));
                if(c.y-1 >= 0)
                    v.add(new Move(c.x, c.y, c.x-2, c.y-1));
            }
        }

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> getBishopMoves(Coord c)
    {
        ArrayList<Move> v = new ArrayList<>();

        byte i;

        // Top Right
        i = 1;
        while(c.x+i <= 7 && c.y+i <= 7)
        {
            v.add(new Move(c.x, c.y, c.x+i, c.y+i));
            if(board.p[c.x+i][c.y+i] != null)
                break;
            i++;
        }

        // Bottom Right
        i = 1;
        while(c.x+i <= 7 && c.y-i >= 0)
        {
            v.add(new Move(c.x, c.y, c.x+i, c.y-i));
            if(board.p[c.x+i][c.y-i] != null)
                break;
            i++;
        }

        // Bottom Left
        i = 1;
        while(c.x-i >= 0 && c.y-i >= 0)
        {
            v.add(new Move(c.x, c.y, c.x-i, c.y-i));
            if(board.p[c.x-i][c.y-i] != null)
                break;
            i++;
        }

        // Top Left
        i = 1;
        while(c.x-i >= 0 && c.y+i <= 7)
        {
            v.add(new Move(c.x, c.y, c.x-i, c.y+i));
            if(board.p[c.x-i][c.y+i] != null)
                break;
            i++;
        }

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> getRookMoves(Coord c)
    {
        ArrayList<Move> v = new ArrayList<>();
        byte i;

        // Top
        i = 1;
        while(c.y+i <= 7)
        {
            v.add(new Move(c.x, c.y, c.x, c.y+i));
            if(board.p[c.x][c.y+i] != null)
                break;
            i++;
        }

        // Right
        i = 1;
        while(c.x+i <= 7)
        {
            v.add(new Move(c.x, c.y, c.x+i, c.y));
            if(board.p[c.x+i][c.y] != null)
                break;
            i++;
        }

        // Bottom
        i = 1;
        while(c.y-i >= 0)
        {
            v.add(new Move(c.x, c.y, c.x, c.y-i));
            if(board.p[c.x][c.y-i] != null)
                break;
            i++;
        }

        // Left
        i = 1;
        while(c.x-i >= 0)
        {
            v.add(new Move(c.x, c.y, c.x-i, c.y));
            if(board.p[c.x-i][c.y] != null)
                break;
            i++;
        }

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> getQueenMoves(Coord c)
    {
        return sumVectors(getRookMoves(c), getBishopMoves(c));
    }

    private ArrayList<Move> getBasicKingMoves(Coord c)
    {
        ArrayList<Move> v = new ArrayList<>();

        if(c.x+1 <= 7)
        {
            v.add(new Move(c.x, c.y, c.x+1, c.y));
            if(c.y+1 <= 7)
                v.add(new Move(c.x, c.y, c.x+1, c.y+1));
            if(c.y-1 >= 0)
                v.add(new Move(c.x, c.y, c.x+1, c.y-1));
        }

        if(c.x-1 >= 0)
        {
            v.add(new Move(c.x, c.y, c.x-1, c.y));
            if(c.y+1 <= 7)
                v.add(new Move(c.x, c.y, c.x-1, c.y+1));
            if(c.y-1 >= 0)
                v.add(new Move(c.x, c.y, c.x-1, c.y-1));
        }

        if(c.y+1 <= 7)
            v.add(new Move(c.x, c.y, c.x, c.y+1));

        if(c.y-1 >= 0)
            v.add(new Move(c.x, c.y, c.x, c.y-1));

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> getKingMoves(Coord c)
    {
        ArrayList<Move> v = getBasicKingMoves(c);

        if(v == null)
            v = new ArrayList<>();

        // Castling
        if(board.p[c.x][c.y].white)
        {
            if(c.x == 4 && c.y == 0 && !isAttacked(new Coord(4, 0), false) && !board.whiteKMoved)
            {
                // Short
                if(!board.whiteTRMoved &&
                        board.p[5][0] == null && !isAttacked(new Coord(5, 0), false) &&
                        board.p[6][0] == null && !isAttacked(new Coord(6, 0), false) &&
                        board.p[7][0] != null && board.p[7][0].type == TYPE_ROOK && board.p[7][0].white)
                {
                    v.add(new Move(4, 0, 6, 0));
                }

                // Long
                if(!board.whiteTLMoved &&
                        board.p[3][0] == null && !isAttacked(new Coord(3, 0), false) &&
                        board.p[2][0] == null && !isAttacked(new Coord(2, 0), false) &&
                        board.p[1][0] == null &&
                        board.p[0][0] != null && board.p[0][0].type == TYPE_ROOK && board.p[0][0].white)
                {
                    v.add(new Move(4, 0, 2, 0));
                }
            }
        }
        else
        {
            if(c.x == 4 && c.y == 7 && !isAttacked(new Coord(4, 7), true) && !board.blackKMoved)
            {
                // Short
                if(!board.blackTLMoved &&
                        board.p[5][7] == null && !isAttacked(new Coord(5, 7), true) &&
                        board.p[6][7] == null && !isAttacked(new Coord(6, 7), true) &&
                        board.p[7][7] != null && board.p[7][7].type == TYPE_ROOK && !board.p[7][7].white)
                {
                    v.add(new Move(4, 7, 6, 7));
                }

                // Long
                if(!board.blackTRMoved &&
                        board.p[3][7] == null && !isAttacked(new Coord(3, 7), true) &&
                        board.p[2][7] == null && !isAttacked(new Coord(2, 7), true) &&
                        board.p[1][7] == null &&
                        board.p[0][7] != null && board.p[0][7].type == TYPE_ROOK && !board.p[0][7].white)
                {
                    v.add(new Move(4, 7, 2, 7));
                }
            }
        }

        return (v.size()==0?null:v);
    }

    private ArrayList<Move> sumVectors(ArrayList<Move> a, ArrayList<Move> b)
    {
        ArrayList<Move> v = new ArrayList<>();

        if(a != null)
            v.addAll(a);

        if(b != null)
            v.addAll(b);

        return (v.size()==0?null:v);
    }

    public boolean isCheckMate()
    {
        return isAttacked(board.blackKing, true) && !replyForMate(false) ||
                isAttacked(board.whiteKing, false) && !replyForMate(true);
    }

    public boolean isCheck(boolean white)
    {
        return isAttacked((white?board.whiteKing:board.blackKing), !white);
    }

    private boolean isAttacked(Coord c, boolean white)
    {
        for(byte a=0; a<8; a++)
        {
            for(byte b=0; b<8; b++)
            {
                if(board.p[a][b] != null && board.p[a][b].white == white)
                {
                    ArrayList<Move> v = getAttacks(new Coord(a, b));
                    if(v != null)
                    {
                        for(int i=0; i<v.size(); i++)
                        {
                            Move m = v.get(i);

                            if(m.x2 == c.x && m.y2 == c.y)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean replyForMate(boolean white)
    {
        for(byte a=0; a<8; a++)
            for(byte b=0; b<8; b++)
                if(board.p[a][b] != null && board.p[a][b].white == white)
                    if(getRealAll(new Coord(a, b)) != null)
                        return true;

        return false;
    }

    public Move isMoveLegal(Coord c1, Coord c2)
    {
        ArrayList<Move> legalMoves = getRealAll(c1);

        if(legalMoves != null)
        {
            for(int i=0; i<legalMoves.size(); i++)
            {
                Move m = legalMoves.get(i);

                if(m.x2 == c2.x && m.y2 == c2.y)
                {
                    return lastMove = m;
                }
            }
        }
        return null;
    }

    public boolean checkIfWouldBeRook(Move m, Board b)
    {
        boolean rooked = false;

        m.perform(b);

        if(m.getRook() != null)
            rooked = true;
        else
            rooked = false;

        m.undo(b);

        return rooked;
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
            Log.e(TAG, "MOVE ALGORITHM CLASS cant duplicate");
        }

        return obj;
    }

}
