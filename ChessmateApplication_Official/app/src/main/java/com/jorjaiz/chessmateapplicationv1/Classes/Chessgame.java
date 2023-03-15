package com.jorjaiz.chessmateapplicationv1.Classes;

import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chessgame implements Constants
{
    private int idGame;

    private Engine engine;
    private Board board;
    private Move lastMove;

    private Player whitePlayer;
    private Player blackPlayer;

    private int difficulty;
    private int mode;

    private ArrayList<String> arrayListMoves;
    private String stringMoves;
    private long secondsLeft;
    private int rewinds;
    private long limitTime;

    private int state;

    private Move auxLastMove;


    // Getters & Setters

    public void setIdGame(int idGame) { this.idGame = idGame; }
    public int getIdGame() { return this.idGame; }

    public int getMode()
    {
        return this.mode;
    }

    public int getDifficulty()
    {
        return this.difficulty;
    }

    public void setStringMoves(String stringMoves) { this.stringMoves = stringMoves; }
    public String getStringMoves() { return stringMoves; }

    public ArrayList<String> getArrayListMoves() { return this.arrayListMoves; }

    public void setSecondsLeft(long secondsLeft) { this.secondsLeft = secondsLeft; }
    public long getSecondsLeft() { return this.secondsLeft; }

    public void setRewinds(int rewinds) { this.rewinds = rewinds; }
    public int getRewinds() { return this.rewinds; }

    public long getLimitTime()
    {
        return this.limitTime;
    }
    public void setLimitTime(long lT)
    {
        this.limitTime = lT;
    }

    public Move getLastMove() { return this.lastMove; }
    public void setLastMove(Move m){this.lastMove = m; }

    public MoveAlgorithm getAlgorithm()
    {
        return this.engine.algorithm;
    }

    public void setState(int state) { this.state = state; }
    public int getState()
    {
        return this.state;
    }

    public boolean getBoardTurn()
    {
        return board.turn;
    }

    public boolean isWhiteKMoved()
    {
        return board.whiteKMoved;
    }

    public boolean isBlackKMoved()
    {
        return board.blackKMoved;
    }

    public Piece getSpot(Coord c)
    {
        return board.p[c.x][c.y];
    }

    public boolean isSpotWhite(Coord c)
    {
        return board.p[c.x][c.y].white;
    }

    public boolean isOccupied(Coord c)
    {
        return (board.p[c.x][c.y]!=null);
    }

    public int getIntOfAPiece(Coord c) { return (board.p[c.x][c.y].type+1+(board.p[c.x][c.y].white?10:0)); }

    public int getIdWhitePlayer() { return whitePlayer.getId(); }
    public int getIdBlackPlayer() { return blackPlayer.getId(); }

    public Coord getBlackKingCoord() { return board.getBlackKingCoord(); }
    public Coord getWhiteKingCoord() { return board.getWhiteKingCoord(); }

    public boolean isPromot() { return (lastMove!=null && lastMove.promot); }

    public Move getIfRook() { return (lastMove==null?null:lastMove.rook); }

    public boolean canMove(boolean white)
    {
        return engine.algorithm.replyForMate(white);
    }


    public Chessgame(int p1id, boolean p1c,
                     int p2id, boolean p2c,
                     int mode, int difficulty, int lastState)
    {
        this.whitePlayer = new Player(p1id, p1c);
        this.blackPlayer = new Player(p2id, p2c);

        this.mode = mode;
        this.difficulty = difficulty;

        this.arrayListMoves = new ArrayList<>();
        this.stringMoves = "";
        this.secondsLeft = 0;
        this.limitTime = 0;

        this.state = lastState;

        this.lastMove = null;
        this.auxLastMove = null;
    }

    public boolean startGame(String s)
    {
        try
        {
            engine = new Engine();

            if(mode == PVIA)
            {
                if(difficulty == EASY)
                {
                    rewinds = INFINITY_REWINDS;
                    limitTime = INFINITY_MINUTES;
                    engine.algorithm.setDepth((byte)3);
                    engine.algorithm.setDifficulty(EASY);

                    engine.algorithm.setEasyParameter2(4);
                }
                else if(difficulty == INTERMEDIATE)
                {
                    rewinds = FIVE_REWINDS;
                    limitTime = FIFTEEN_MINUTES;
                    engine.algorithm.setDepth((byte)3);
                    engine.algorithm.setDifficulty(INTERMEDIATE);

                    engine.algorithm.setEasyParameter2(0);
                }
                else if(difficulty == DIFFICULT)
                {
                    rewinds = ZERO_REWINDS;
                    limitTime = FIVE_MINUTES;
                    engine.algorithm.setDepth((byte)4);
                    engine.algorithm.setDifficulty(DIFFICULT);

                    engine.algorithm.setEasyParameter2(0);
                }

                board = new Board();
                engine.setBoard(board);
                engine.algorithm.setBoard(board);

                if(s.equals(NEW))
                    return startPVIAGame();
                else
                    return false;
            }
            else if(mode == PVPLOCAL)
            {
                rewinds = ZERO_REWINDS;
                limitTime = INFINITY_MINUTES;

                board = new Board();
                engine.setBoard(board);
                engine.algorithm.setBoard(board);
                return false;
            }
            else
            {
                rewinds = ZERO_REWINDS;
                limitTime = INFINITY_MINUTES;

                board = new Board();
                engine.setBoard(board);
                engine.algorithm.setBoard(board);
                return false;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME startGame: " + e.toString());
            return false;
        }
    }

    private boolean startPVIAGame()
    {
        try
        {
            if(isIATurn())
            {
                moveIA();
                addNewMove(board.turn?getIdWhitePlayer():getIdBlackPlayer(), lastMove);
                board.turn = !board.turn;
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME startPVIAGame: " + e.toString());
            return false;
        }
    }

    private boolean isIATurn()
    {
        return((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()));
    }

    public void moveHuman(Move m)
    {
        try
        {
            movePiece(engine.algorithm.replyHuman(m));

            board.pC.refreshPieceCount();

            if(!engine.algorithm.isCheckMate())
            {
                if(engine.algorithm.isCheck(!board.turn))
                    state = CHECKBYP;
                else
                {
                    if(board.pC.isDrawWithoutMaterial())
                        state = DRAWMAT;
                    else
                        state = PLAYING;
                }
            }
            else
            {
                Log.e(MainActivity.TAG, "Game finished by Player");
                putCheckMate();
            }

            addNewMove(board.turn?getIdWhitePlayer():getIdBlackPlayer(), lastMove);

            board.turn = !board.turn;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME moveHuman: " + e.toString());
        }
    }

    private void moveIA()
    {
        try
        {
            Log.w(TAG," ");

            state = WAITING;

            Log.i(TAG,"============================================================================");
            Log.i(TAG," ");

            movePiece(engine.reply(board.turn));

            Log.i(TAG," ");
            Log.i(TAG,"============================================================================");

            board.pC.refreshPieceCount();

            if(lastMove != null && !engine.algorithm.isCheckMate())
            {
                if(engine.algorithm.isCheck(!board.turn))
                    state = CHECKBYIA;
                else
                {
                    if(board.pC.isDrawWithoutMaterial())
                        state = DRAWMAT;
                    else
                        state = PLAYING;
                }
            }
            else
            {
                if(lastMove == null)
                {
                    Log.e(MainActivity.TAG, "Game in draw");
                    putDrawWithoutMov();
                }
                else
                {
                    Log.e(MainActivity.TAG, "Game finished by IA");
                    putCheckMate();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME moveIA: " + e.toString());
        }
    }

    public void afterMoveHuman()
    {
        if(state == PLAYING || state == CHECKBYP)
        {
            moveIA();
            addNewMove(board.turn?getIdWhitePlayer():getIdBlackPlayer(), lastMove);

            board.turn = !board.turn;
        }
    }

    private void movePiece(Move m) // For both IA and Player
    {
        try
        {
            if(m != null)
            {
                watchaIfRookWasMoved(m, board);
                watchaIfKingWasMoved(m, board);
                m.perform(board);
                lastMove = m;
            }
            else
            {
                lastMove = null;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME movePiece: " + e.toString());
        }
    }

    private void watchaIfRookWasMoved(Move m, Board b)
    {
        // Check if a Rook were moved
        if(b.p[m.x1][m.y1] != null && b.p[m.x1][m.y1].type == TYPE_ROOK)
        {
            if(b.p[m.x1][m.y1].white)
            {
                if(!b.whiteTRMoved)
                    if(m.x1 == 7 && m.y1 == 0)
                        b.whiteTRMoved = true; // White right rook was moved
                if(!b.whiteTLMoved)
                    if(m.x1 == 0 && m.y1 == 0)
                        b.whiteTLMoved = true; // White left rook was moved
            }
            else
            {
                if(!b.blackTRMoved)
                    if(m.x1 == 0 && m.y1 == 7)
                        b.blackTRMoved = true; // Black right rook was moved
                if(!b.blackTLMoved)
                    if(m.x1 == 7 && m.y1 == 7)
                        b.blackTLMoved = true; // Black left rook was moved
            }
        }

        // Check if an enemy rook was eliminated
        if(b.p[m.x2][m.y2] != null && b.p[m.x2][m.y2].type == TYPE_ROOK)
        {
            if(b.p[m.x1][m.y1].white)
            {
                if(!b.blackTRMoved)
                    if(m.x2 == 0 && m.y2 == 7)
                        b.blackTRMoved = true; // Black right tower was eliminated by White
                if(!b.blackTLMoved)
                    if(m.x2 == 7 && m.y2 == 7)
                        b.blackTLMoved = true; // Black left tower was eliminated by White
            }
            else
            {
                if(!b.whiteTRMoved)
                    if(m.x2 == 7 && m.y2 == 0)
                        b.whiteTRMoved = true; // White right tower was eliminated by Black
                if(!b.whiteTLMoved)
                    if(m.x2 == 0 && m.y2 == 0)
                        b.whiteTLMoved = true; // White left tower was eliminated by Black
            }
        }
    }

    private void watchaIfKingWasMoved(Move m, Board b)
    {
        // Check if a king was moved to update its coord
        if(b.p[m.x1][m.y1] != null && b.p[m.x1][m.y1].type == TYPE_KING
                && (!b.whiteKMoved || !b.blackKMoved))
        {
            if(b.p[m.x1][m.y1].white)
                b.whiteKMoved = true;
            else
                b.blackKMoved = true;
        }
    }

    private void putCheckMate()
    {
        // Check who lost
        if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
            state = FINISHEDBYIA; // Player lost
        else
            state = FINISHEDBYP; // IA lost
    }

    private void putDrawWithoutMov()
    {
        Log.e(TAG, "ESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS: " + board.turn);
        Log.e(TAG, "whitePlayer.isHuman(): " + whitePlayer.isHuman());
        Log.e(TAG, "blackPlayer.isHuman():  " + blackPlayer.isHuman());

        // Check who got drawn
        if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
        {
            state = DRAWMOVBYP; // IA got drawn

            Log.e(TAG, "ESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS: DRAWMOVP");
        }
        else
        {
            state = DRAWMOVBYIA; // Player got drawn

            Log.e(TAG, "ESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS: DRAWMOVBYIA");
        }
    }

    public boolean isCastling(Coord c, String m)
    {
        return (board.p[c.x][c.y].type == TYPE_KING &&
                (c.x - Character.getNumericValue(m.charAt(0)) == 2 || c.x - Character.getNumericValue(m.charAt(0)) == -2));
    }

    public boolean watchaIfMoveIsLegal(Coord fC, Coord sC)
    {
        if(engine.algorithm.isMoveLegal(fC, sC) != null)
        {
            engine.setLastMove(engine.algorithm.getLastMove());
            lastMove = engine.getLastMove();
            return  true;
        }
        return false;
    }

    // Add a Move to stringMoves and arrayListMoves

    public void addNewMove(int idP, Move m)
    {
        try
        {
            if(m != null)
            {
                Piece p = board.p[m.x2][m.y2];

                String action2;

                if(m.promot)
                    action2 = "p";
                else if(m.rook != null)
                    action2 = "e";
                else
                    action2 = "n";

                String consequence;

                if(engine.algorithm.isCheckMate())
                {
                    consequence = "k";
                }
                else
                {
                    switch(state)
                    {
                        case CHECKBYIA:
                        case CHECKBYP:
                            consequence = "j";
                            break;
                        case DRAWMAT:
                            consequence = "l";
                            break;
                        case DRAWMOVBYIA:
                        case DRAWMOVBYP:
                            consequence = "m";
                            break;
                        default:
                            consequence = "n";
                    }
                }

                addNewMoveIntoString(idP, (p==null?" ":TYPE[p.type]), COORDX[m.x1], m.y1,
                        (m.pSaved==null?" ":TYPE[m.pSaved.type]), COORDX[m.x2], m.y2, action2, consequence);
            }
            else
            {
                String coordX = COORDX[(board.turn?board.whiteKing:board.blackKing).x];
                byte coordY = (board.turn?board.whiteKing:board.blackKing).y;

                addNewMoveIntoString(idP, "K", coordX, coordY, " ", coordX, coordY, "n", "m");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME addNewMove: " + e.toString());
        }
    }

    private void addNewMoveIntoString(int idP, String piece, String Xi, byte Yi,
                                      String action1, String Xf, byte Yf, String action2, String consequence)
    {
        if(action2.equals("e"))
            addNewMove(idP, lastMove.rook);

        StringBuilder sb = new StringBuilder();

        sb.append(idP).append(piece).append(Xi).append(Yi).
                append(action1).append(Xf).append(Yf).append(action2).append(consequence);

        arrayListMoves.add(sb.toString());

        sb.insert(0, "@");

        stringMoves += sb.toString();

        Log.i(TAG, " ");
        Log.i(TAG, "    >>>>>>>>>>>>>>>><<<<<<<<<<<<<<<");
        Log.i(TAG, "    >>>   New Move " + sb.toString() + "   <<<");
        Log.i(TAG, "    >>>>>>>>>>>>>>>><<<<<<<<<<<<<<<");
        Log.i(TAG, " ");
    }

    // For the BACK function

    public Map<Integer, Move> undoneMove(int idWhite, boolean delete, int posAux)
    {
        try
        {
            Map<Integer, Move> map = new HashMap<>();

            if(delete)
            {
                if(arrayListMoves.size() != 0 && rewinds != ZERO_REWINDS)
                {
                    rewind(idWhite, 1,true, posAux);

                    if(lastMove.rook != null)
                    {
                        map.put(0, lastMove.rook);
                        map.put(1, lastMove);
                    }
                    else
                    {
                        map.put(0, lastMove);
                    }

                    lastMove.undo(board);

                    board.turn = !board.turn;

                    if(!isIATurn())
                        rewinds--;
                }
                else
                {
                    map.put(0, null);
                }
            }
            else
            {
                if(arrayListMoves.size() != 0)
                {
                    rewind(idWhite, 1, false, posAux);

                    if(lastMove.rook != null)
                    {
                        map.put(0, lastMove.rook);
                        map.put(1, lastMove);
                    }
                    else
                    {
                        map.put(0, lastMove);
                    }

                    if(!isM(arrayListMoves.get(posAux)))
                    {
                        lastMove.undo(board);
                    }

                    board.turn = !board.turn;
                }
                else
                {
                    map.put(0, null);
                }
            }

            return map;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME undoneMove: " + e.toString());
            return null;
        }
    }

    // Desintagrate an Arraylist to Move

    public void rewind(int idWhite, int pos, boolean delete, int posAux)
    {
        try
        {
            if(arrayListMoves.size() != 0)
            {
                String sM;
                String sMAux;

                if(delete)
                    sMAux = arrayListMoves.get(arrayListMoves.size() - pos);
                else
                    sMAux = arrayListMoves.get(posAux);

                int idP = 0;
                byte piece = 0;
                byte action1 = 0;
                String action2 = "";
                String consequence = "";

                idP = !board.turn?getIdWhitePlayer():getIdBlackPlayer();

                sM = reformatArrayListMove(sMAux);

                for (int i = 0; i < sM.length(); i++)
                {
                    switch (i)
                    {
                        case 1:
                            piece = Piece.getByteValueByChar(sM.charAt(i));
                            break;
                        case 4:
                            if (sM.charAt(i) == ' ')
                                action1 = 6;
                            else
                                action1 = Piece.getByteValueByChar(sM.charAt(i));
                            break;
                        case 7:
                            action2 = Character.toString(sM.charAt(i));
                            break;
                        case 8:
                            consequence = Character.toString(sM.charAt(i));
                            break;
                    }
                }


                Move m = new Move(Coord.getIntOfCoordByChar(sM.charAt(2)), Character.getNumericValue(sM.charAt(3)),
                        Coord.getIntOfCoordByChar(sM.charAt(5)), Character.getNumericValue(sM.charAt(6)));

                switch (action2)
                {
                    case "p":
                        m.promot = true;
                        break;
                    case "e":
                        rewind(idWhite,2, delete, posAux-1);    // Check if there was a castling
                        break;
                    default:
                        m.rook = null;
                }

                if(action1 == 6)
                {
                    if(consequence.equals("m"))
                        m.pSaved = new Piece((byte)5, (idP==idWhite));
                    else
                        m.pSaved = null;
                }
                else
                {
                    m.pSaved = new Piece(action1, !(idP==idWhite));
                }

                m.type = (int)action1;

                if (pos == 1)
                {
                    lastMove = m;

                    String aux;

                    if(delete)
                    {
                        if (auxLastMove != null)
                        {
                            lastMove.rook = auxLastMove;
                            aux = reformatArrayListMove(arrayListMoves.get(arrayListMoves.size() - (pos + 2)));
                        }
                        else
                        {
                            if (arrayListMoves.size() == 1)
                                aux = "nnnnnnnnnn";
                            else
                                aux = reformatArrayListMove(arrayListMoves.get(arrayListMoves.size() - (pos + 1)));
                        }
                    }
                    else
                    {
                        if (auxLastMove != null)
                        {
                            lastMove.rook = auxLastMove;
                            aux = reformatArrayListMove(arrayListMoves.get(posAux - 2));
                        }
                        else
                        {
                            if (posAux == 0)
                                aux = "nnnnnnnnnn";
                            else
                                aux = reformatArrayListMove(arrayListMoves.get(posAux - 1));
                        }
                    }

                    switch (Character.toString(aux.charAt(8)))
                    {
                        case "j":
                            if(idP == ID_IA)
                                state = CHECKBYIA;
                            else
                                state = CHECKBYP;
                            break;
                        case "k":
                            if(idP == ID_IA)
                                state = FINISHEDBYIA;
                            else
                                state = FINISHEDBYP;
                            break;
                        case "l":
                            state = DRAWMAT;
                            break;
                        case "m":
                            if(idP == ID_IA)
                                state = DRAWMOVBYIA;
                            else
                                state = DRAWMOVBYP;
                            break;
                        case "n":
                            state = PLAYING;
                    }

                    auxLastMove = null;

                    if(delete)
                    {
                        arrayListMoves.remove(arrayListMoves.size() - 1);

                        if(lastMove.rook != null)
                            arrayListMoves.remove(arrayListMoves.size() - 1);

                        copyArrayListMovesToStringMoves();
                        virtualResumeAllSavedMoves(true,0);
                    }
                    else
                    {
                        virtualResumeAllSavedMoves(false, posAux);
                    }
                }
                else
                {
                    auxLastMove = m;
                }

                Log.i(TAG, " ");
                Log.i(TAG, "MOVE WAS REWINDED: " + sM);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME rewind: " + e.toString());
        }

    }

    // For SHOW MOVEMENTS OR RESUME GAME FUNCTION
    // Copy the moves from stringMoves to arrayListMoves

    public void fillArrayListMoves(String s)
    {
        try
        {
            Log.w(TAG, "Recieved stringMoves: " + s);

            if(s.length() != 0)
            {
                StringBuilder sb;

                for (int i=0; i < s.length(); i++)
                {
                    if (s.charAt(i) == '@')
                    {
                        sb = new StringBuilder();

                        for (int j=i+1; j<s.length(); j++)
                        {
                            if (s.charAt(j) == '@')
                            {
                                break;
                            }
                            else if(i + 1 == s.length())
                            {
                                sb.append(s.charAt(j));
                                break;
                            }
                            else
                            {
                                sb.append(s.charAt(j));
                            }

                        }

                        arrayListMoves.add(sb.toString());
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME fillArrayListMoves: " + e.toString());
        }
    }

    // Copy arraylistMoves to stringMoves

    public void copyArrayListMovesToStringMoves()
    {
        String sM = "";

        for(int i=0; i<arrayListMoves.size(); i++)
            sM += "@" + arrayListMoves.get(i);

        stringMoves = sM;
    }

    // Execute all moves from de arrayListMoves in a virtual manner

    public void virtualResumeAllSavedMoves(boolean delete, int l)
    {
        try
        {
            MoveAlgorithm auxAlgorithm = new MoveAlgorithm();
            Board auxBoard = new Board();
            auxAlgorithm.setBoard(auxBoard);

            String sM;

            int limit;

            if(delete)
                limit = arrayListMoves.size();
            else
                limit = l;

            for(int i=0; i<limit; i++)
            {
                if(i+1 < limit)
                {
                    if(isE(arrayListMoves.get(i+1)))
                    {
                        sM = reformatArrayListMove(arrayListMoves.get(i+1));
                        i++;
                    }
                    else
                    {
                        sM = reformatArrayListMove(arrayListMoves.get(i));
                    }
                }
                else
                {
                    sM = reformatArrayListMove(arrayListMoves.get(i));
                }

                Move m = new Move(Coord.getIntOfCoordByChar(sM.charAt(2)), Character.getNumericValue(sM.charAt(3)),
                        Coord.getIntOfCoordByChar(sM.charAt(5)), Character.getNumericValue(sM.charAt(6)));

                watchaIfRookWasMoved(m, auxBoard);
                watchaIfKingWasMoved(m, auxBoard);

                if(sM.charAt(8) != 'm')
                    m.perform(auxBoard);

                auxBoard.pC.refreshPieceCount();

                board.whiteKMoved = auxBoard.whiteKMoved;
                board.blackKMoved = auxBoard.blackKMoved;
                board.whiteTRMoved = auxBoard.whiteTRMoved;
                board.whiteTLMoved = auxBoard.whiteTLMoved;
                board.blackTRMoved = auxBoard.blackTRMoved;
                board.blackTLMoved = auxBoard.blackTLMoved;

                switch(sM.charAt(8))
                {
                    case 'j':
                        if((!auxBoard.turn && !blackPlayer.isHuman()) || (auxBoard.turn && !whitePlayer.isHuman()))
                            state = CHECKBYIA;
                        else
                            state = CHECKBYP;
                        break;
                    case 'k':
                        if((!auxBoard.turn && !blackPlayer.isHuman()) || (auxBoard.turn && !whitePlayer.isHuman()))
                            state = FINISHEDBYIA;
                        else
                            state = FINISHEDBYP;
                        break;
                    case 'l':
                        state = DRAWMAT;
                        break;
                    case 'm':
                        if((!auxBoard.turn && !blackPlayer.isHuman()) || (auxBoard.turn && !whitePlayer.isHuman()))
                            state = DRAWMOVBYP;
                        else
                            state = DRAWMOVBYIA;
                        break;
                    case 'n':
                        state = PLAYING;
                }

                auxBoard.turn = !auxBoard.turn;
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME virtualResumeAllSavedMoves: " + e.toString());
        }
    }

    // For RESUME GAME FUNCTION
    // Execute all the moves from arrayListMoves in an original manner

    public boolean resumeAllSavedMoves(int i)
    {
        try
        {
            if(i+1 < arrayListMoves.size())
            {
                if(isE(arrayListMoves.get(i+1)))
                {
                    executeAllSavedMoves(i+1);
                    return true;
                }
                else
                {
                    executeAllSavedMoves(i);
                    return false;
                }
            }
            else
            {
                executeAllSavedMoves(i);
                return false;
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME resumeAllSavedMoves: " + e.toString());
            return true;
        }
    }

    // Execute all moves from de arrayListMoves in an original manner

    public void executeAllSavedMoves(int i)
    {
        try
        {
            String sM = reformatArrayListMove(arrayListMoves.get(i));

            Move m = new Move(Coord.getIntOfCoordByChar(sM.charAt(2)), Character.getNumericValue(sM.charAt(3)),
                    Coord.getIntOfCoordByChar(sM.charAt(5)), Character.getNumericValue(sM.charAt(6)));
            watchaIfRookWasMoved(m, board);
            watchaIfKingWasMoved(m, board);

            if(sM.charAt(8) != 'm')
            {
                m.perform(board);
                lastMove = m;
            }

            board.pC.refreshPieceCount();

            switch(sM.charAt(8))
            {
                case 'j':
                    if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
                        state = CHECKBYIA;
                    else
                        state = CHECKBYP;
                    break;
                case 'k':
                    if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
                        state = FINISHEDBYIA;
                    else
                        state = FINISHEDBYP;
                    break;
                case 'l':
                    state = DRAWMAT;
                    break;
                case 'm':
                    if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
                        state = DRAWMOVBYP;
                    else
                        state = DRAWMOVBYIA;
                    break;
                case 'n':
                    state = PLAYING;
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME executeAllSavedMoves: " + e.toString());
        }
    }

    // Execute a move from a string in an original manner

    public void executeTheStringMove(String move)
    {
        try
        {
            String sM = reformatArrayListMove(move);

            Move m = new Move(Coord.getIntOfCoordByChar(sM.charAt(2)), Character.getNumericValue(sM.charAt(3)),
                    Coord.getIntOfCoordByChar(sM.charAt(5)), Character.getNumericValue(sM.charAt(6)));

            watchaIfRookWasMoved(m, board);
            watchaIfKingWasMoved(m, board);

            if(sM.charAt(8) != 'm')
            {
                m.perform(board);
                lastMove = m;
            }

            board.pC.refreshPieceCount();

            switch(sM.charAt(8))
            {
                case 'j':
                    if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
                        state = CHECKBYIA;
                    else
                        state = CHECKBYP;
                    break;
                case 'k':
                    if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
                        state = FINISHEDBYIA;
                    else
                        state = FINISHEDBYP;
                    break;
                case 'l':
                    state = DRAWMAT;
                    break;
                case 'm':
                    if((!board.turn && !blackPlayer.isHuman()) || (board.turn && !whitePlayer.isHuman()))
                        state = DRAWMOVBYP;
                    else
                        state = DRAWMOVBYIA;
                    break;
                case 'n':
                    state = PLAYING;
            }

            arrayListMoves.add(move);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME executeTheStringMove: " + e.toString());
        }
    }

    public boolean watchaIfIAHasToRespondFirst()
    {
        return (arrayListMoves.size() == 0 && isIATurn());
    }

    public void switchTurn()
    {
        board.turn = !board.turn;
    }

    public Notation getNotationSavedMoves(int i, boolean turn)
    {
        try
        {
            boolean e;

            if(i+1 < arrayListMoves.size())
                e = isE(arrayListMoves.get(i+1));
            else
                e = false;

            String sM = reformatArrayListMove(arrayListMoves.get(i));

            Notation notation = new Notation();
            notation.setPiece(Piece.getIntValueByChar(sM.charAt(1))+1+(turn?10:0));

            notation.setXi(sM.charAt(2));
            notation.setYi((char)(Character.getNumericValue(sM.charAt(3))+'1'));

            switch(sM.charAt(4))
            {
                case ' ':
                    notation.setPiece2(0);
                    notation.setAction1(0); // 0 = Movement, 1 = Eat, 2 = Castling. For symbols of panel "saved moves"
                    break;
                default:
                    notation.setPiece2(Piece.getIntValueByChar(sM.charAt(4))+1+(!turn?10:0));
                    notation.setAction1(1);
            }

            notation.setXf(sM.charAt(5));
            notation.setYf((char)(Character.getNumericValue(sM.charAt(6))+'1'));

            if(e)
            {
                Notation auxNot = getNotationSavedMoves(i+1, turn);
                notation.setAction1(2);
                notation.setPiece2(notation.getPiece());
                notation.setPiece(auxNot.getPiece());
                notation.setXi(auxNot.getXi());
                notation.setYi(auxNot.getYi());
                notation.setXf(auxNot.getXf());
                notation.setYf(auxNot.getYf());
            }

            return notation;
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception CHESSGAME getNotationSavedMoves: " + e.toString());
            return null;
        }
    }

    //////////////////////////////////////////

    private String reformatArrayListMove(String s)
    {
        int sizeOfMove = s.length();

        int difference = sizeOfMove - 9;

        String reformatString;

        if(difference > 0)
        {
            reformatString = "0" + s.substring(difference+1);
        }
        else
        {
            reformatString = s;
        }

        return reformatString;
    }

    private boolean isE(String s)
    {
        String res = reformatArrayListMove(s);
        return res.charAt(7) == 'e';
    }

    private boolean isM(String s)
    {
        String res = reformatArrayListMove(s);
        return res.charAt(8) == 'm';
    }

    private String getIdFromStringMove(String s)
    {
        int sizeOfMove = s.length();

        int difference = sizeOfMove - 9;

        return s.substring(0, difference + 1);
    }

    //--------------------------------------------------------------------------------------------

    public boolean willBeRook(Move m)
    {
        Board b = Board.newClonedBoard(board);
        return engine.algorithm.checkIfWouldBeRook(m, b);
    }
}
