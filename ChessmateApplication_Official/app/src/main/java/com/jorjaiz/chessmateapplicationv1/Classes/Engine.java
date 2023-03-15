package com.jorjaiz.chessmateapplicationv1.Classes;

import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.MainActivity;

import java.util.ArrayList;

public class Engine implements Constants
{
    private Board board;
    private Move lastMove;
    MoveAlgorithm algorithm;

    private ArrayList<Move> bestMoves;
    int[] sC;

    public Engine()
    {
        this.board = new Board();
        this.lastMove = null;
        this.algorithm = new MoveAlgorithm();
        this.sC = new int[4];
    }

    public void setBoard(Board board)
    {
        this.board = board;
    }

    public Move getLastMove()
    {
        return this.lastMove;
    }
    public void setLastMove(Move lastMove)
    {
        this.lastMove = lastMove;
    }


    // Response of IA
    public Move reply(boolean white)
    {
        try
        {
            algorithm.estimateBase();

            algorithm.testAlgorithm();

            Log.w(TAG, "    >>>>>>>>>>   IA TO START > IA is: " + (white?"WHITE":"BLACK") + "   <<<<<<<<<<");
            Log.w(TAG, " ");

            Move finalMove = getReply(white);

            Log.w(TAG, " ");
            Log.w(TAG, "    >>>>>>>>>>   IA FINISHES   <<<<<<<<<<");
            Log.w(TAG, " ");

            algorithm.testAlgorithm();

            Log.w(TAG, "        ### TOTAL OF NODES analyzed: " + (sC[0]+sC[1]+sC[2]+sC[3]));

            return finalMove;
        }
        catch (Exception ex)
        {
            Log.e(MainActivity.TAG, ex.toString());
            return lastMove;
        }
    }

    public class alphaBetaThread extends Thread
    {
        Board b;
        boolean white;
        MoveAlgorithm mA;
        int numThread;

        public alphaBetaThread(int numThread, boolean white, Board b)
        {
            this.b = b;
            this.white = white;
            this.mA = new MoveAlgorithm();

            this.mA.setBoard(this.b);
            this.mA.setDepth(algorithm.getDepth());
            this.mA.setDifficulty(algorithm.getDifficulty());
            this.mA.setNumThread(numThread);

            this.numThread = numThread;
        }

        @Override
        public void run()
        {
            try
            {
                mA.setStepCounter(0);
                mA.setNumNodes(0);
                mA.setTheNode(0);

                mA.testSubAlgorithm(numThread);
                mA.alphaBeta(white, -INFINITY, INFINITY, algorithm.getDepth());
                mA.testSubAlgorithm(numThread);

                bestMoves.add(mA.getLastMove());

                sC[numThread-1] = mA.getStepCounter();
            }
            catch (Exception e)
            {
                Log.e(TAG, "ALPHABETATHREAD run exception: " + e.toString());
            }
        }

    }

    private Move getReply(boolean white)
    {
        lastMove = null;

        try
        {
            bestMoves = new ArrayList<>();

            if(algorithm.getDifficulty() == Constants.DIFFICULT)
            {
                Board b1 = Board.newClonedBoard(board);
                Board b2 = Board.newClonedBoard(board);
                Board b3 = Board.newClonedBoard(board);
                Board b4 = Board.newClonedBoard(board);

                Thread aB1 = new alphaBetaThread(1, white, b1);
                Thread aB2 = new alphaBetaThread(2, white, b2);
                Thread aB3 = new alphaBetaThread(3, white, b3);
                Thread aB4 = new alphaBetaThread(4, white, b4);

                Log.w(TAG, " ");
                Log.w(TAG, "        *********** START OF THREADS *********** ");
                Log.w(TAG, " ");

                aB1.start();
                aB2.start();
                aB3.start();
                aB4.start();

                Log.w(TAG, " ");
                Log.w(TAG, "        *********** END OF THREADS *********** ");
                Log.w(TAG, " ");

                do {

                }while(aB1.isAlive() || aB2.isAlive() || aB3.isAlive() || aB4.isAlive());
            }
            else
            {
                Board b1 = Board.newClonedBoard(board);

                Thread aB1 = new alphaBetaThread(1, white, b1);

                Log.w(TAG, " ");
                Log.w(TAG, "        *********** START OF THREADS *********** ");
                Log.w(TAG, " ");

                aB1.start();

                Log.w(TAG, " ");
                Log.w(TAG, "        *********** END OF THREADS *********** ");
                Log.w(TAG, " ");

                do {

                }while(aB1.isAlive());
            }

            if(algorithm.getDifficulty() != Constants.DIFFICULT)
            {
                if(bestMoves.get(0) == null)
                {
                    Log.w(TAG, "BEST MOVES (0) IS NULL");

                    if(algorithm.getDifficulty() == EASY)
                    {
                        if(algorithm.getEasyParameter() < algorithm.getEasyParameter2())
                        {
                            algorithm.setEasyParameter2(-1);

                            lastMove = getReply(white);
                        }
                        else
                        {
                            return null;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }
            else
            {
                if(bestMoves.get(0) == null && bestMoves.get(1) == null &&
                        bestMoves.get(2) == null && bestMoves.get(3) == null)
                {
                    return null;
                }
            }

            algorithm.setStepCounter(0);
            algorithm.alphaBetaFather(bestMoves, -INFINITY, INFINITY, 1);
            lastMove = algorithm.getLastMove();

            if(algorithm.getDifficulty() == Constants.EASY)
                algorithm.setEasyParameter2(algorithm.getEasyParameter2()-1);

            if(lastMove == null)
                Log.w(TAG, "CURRENT MOVE IS NULL");

            //Log.w(TAG, "    *** THE BEST MOVE IN NODE: " + mA.getTheNode() + " ***");
        }
        catch (Exception e)
        {
            Log.e(TAG,"IA getReply exception " + e.toString());
        }

        return lastMove;
    }


}
