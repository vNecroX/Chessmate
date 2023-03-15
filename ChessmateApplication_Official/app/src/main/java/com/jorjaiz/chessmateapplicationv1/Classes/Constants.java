package com.jorjaiz.chessmateapplicationv1.Classes;

import com.jorjaiz.chessmateapplicationv1.MainActivity;

public interface Constants
{
    int ID_IA = 1;

    String[] TYPE = {"P", "N", "B", "R", "Q", "K"};
    String[] COORDX = {"A", "B", "C", "D", "E", "F", "G", "H"};
    int[] cost = {1, 5, 5, 10, 40, 100};

    int INFINITY = 100000;

    // Type of piece
    byte TYPE_PAWN = 0;
    byte TYPE_KNIGHT = 1;
    byte TYPE_BISHOP = 2;
    byte TYPE_ROOK = 3;
    byte TYPE_QUEEN = 4;
    byte TYPE_KING = 5;

    // States of game
    int PAUSED = 0;
    int FINISHEDBYIA = 1;
    int FINISHEDBYP = 2;
    int INTERRUPTED = 3;
    int PLAYING = 4;
    int WAITING = 5;
    int CHECKBYIA = 6;
    int CHECKBYP = 7;
    int DRAWMOVBYIA = 8;
    int DRAWMOVBYP = 9;
    int DRAWMAT = 10;
    int FINISHEDBYOPPONENT = 11;
    int REMOTE_WAITING = 12;

    // Quantity of rewinds
    int INFINITY_REWINDS = 10000;
    int FIVE_REWINDS = 5;
    int ZERO_REWINDS = 0;

    // Quantity of minutes
    long INFINITY_MINUTES = 1000*60*60*24;
    long FIFTEEN_MINUTES = 1000*60*15;
    long FIVE_MINUTES = 1000*60*5;

    // Modes
    int PVPLOCAL = 1;
    int PVPONLINE = 2;
    int PVIA = 3;

    // Difficulties
    int EASY = 1;
    int INTERMEDIATE = 2;
    int DIFFICULT = 3;
    int NULL = 4;

    // Type of connection int
    int ONLINE = 1;
    int OFFLINE = 2;
    int BUSSY = 3;

    // Kind of view
    String SAVEDMOVES = "savedMoves";
    String GAMEINTERFACE = "gameInterface";

    //
    String NEW = "NewGame";
    String SAVED = "SavedGame";

    //String URL_DATABASE = "https://jorjaizenxfiles.000webhostapp.com/MainPHP_WEB.php "; // Path of PHP
    String URL_DATABASE = "http://heychessmate.tech/MainPHP_WEB.php"; // NEW Path of PHP

    String TAG = MainActivity.class.getSimpleName(); // For Log

    // Kind of player in a peer
    int NO_ONE = 0;
    int MASTER = 1;
    int SLAVE = 2;

    // Kind of user
    int NO_USER = 0;
    int REGISTERED = 1;
    int ANONYMOUS = 2;

    // Color or Pieces
    String WHITE = "White";
    String BLACK = "Black";

    // ABSOLUT States of game | For fragments
    int S_PAUSED = 1;
    int S_FINISHED = 2;

    // Params to save a game
    int SAVEGAME = 1;
    int NOSAVEGAME = 2;

    // String group key notification
    String GROUP_KEY_NOTIFICATION = "Group_Chessmate";

    // Kinds of local
    int IS_LOCALIA_T = 1;
    int IS_LOCALPVP = 2;
    int IS_REMOTE_T = 3;
    int IS_NOLOCAL = 4;
    int IS_LOCALPVP_T = 5;

    // moderator
    int NOTHING = 0;
    int FIRST_MOVE = 1;
    int SECOND_MOVE = 2;
    int ROOKED = 3;
    int ILEGAL_MOVE = 4;
    int EATEN = 5;

    // moderator2
    int PRE_GAME = 1;
    int IN_GAME = 2;

    // Moved from...
    int APP = 1;
    int BOARD = 2;
    int UNDEFINED_FROM = 3;

    // Move in board
    int MOVEIA = 0;
    int MOVEYOU = 1;
    int MOVEOPPONENT = 2;
    int MOVEINBOARD = 3;
}
