package com.jorjaiz.chessmateapplicationv1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jorjaiz.chessmateapplicationv1.Adapters.Adapter_CardMove;
import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Chat.Fragment_Chat;
import com.jorjaiz.chessmateapplicationv1.Chat.Interface_BackPressed;
import com.jorjaiz.chessmateapplicationv1.Classes.*;

import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.Communication;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Constants,
        Fragment_PauseGame.OnFragmentInteractionListener, Fragment_GameFinished.OnFragmentInteractionListener,
        CommunicationInterface.OnCommunicationListener, Fragment_InviteAgain.OnFragmentInteractionListener,
        Query.OnResponseDatabase, Interface_BackPressed, BTCommunication.OnBTCommunicationListener
{
    // GLOBAL VARIABLES

    // Turquoise => Moves, Purple => Attacks, LightOrange => Check, Red => Checkmate, Lima => Castling, Yellow => Draw
    int colorTurquoise, colorPurple, colorLightOrange, colorRed, colorLima, colorYellow, colorSemiLightOrange, colorOrangeYellow;
    int colorLightBlue, colorNormalBlue, colorLightPurple, colorNormalPurple;

    Chessgame game; // Instance of game

    ArrayList<Integer> cBB; // Collection of colors of each button of board
    Button[][] buttonsBoard, buttonsBoardLayer; // Arrays of buttons of board and layer

    ParamsGame pG;

    boolean colorPlayer;

    boolean resume;

    String nameView;

    String newOrSaved;

    Fragment_PauseGame fragPause;

    // Variables for Game Interface View

    private Coord firstCoord; // Coord of first touch
    private Coord secondCoord; // Coord of second touch
    private boolean nextTouch = false; // Validator for the second touch

    Button bBRook, bWRook;
    AnimatorSet animTurn;

    Handler handlerC;
    Runnable runnableC;

    TextView tVCounter;
    int timeMove;
    int limitTimeMove;
    boolean firstTimeMove;
    boolean opponentWithBoard;
    ObjectAnimator animCounter;

    public Chronometer chrono; // Object of Chronometer

    String oponentName;

    DatabaseReference reference;

    Fragment_InviteAgain fragInvitation;

    //Things of connection
    boolean warned = false;

    int serverErrors;

    // Local game

    public static Timer timerMainActivity;
    Fragment_LoadingBoard fragLoadingBoard;
    Fragment_PlayerWaiting fragPlayerWaiting;
    Fragment_MoveInBoard fragMoveInBoard;
    public static Timer timerCheckConnBoard;

    boolean showFragMoveIA;

    boolean backMove;

    // Variables for Saved Moves View

    int numMoves;
    int posMoves;
    TextView tVPlayerName;
    ImageView iVColorPiece;
    HorizontalInfiniteCycleViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_GAME INTERFACE ON CREATE");

            colorPurple = getResources().getColor(R.color.colorPurple);
            colorTurquoise = getResources().getColor(R.color.colorTurquoise);
            colorRed = getResources().getColor(R.color.colorLightRed);
            colorLightOrange = getResources().getColor(R.color.colorLightOrange);
            colorSemiLightOrange = getResources().getColor(R.color.colorSemiLightOrange);
            colorLima = getResources().getColor(R.color.colorLima);
            colorYellow = getResources().getColor(R.color.colorYellow);
            colorOrangeYellow = getResources().getColor(R.color.colorOrangeYellow);

            colorLightBlue = getResources().getColor(R.color.colorLightBlue);
            colorNormalBlue = getResources().getColor(R.color.colorNormalBlue);
            colorLightPurple = getResources().getColor(R.color.colorLightPurple);
            colorNormalPurple = getResources().getColor(R.color.colorNormalPurple);

            Bundle parameters = getIntent().getExtras();
            pG = parameters.getParcelable("pG");

            colorPlayer = (pG.getColorPlayer()==1); // true => white, false => black
            resume = (pG.getResume()==1); // false => show moves, true => play a game
            oponentName = pG.getOponentName();
            newOrSaved = pG.getNewSaved();

            // Initialize the Game | Build the Game
            game = new Chessgame(pG.getpOneId(), (pG.getpOneWhite()==1), pG.getpTwoId(), (pG.getpTwoBlack()==1),
                    pG.getMode(), pG.getDifficulty(), PLAYING);

            game.setIdGame(pG.getIdGame());
            game.setStringMoves(pG.getStringMoves());
            game.setRewinds(pG.getRewinds());
            game.setSecondsLeft(pG.getSecondsLeft());

            if(newOrSaved.equals(SAVED) && !resume)
            {
                setContentView(R.layout.activity_view__saved_moves);
                nameView = SAVEDMOVES;

                declareButtonsBoard();
                declareButtonsBoardLayer();
                saveColorsButtonsBoard();
                initIconsOnBoard();

                game.fillArrayListMoves(game.getStringMoves());
                game.startGame(SAVED);

                numMoves = -1;
                posMoves = 0;

                tVPlayerName = findViewById(R.id.tvUserName);
                iVColorPiece = findViewById(R.id.iVColorPieces);

                iVColorPiece.setImageResource(R.color.colorTransparent);
                tVPlayerName.setText(" ");

                notationAllSavedMoves();
                setForwardBackwardListener();
            }
            else
            {
                setContentView(R.layout.activity_main);
                nameView = GAMEINTERFACE;

                serverErrors = 0;

                findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);

                TextView tVWhiteName = findViewById(R.id.tvWhiteName);
                TextView tVBlackName = findViewById(R.id.tvBlackName);

                if(game.getMode() == PVIA)
                {
                    tVWhiteName.setText(colorPlayer?CP.get().getNamePlayer():"Chessy");
                    tVBlackName.setText(colorPlayer?"Chessy":CP.get().getNamePlayer());
                }
                else
                {
                    tVWhiteName.setText(colorPlayer?CP.get().getNamePlayer():oponentName);
                    tVBlackName.setText(colorPlayer?oponentName:CP.get().getNamePlayer());
                }

                declareButtonsBoard();
                declareButtonsBoardLayer();
                saveColorsButtonsBoard();
                initIconsOnBoard();

                setPauseListener();
                setRewatchListener();

                if(game.getMode() == Constants.PVIA)
                {
                    if(pG.getKindOfLocal() == IS_LOCALIA_T)
                    {
                        timerMainActivity = new Timer();

                        BTCommunication.getInstance().setListener(this);

                        timerData();

                        movedFrom = UNDEFINED_FROM;
                        moderator = NOTHING;
                        moderator2 = PRE_GAME;
                        rooked = false;
                        game.setState(Constants.WAITING);
                        backMove = false;

                        checkConnectionWithBoard(60000);

                        if(newOrSaved.equals(NEW))
                            BTCommunication.getInstance().sendData("NEW");
                    }

                    setChronoListener();
                    setBackListener();
                    hideCounter();
                    hideChat();
                }
                else
                {
                    if(game.getMode() == Constants.PVPLOCAL)
                    {
                        hideChat();
                    }
                    else
                    {
                        if(CP.get().isChat())
                            setChatListener();
                        else
                            hideChat();
                    }

                    hideChrono();
                    hideBack();
                    hideCounter();

                    if(pG.getMode() == PVPONLINE && CP.get().getConn() == ONLINE)
                    {
                        showFragPlayerWaiting(false);

                        CommunicationInterface.getInstance().setListener(this);

                        game.setState(Constants.WAITING);

                        opponentWithBoard = false;

                        if(pG.getKindOfLocal() == IS_REMOTE_T)
                        {
                            timerMainActivity = new Timer();

                            BTCommunication.getInstance().setListener(this);

                            timerData();

                            movedFrom = UNDEFINED_FROM;
                            moderator = NOTHING;
                            moderator2 = PRE_GAME;
                            rooked = false;

                            limitTimeMove = 50;

                            BTCommunication.getInstance().sendData("NEW");

                            checkConnectionWithBoard(60000);
                        }
                        else
                        {
                            if(pG.getKindPlayer() == MASTER)
                            {
                                Handler hdlr = new Handler();
                                hdlr.postDelayed(
                                        ()->
                                        {
                                            if(pG.getKindPlayer() == MASTER)
                                                CommunicationInterface.getInstance().respondAsMaster("YA!");
                                            else
                                                CommunicationInterface.getInstance().respondAsSlave("YA!");

                                        }, 5000
                                );
                            }
                        }
                    }
                    else if(pG.getMode() == PVPLOCAL)
                    {
                        timerMainActivity = new Timer();

                        BTCommunication.getInstance().setListener(this);

                        timerData();

                        if(pG.getKindOfLocal() == IS_LOCALPVP_T)
                        {
                            game.setState(WAITING);

                            movedFrom = UNDEFINED_FROM;
                            moderator = NOTHING;
                            moderator2 = PRE_GAME;

                            rooked = false;

                            if(pG.getKindPlayer() == SLAVE)
                                BTCommunication.getInstance().sendData("NEW");

                            checkConnectionWithBoard(60000);
                        }
                        else if(pG.getKindOfLocal() == IS_LOCALPVP)
                        {
                            game.setState(WAITING);

                            movedFrom = UNDEFINED_FROM;
                            moderator = NOTHING;
                            moderator2 = IN_GAME;

                            rooked = false;

                            if(pG.getKindPlayer() == SLAVE)
                                BTCommunication.getInstance().sendData("YEAH");

                            checkConnectionWithBoard(60000);
                        }
                    }
                }

                if(newOrSaved.equals(NEW))
                {
                    if(pG.getKindOfLocal() != IS_LOCALIA_T && game.startGame(NEW))
                    {
                        updateIcon(game.getLastMove());
                        watchaPromotionAndCastling();
                        watchaChecksMatesAndDraws();
                    }
                }
                else
                {
                    game.fillArrayListMoves(game.getStringMoves());
                    game.startGame(SAVED);
                    resumeGame();

                    if(pG.getKindOfLocal() == IS_LOCALIA_T)
                    {
                        String listInitialPositions = "$" + getPositionsOfPieces();
                        BTCommunication.getInstance().sendData(listInitialPositions);
                    }
                }

                if(pG.getKindOfLocal() != IS_LOCALIA_T && game.getMode() == Constants.PVIA)
                {
                    chrono.setBase(SystemClock.elapsedRealtime()-game.getSecondsLeft());
                    chrono.start();
                }

                bBRook = findViewById(R.id.btnBlackRook);
                bWRook = findViewById(R.id.btnWhiteRook);

                if(pG.getKindOfLocal() != IS_LOCALIA_T)
                    setAnimationTurn(game.getBoardTurn());
            }

            Log.w(TAG, " ");
            Log.w(TAG, "====================   GAME STARTS   =========================");
            Log.w(TAG, " ");

        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(nameView.equals(SAVEDMOVES))
        {
            ParamsGame paramsGame = new ParamsGame();
            paramsGame.setIdPlayer(CP.get().getIdPlayer());
            paramsGame.setNewSaved(SAVED);

            Intent i = new Intent(this, View_GamesSaved.class);
            i.putExtra("pG", paramsGame);
            this.startActivity(i);
        }
        else
        {
            if(pG.getMode() == PVPONLINE && CP.get().isChat())
            {
                if(interface_backPressed != null)
                    interface_backPressed.onBackPressed();

                timerChat();
                hideBadgeCounter();
                btnChat.setEnabled(true);
                fChatOpened = false;
            }
        }
    }

    // LISTENERS

    public void setPauseListener()
    {
        findViewById(R.id.btnPause).setOnClickListener(
                v ->
                {
                    if(game.getMode() == Constants.PVIA)
                    {
                        if(game.getState() == Constants.CHECKBYIA || game.getState() == Constants.PLAYING)  // Check if game can be paused
                        {
                            game.setSecondsLeft(SystemClock.elapsedRealtime() - chrono.getBase()); // Save the elapsed time of game
                            chrono.stop();
                            game.setState(Constants.PAUSED);
                            gotoFragPause();
                        }
                    }
                    else
                    {
                        if(pG.getKindOfLocal() != IS_NOLOCAL)
                        {
                            if(game.getState() != WAITING)
                            {
                                game.setState(Constants.PAUSED);
                                gotoFragPause();
                            }
                        }
                        else
                        {
                            gotoFragPause();
                        }
                    }
                });
    }

    private void hidePause()
    {
        if(fragPause != null)
        {
            if(fragPause.isResumed())
            {
                getSupportFragmentManager().beginTransaction().remove(fragPause).commit();
            }
        }

        findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);
        findViewById(R.id.bLayer).setVisibility(View.GONE);
    }

    public void setChronoListener()
    {
        chrono = findViewById(R.id.btnChronometer);
        chrono.setOnChronometerTickListener(
                chronometer ->
                {
                    if((SystemClock.elapsedRealtime() - chrono.getBase()) > game.getLimitTime() &&
                            (game.getState() == Constants.CHECKBYIA ||
                                    game.getState() == Constants.CHECKBYP ||
                                    game.getState() == Constants.PLAYING))      // Check if time out
                    {
                        game.setSecondsLeft(SystemClock.elapsedRealtime() - chrono.getBase()); // Save the elapsed time of game
                        chrono.stop();
                        game.setState(Constants.FINISHEDBYIA);  // IA wins

                        watchaChecksMatesAndDraws();

                        Log.e(TAG, "*****   TIMEOUT | GAME FINISHED   *****");

                        if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                        {
                            MySQL.query(this)
                                    .setGame(2, game.getStringMoves(), game.getSecondsLeft(),
                                            game.getRewinds(), game.getIdGame());
                        }

                        if(game.getMode() == PVIA && pG.getKindOfLocal() == IS_LOCALIA_T)
                        {
                            BTCommunication.getInstance().sendData("END");
                        }

                        gotoFragGameFinished(true);
                    }
                    else
                    {
                        if(game.getState() != Constants.CHECKBYIA && game.getState() != Constants.CHECKBYP &&
                                game.getState() != Constants.PLAYING && game.getState() != WAITING)       // Check if game is finished
                        {
                            game.setSecondsLeft(SystemClock.elapsedRealtime() - chrono.getBase()); // Save the elapsed time of game
                            chrono.stop();

                            Log.w(TAG, " ");
                            Log.w(TAG, "Time of Chronometer: " + game.getSecondsLeft());
                            Log.e(TAG, "*****   GAME IS FINISHED   *****");

                            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                            {
                                MySQL.query(this)
                                        .setGame(2, game.getStringMoves(), game.getSecondsLeft(),
                                                game.getRewinds(), game.getIdGame());
                            }

                            if(game.getMode() == PVIA && pG.getKindOfLocal() == IS_LOCALIA_T)
                            {
                                BTCommunication.getInstance().sendData("END");
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(
                                    () ->
                                    {
                                        gotoFragGameFinished(true);
                                    }, 2500);
                        }
                    }

                    game.setSecondsLeft(SystemClock.elapsedRealtime() - chrono.getBase()); // Capturing every second
                });
    }

    public void hideChrono()
    {
        findViewById(R.id.btnChronometer).setVisibility(View.INVISIBLE);
    }

    public void setBackListener()
    {
        findViewById(R.id.btnBack).setOnClickListener(v -> goBack());
    }

    public void hideBack()
    {
        findViewById(R.id.btnBack).setVisibility(View.INVISIBLE);
    }

    public void setCounterListener()
    {
        timeMove = 0;
        firstTimeMove = true;

        handlerC = new Handler();
        runnableC = new Runnable()
        {
            @Override
            public void run()
            {
                if(colorPlayer == game.getBoardTurn())
                {
                    timeMove++;

                    if(firstTimeMove)
                    {
                        if(timeMove == limitTimeMove)
                        {
                            game.setState(Constants.FINISHEDBYIA);
                            watchaChecksMatesAndDraws();
                            timeMove = 0;
                            tVCounter.setText("");
                            tVCounter.setVisibility(View.INVISIBLE);
                            animCounter.cancel();

                        }

                        if((limitTimeMove-timeMove) < 10)
                        {
                            tVCounter.setVisibility(View.VISIBLE);
                            tVCounter.setText((limitTimeMove-timeMove)+"");
                            animCounter.start();
                        }
                    }
                    else
                    {
                        if(timeMove == limitTimeMove)
                        {
                            game.setState(Constants.FINISHEDBYIA);
                            watchaChecksMatesAndDraws();
                            timeMove = 0;
                            tVCounter.setText("");
                            tVCounter.setVisibility(View.INVISIBLE);
                            animCounter.cancel();

                        }

                        if((limitTimeMove-timeMove) < 15)
                        {
                            tVCounter.setVisibility(View.VISIBLE);
                            tVCounter.setText((limitTimeMove-timeMove)+"");
                            animCounter.start();
                        }
                    }
                }
                else
                {
                    timeMove++;

                    if(firstTimeMove)
                    {
                        if(timeMove == limitTimeMove+15)
                        {
                            game.setState(Constants.FINISHEDBYOPPONENT);
                            watchaChecksMatesAndDraws();
                            timeMove = 0;
                        }
                    }
                    else
                    {
                        if(timeMove == limitTimeMove+15)
                        {
                            game.setState(Constants.FINISHEDBYOPPONENT);
                            watchaChecksMatesAndDraws();
                            timeMove = 0;
                        }
                    }
                }

                handlerC.postDelayed(this, 1000);
            }
        };
        handlerC.post(runnableC);

        tVCounter = findViewById(R.id.tVCounter);

        animCounter = ObjectAnimator.ofInt(tVCounter, "backgroundColor", Color.WHITE, Color.RED, Color.WHITE);
        animCounter.setDuration(1500);
        animCounter.setEvaluator(new ArgbEvaluator());
        animCounter.setRepeatMode(ValueAnimator.REVERSE);
        animCounter.setRepeatCount(Animation.INFINITE);
    }

    public void hideCounter()
    {
        findViewById(R.id.tVCounter).setVisibility(View.INVISIBLE);
    }

    public void setChatListener()
    {
        btnChat = findViewById(R.id.btnChat);
        btnBadgeCounter = findViewById(R.id.btnBadgeCounter);

        btnChat.setOnClickListener(
                v ->
                {
                    Log.w(TAG, "-> -> -> CHAT OPENED");
                    showFragment_Chat();
                });

        timerChat();
    }

    public void hideChat()
    {
        findViewById(R.id.btnChat).setVisibility(View.INVISIBLE);
    }

    public void setRewatchListener()
    {
        findViewById(R.id.bRewatch).setOnClickListener(
                v ->
                {
                    hideRewatch();

                    pG.setMode(game.getMode());
                    pG.setIdPlayer(CP.get().getIdPlayer());
                    pG.setNewSaved(NEW);

                    Bundle b = new Bundle();
                    b.putParcelable("pG", pG);
                    b.putInt("gameState", game.getState());

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Fragment_GameFinished frag = new Fragment_GameFinished();
                    frag.setArguments(b);
                    transaction.replace(R.id.fragmentPlace2, frag);
                    transaction.commit();
                }
        );

        hideRewatch();
    }

    public void hideRewatch()
    {
        findViewById(R.id.bRewatch).setVisibility(View.INVISIBLE);
    }

    public void setForwardBackwardListener()
    {
        findViewById(R.id.btnRedo).setOnClickListener(v -> showMovements(true)); // Forward Listener
        findViewById(R.id.btnUndo).setOnClickListener(v -> showMovements(false)); // Backward Listener
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onFragResumeChrono(long secondsLeft)
    {
        game.setState(PLAYING);

        if(game.getMode() == PVIA)
        {
            chrono.setBase(SystemClock.elapsedRealtime()-game.getSecondsLeft());
            chrono.start();
        }
    }

    @Override
    public void onFragGotoFragGameFinished()
    {
        if(game.getMode() == Constants.PVPONLINE)
        {
            if(pG.getKindPlayer() == MASTER)
                CommunicationInterface.getInstance().respondAsMaster("LOSE");
            else
                CommunicationInterface.getInstance().respondAsSlave("LOSE");
        }
        else if(game.getMode() == Constants.PVPLOCAL)
        {
            BTCommunication.getInstance().sendData("LOSE");
        }

        game.setState(Constants.FINISHEDBYIA);
        gotoFragGameFinished(true);
    }

    @Override
    public void onFragRemoveBLayerPause() { findViewById(R.id.bLayer).setVisibility(View.INVISIBLE); }

    @Override
    public void onFragRewatch()
    {
        findViewById(R.id.bRewatch).setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragInviteAgain()
    {
        gotoFragInviteAgain( false, true);
    }

    @Override
    public void onPlayAgain()
    {
        if(game.getMode() == PVPONLINE)
        {
            if(pG.getKindOfLocal() == IS_REMOTE_T)
            {
                BTCommunication.getInstance().sendData("AGAIN");

                if(timerMainActivity != null)
                    timerMainActivity.cancel();
            }

            gotoViewPiecesColor();
        }
        else
        {
            if(timerMainActivity != null)
                timerMainActivity.cancel();

            gotoViewPiecesColorBluetooth();
        }
    }

    @Override
    public void onCancelInvitation()
    {
        getSupportFragmentManager().beginTransaction().remove(fragInvitation).commit();

        pG.setMode(game.getMode());
        pG.setIdPlayer(CP.get().getIdPlayer());
        pG.setNewSaved(NEW);

        Bundle b = new Bundle();
        b.putParcelable("pG", pG);
        b.putInt("gameState", game.getState());
        b.putBoolean("enableNewGame", false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_GameFinished frag = new Fragment_GameFinished();
        frag.setArguments(b);
        transaction.replace(R.id.fragmentPlace2, frag);
        transaction.commit();
    }

    // GOTO FRAGMENTS

    public void gotoFragGameFinished(boolean enableNewGame)
    {
        try
        {
            if(game.getState() != Constants.PLAYING)
            {
                if(game.getMode() == Constants.PVIA)
                {
                    hideBack();
                    hideChrono();

                    if(pG.getKindOfLocal() == IS_LOCALIA_T)
                        BTCommunication.getInstance().sendData("END");
                }
                else if(game.getMode() == Constants.PVPONLINE)
                {
                    hideCounter();

                    if(runnableC != null && handlerC != null)
                        handlerC.removeCallbacks(runnableC);

                    if(CP.get().isChat())
                        quitChat();

                    else if(pG.getKindOfLocal() == IS_REMOTE_T)
                        BTCommunication.getInstance().sendData("OVER");
                }

                if(pG.getKindOfLocal() != IS_NOLOCAL)
                {
                    if(timerCheckConnBoard != null)
                        timerCheckConnBoard.cancel();

                    showFragMoveIA = false;

                    hideFragLoadingBoard();
                    hideFragPlayerWaiting();
                    hideFragMoveInBoard();
                }

                pG.setMode(game.getMode());
                pG.setIdPlayer(CP.get().getIdPlayer());
                pG.setNewSaved(NEW);

                Bundle b = new Bundle();
                b.putParcelable("pG", pG);
                b.putInt("gameState", game.getState());
                b.putBoolean("enableNewGame", enableNewGame);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment_GameFinished frag = new Fragment_GameFinished();
                frag.setArguments(b);
                transaction.replace(R.id.fragmentPlace2, frag);
                transaction.commit();

                findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
                findViewById(R.id.btnPause).setVisibility(View.INVISIBLE);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE gotoFragGameFinished: " + e.toString());
        }
    }

    public void gotoFragPause()
    {
        try
        {
            ParamsGame paramsGame = new ParamsGame();
            paramsGame.setIdGame(game.getIdGame());
            paramsGame.setStringMoves(game.getStringMoves());
            paramsGame.setSecondsLeft(game.getSecondsLeft());
            paramsGame.setRewinds(game.getRewinds());
            paramsGame.setMode(game.getMode());
            paramsGame.setSaveGame(pG.getSaveGame());
            paramsGame.setNewSaved(pG.getNewSaved());
            paramsGame.setKindOfLocal(pG.getKindOfLocal());

            Bundle b = new Bundle();
            b.putParcelable("pG", paramsGame);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragPause = new Fragment_PauseGame();
            fragPause.setArguments(b);
            transaction.replace(R.id.fragmentPlace, fragPause);
            transaction.commit();

            findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE gotoFragPause: " + e.toString());
        }
    }

    public void gotoFragInviteAgain(boolean oponentDisconnected, boolean inviting)
    {
        Bundle b = new Bundle();
        b.putParcelable("pG", pG);
        b.putBoolean("oponentDisconnected", oponentDisconnected);
        b.putBoolean("inviting", inviting);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragInvitation = new Fragment_InviteAgain();
        fragInvitation.setArguments(b);
        transaction.replace(R.id.fragmentPlace4, fragInvitation);
        transaction.commit();
    }

    public void gotoViewPiecesColor()
    {
        Intent i = new Intent(this, View_PiecesColor.class);
        i.putExtra("pG", pG);
        startActivity(i);
    }

    public void gotoViewPiecesColorBluetooth()
    {
        Intent i = new Intent(this, View_PiecesColor_Bluetooth.class);
        i.putExtra("pG", pG);
        startActivity(i);
    }

    public void showFragLoadingBoard()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragLoadingBoard = new Fragment_LoadingBoard();
        transaction.replace(R.id.fragmentLoadingBoard, fragLoadingBoard);
        transaction.commit();
    }

    public void hideFragLoadingBoard()
    {
        if(fragLoadingBoard != null)
        {
            getSupportFragmentManager().beginTransaction().remove(fragLoadingBoard).commit();
        }
    }

    public void showFragPlayerWaiting(boolean playerWaiting)
    {
        Bundle b = new Bundle();
        b.putBoolean("playerWaiting", playerWaiting);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragPlayerWaiting = new Fragment_PlayerWaiting();
        fragPlayerWaiting.setArguments(b);
        transaction.replace(R.id.fragmentPlayerWaiting, fragPlayerWaiting);
        transaction.commit();
    }

    public void hideFragPlayerWaiting()
    {
        if(fragPlayerWaiting != null)
        {
            getSupportFragmentManager().beginTransaction().remove(fragPlayerWaiting).commit();
        }
    }

    public void showFragMoveInBoard(int moveWho)
    {
        Bundle b = new Bundle();
        b.putInt("moveWho", moveWho);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragMoveInBoard = new Fragment_MoveInBoard();
        fragMoveInBoard.setArguments(b);
        transaction.replace(R.id.fragmentMoveInBoard, fragMoveInBoard);
        transaction.commit();
    }

    public void hideFragMoveInBoard()
    {
        if(fragMoveInBoard != null)
        {
            getSupportFragmentManager().beginTransaction().remove(fragMoveInBoard).commit();
        }
    }

    public void showFragUnknownError()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_UnknownError fragUnknownError = new Fragment_UnknownError();
        transaction.replace(R.id.fragmentUnknownError, fragUnknownError);
        transaction.commit();
    }

    public void setAnimationTurn(boolean turn)
    {
        if(animTurn != null && animTurn.isRunning())
            animTurn.cancel();

        ObjectAnimator animX = ObjectAnimator.ofFloat(turn?bWRook:bBRook, "scaleX", 1.0f, 1.4f);
        animX.setDuration(1500);
        animX.setRepeatMode(ValueAnimator.REVERSE);
        animX.setRepeatCount(Animation.INFINITE);
        animX.setInterpolator(new LinearInterpolator());

        ObjectAnimator animY = ObjectAnimator.ofFloat(turn?bWRook:bBRook, "scaleY", 1.0f, 1.4f);
        animY.setDuration(1500);
        animY.setRepeatMode(ValueAnimator.REVERSE);
        animY.setRepeatCount(Animation.INFINITE);
        animY.setInterpolator(new LinearInterpolator());

        animTurn = new AnimatorSet();
        animTurn.playTogether(animX, animY);

        animTurn.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationCancel(Animator animation)
            {
                super.onAnimationCancel(animation);

                ObjectAnimator auxX = ObjectAnimator.ofFloat(turn?bWRook:bBRook, "scaleX", 1.0f);
                auxX.setDuration(100);
                auxX.setRepeatCount(1);
                auxX.setInterpolator(new LinearInterpolator());

                ObjectAnimator auxY = ObjectAnimator.ofFloat(turn?bWRook:bBRook, "scaleY", 1.0f);
                auxY.setDuration(100);
                auxY.setRepeatCount(1);
                auxY.setInterpolator(new LinearInterpolator());

                AnimatorSet animatorAuxSet = new AnimatorSet();
                animatorAuxSet.playTogether(auxX, auxY);

                animatorAuxSet.start();
            }
        });

        animTurn.start();
    }

    public void pauseChrono()
    {
        chrono.stop();
    }

    public void resumeChrono()
    {
        chrono.setBase(SystemClock.elapsedRealtime()-game.getSecondsLeft());
        chrono.start();
    }

    private void checkConnectionWithBoard(int time)
    {
        timerCheckConnBoard = new Timer();

        final Handler handler = new Handler();
        timerCheckConnBoard.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.e(TAG, "----------------------- TABLERO NO DETECTADO -------------------------");
                        disableConnectionWithBoard();
                    }
                });
            }
        }, time, time);
    }

    private void disableConnectionWithBoard()
    {
        if(game.getMode() == PVPLOCAL && (pG.getKindOfLocal() == IS_LOCALPVP_T || pG.getKindOfLocal() == IS_LOCALPVP))
        {
            Toast.makeText(this, "Parece que se perdió la conexión con el tablero", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Partida tendrá que terminarse...", Toast.LENGTH_LONG).show();
            game.setState(Constants.FINISHEDBYIA);
            gotoFragGameFinished(false);
            showFragUnknownError();
        }
        else
        {
            Toast.makeText(this, "Parece que se perdió la conexión con el tablero", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "No te preocupes, la partida continuará", Toast.LENGTH_LONG).show();
            showFragUnknownError();

            if(moderator2 == PRE_GAME)
            {
                movedFrom = UNDEFINED_FROM;
                game.setState(PLAYING);
                eatCoord = null;
                rooked = false;
                hideFragLoadingBoard();
                hideFragPlayerWaiting();
                hideFragMoveInBoard();
                setAnimationTurn(game.getBoardTurn());
                resetColorButtonsBoard();

                if (game.getMode() == PVIA)
                {
                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            ()->
                            {
                                if(game.startGame(NEW))
                                {
                                    updateIcon(game.getLastMove());
                                    watchaPromotionAndCastling();
                                    watchaChecksMatesAndDraws();

                                    chrono.setBase(SystemClock.elapsedRealtime()-game.getSecondsLeft());
                                    chrono.start();

                                    setAnimationTurn(game.getBoardTurn());
                                }

                            }, 500
                    );
                }
                else
                {
                    if(pG.getKindPlayer() == MASTER)
                    {
                        if(pG.getKindPlayer() == MASTER)
                            CommunicationInterface.getInstance().respondAsMaster("YA?");
                        else
                            CommunicationInterface.getInstance().respondAsSlave("YA?");

                        if(game.getState() == Constants.REMOTE_WAITING)
                        {
                            opponentWithBoard = true;
                            limitTimeMove = 50;
                            setCounterListener();
                        }
                    }
                    else
                    {
                        if(game.getState() != Constants.REMOTE_WAITING)
                        {
                            game.setState(Constants.WAITING);

                            if(pG.getKindPlayer() == MASTER)
                                CommunicationInterface.getInstance().respondAsMaster("YA#");
                            else
                                CommunicationInterface.getInstance().respondAsSlave("YA#");
                        }
                        else
                        {
                            if(pG.getKindPlayer() == MASTER)
                                CommunicationInterface.getInstance().respondAsMaster("YA?");
                            else
                                CommunicationInterface.getInstance().respondAsSlave("YA?");

                            opponentWithBoard = true;
                            limitTimeMove = 50;
                            setCounterListener();
                        }
                    }
                }
            }
            else
            {
                if(movedFrom == APP)
                {
                    movedFrom = UNDEFINED_FROM;
                    game.setState(PLAYING);
                    eatCoord = null;
                    rooked = false;
                    hideFragLoadingBoard();
                    hideFragPlayerWaiting();
                    hideFragMoveInBoard();
                    setAnimationTurn(game.getBoardTurn());
                    resetColorButtonsBoard();

                    if(game.getMode() == PVIA)
                    {
                        if(colorPlayer == game.getBoardTurn())
                        {
                            resumeChrono();
                        }
                        else
                        {
                            Handler hdlr = new Handler();
                            hdlr.postDelayed(
                                    () ->
                                    {
                                        afterHumanMove();

                                    }, 200);
                        }
                    }
                    else
                    {
                        Log.e(TAG, "        (   APP ----> BAD BOARD  )");

                        if(colorPlayer != game.getBoardTurn())
                        {
                            if(pG.getKindPlayer() == MASTER)
                                CommunicationInterface.getInstance().respondAsMaster("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                            else
                                CommunicationInterface.getInstance().respondAsSlave("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                        }
                    }
                }
            }

            pG.setKindOfLocal(IS_NOLOCAL);
        }

        if(timerCheckConnBoard != null)
            timerCheckConnBoard.cancel();
    }

    // INITIALIZE ARRAYS BUTTONS OF BOARD AND LAYER

    private void declareButtonsBoard()
    {
        try
        {
            buttonsBoard = new Button[8][8];
            String x = "";
            String y;

            for(int a=0; a<8; a++)
            {
                y = (colorPlayer?String.valueOf(a+1):String.valueOf(8-a));

                for(int b=0; b<8; b++)
                {
                    switch(b)
                    {
                        case 0: x = (colorPlayer?"A":"H"); break;
                        case 1: x = (colorPlayer?"B":"G"); break;
                        case 2: x = (colorPlayer?"C":"F"); break;
                        case 3: x = (colorPlayer?"D":"E"); break;
                        case 4: x = (colorPlayer?"E":"D"); break;
                        case 5: x = (colorPlayer?"F":"C"); break;
                        case 6: x = (colorPlayer?"G":"B"); break;
                        case 7: x = (colorPlayer?"H":"A"); break;
                    }

                    String buttonID = "btn" + x + y;
                    buttonsBoard[b][a] = findViewById(getResources().getIdentifier(buttonID, "id", getPackageName()));
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE declareButtonsBoard: " + e.toString());
        }
    }

    private void declareButtonsBoardLayer()
    {
        try
        {
            buttonsBoardLayer = new Button[8][8];
            String x;
            String y = "";

            for(int a=0; a<8; a++)
            {
                x = (colorPlayer?String.valueOf(a+1):String.valueOf(8-a));

                for(int b=0; b<8; b++)
                {
                    switch(b)
                    {
                        case 0: y = (colorPlayer?"A":"H"); break;
                        case 1: y = (colorPlayer?"B":"G"); break;
                        case 2: y = (colorPlayer?"C":"F"); break;
                        case 3: y = (colorPlayer?"D":"E"); break;
                        case 4: y = (colorPlayer?"E":"D"); break;
                        case 5: y = (colorPlayer?"F":"C"); break;
                        case 6: y = (colorPlayer?"G":"B"); break;
                        case 7: y = (colorPlayer?"H":"A"); break;
                    }

                    String buttonID = "btn" + x + y;
                    buttonsBoardLayer[b][a] = findViewById(getResources().getIdentifier(buttonID, "id", getPackageName()));

                    if(!nameView.equals(SAVEDMOVES))
                    {
                        buttonsBoardLayer[b][a].setOnClickListener(this::touch);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE declareButtonsBoardLayer: " + e.toString());
        }
    }

    private void saveColorsButtonsBoard()
    {
        cBB = new ArrayList<>();

        for(int a=0; a<8; a++)
            for(int b=0; b<8; b++)
                cBB.add(((ColorDrawable)buttonsBoard[a][b].getBackground()).getColor());
    }

    private void initIconsOnBoard()
    {
        for(byte i=0; i<8; i++)
        {
            buttonsBoardLayer[i][1].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_pawnwhite));
            buttonsBoardLayer[i][6].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_pawnblack));
        }

        buttonsBoardLayer[0][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rookwhite));
        buttonsBoardLayer[7][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rookwhite));
        buttonsBoardLayer[1][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_knightwhite));
        buttonsBoardLayer[6][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_knightwhite));
        buttonsBoardLayer[2][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_bishopwhite));
        buttonsBoardLayer[5][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_bishopwhite));
        buttonsBoardLayer[3][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_queenwhite));
        buttonsBoardLayer[4][0].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_kingwhite));

        buttonsBoardLayer[0][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rookblack));
        buttonsBoardLayer[7][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_rookblack));
        buttonsBoardLayer[1][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_knightblack));
        buttonsBoardLayer[6][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_knightblack));
        buttonsBoardLayer[2][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_bishopblack));
        buttonsBoardLayer[5][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_bishopblack));
        buttonsBoardLayer[3][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_queenblack));
        buttonsBoardLayer[4][7].setBackground(ContextCompat.getDrawable(this, R.drawable.ic_kingblack));
    }

    private void resetColorButtonsBoard()
    {
        int x = 0;

        for(int a=0; a<8; a++)
        {
            for(int b=0; b<8; b++)
            {
                buttonsBoard[b][a].setBackgroundColor(cBB.get(x));
                x++;
            }
        }
    }

    private Coord getCoordOfPressButton(View v)
    {
        String idButton = v.getResources().getResourceEntryName(v.getId());

        int a = 0;
        int b;

        b = (colorPlayer?
                Character.getNumericValue(idButton.charAt(3))-1:
                8-Character.getNumericValue(idButton.charAt(3)));

        switch (idButton.charAt(4))
        {
            case 'A': a = colorPlayer?0:7; break;
            case 'B': a = colorPlayer?1:6; break;
            case 'C': a = colorPlayer?2:5; break;
            case 'D': a = colorPlayer?3:4; break;
            case 'E': a = colorPlayer?4:3; break;
            case 'F': a = colorPlayer?5:2; break;
            case 'G': a = colorPlayer?6:1; break;
            case 'H': a = colorPlayer?7:0; break;
        }

        return new Coord(a, b);
    }


    public void updateIcon(Move m)
    {
        if(m != null)
        {
            try
            {
                buttonsBoardLayer[m.getX1()][m.getY1()].setBackgroundColor(Color.TRANSPARENT);
                int x = game.getIntOfAPiece(new Coord(m.getX2(), m.getY2()));
                buttonsBoardLayer[m.getX2()][m.getY2()].setBackground(ContextCompat.getDrawable(this, getIconByInt(x)));
            }
            catch(Exception e)
            {
                Log.e(TAG, "Exception VIEW_GAMEINTERFACE updateIcon: " + e.toString());
            }
        }
    }

    public void unUpdateIcon(Move m)
    {
        if(m != null)
        {
            try
            {
                int x;
                buttonsBoardLayer[m.getX2()][m.getY2()].setBackgroundColor(Color.TRANSPARENT);

                if(game.getSpot(new Coord(m.getX2(), m.getY2())) != null)
                {
                    x = game.getIntOfAPiece(new Coord(m.getX2(), m.getY2()));
                    buttonsBoardLayer[m.getX2()][m.getY2()].setBackground(ContextCompat.getDrawable(this, getIconByInt(x)));
                }
                x = game.getIntOfAPiece(new Coord(m.getX1(), m.getY1()));
                buttonsBoardLayer[m.getX1()][m.getY1()].setBackground(ContextCompat.getDrawable(this, getIconByInt(x)));
            }
            catch(Exception e)
            {
                Log.e(TAG, "Exception VIEW_GAMEINTERFACE unUpdateIcon: " + e.toString());
            }
        }
    }

    private int getIconByInt(int ic)
    {
        switch(ic)
        {
            case 1: return R.drawable.ic_pawnblack;
            case 2: return R.drawable.ic_knightblack;
            case 3: return R.drawable.ic_bishopblack;
            case 4: return R.drawable.ic_rookblack;
            case 5: return R.drawable.ic_queenblack;
            case 6: return R.drawable.ic_kingblack;
            case 11: return R.drawable.ic_pawnwhite;
            case 12: return R.drawable.ic_knightwhite;
            case 13: return R.drawable.ic_bishopwhite;
            case 14: return R.drawable.ic_rookwhite;
            case 15: return R.drawable.ic_queenwhite;
            case 16: return R.drawable.ic_kingwhite;
        }
        return 0;
    }


    // HUMAN TOUCH

    public void touch(View v)
    {
        try
        {
            if(game.getState() == PLAYING || game.getState() == Constants.CHECKBYIA || game.getState() == Constants.CHECKBYP)
            {
                if(!nextTouch)
                {
                    Log.w(TAG, "    < First touch >");
                    firstTouch(v);
                }
                else
                {
                    Log.w(TAG, "    < Second touch >");
                    secondTouch(v);
                }
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE touch: " + e.toString());
        }
    }

    private void firstTouch(View v)
    {
        try
        {
            firstCoord = getCoordOfPressButton(v);

            if(game.getSpot(firstCoord) != null && colorPlayer == game.getBoardTurn() &&
                    game.isSpotWhite(firstCoord) == game.getBoardTurn())
            {
                resetColorButtonsBoard();

                if(game.getDifficulty() != DIFFICULT)
                {
                    showPossible(game.getAlgorithm().getRealMoves(firstCoord), true); //MAYBE PUT THIS GET IN CHESSGAME
                    showPossible(game.getAlgorithm().getRealAttacks(firstCoord), false);
                }

                if(game.getAlgorithm().getRealMoves(firstCoord) != null ||
                        game.getAlgorithm().getRealAttacks(firstCoord) != null)
                {
                    watchaIfCheck();
                    nextTouch = true;
                }
                else
                {
                    if(!game.canMove(game.getBoardTurn()))
                    {
                        Log.e(TAG, "*****   DRAW OF MOVES   *****");

                        game.moveHuman(null);
                        game.switchTurn();

                        if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                        {
                            MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                        }

                        game.setState(Constants.DRAWMOVBYIA);
                        watchaChecksMatesAndDraws();
                    }
                    else
                    {
                        watchaIfCheck();
                    }
                }

                //Log.i(TAG, "    Piece choosen in " + COORDX[firstCoord.x] + (firstCoord.y+1));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE firstTouch: " + e.toString());
        }
    }

    private void secondTouch(View v)
    {
        try
        {
            secondCoord = getCoordOfPressButton(v);

            if(game.watchaIfMoveIsLegal(firstCoord, secondCoord))
            {
                resetColorButtonsBoard();
                humanMove();

                //Log.i(TAG, "    Place choosen in " + COORDX[secondCoord.x] + (secondCoord.y+1));

                if(game.getMode() == Constants.PVIA)
                {
                    if(pG.getKindOfLocal() != IS_LOCALIA_T)
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(
                                () ->
                                {
                                    afterHumanMove();

                                }, 50);
                    }
                }
            }
            else
            {
                if(game.getSpot(secondCoord) != null && game.isSpotWhite(secondCoord) == game.getBoardTurn())
                {
                    nextTouch = false;
                    touch(v);
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE secondTouch: " + e.toString());
        }
    }

    private void showPossible(ArrayList<Move> moves, boolean move)
    {
        try
        {
            if(moves != null)
            {
                ArrayList<String> movesS = new ArrayList<>();

                for(int i=0; i<moves.size(); i++)
                {
                    Move m = moves.get(i);
                    movesS.add(Byte.toString((m.getX2()))+m.getY2());
                }
                //moves.forEach((m) -> movesS.add(Byte.toString((m.getX2()))+m.getY2()));

                int x = 0;

                while(x < movesS.size())
                {
                    for(int a=0; a<8; a++)
                    {
                        for(int b=0; b<8; b++)
                        {
                            if(movesS.get(x).equals(String.valueOf(b)+String.valueOf(a)))
                            {
                                if(move)
                                {
                                    if(!game.isWhiteKMoved() || !game.isBlackKMoved())
                                    {
                                        if(game.isCastling(firstCoord, movesS.get(x)))
                                        {
                                            buttonsBoard[b][a].setBackgroundColor(colorLima);
                                        }
                                        else
                                        {
                                            buttonsBoard[b][a].setBackgroundColor(colorTurquoise);
                                        }
                                    }
                                    else
                                    {
                                        buttonsBoard[b][a].setBackgroundColor(colorTurquoise);
                                    }
                                }
                                else
                                {
                                    buttonsBoard[b][a].setBackgroundColor(colorPurple);
                                }
                                break;
                            }
                        }
                    }
                    x++;
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE showPossible: " + e.toString());
        }
    }

    // HUMAN AND IA MOVE

    private void humanMove()
    {
        try
        {
            game.moveHuman(game.getLastMove());
            updateIcon(game.getLastMove());
            watchaPromotionAndCastling();
            watchaChecksMatesAndDraws();

            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
            {
                MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
            }

            if(game.getMode() == Constants.PVPONLINE)
            {
                if(pG.getKindOfLocal() == IS_REMOTE_T)
                {
                    if(movedFrom != BOARD)
                    {
                        movedFrom = APP;

                        if(colorPlayer != game.getBoardTurn())
                            showFragMoveInBoard(MOVEYOU);
                        else
                            showFragMoveInBoard(MOVEOPPONENT);

                        checkConnectionWithBoard(60000);

                        if(game.getLastMove().isEat())
                        {
                            moderator = EATEN;
                        }

                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                        BTCommunication.getInstance().sendData(move);
                    }
                    else
                    {
                        Log.e(TAG, "        (   BOARD ----> APP   )");

                        if(pG.getKindPlayer() == MASTER)
                            CommunicationInterface.getInstance().respondAsMaster("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                        else
                            CommunicationInterface.getInstance().respondAsSlave("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));

                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                        BTCommunication.getInstance().sendData(move);

                        movedFrom = UNDEFINED_FROM;

                        setAnimationTurn(game.getBoardTurn());
                    }
                }
                else
                {
                    if(pG.getKindPlayer() == MASTER)
                        CommunicationInterface.getInstance().respondAsMaster(game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                    else
                        CommunicationInterface.getInstance().respondAsSlave(game.getArrayListMoves().get(game.getArrayListMoves().size()-1));

                    setAnimationTurn(game.getBoardTurn());
                }

                timeMove = 0;
                firstTimeMove = false;
                tVCounter.setText("");
                tVCounter.setVisibility(View.INVISIBLE);
                animCounter.cancel();

                if(pG.getKindOfLocal() == IS_REMOTE_T)
                {
                    limitTimeMove = 90;
                }
                else
                {
                    if(opponentWithBoard)
                    {
                        limitTimeMove = 90;
                    }
                    else
                    {
                        limitTimeMove = 30;
                    }
                }
            }
            else if(game.getMode() == Constants.PVPLOCAL)
            {
                if(pG.getKindOfLocal() == IS_LOCALPVP)
                {
                    if(game.getState() == Constants.CHECKBYIA && game.getState() == Constants.CHECKBYP &&
                            game.getState() == Constants.PLAYING && game.getState() == WAITING)
                    {
                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                        BTCommunication.getInstance().sendData(move);
                    }

                    setAnimationTurn(game.getBoardTurn());
                    checkConnectionWithBoard(60000);
                }
                else if(pG.getKindOfLocal() == IS_LOCALPVP_T)
                {
                    if(movedFrom != BOARD)
                    {
                        movedFrom = APP;
                        showFragMoveInBoard(MOVEINBOARD);
                        checkConnectionWithBoard(60000);

                        if(game.getLastMove().isEat())
                        {
                            moderator = EATEN;
                        }

                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                        BTCommunication.getInstance().sendData(move);
                    }
                    else
                    {
                        Log.e(TAG, "        (   BOARD ----> APP   )");

                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                        BTCommunication.getInstance().sendData(move);

                        movedFrom = UNDEFINED_FROM;
                    }
                }
            }
            else if(game.getMode() == Constants.PVIA)
            {
                if(pG.getKindOfLocal() == IS_LOCALIA_T && movedFrom == UNDEFINED_FROM)
                {
                    Log.e(TAG, "MOVED FROM THE APP");

                    movedFrom = APP;

                    if(game.getLastMove().isEat())
                    {
                        moderator = EATEN;
                    }

                    if(game.getState() == Constants.CHECKBYIA || game.getState() == Constants.CHECKBYP ||
                            game.getState() == Constants.PLAYING || game.getState() == WAITING)
                    {
                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                        BTCommunication.getInstance().sendData(move);
                    }

                    pauseChrono();
                    showFragMoveInBoard(MOVEYOU);
                    checkConnectionWithBoard(60000);
                }

                if(pG.getKindOfLocal() == IS_NOLOCAL)
                    setAnimationTurn(game.getBoardTurn());
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE humanMove: " + e.toString());
        }
    }

    private void afterHumanMove()
    {
        try
        {
            pauseChrono();

            if(pG.getKindOfLocal() == IS_LOCALIA_T)
            {
                Handler hdlr = new Handler();
                hdlr.postDelayed(
                        ()->
                        {
                            movedFrom = APP;

                            game.afterMoveHuman();
                            updateIcon(game.getLastMove());
                            watchaPromotionAndCastling();
                            watchaChecksMatesAndDraws();

                            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                            {
                                MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                            }

                            firstCoord = new Coord(game.getLastMove().getX1(), game.getLastMove().getY1());
                            secondCoord = new Coord(game.getLastMove().getX2(), game.getLastMove().getY2());

                            if(game.getLastMove().isEat())
                            {
                                moderator = EATEN;
                            }

                            if(game.getState() == Constants.CHECKBYIA || game.getState() == Constants.CHECKBYP ||
                                    game.getState() == Constants.PLAYING || game.getState() == WAITING)
                            {
                                String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                BTCommunication.getInstance().sendData(move);
                            }

                            showFragMoveIA = true;

                            game.setState(WAITING);
                            showFragMoveInBoard(MOVEIA);
                            checkConnectionWithBoard(60000);

                        }, 1000
                );
            }
            else
            {
                game.afterMoveHuman();
                updateIcon(game.getLastMove());
                watchaPromotionAndCastling();
                watchaChecksMatesAndDraws();

                if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                {
                    MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                }

                if(game.getMode() == Constants.PVPLOCAL)
                {
                    String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                    BTCommunication.getInstance().sendData(move);
                }

                setAnimationTurn(game.getBoardTurn());
                resumeChrono();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE afterHumanMove: " + e.toString());
        }

        nextTouch = false;
    }

    // WATCH OUT'S OF DANGER

    private void watchaChecksMatesAndDraws()
    {
        try
        {
            Log.w(TAG, " ");
            Log.w(TAG, "(    Watcha checks, checkmates and draws   )");

            watchaIfCheck();
            watchaIfCheckMate();
            watchaIfDraw();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE watchaChecksMatesAndDraws: " + e.toString());
        }
    }

    private void watchaIfCheckMate()
    {
        if(game.getState() == Constants.FINISHEDBYIA || game.getState() == Constants.FINISHEDBYP)
        {
            if(game.getState() == Constants.FINISHEDBYIA)
            {
                if(colorPlayer)
                {
                    drawCheckmate(game.getWhiteKingCoord());
                }
                else
                {
                    drawCheckmate(game.getBlackKingCoord());
                }
            }
            else
            {
                if(game.getMode() != Constants.PVIA)
                {
                    if(colorPlayer)
                    {
                        if(!game.getBoardTurn())
                        {
                            game.setState(Constants.FINISHEDBYP);
                            drawCheckmate(game.getBlackKingCoord());
                        }
                        else
                        {
                            game.setState(Constants.FINISHEDBYIA);
                            drawCheckmate(game.getWhiteKingCoord());
                        }
                    }
                    else
                    {
                        if(game.getBoardTurn())
                        {
                            game.setState(Constants.FINISHEDBYP);
                            drawCheckmate(game.getWhiteKingCoord());
                        }
                        else
                        {
                            game.setState(Constants.FINISHEDBYIA);
                            drawCheckmate(game.getBlackKingCoord());
                        }
                    }
                }
                else
                {
                    if(colorPlayer)
                    {
                        drawCheckmate(game.getBlackKingCoord());
                    }
                    else
                    {
                        drawCheckmate(game.getWhiteKingCoord());
                    }
                }
            }
        }
        else if(game.getState() == Constants.FINISHEDBYOPPONENT)
        {
            game.setState(Constants.FINISHEDBYP);
            drawCheckmate(colorPlayer?game.getBlackKingCoord():game.getWhiteKingCoord());
        }
    }

    private void watchaIfCheck()
    {
        if(game.getState() == Constants.CHECKBYIA || game.getState() == Constants.CHECKBYP)
        {
            if(game.getState() == Constants.CHECKBYIA)
            {
                if(colorPlayer)
                    drawCheck(game.getWhiteKingCoord());
                else
                    drawCheck(game.getBlackKingCoord());
            }
            else
            {
                if(game.getMode() != Constants.PVIA)
                {
                    if(colorPlayer)
                    {
                        if(!game.getBoardTurn())
                            drawCheck(game.getBlackKingCoord());
                        else
                            drawCheck(game.getWhiteKingCoord());
                    }
                    else
                    {
                        if(game.getBoardTurn())
                            drawCheck(game.getWhiteKingCoord());
                        else
                            drawCheck(game.getBlackKingCoord());
                    }
                }
                else
                {
                    if(colorPlayer)
                        drawCheck(game.getBlackKingCoord());
                    else
                        drawCheck(game.getWhiteKingCoord());
                }
            }
        }
    }

    private void watchaIfDraw()
    {
        if(game.getState() == Constants.DRAWMOVBYIA || game.getState() == Constants.DRAWMOVBYP)
        {
            if(game.getState() == Constants.DRAWMOVBYIA)
            {
                if(colorPlayer)
                    drawDrawLackOfMoves(game.getWhiteKingCoord());
                else
                    drawDrawLackOfMoves(game.getBlackKingCoord());
            }
            else
            {
                if(game.getMode() != Constants.PVIA)
                {
                    if(colorPlayer)
                    {
                        if(!game.getBoardTurn())
                            drawDrawLackOfMoves(game.getBlackKingCoord());
                        else
                            drawDrawLackOfMoves(game.getWhiteKingCoord());
                    }
                    else
                    {
                        if(game.getBoardTurn())
                            drawDrawLackOfMoves(game.getWhiteKingCoord());
                        else
                            drawDrawLackOfMoves(game.getBlackKingCoord());
                    }
                }
                else
                {
                    if(colorPlayer)
                        drawDrawLackOfMoves(game.getBlackKingCoord());
                    else
                        drawDrawLackOfMoves(game.getWhiteKingCoord());
                }
            }
        }
        else if(game.getState() == Constants.DRAWMAT)
        {
            drawDrawWithoutMaterial();
        }
    }

    // DRAWING CHECK, CHECKMATE AND DRAWS

    private void drawCheck(Coord c)
    {
        buttonsBoard[c.x][c.y].setBackgroundColor(colorLightOrange);
    }

    private void drawCheckmate(Coord c)
    {
        buttonsBoard[c.x][c.y].setBackgroundColor(colorRed);

        if(!nameView.equals(SAVEDMOVES))
        {
            if(game.getMode() == PVPONLINE)
            {
                hideCounter();
                if(runnableC != null && handlerC != null)
                    handlerC.removeCallbacks(runnableC);
            }

            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
            {
                MySQL.query(this)
                        .setGame(2, game.getStringMoves(), game.getSecondsLeft(),
                                game.getRewinds(), game.getIdGame());
            }

            if(game.getMode() == PVPONLINE)
            {
                if(pG.getKindPlayer() == MASTER)
                {
                    if(game.getState() == Constants.FINISHEDBYP)
                        CommunicationInterface.getInstance().respondAsMaster("WIN");
                    else if(game.getState() == Constants.FINISHEDBYIA)
                        CommunicationInterface.getInstance().respondAsMaster("LOSE");
                }
                else
                {
                    if(game.getState() == Constants.FINISHEDBYP)
                        CommunicationInterface.getInstance().respondAsSlave("WIN");
                    else if(game.getState() == Constants.FINISHEDBYIA)
                        CommunicationInterface.getInstance().respondAsSlave("LOSE");
                }
            }
            else if(game.getMode() == PVPLOCAL)
            {
                if(pG.getKindPlayer() == MASTER)
                {
                    if(game.getState() == Constants.FINISHEDBYP)
                        BTCommunication.getInstance().sendData("WIN");
                    else if(game.getState() == Constants.FINISHEDBYIA)
                        BTCommunication.getInstance().sendData("LOSE");
                }
                else
                {
                    if(game.getState() == Constants.FINISHEDBYP)
                        BTCommunication.getInstance().sendData("WIN");
                    else if(game.getState() == Constants.FINISHEDBYIA)
                        BTCommunication.getInstance().sendData("LOSE");
                }
            }

            Handler handler = new Handler();
            handler.postDelayed(
                    () ->
                    {
                        gotoFragGameFinished(true);
                    }, 2500);
        }
    }

    private void drawDrawLackOfMoves(Coord c)
    {
        buttonsBoard[c.x][c.y].setBackgroundColor(colorYellow);

        if(!nameView.equals(SAVEDMOVES))
        {
            if(game.getMode() == PVPONLINE)
            {
                hideCounter();
                if(runnableC != null && handlerC != null)
                    handlerC.removeCallbacks(runnableC);
            }

            if(CP.get().getConn() == ONLINE)
            {
                MySQL.query(this)
                        .setGame(2, game.getStringMoves(), game.getSecondsLeft(),
                                game.getRewinds(), game.getIdGame());
            }

            if(game.getMode() == PVPONLINE)
            {
                if(pG.getKindPlayer() == MASTER)
                    CommunicationInterface.getInstance().respondAsMaster("DRAW");
                else
                    CommunicationInterface.getInstance().respondAsSlave("DRAW");
            }
            else if(game.getMode() == PVPLOCAL)
            {
                if(pG.getKindPlayer() == MASTER)
                    BTCommunication.getInstance().sendData("DRAW");
                else
                    BTCommunication.getInstance().sendData("DRAW");
            }

            Handler handler = new Handler();
            handler.postDelayed(
                    () ->
                    {
                        gotoFragGameFinished(true);
                    }, 2500);
        }
    }

    private void drawDrawWithoutMaterial()
    {
        for(int a=0; a<8; a++)
            for(int b=0; b<8; b++)
                if(game.isOccupied(new Coord(a, b)))
                    buttonsBoard[a][b].setBackgroundColor(colorYellow);

        if(!nameView.equals(SAVEDMOVES))
        {
            if(game.getMode() == PVPONLINE)
            {
                hideCounter();
                if(runnableC != null && handlerC != null)
                    handlerC.removeCallbacks(runnableC);
            }

            if(CP.get().getConn() == ONLINE)
            {
                MySQL.query(this)
                        .setGame(2, game.getStringMoves(), game.getSecondsLeft(),
                                game.getRewinds(), game.getIdGame());
            }

            if(game.getMode() == PVPONLINE)
            {
                if(pG.getKindPlayer() == MASTER)
                    CommunicationInterface.getInstance().respondAsMaster("DRAW");
                else
                    CommunicationInterface.getInstance().respondAsSlave("DRAW");
            }
            else if(game.getMode() == PVPLOCAL)
            {
                if(pG.getKindPlayer() == MASTER)
                    BTCommunication.getInstance().sendData("DRAW");
                else
                    BTCommunication.getInstance().sendData("DRAW");
            }

            Handler handler = new Handler();
            handler.postDelayed(
                    () ->
                    {
                        gotoFragGameFinished(true);
                    }, 2500);
        }
    }

    // WATCH OUT'S OF MOVEMENT

    public void watchaPromotionAndCastling()
    {
        try
        {
            watchaIfPromote();
            watchaIfCastling();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE watchaPromotionAndCastling: " + e.toString());
        }
    }

    public void watchaIfPromote()
    {
        if(game.isPromot())
        {
            updateIcon(game.getLastMove());
        }
    }

    public void watchaIfCastling()
    {
        if(game.getIfRook() != null)
        {
            updateIcon(game.getIfRook());
        }
    }

    // FUNCIONES ESPECIALES (GO BACK, RESUME GAME, FORWARD AND BACKWARD)

    Move moveBack1;
    Move moveBack2;

    public void goBack()
    {
        try
        {
            if(game.getState() != WAITING)
            {
                if(pG.getKindOfLocal() == IS_LOCALIA_T)
                {
                    moveBack1 = null;
                    moveBack2 = null;
                }

                for(int i = 0; i < 2; i++)
                {
                    Map<Integer, Move> res = game.undoneMove(game.getIdWhitePlayer(),  true,0);

                    resetColorButtonsBoard();

                    if(res.size() == 2)
                    {
                        unUpdateIcon(res.get(0));
                        unUpdateIcon(res.get(1));
                    }
                    else
                    {
                        if(res.get(0) != null)
                        {
                            unUpdateIcon(res.get(0));
                        }
                    }

                    watchaPromotionAndCastling();
                    watchaChecksMatesAndDraws();

                    if(game.watchaIfIAHasToRespondFirst())
                    {
                        afterHumanMove();

                        if(pG.getKindOfLocal() == IS_LOCALIA_T)
                        {
                            moveBack1 = game.getLastMove();
                            moveBack2 = null;
                        }
                    }
                    else
                    {
                        if(pG.getKindOfLocal() == IS_LOCALIA_T)
                        {
                            if(moveBack1 == null)
                            {
                                moveBack1 = game.getLastMove();
                            }
                            else
                            {
                                if(moveBack2 == null)
                                {
                                    moveBack2 = game.getLastMove();
                                }
                            }
                        }
                    }

                    if(res.get(0) != null)
                    {
                        if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                        {
                            MySQL.query(this)
                                    .deleteMoves(game.getStringMoves(), game.getRewinds(), game.getIdGame());
                        }
                    }
                }

                if(pG.getKindOfLocal() == IS_LOCALIA_T)
                {
                    backMove = true;
                    game.setState(WAITING);
                    BTCommunication.getInstance().sendData("BACK");
                }
                else
                {
                    game.setState(PLAYING);
                }

                chrono.setBase(SystemClock.elapsedRealtime()-game.getSecondsLeft());
                chrono.start();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE goBack: " + e.toString());
        }
    }

    public void resumeGame()
    {
        try
        {
            for(int i=0; i<game.getArrayListMoves().size(); i++)
            {
                resetColorButtonsBoard();

                if(game.resumeAllSavedMoves(i))
                    i++;

                updateIcon(game.getLastMove());

                watchaPromotionAndCastling();
                watchaChecksMatesAndDraws();

                game.switchTurn();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE resumeGame: " + e.toString());
        }
    }

    public void showMovements(boolean forward)
    {
        try
        {
            if(forward) // true => Forward | false => Backward
            {
                if(numMoves < game.getArrayListMoves().size()-1)
                {
                    showWhoMoved();

                    numMoves++;

                    posMoves++;
                    pager.setCurrentItem(posMoves);

                    resetColorButtonsBoard();

                    if(game.resumeAllSavedMoves(numMoves))
                        numMoves++;

                    updateIcon(game.getLastMove());

                    watchaPromotionAndCastling();
                    watchaChecksMatesAndDraws();

                    game.switchTurn();

                    Log.w(TAG, "===>>> FORWARD #" + numMoves);
                }
            }
            else
            {
                if(numMoves != -1)
                {
                    showWhoMoved();

                    Map<Integer, Move> res = game.undoneMove(game.getIdWhitePlayer(),false, numMoves);

                    resetColorButtonsBoard();

                    if(res.size() == 2)
                    {
                        unUpdateIcon(res.get(0));
                        unUpdateIcon(res.get(1));
                        numMoves--;
                        numMoves--;
                    }
                    else
                    {
                        if(res.get(0) != null)
                        {
                            unUpdateIcon(res.get(0));
                            numMoves--;
                        }
                    }

                    watchaPromotionAndCastling();
                    watchaChecksMatesAndDraws();

                    posMoves--;
                    pager.setCurrentItem(posMoves);

                    Log.w(TAG, "<<<=== BACKWARD #" + numMoves);
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE showMovements: " + e.toString());
        }
    }

    public void showWhoMoved()
    {
        if(game.getBoardTurn())
            iVColorPiece.setImageResource(R.drawable.ic_whitepieces);
        else
            iVColorPiece.setImageResource(R.drawable.ic_blackpieces);

        if((colorPlayer && game.getBoardTurn()) || (!colorPlayer && !game.getBoardTurn()))
            tVPlayerName.setText(CP.get().getNamePlayer());
        else
            if(game.getMode() == PVIA)
                tVPlayerName.setText("Chessy");
            else
                tVPlayerName.setText(oponentName);
    }

    public void notationAllSavedMoves()
    {
        try
        {
            ArrayList<Notation> listNotation = new ArrayList<>();

            boolean turn = true;

            Notation firstNotation = new Notation();
            firstNotation.setPiece(0);
            firstNotation.setXi(' ');
            firstNotation.setYi(' ');
            firstNotation.setAction1(3);
            firstNotation.setPiece2(0);
            firstNotation.setXf(' ');
            firstNotation.setYf(' ');

            listNotation.add(firstNotation);

            for(int i=0; i<game.getArrayListMoves().size(); i++)
            {
                Notation notation = game.getNotationSavedMoves(i, turn);

                if(notation.getAction1() == 2)
                    i++;

                listNotation.add(notation);

                Log.i(TAG, "Mov #" + i + " :" + notation.getPiece() + " turn: " + turn);

                turn = !turn;
            }

            pager = findViewById(R.id.horizontal_cycle);
            Adapter_CardMove adapter = new Adapter_CardMove(listNotation, getBaseContext());
            pager.setAdapter(adapter);
            pager.setCurrentItem(0);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMEINTERFACE notationAllSavedMoves: " + e.toString());
        }
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, "PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, "FATAL ERROR (DATA NULL) VIEW_GAMEINTERFACE getResponseDB");
        }
        else
        {
            if(data.containsKey("DATA"))
            {
                serverErrors = 0;

                try
                {
                    JSONArray jA = null;

                    if(data.get("DATA")!=null)
                        jA = data.get("DATA");

                    switch(purpose)
                    {
                        case "addNewMove": break;
                        case "deleteMoves": break;
                        case "setGame": break;
                        case "UnseenMessages":

                            if(jA != null)
                            {
                                JSONObject jsonObject;

                                for (int i = 0; i < jA.length(); i++)
                                {
                                    try
                                    {
                                        rLength = jA.length();

                                        jsonObject = jA.getJSONObject(i);
                                        idMssg = jsonObject.getInt("message_id");
                                        type = jsonObject.getInt("message_type");

                                        Log.w(TAG, "ID OF MESSAGE CHAT: " + idMssg);

                                        if(Fragment_Chat.msggOp)
                                        {
                                            Log.w(TAG, "MSSG OP IS TRUE");

                                            if(type == 2)
                                            {
                                                if(idMssg - Fragment_Chat.contOp > auxId)
                                                {
                                                    hmNewMssgs++;

                                                    if(hmNewMssgs > 0)
                                                    {
                                                        btnBadgeCounter.setText(hmNewMssgs + "");
                                                        btnBadgeCounter.setVisibility(View.VISIBLE);
                                                    }

                                                    auxId = idMssg;
                                                    Fragment_Chat.msggOp = false;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            Log.w(TAG, "MSSG OP IS FALSE");

                                            if (type == 2)
                                            {
                                                if (idMssg > auxId)
                                                {
                                                    hmNewMssgs++;

                                                    Log.i(TAG, "hm: " + hmNewMssgs);

                                                    if (hmNewMssgs > 0)
                                                    {
                                                        if (Fragment_Chat.msggOp)
                                                        {
                                                            hmNewMssgs--;
                                                            Fragment_Chat.msggOp = false;
                                                        }

                                                        btnBadgeCounter.setText(hmNewMssgs + "");
                                                        btnBadgeCounter.setVisibility(View.VISIBLE);
                                                    }

                                                    auxId = idMssg;
                                                }
                                            }
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        Log.e(TAG, "MAIN ACTIVITY unseenMessages exception: " + e.toString());
                                    }
                                }
                            }
                            break;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception VIEW_GAMEINTERFACE getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                Log.e(TAG, "ERROR connecting to database MAIN ACTIVITY");

                if(serverErrors >= 2)
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if(nameView.equals(SAVEDMOVES))
                    {
                        Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                        transaction.replace(R.id.fragmentPlace, frag);
                        transaction.commit();
                    }
                    else
                    {
                        if(!warned)
                        {
                            warned = true;

                            if(pG.getMode() == PVPONLINE)
                            {
                                Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                                transaction.replace(R.id.fragmentPlace, frag);
                                transaction.commit();

                                gotoFragGameFinished(false);
                            }
                            else
                            {
                                Fragment_NoWifiConnection frag = new Fragment_NoWifiConnection();
                                transaction.replace(R.id.fragmentPlace3, frag);
                                transaction.commit();
                            }
                        }
                    }

                    serverErrors = 0;

                    CP.get().setConn(OFFLINE);
                }
                else
                {
                    serverErrors++;

                    if(CP.get().getConn() == ONLINE)
                    {
                        switch(purpose)
                        {
                            case "addNewMove":
                                MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                                break;

                            case "deleteMoves":
                                MySQL.query(this)
                                        .deleteMoves(game.getStringMoves(), game.getRewinds(), game.getIdGame());
                                break;

                            case "setGame":
                                MySQL.query(this)
                                        .setGame(2, game.getStringMoves(), game.getSecondsLeft(),
                                                game.getRewinds(), game.getIdGame());
                                break;
                        }
                    }
                }
            }
        }
    }

    // COMMUNICATION IMPLEMENTATIONS

    @Override
    public void masterReplays(String data)
    {
        if(pG.getKindPlayer() == SLAVE)
            someoneReplay(data);
    }

    @Override
    public void slaveReplays(String data)
    {
        if(pG.getKindPlayer() == MASTER)
            someoneReplay(data);
    }

    public void someoneReplay(String data)
    {
        Log.w(TAG, "DATA: " + data);

        if(data.equals("YA!") || data.equals("YA?") || data.equals("YA#"))
        {
            if(data.equals("YA#"))
            {
                opponentWithBoard = true;
                game.setState(PLAYING);
                limitTimeMove = 50;
                hideFragPlayerWaiting();
                setCounterListener();
            }
            else
            {
                showFragPlayerWaiting(true);

                if(data.equals("YA!"))
                    opponentWithBoard = false;
                else
                    opponentWithBoard = true;

                game.setState(Constants.REMOTE_WAITING);

                if(pG.getKindOfLocal() == IS_REMOTE_T)
                {
                    if(moderator2 == IN_GAME)
                    {
                        hideFragPlayerWaiting();
                        game.setState(PLAYING);
                        limitTimeMove = 50;
                        setCounterListener();
                    }
                }
                else
                {
                    if(pG.getKindPlayer() == SLAVE)
                    {
                        CommunicationInterface.getInstance().respondAsSlave("YA!");
                    }

                    hideFragPlayerWaiting();
                    game.setState(PLAYING);

                    if(opponentWithBoard)
                    {
                        limitTimeMove = 50;
                        setCounterListener();
                    }
                    else
                    {
                        limitTimeMove = 20;
                        setCounterListener();
                    }
                }
            }
        }
        else if(data.equals("WIN"))
        {
            /*if(pG.getKindOfLocal() == IS_REMOTE_T)
                BTCommunication.getInstance().sendData("OVER");*/

            game.setState(Constants.FINISHEDBYIA);
            gotoFragGameFinished(true);
        }
        else if(data.equals("LOSE"))
        {
            /*if(pG.getKindOfLocal() == IS_REMOTE_T)
                BTCommunication.getInstance().sendData("OVER");*/

            game.setState(Constants.FINISHEDBYP);
            gotoFragGameFinished(true);
        }
        else if(data.equals("DRAW"))
        {
            /*if(pG.getKindOfLocal() == IS_REMOTE_T)
                BTCommunication.getInstance().sendData("OVER");*/

            game.setState(Constants.DRAWMOVBYIA);
            gotoFragGameFinished(true);
        }
        else if(data.equals("ASK"))
        {
            gotoFragInviteAgain(false, false);
        }
        else if(data.equals("AGAIN"))
        {
            if(pG.getKindOfLocal() == IS_REMOTE_T)
            {
                BTCommunication.getInstance().sendData("AGAIN");
                timerMainActivity.cancel();
            }

            gotoViewPiecesColor();
        }
        else if(data.equals("CANCEL"))
        {
            getSupportFragmentManager().beginTransaction().remove(fragInvitation).commit();
        }
        else if(data.equals("FINISH"))
        {
            gotoFragInviteAgain(true, false);
            gotoFragGameFinished(false);
        }
        else
        {
            Log.w(TAG, "--------------------> NEW MOVE GOTTEN: " + data);

            resetColorButtonsBoard();
            game.executeTheStringMove(data);

            game.switchTurn();

            updateIcon(game.getLastMove());

            watchaPromotionAndCastling();
            watchaChecksMatesAndDraws();

            timeMove = 0;
            firstTimeMove = false;

            if(pG.getKindOfLocal() != IS_REMOTE_T)
            {
                setAnimationTurn(game.getBoardTurn());
            }
            else
            {
                Handler hdlr = new Handler();
                hdlr.postDelayed(
                        ()->
                        {
                            movedFrom = APP;

                            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                            {
                                MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                            }

                            firstCoord = new Coord(game.getLastMove().getX1(), game.getLastMove().getY1());
                            secondCoord = new Coord(game.getLastMove().getX2(), game.getLastMove().getY2());

                            if(game.getLastMove().isEat())
                            {
                                moderator = EATEN;
                            }

                            String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                            BTCommunication.getInstance().sendData(move);

                            //game.setState(WAITING);

                            showFragMoveInBoard(MOVEOPPONENT);

                        }, 1000
                );
            }

        }
    }

    // COMMUNICATION BLUETOOTH IMPLEMENTATIONS

    public void timerData()
    {
        final Handler handler = new Handler();
        timerMainActivity.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.i(TAG, "- Run Game");
                        BTCommunication.getInstance().listenForData();
                    }
                });
            }
        },1000,500);
    }

    int moderator;
    int moderator2;

    int movedFrom;

    Coord eatCoord;

    boolean good;

    boolean rooked;

    Move auxRookMove;

    @Override
    public void bluetoothReply(String data)
    {
        if(!data.equals(""))
        {
            Log.w(TAG, "> > > > > > > > > > > > > > > BLUETOOTH DATA: " + data);

            if(moderator2 == PRE_GAME)
            {
                if(pG.getKindOfLocal() == IS_LOCALIA_T)
                {
                    if(data.equals("PLAY"))
                    {
                        if(timerCheckConnBoard != null)
                            timerCheckConnBoard.cancel();

                        resetColorButtonsBoard();
                        showComponents();
                        moderator2 = IN_GAME;
                        movedFrom = UNDEFINED_FROM;
                        rooked = false;
                        BTCommunication.getInstance().sendData("YEAH");

                        game.setState(Constants.PLAYING);

                        if(newOrSaved.equals(NEW))
                        {
                            Handler hdlr = new Handler();
                            hdlr.postDelayed(
                                    ()->
                                    {
                                        if(game.startGame(NEW))
                                        {
                                            updateIcon(game.getLastMove());
                                            watchaPromotionAndCastling();
                                            watchaChecksMatesAndDraws();

                                            //game.setState(Constants.WAITING);

                                            movedFrom = APP;

                                            firstCoord = new Coord(game.getLastMove().getX1(), game.getLastMove().getY1());
                                            secondCoord = new Coord(game.getLastMove().getX2(), game.getLastMove().getY2());

                                            String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                            BTCommunication.getInstance().sendData(move);

                                            chrono.setBase(SystemClock.elapsedRealtime()-game.getSecondsLeft());
                                            chrono.start();

                                            showFragMoveIA = true;

                                            pauseChrono();
                                            showFragMoveInBoard(MOVEIA);
                                        }
                                        else
                                        {
                                            setAnimationTurn(game.getBoardTurn());
                                            resumeChrono();
                                        }

                                    }, 500
                            );
                        }
                        else
                        {
                            setAnimationTurn(game.getBoardTurn());
                            resumeChrono();
                        }
                    }
                    else if(data.charAt(0) == 'I')
                    {
                        if(timerCheckConnBoard != null)
                            timerCheckConnBoard.cancel();

                        hideComponents();
                        resetColorButtonsBoard();
                        tintBadInitialPositions(stringToListPositions(data.substring(1)));
                    }
                }
                else if(pG.getKindOfLocal() == IS_LOCALPVP_T)
                {
                    if(data.equals("PLAY"))
                    {
                        if(timerCheckConnBoard != null)
                            timerCheckConnBoard.cancel();

                        resetColorButtonsBoard();
                        showComponents();
                        moderator2 = IN_GAME;
                        game.setState(PLAYING);
                        movedFrom = UNDEFINED_FROM;
                        rooked = false;

                        if(pG.getKindPlayer() == MASTER)
                            BTCommunication.getInstance().sendData("YEAH");
                    }
                    else if(data.charAt(0) == 'I')
                    {
                        if(timerCheckConnBoard != null)
                            timerCheckConnBoard.cancel();

                        hideComponents();
                        resetColorButtonsBoard();
                        tintBadInitialPositions(stringToListPositions(data.substring(1)));
                    }
                    else if(data.equals("LOSE"))
                    {
                        BTCommunication.getInstance().sendData("OVER");
                        game.setState(Constants.FINISHEDBYP);
                        gotoFragGameFinished(false);
                    }
                }
                else if(pG.getKindOfLocal() == IS_REMOTE_T)
                {
                    if(data.equals("PLAY"))
                    {
                        if(timerCheckConnBoard != null)
                            timerCheckConnBoard.cancel();

                        resetColorButtonsBoard();
                        showComponents();
                        moderator2 = IN_GAME;
                        movedFrom = UNDEFINED_FROM;
                        rooked = false;

                        BTCommunication.getInstance().sendData("YEAH");

                        if(pG.getKindPlayer() == MASTER)
                        {
                            if(pG.getKindPlayer() == MASTER)
                                CommunicationInterface.getInstance().respondAsMaster("YA?");
                            else
                                CommunicationInterface.getInstance().respondAsSlave("YA?");

                            if(game.getState() == Constants.REMOTE_WAITING)
                            {
                                game.setState(PLAYING);
                                hideFragPlayerWaiting();
                                limitTimeMove = 50;
                                setCounterListener();
                            }
                        }
                        else
                        {
                            if(game.getState() != Constants.REMOTE_WAITING)
                            {
                                game.setState(Constants.WAITING);
                            }
                            else
                            {
                                if(pG.getKindPlayer() == MASTER)
                                    CommunicationInterface.getInstance().respondAsMaster("YA?");
                                else
                                    CommunicationInterface.getInstance().respondAsSlave("YA?");

                                game.setState(PLAYING);
                                opponentWithBoard = true;
                                limitTimeMove = 50;
                                hideFragPlayerWaiting();
                                setCounterListener();
                            }
                        }
                    }
                    else if(data.charAt(0) == 'I')
                    {
                        if(timerCheckConnBoard != null)
                            timerCheckConnBoard.cancel();

                        hideComponents();
                        resetColorButtonsBoard();
                        tintBadInitialPositions(stringToListPositions(data.substring(1)));
                    }
                }
            }
            else
            {
                //--------------------------------------------------------------------------------------------------------------
                if(pG.getKindOfLocal() == IS_LOCALPVP_T)
                {
                    if(movedFrom == UNDEFINED_FROM)
                        movedFrom = BOARD;

                    if(timerCheckConnBoard != null)
                        timerCheckConnBoard.cancel();

                    Log.e(TAG, "---------------------------- TO IS_LOCALPVP_T");
                    Log.e(TAG, "---------------------------- MOVED FROM: " + (movedFrom==APP?"APP":"BOARD"));
                    Log.e(TAG, "---------------------------- COLOR PLAYER: " + (colorPlayer?"BLANCAS":"NEGRAS"));
                    Log.e(TAG, "---------------------------- BOARD TURN: " + (game.getBoardTurn()?"BLANCAS TURNO":"NEGRAS TURNO"));

                    if((movedFrom == BOARD && colorPlayer == game.getBoardTurn()) ||
                            (movedFrom == APP && colorPlayer != game.getBoardTurn()) || data.charAt(0) == '@')
                    {
                        Log.e(TAG, "----------------------------");
                        Log.e(TAG, "---------------------------- SI ME CORRESPONDE OBTENER LA DATA");

                        if (data.charAt(0) == 'M')
                        {
                            if(game.getState() != Constants.WAITING)
                                showFragLoadingBoard();

                            hidePause();

                            game.setState(Constants.WAITING);

                            Log.e(TAG, "---------------------------- M");

                            Coord newCoord = new Coord(data.charAt(1) - '0', data.charAt(2) - '0');

                            if(moderator == EATEN)
                            {
                                Log.e(TAG, "---------------------------- EATEN");

                                if(movedFrom == APP)
                                {
                                    Log.w(TAG, "getEatCoord().x: " + game.getLastMove().getEatCoord().x +
                                            "  getEatCoord().y: " + game.getLastMove().getEatCoord().y);
                                    Log.w(TAG, "newCoord.x: " + newCoord.x + "  newCoord.y: " + newCoord.y);

                                    if((game.getLastMove().getEatCoord().x == newCoord.x) ||
                                            (game.getLastMove().getEatCoord().y == newCoord.y))
                                    {
                                        Log.e(TAG, "GOOD: EAT COORD IS CORRECT | MOVED FROM APP");
                                        eatCoord = newCoord;
                                        moderator = FIRST_MOVE;
                                        BTCommunication.getInstance().sendData("eaten");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: EAT COORD IS  NOT CORRECT | MOVED FROM APP");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                                else
                                {
                                    if(game.getSpot(newCoord) != null &&
                                            game.isSpotWhite(newCoord) != game.getBoardTurn())
                                    {
                                        Log.e(TAG, "GOOD: NEW EAT COORD MAY BE CORRECT | MOVED FROM BOARD");
                                        eatCoord = newCoord;
                                        moderator = FIRST_MOVE;
                                        BTCommunication.getInstance().sendData("eaten");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: EAT COORD IS NOT CORRECT | MOVED FROM BOARD");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                            }
                            else if (moderator == FIRST_MOVE || moderator == NOTHING)
                            {
                                Log.e(TAG, "---------------------------- FIRST MOVE");

                                if(movedFrom == APP)
                                {
                                    Log.w(TAG, "firstCoord.x: " + firstCoord.x + "  firstCoord.y: " + firstCoord.y);
                                    Log.w(TAG, "newCoord.x: " + newCoord.x + "  newCoord.y: " + newCoord.y);

                                    if((!rooked && firstCoord.x == newCoord.x && firstCoord.y == newCoord.y) ||
                                            (rooked && game.getLastMove().getRook().getX1() == newCoord.x && game.getLastMove().getRook().getY1() == newCoord.y))
                                    {
                                        Log.e(TAG, "GOOD: FIRST CORRECT | MOVED FROM APP");
                                        moderator = SECOND_MOVE;
                                        BTCommunication.getInstance().sendData("first");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM APP");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                                else
                                {
                                    if(game.getSpot(newCoord) != null && colorPlayer == game.getBoardTurn() &&
                                            game.isSpotWhite(newCoord) == game.getBoardTurn())
                                    {
                                        if(game.getAlgorithm().getRealMoves(newCoord) != null ||
                                                game.getAlgorithm().getRealAttacks(newCoord) != null)
                                        {
                                            Log.e(TAG, "GOOD: FIRST CORRECT | MOVED FROM BOARD");
                                            firstCoord = newCoord;
                                            moderator = SECOND_MOVE;
                                            BTCommunication.getInstance().sendData("first");
                                        }
                                        else
                                        {
                                            if(!game.canMove(game.getBoardTurn()))
                                            {
                                                Log.e(TAG, "*****   DRAW OF MOVES   *****");

                                                game.moveHuman(null);
                                                game.switchTurn();

                                                if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                                                {
                                                    MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                                                }

                                                game.setState(Constants.DRAWMOVBYIA);

                                                BTCommunication.getInstance().sendData("finished");
                                            }
                                            else
                                            {
                                                Log.e(TAG, "WATAFACK IS THIS");
                                                moderator = ILEGAL_MOVE;
                                                BTCommunication.getInstance().sendData("bad");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        if(game.getSpot(newCoord) != null &&
                                                game.isSpotWhite(newCoord) != game.getBoardTurn() && eatCoord == null)
                                        {
                                            if(rooked)
                                            {
                                                Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                                moderator = ILEGAL_MOVE;
                                                BTCommunication.getInstance().sendData("bad");
                                            }
                                            else
                                            {
                                                Log.e(TAG, "GOOD: FIRST COORD MAY BE EAT COORD | MOVED FROM BOARD");
                                                eatCoord = newCoord;
                                                moderator = FIRST_MOVE;
                                                BTCommunication.getInstance().sendData("eaten");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                            moderator = ILEGAL_MOVE;
                                            BTCommunication.getInstance().sendData("bad");
                                        }
                                    }
                                }
                            }
                            else if (moderator == SECOND_MOVE)
                            {
                                Log.e(TAG, "---------------------------- SECOND MOVE");

                                if(movedFrom == APP)
                                {
                                    if((!rooked && secondCoord.x == newCoord.x && secondCoord.y == newCoord.y) ||
                                        (rooked && game.getLastMove().getRook().getX2() == newCoord.x && game.getLastMove().getRook().getY2() == newCoord.y))
                                    {
                                        Log.e(TAG, "-------------------= MOVEMENT FROM APP =-------------------");

                                        moderator = FIRST_MOVE;

                                        if(game.getLastMove().getRook() != null && !rooked)
                                        {
                                            Log.e(TAG, "GOOD: ROOK DETECTED | MOVED FROM APP");
                                            BTCommunication.getInstance().sendData("rooked");
                                            moderator = FIRST_MOVE;
                                        }
                                        else
                                        {
                                            movedFrom = UNDEFINED_FROM;
                                            game.setState(PLAYING);
                                            eatCoord = null;
                                            rooked = false;

                                            BTCommunication.getInstance().sendData("second");

                                            resetColorButtonsBoard();
                                            watchaChecksMatesAndDraws();
                                            hideFragMoveInBoard();

                                            Handler hdlr = new Handler();
                                            hdlr.postDelayed(
                                                    () ->
                                                    {
                                                        Log.e(TAG, "        (   APP ----> BOARD   )");

                                                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                                        BTCommunication.getInstance().sendData(move);

                                                        setAnimationTurn(game.getBoardTurn());

                                                        hideFragLoadingBoard();

                                                    }, 1000);
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: SECOND COORD INCORRECT | MOVED FROM APP");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                                else
                                {
                                    if(game.watchaIfMoveIsLegal(firstCoord, newCoord))
                                    {
                                        secondCoord = newCoord;

                                        if(eatCoord != null &&
                                                (secondCoord.x != eatCoord.x || secondCoord.y != eatCoord.y))
                                        {
                                            Log.e(TAG, "ERROR: EAT COORD INCORRECT | MOVED FROM BOARD");
                                            moderator = ILEGAL_MOVE;
                                            BTCommunication.getInstance().sendData("bad");
                                        }
                                        else
                                        {
                                            Log.e(TAG, "-------------------= MOVEMENT FROM BOARD =-------------------");

                                            moderator = FIRST_MOVE;

                                            if(game.willBeRook(game.getLastMove()) && !rooked)
                                            {
                                                Log.e(TAG, "GOOD: ROOK DETECTED | MOVED FROM BOARD");
                                                BTCommunication.getInstance().sendData("rooked");
                                                auxRookMove = game.getLastMove();
                                            }
                                            else
                                            {
                                                BTCommunication.getInstance().sendData("second");

                                                resetColorButtonsBoard();

                                                Handler hdlr = new Handler();
                                                hdlr.postDelayed(
                                                        ()->
                                                        {
                                                            if(rooked)
                                                                game.setLastMove(auxRookMove);

                                                            humanMove();

                                                            movedFrom = UNDEFINED_FROM;
                                                            moderator = FIRST_MOVE;
                                                            game.setState(PLAYING);
                                                            eatCoord = null;
                                                            rooked = false;

                                                            setAnimationTurn(game.getBoardTurn());

                                                            hideFragLoadingBoard();

                                                        }, 1000
                                                );
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: SECOND COORD INCORRECT | MOVED FROM BOARD");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(data.charAt(0) == 'B')
                            {
                                Log.e(TAG, "-------------------= CHECK BAD POSITIONS =-------------------");

                                refreshTintBadPositions(stringToListPositions(data.substring(1)));

                                if(good)
                                {
                                    game.setState(PLAYING);
                                    moderator = FIRST_MOVE;
                                    eatCoord = null;
                                    rooked = false;

                                    showComponents();

                                    if(movedFrom == APP)
                                    {
                                        movedFrom = UNDEFINED_FROM;

                                        Log.e(TAG, "-------------------= MOVEMENT FIXED FROM APP =-------------------");

                                        BTCommunication.getInstance().sendData("fixedApp");

                                        resetColorButtonsBoard();
                                        hideFragMoveInBoard();

                                        Handler hdlr = new Handler();
                                        hdlr.postDelayed(
                                                () ->
                                                {
                                                    Log.e(TAG, "        (   APP ----> BOARD   )");

                                                    String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                                    BTCommunication.getInstance().sendData(move);

                                                    setAnimationTurn(game.getBoardTurn());

                                                    hideFragLoadingBoard();

                                                }, 1000);
                                    }
                                    else
                                    {
                                        movedFrom = UNDEFINED_FROM;
                                        Log.e(TAG, "-------------------= MOVEMENT FIXED FROM BOARD =-------------------");
                                        BTCommunication.getInstance().sendData("fixedBoard");
                                        hideFragLoadingBoard();
                                    }
                                }
                                else
                                {
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else if(data.charAt(0) == 'N')
                            {
                                Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                                showComponents();

                                movedFrom = UNDEFINED_FROM;
                                moderator = FIRST_MOVE;
                                game.setState(PLAYING);
                                eatCoord = null;
                                rooked = false;

                                resetColorButtonsBoard();
                                watchaChecksMatesAndDraws();
                                hideFragMoveInBoard();

                                Handler hdlr = new Handler();
                                hdlr.postDelayed(
                                        () ->
                                        {
                                            Log.e(TAG, "        (   APP ----> BOARD   )");

                                            String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                            BTCommunication.getInstance().sendData(move);

                                            setAnimationTurn(game.getBoardTurn());

                                            hideFragLoadingBoard();

                                        }, 1000);
                            }
                            else if(data.charAt(0) == 'X')
                            {
                                if(game.getState() != Constants.WAITING)
                                    showFragLoadingBoard();

                                game.setState(Constants.WAITING);

                                hidePause();

                                Log.e(TAG, "---------------------------- X");

                                Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                                refreshTintMovingBadPositions(stringToListPositions(data.substring(1)), firstCoord, secondCoord);

                                if(good)
                                {
                                    Log.e(TAG, "-------------------= ALL GOOD DURING MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("fixedBoard");
                                }
                                else
                                {
                                    Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else if(data.charAt(0) == 'Y')
                            {
                                Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS FINAL DURING MOVING SYSTEM =-------------------");

                                refreshTintBadPositions(stringToListPositions(data.substring(1)));

                                if(good)
                                {
                                    Log.e(TAG, "-------------------= ALL GOOD DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("fixedSystem");
                                }
                                else
                                {
                                    Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else if(data.charAt(0) == '@')
                            {
                                Log.w(TAG, "-----------------------> NEW MOVE GOTTEN: " + data);

                                resetColorButtonsBoard();
                                game.executeTheStringMove(data.substring(1));

                                game.switchTurn();

                                updateIcon(game.getLastMove());

                                watchaPromotionAndCastling();
                                watchaChecksMatesAndDraws();

                                setAnimationTurn(game.getBoardTurn());

                                timeMove = 0;
                                firstTimeMove = false;

                                movedFrom = UNDEFINED_FROM;

                                BTCommunication.getInstance().sendData("gotit");
                            }
                            else if(data.equals('E'))
                            {
                                BTCommunication.getInstance().sendData("again");
                            }
                            else
                            {
                                checkData(data);
                            }
                        }

                    }
                    else
                    {
                        checkData(data);
                    }
                }
                //--------------------------------------------------------------------------------------------------------------
                else if(pG.getKindOfLocal() == IS_LOCALPVP)
                {
                    if(data.charAt(0) == '@')
                    {
                        Log.w(TAG, "-----------------------> NEW MOVE GOTTEN: " + data);

                        resetColorButtonsBoard();
                        game.executeTheStringMove(data.substring(1));

                        game.switchTurn();

                        updateIcon(game.getLastMove());

                        watchaPromotionAndCastling();
                        watchaChecksMatesAndDraws();

                        setAnimationTurn(game.getBoardTurn());

                        timeMove = 0;
                        firstTimeMove = false;

                        BTCommunication.getInstance().sendData("gotit");
                    }
                    else
                    {
                        checkData(data);
                    }
                }
                //--------------------------------------------------------------------------------------------------------------
                else if(pG.getKindOfLocal() == IS_LOCALIA_T)
                {
                    Log.e(TAG, "---------------------------- TO IS_LOCALIA_T");
                    Log.e(TAG, "---------------------------- MOVED FROM: " + (movedFrom==APP?"APP":"BOARD"));

                    if(movedFrom == UNDEFINED_FROM)
                        movedFrom = BOARD;

                    if(timerCheckConnBoard != null)
                        timerCheckConnBoard.cancel();

                    if (data.charAt(0) == 'M')
                    {
                        if(colorPlayer == game.getBoardTurn() && showFragMoveIA)
                        {
                            showFragMoveIA = false;
                            showFragLoadingBoard();
                        }
                        else
                        {
                            if(game.getState() != Constants.WAITING)
                                showFragLoadingBoard();
                        }

                        hidePause();

                        game.setState(Constants.WAITING);

                        Log.e(TAG, "---------------------------- M");

                        Coord newCoord = new Coord(data.charAt(1) - '0', data.charAt(2) - '0');

                        if(moderator == EATEN)
                        {
                            Log.e(TAG, "---------------------------- EATEN");

                            if(movedFrom == APP)
                            {
                                Log.w(TAG, "getEatCoord().x: " + game.getLastMove().getEatCoord().x +
                                        "  getEatCoord().y: " + game.getLastMove().getEatCoord().y);
                                Log.w(TAG, "newCoord.x: " + newCoord.x + "  newCoord.y: " + newCoord.y);

                                if((game.getLastMove().getEatCoord().x == newCoord.x) &&
                                        (game.getLastMove().getEatCoord().y == newCoord.y))
                                {
                                    Log.e(TAG, "GOOD: EAT COORD IS CORRECT | MOVED FROM APP");
                                    eatCoord = newCoord;
                                    moderator = FIRST_MOVE;
                                    BTCommunication.getInstance().sendData("eaten");
                                }
                                else
                                {
                                    Log.e(TAG, "ERROR: EAT COORD IS INCORRECT | MOVED FROM APP");
                                    moderator = ILEGAL_MOVE;
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else
                            {
                                if(game.getSpot(newCoord) != null &&
                                        game.isSpotWhite(newCoord) != game.getBoardTurn())
                                {
                                    Log.e(TAG, "I DUNNO WHEN ENTRIES HERE");
                                    eatCoord = newCoord;
                                    moderator = FIRST_MOVE;
                                    BTCommunication.getInstance().sendData("eaten");
                                }
                                else
                                {
                                    Log.e(TAG, "I DUNNO WHEN ENTRIES HERE x2");
                                    moderator = ILEGAL_MOVE;
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                        }
                        else if (moderator == FIRST_MOVE || moderator == NOTHING)
                        {
                            Log.e(TAG, "---------------------------- FIRST MOVE");

                            if(movedFrom == APP)
                            {
                                Log.w(TAG, "firstCoord.x: " + firstCoord.x + "  firstCoord.y: " + firstCoord.y);
                                Log.w(TAG, "newCoord.x: " + newCoord.x + "  newCoord.y: " + newCoord.y);

                                if((!rooked && firstCoord.x == newCoord.x && firstCoord.y == newCoord.y) ||
                                        (rooked && game.getLastMove().getRook().getX1() == newCoord.x && game.getLastMove().getRook().getY1() == newCoord.y))
                                {
                                    Log.e(TAG, "GOOD: FIRST CORRECT | MOVED FROM APP");
                                    moderator = SECOND_MOVE;
                                    BTCommunication.getInstance().sendData("first");
                                }
                                else
                                {
                                    Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM APP");
                                    moderator = ILEGAL_MOVE;
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else
                            {
                                if(game.getSpot(newCoord) != null && colorPlayer == game.getBoardTurn() &&
                                        game.isSpotWhite(newCoord) == game.getBoardTurn())
                                {
                                    if(game.getAlgorithm().getRealMoves(newCoord) != null ||
                                            game.getAlgorithm().getRealAttacks(newCoord) != null)
                                    {
                                        Log.e(TAG, "GOOD: FIRST CORRECT | MOVED FROM BOARD");
                                        firstCoord = newCoord;
                                        moderator = SECOND_MOVE;
                                        if(pG.getKindPlayer() == MASTER)
                                            BTCommunication.getInstance().sendData("first");
                                    }
                                    else
                                    {
                                        if(!game.canMove(game.getBoardTurn()))
                                        {
                                            Log.e(TAG, "*****   DRAW OF MOVES   *****");

                                            game.moveHuman(null);
                                            game.switchTurn();

                                            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                                            {
                                                MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                                            }

                                            game.setState(Constants.DRAWMOVBYIA);

                                            BTCommunication.getInstance().sendData("finished");
                                        }
                                        else
                                        {
                                            Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                            moderator = ILEGAL_MOVE;
                                            BTCommunication.getInstance().sendData("bad");
                                        }
                                    }
                                }
                                else
                                {
                                    if(game.getSpot(newCoord) != null &&
                                            game.isSpotWhite(newCoord) != game.getBoardTurn() && eatCoord == null)
                                    {
                                        if(rooked)
                                        {
                                            Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                            moderator = ILEGAL_MOVE;
                                            BTCommunication.getInstance().sendData("bad");
                                        }
                                        else
                                        {
                                            Log.e(TAG, "GOOD: FIRST COORD MAY BE EAT COORD | MOVED FROM BOARD");
                                            eatCoord = newCoord;
                                            moderator = FIRST_MOVE;
                                            BTCommunication.getInstance().sendData("eaten");
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                            }
                        }
                        else if (moderator == SECOND_MOVE)
                        {
                            Log.e(TAG, "---------------------------- SECOND MOVE");

                            if(movedFrom == APP)
                            {
                                if((!rooked && secondCoord.x == newCoord.x && secondCoord.y == newCoord.y) ||
                                        (rooked && game.getLastMove().getRook().getX2() == newCoord.x && game.getLastMove().getRook().getY2() == newCoord.y))
                                {
                                    Log.e(TAG, "-------------------= MOVEMENT FROM APP =-------------------");

                                    moderator = FIRST_MOVE;

                                    if(game.getLastMove().getRook() != null && !rooked)
                                    {
                                        Log.e(TAG, "GOOD: ROOK DETECTED | MOVED FROM APP");
                                        BTCommunication.getInstance().sendData("rooked");
                                    }
                                    else
                                    {
                                        BTCommunication.getInstance().sendData("second");

                                        movedFrom = UNDEFINED_FROM;
                                        game.setState(PLAYING);
                                        eatCoord = null;
                                        rooked = false;

                                        resetColorButtonsBoard();
                                        watchaChecksMatesAndDraws();
                                        setAnimationTurn(game.getBoardTurn());

                                        hideFragLoadingBoard();
                                        hideFragMoveInBoard();

                                        if(colorPlayer == game.getBoardTurn())
                                        {
                                            resumeChrono();
                                        }
                                        else
                                        {
                                            Handler hdlr = new Handler();
                                            hdlr.postDelayed(
                                                    () ->
                                                    {
                                                        afterHumanMove();

                                                    }, 200);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "ERROR: SECOND INCORRECT | MOVED FROM APP");
                                    moderator = ILEGAL_MOVE;
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else
                            {
                                if(game.watchaIfMoveIsLegal(firstCoord, newCoord))
                                {
                                    secondCoord = newCoord;

                                    if(eatCoord != null &&
                                            (secondCoord.x != eatCoord.x || secondCoord.y != eatCoord.y))
                                    {
                                        Log.e(TAG, "ERROR: SECOND COORD IS NOT EAT COORD | MOVED FROM BOARD");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "-------------------= MOVEMENT FROM BOARD =-------------------");

                                        moderator = FIRST_MOVE;

                                        if(game.willBeRook(game.getLastMove()) && !rooked)
                                        {
                                            Log.e(TAG, "GOOD: ROOK DETECTED | MOVED FROM BOARD");
                                            BTCommunication.getInstance().sendData("rooked");
                                            auxRookMove = game.getLastMove();
                                        }
                                        else
                                        {
                                            if(rooked)
                                                game.setLastMove(auxRookMove);

                                            resetColorButtonsBoard();
                                            humanMove();

                                            movedFrom = UNDEFINED_FROM;
                                            game.setState(PLAYING);
                                            eatCoord = null;
                                            rooked = false;

                                            if(pG.getKindPlayer() == MASTER)
                                                BTCommunication.getInstance().sendData("second");

                                            setAnimationTurn(game.getBoardTurn());

                                            hideFragLoadingBoard();

                                            if(colorPlayer == game.getBoardTurn())
                                            {
                                                resumeChrono();
                                            }
                                            else
                                            {
                                                Handler hdlr = new Handler();
                                                hdlr.postDelayed(
                                                        () ->
                                                        {
                                                            resetColorButtonsBoard();
                                                            afterHumanMove();

                                                        }, 200);
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "ERROR: SECOND INCORRECT | MOVED FROM BOARD");
                                    moderator = ILEGAL_MOVE;
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                        }
                    }
                    else
                    {
                        if(data.charAt(0) == 'B')
                        {
                            if(backMove)
                                refreshTintBackPositions(stringToListPositions(data.substring(1)));
                            else
                                refreshTintBadPositions(stringToListPositions(data.substring(1)));

                            if(good)
                            {
                                showComponents();

                                game.setState(PLAYING);
                                moderator = FIRST_MOVE;
                                eatCoord = null;
                                rooked = false;
                                backMove = false;

                                if(movedFrom == APP)
                                {
                                    movedFrom = UNDEFINED_FROM;

                                    Log.e(TAG, "-------------------= MOVEMENT FIXED FROM APP =-------------------");

                                    BTCommunication.getInstance().sendData("fixedApp");

                                    //humanMove();
                                    setAnimationTurn(game.getBoardTurn());
                                    hideFragLoadingBoard();
                                    hideFragMoveInBoard();

                                    if(colorPlayer == game.getBoardTurn())
                                    {
                                        resumeChrono();
                                    }
                                    else
                                    {
                                        Handler hdlr = new Handler();
                                        hdlr.postDelayed(
                                                () ->
                                                {
                                                    resetColorButtonsBoard();
                                                    afterHumanMove();

                                                }, 1000);
                                    }
                                }
                                else
                                {
                                    movedFrom = UNDEFINED_FROM;
                                    Log.e(TAG, "-------------------= MOVEMENT FIXED FROM BOARD =-------------------");
                                    BTCommunication.getInstance().sendData("fixedBoard");
                                    hideFragLoadingBoard();
                                }
                            }
                            else
                            {
                                BTCommunication.getInstance().sendData("bad");
                            }
                        }
                        else if(data.charAt(0) == 'N')
                        {
                            Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                            showComponents();

                            movedFrom = UNDEFINED_FROM;
                            moderator = FIRST_MOVE;
                            game.setState(PLAYING);
                            eatCoord = null;
                            rooked = false;

                            resetColorButtonsBoard();
                            watchaChecksMatesAndDraws();
                            setAnimationTurn(game.getBoardTurn());
                            hideFragLoadingBoard();
                            hideFragMoveInBoard();

                            if(colorPlayer == game.getBoardTurn())
                            {
                                resumeChrono();
                            }
                            else
                            {
                                Handler hdlr = new Handler();
                                hdlr.postDelayed(
                                        () ->
                                        {
                                            afterHumanMove();

                                        }, 200);
                            }
                        }
                        else if(data.charAt(0) == 'X')
                        {
                            if(colorPlayer == game.getBoardTurn() && showFragMoveIA)
                            {
                                showFragMoveIA = false;
                                showFragLoadingBoard();
                            }
                            else
                            {
                                if(game.getState() != Constants.WAITING)
                                    showFragLoadingBoard();
                            }

                            game.setState(Constants.WAITING);

                            hidePause();

                            Log.e(TAG, "---------------------------- X");

                            Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                            refreshTintMovingBadPositions(stringToListPositions(data.substring(1)), firstCoord, secondCoord);

                            if(good)
                            {
                                Log.e(TAG, "-------------------= ALL GOOD DURING MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("fixedBoard");
                            }
                            else
                            {
                                Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("bad");
                            }
                        }
                        else if(data.charAt(0) == 'Y')
                        {
                            Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS FINAL DURING MOVING SYSTEM =-------------------");

                            refreshTintBadPositions(stringToListPositions(data.substring(1)));

                            if(good)
                            {
                                Log.e(TAG, "-------------------= ALL GOOD DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("fixedSystem");
                            }
                            else
                            {
                                Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("bad");
                            }
                        }
                        else if(data.charAt(0) == 'E')
                        {
                            BTCommunication.getInstance().sendData("again");
                        }
                        else if(data.equals("OKEND"))
                        {
                            Log.e(TAG, "OK, GOT IT, GAME FINISHED");
                            BTCommunication.getInstance().sendData("YEAH");
                        }
                    }
                }
                //--------------------------------------------------------------------------------------------------------------
                if(pG.getKindOfLocal() == IS_REMOTE_T)
                {
                    if(movedFrom == UNDEFINED_FROM)
                        movedFrom = BOARD;

                    if(timerCheckConnBoard != null)
                        timerCheckConnBoard.cancel();

                    Log.e(TAG, "---------------------------- TO IS_REMOTE_T");
                    Log.e(TAG, "---------------------------- MOVED FROM: " + (movedFrom==APP?"APP":"BOARD"));

                    if((movedFrom == BOARD && colorPlayer == game.getBoardTurn()) ||
                            (movedFrom == APP /*&& colorPlayer != game.getBoardTurn()*/))
                    {
                        Log.e(TAG, "----------------------------");
                        Log.e(TAG, "---------------------------- SI ME CORRESPONDE OBTENER LA DATA");

                        if (data.charAt(0) == 'M' || data.charAt(0) == 'L')
                        {
                            if(game.getState() != Constants.WAITING)
                                showFragLoadingBoard();

                            hidePause();

                            game.setState(Constants.WAITING);

                            Log.e(TAG, "---------------------------- M");

                            Coord newCoord = new Coord(data.charAt(1) - '0', data.charAt(2) - '0');

                            if(moderator == EATEN)
                            {
                                Log.e(TAG, "---------------------------- EATEN");

                                if(movedFrom == APP)
                                {
                                    Log.w(TAG, "getEatCoord().x: " + game.getLastMove().getEatCoord().x +
                                            "  getEatCoord().y: " + game.getLastMove().getEatCoord().y);
                                    Log.w(TAG, "newCoord.x: " + newCoord.x + "  newCoord.y: " + newCoord.y);

                                    if((game.getLastMove().getEatCoord().x == newCoord.x) ||
                                            (game.getLastMove().getEatCoord().y == newCoord.y))
                                    {
                                        Log.e(TAG, "GOOD: EAT COORD IS CORRECT | MOVED FROM APP");
                                        eatCoord = newCoord;
                                        moderator = FIRST_MOVE;
                                        BTCommunication.getInstance().sendData("eaten");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: EAT COORD IS  NOT CORRECT | MOVED FROM APP");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                                else
                                {
                                    if(game.getSpot(newCoord) != null &&
                                            game.isSpotWhite(newCoord) != game.getBoardTurn())
                                    {
                                        Log.e(TAG, "GOOD: NEW EAT COORD MAY BE CORRECT | MOVED FROM BOARD");
                                        eatCoord = newCoord;
                                        moderator = FIRST_MOVE;
                                        BTCommunication.getInstance().sendData("eaten");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: EAT COORD IS NOT CORRECT | MOVED FROM BOARD");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                            }
                            else if (moderator == FIRST_MOVE || moderator == NOTHING)
                            {
                                Log.e(TAG, "---------------------------- FIRST MOVE");

                                if(movedFrom == APP)
                                {
                                    Log.w(TAG, "firstCoord.x: " + firstCoord.x + "  firstCoord.y: " + firstCoord.y);
                                    Log.w(TAG, "newCoord.x: " + newCoord.x + "  newCoord.y: " + newCoord.y);

                                    if((!rooked && firstCoord.x == newCoord.x && firstCoord.y == newCoord.y) ||
                                            (rooked && game.getLastMove().getRook().getX1() == newCoord.x && game.getLastMove().getRook().getY1() == newCoord.y))
                                    {
                                        Log.e(TAG, "GOOD: FIRST CORRECT | MOVED FROM APP");
                                        moderator = SECOND_MOVE;
                                        BTCommunication.getInstance().sendData("first");
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM APP");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                                else
                                {
                                    if(game.getSpot(newCoord) != null && colorPlayer == game.getBoardTurn() &&
                                            game.isSpotWhite(newCoord) == game.getBoardTurn())
                                    {
                                        if(game.getAlgorithm().getRealMoves(newCoord) != null ||
                                                game.getAlgorithm().getRealAttacks(newCoord) != null)
                                        {
                                            Log.e(TAG, "GOOD: FIRST CORRECT | MOVED FROM BOARD");
                                            firstCoord = newCoord;
                                            moderator = SECOND_MOVE;
                                            BTCommunication.getInstance().sendData("first");
                                        }
                                        else
                                        {
                                            if(!game.canMove(game.getBoardTurn()))
                                            {
                                                Log.e(TAG, "*****   DRAW OF MOVES   *****");

                                                game.moveHuman(null);
                                                game.switchTurn();

                                                if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
                                                {
                                                    MySQL.query(this).addNewMove(game.getStringMoves(), game.getSecondsLeft(), game.getIdGame());
                                                }

                                                game.setState(Constants.DRAWMOVBYIA);

                                                BTCommunication.getInstance().sendData("finished");
                                            }
                                            else
                                            {
                                                Log.e(TAG, "WATAFACK IS THIS");
                                                moderator = ILEGAL_MOVE;
                                                BTCommunication.getInstance().sendData("bad");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        if(game.getSpot(newCoord) != null &&
                                                game.isSpotWhite(newCoord) != game.getBoardTurn() && eatCoord == null)
                                        {
                                            if(rooked)
                                            {
                                                Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                                moderator = ILEGAL_MOVE;
                                                BTCommunication.getInstance().sendData("bad");
                                            }
                                            else
                                            {
                                                Log.e(TAG, "GOOD: FIRST COORD MAY BE EAT COORD | MOVED FROM BOARD");
                                                eatCoord = newCoord;
                                                moderator = FIRST_MOVE;
                                                BTCommunication.getInstance().sendData("eaten");
                                            }
                                        }
                                        else
                                        {
                                            Log.e(TAG, "ERROR: FIRST INCORRECT | MOVED FROM BOARD");
                                            moderator = ILEGAL_MOVE;
                                            BTCommunication.getInstance().sendData("bad");
                                        }
                                    }
                                }
                            }
                            else if (moderator == SECOND_MOVE)
                            {
                                Log.e(TAG, "---------------------------- SECOND MOVE");

                                if(movedFrom == APP)
                                {
                                    if((!rooked && secondCoord.x == newCoord.x && secondCoord.y == newCoord.y) ||
                                            (rooked && game.getLastMove().getX2() == newCoord.x && game.getLastMove().getY2() == newCoord.y))
                                    {
                                        Log.e(TAG, "-------------------= MOVEMENT FROM APP =-------------------");

                                        moderator = FIRST_MOVE;

                                        if(game.getLastMove().getRook() != null && !rooked)
                                        {
                                            Log.e(TAG, "GOOD: ROOK DETECTED | MOVED FROM APP");
                                            BTCommunication.getInstance().sendData("rooked");
                                        }
                                        else
                                        {
                                            movedFrom = UNDEFINED_FROM;
                                            game.setState(PLAYING);
                                            eatCoord = null;
                                            rooked = false;

                                            BTCommunication.getInstance().sendData("second");

                                            resetColorButtonsBoard();
                                            watchaChecksMatesAndDraws();
                                            hideFragLoadingBoard();
                                            hideFragMoveInBoard();

                                            if(colorPlayer != game.getBoardTurn())
                                            {
                                                Log.e(TAG, "        (   APP ----> BOARD   )");

                                                if(pG.getKindPlayer() == MASTER)
                                                    CommunicationInterface.getInstance().respondAsMaster("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                                                else
                                                    CommunicationInterface.getInstance().respondAsSlave("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));

                                                String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                                BTCommunication.getInstance().sendData(move);
                                            }
                                            else
                                            {
                                                timeMove = 0;
                                                firstTimeMove = false;
                                                tVCounter.setText("");
                                                tVCounter.setVisibility(View.INVISIBLE);
                                                animCounter.cancel();

                                                if(pG.getKindOfLocal() == IS_REMOTE_T)
                                                {
                                                    limitTimeMove = 70;
                                                }
                                                else
                                                {
                                                    if(opponentWithBoard)
                                                    {
                                                        limitTimeMove = 70;
                                                    }
                                                    else
                                                    {
                                                        limitTimeMove = 30;
                                                    }
                                                }
                                            }

                                            setAnimationTurn(game.getBoardTurn());

                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: SECOND COORD INCORRECT | MOVED FROM APP");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                                else
                                {
                                    if(game.watchaIfMoveIsLegal(firstCoord, newCoord))
                                    {
                                        secondCoord = newCoord;

                                        if(eatCoord != null &&
                                                (secondCoord.x != eatCoord.x || secondCoord.y != eatCoord.y))
                                        {
                                            Log.e(TAG, "ERROR: EAT COORD INCORRECT | MOVED FROM BOARD");
                                            moderator = ILEGAL_MOVE;
                                            BTCommunication.getInstance().sendData("bad");
                                        }
                                        else
                                        {
                                            Log.e(TAG, "-------------------= MOVEMENT FROM BOARD =-------------------");

                                            moderator = FIRST_MOVE;

                                            if(game.getLastMove().getRook() != null && !rooked)
                                            {
                                                Log.e(TAG, "GOOD: ROOK DETECTED | MOVED FROM BOARD");
                                                BTCommunication.getInstance().sendData("rooked");
                                                auxRookMove = game.getLastMove();
                                            }
                                            else
                                            {
                                                BTCommunication.getInstance().sendData("second");

                                                if(rooked)
                                                    game.setLastMove(auxRookMove);

                                                resetColorButtonsBoard();

                                                Handler hdlr = new Handler();
                                                hdlr.postDelayed(
                                                        ()->
                                                        {
                                                            humanMove();

                                                            movedFrom = UNDEFINED_FROM;
                                                            game.setState(PLAYING);
                                                            eatCoord = null;
                                                            rooked = false;

                                                            setAnimationTurn(game.getBoardTurn());

                                                            hideFragLoadingBoard();

                                                        }, 2000
                                                );
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Log.e(TAG, "ERROR: SECOND COORD INCORRECT | MOVED FROM BOARD");
                                        moderator = ILEGAL_MOVE;
                                        BTCommunication.getInstance().sendData("bad");
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(data.charAt(0) == 'B')
                            {
                                Log.e(TAG, "-------------------= CHECK BAD POSITIONS =-------------------");

                                refreshTintBadPositions(stringToListPositions(data.substring(1)));

                                if(good)
                                {
                                    showComponents();

                                    game.setState(PLAYING);
                                    moderator = FIRST_MOVE;
                                    eatCoord = null;
                                    rooked = false;

                                    if(movedFrom == APP)
                                    {
                                        movedFrom = UNDEFINED_FROM;
                                        Log.e(TAG, "-------------------= MOVEMENT FIXED FROM APP =-------------------");

                                        BTCommunication.getInstance().sendData("fixedApp");

                                        hideFragLoadingBoard();
                                        hideFragMoveInBoard();

                                        Handler hdlr = new Handler();
                                        hdlr.postDelayed(
                                                () ->
                                                {
                                                    Log.e(TAG, "        (   APP ----> BOARD   )");

                                                    if(colorPlayer != game.getBoardTurn())
                                                    {
                                                        if(pG.getKindPlayer() == MASTER)
                                                            CommunicationInterface.getInstance().respondAsMaster("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                                                        else
                                                            CommunicationInterface.getInstance().respondAsSlave("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));

                                                        String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                                        BTCommunication.getInstance().sendData(move);
                                                    }

                                                    setAnimationTurn(game.getBoardTurn());

                                                }, 2000);
                                    }
                                    else
                                    {
                                        movedFrom = UNDEFINED_FROM;
                                        Log.e(TAG, "-------------------= MOVEMENT FIXED FROM BOARD =-------------------");
                                        BTCommunication.getInstance().sendData("fixedBoard");
                                        hideFragLoadingBoard();
                                    }
                                }
                                else
                                {
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else if(data.charAt(0) == 'E')
                            {
                                BTCommunication.getInstance().sendData("again");
                            }
                            else if(data.equals("OKEND"))
                            {
                                Log.e(TAG, "OK, GOT IT, GAME FINISHED");
                                BTCommunication.getInstance().sendData("YEAH");
                            }
                            else if(data.charAt(0) == 'N')
                            {
                                Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                                showComponents();

                                moderator = FIRST_MOVE;
                                movedFrom = UNDEFINED_FROM;
                                game.setState(PLAYING);
                                eatCoord = null;
                                rooked = false;

                                resetColorButtonsBoard();
                                watchaChecksMatesAndDraws();
                                hideFragLoadingBoard();
                                hideFragMoveInBoard();

                                if(colorPlayer != game.getBoardTurn())
                                {
                                    Log.e(TAG, "        (   APP ----> BOARD   )");

                                    if(pG.getKindPlayer() == MASTER)
                                        CommunicationInterface.getInstance().respondAsMaster("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                                    else
                                        CommunicationInterface.getInstance().respondAsSlave("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));

                                /*String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                BTCommunication.getInstance().sendData(move);*/
                                }
                                else
                                {
                                    timeMove = 0;
                                    firstTimeMove = false;
                                    tVCounter.setText("");
                                    tVCounter.setVisibility(View.INVISIBLE);
                                    animCounter.cancel();

                                    if(pG.getKindOfLocal() == IS_REMOTE_T)
                                    {
                                        limitTimeMove = 90;
                                    }
                                    else
                                    {
                                        if(opponentWithBoard)
                                        {
                                            limitTimeMove = 90;
                                        }
                                        else
                                        {
                                            limitTimeMove = 30;
                                        }
                                    }
                                }

                                setAnimationTurn(game.getBoardTurn());
                            }
                            else if(data.charAt(0) == 'X')
                            {
                                if(game.getState() != Constants.WAITING)
                                    showFragLoadingBoard();

                                game.setState(Constants.WAITING);

                                hidePause();

                                Log.e(TAG, "---------------------------- X");

                                Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                                refreshTintMovingBadPositions(stringToListPositions(data.substring(1)), firstCoord, secondCoord);

                                if(good)
                                {
                                    Log.e(TAG, "-------------------= ALL GOOD DURING MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("fixedBoard");
                                }
                                else
                                {
                                    Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                            else if(data.charAt(0) == 'Y')
                            {
                                Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS FINAL DURING MOVING SYSTEM =-------------------");

                                refreshTintBadPositions(stringToListPositions(data.substring(1)));

                                if(good)
                                {
                                    Log.e(TAG, "-------------------= ALL GOOD DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("fixedSystem");
                                }
                                else
                                {
                                    Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                    BTCommunication.getInstance().sendData("bad");
                                }
                            }
                        }
                    }
                    else
                    {
                        if(data.charAt(0) == 'B')
                        {
                            Log.e(TAG, "-------------------= CHECK BAD POSITIONS =-------------------");

                            refreshTintBadPositions(stringToListPositions(data.substring(1)));

                            if(good)
                            {
                                showComponents();

                                movedFrom = UNDEFINED_FROM;
                                game.setState(PLAYING);
                                moderator = FIRST_MOVE;
                                eatCoord = null;
                                rooked = false;

                                if(colorPlayer == game.getBoardTurn())
                                {
                                    Log.e(TAG, "-------------------= MOVEMENT FIXED FROM APP =-------------------");
                                    BTCommunication.getInstance().sendData("fixedApp");
                                    hideFragLoadingBoard();
                                    hideFragMoveInBoard();
                                    Log.e(TAG, "        (   CRASH APP ----> CRASH BOARD   )");
                                    setAnimationTurn(game.getBoardTurn());
                                }
                                else
                                {
                                    Log.e(TAG, "-------------------= MOVEMENT FIXED FROM BOARD =-------------------");
                                    BTCommunication.getInstance().sendData("fixedBoard");
                                    hideFragLoadingBoard();
                                }
                            }
                            else
                            {
                                BTCommunication.getInstance().sendData("bad");
                            }
                        }
                        else if(data.charAt(0) == 'N')
                        {
                            Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                            showComponents();

                            moderator = FIRST_MOVE;
                            movedFrom = UNDEFINED_FROM;
                            game.setState(PLAYING);
                            eatCoord = null;
                            rooked = false;

                            resetColorButtonsBoard();
                            watchaChecksMatesAndDraws();
                            hideFragLoadingBoard();
                            hideFragMoveInBoard();

                            if(colorPlayer != game.getBoardTurn())
                            {
                                Log.e(TAG, "        (   APP ----> BOARD   )");

                                if(pG.getKindPlayer() == MASTER)
                                    CommunicationInterface.getInstance().respondAsMaster("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));
                                else
                                    CommunicationInterface.getInstance().respondAsSlave("@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1));

                                /*String move = "@" + game.getArrayListMoves().get(game.getArrayListMoves().size()-1);
                                BTCommunication.getInstance().sendData(move);*/
                            }
                            else
                            {
                                timeMove = 0;
                                firstTimeMove = false;
                                tVCounter.setText("");
                                tVCounter.setVisibility(View.INVISIBLE);
                                animCounter.cancel();

                                if(pG.getKindOfLocal() == IS_REMOTE_T)
                                {
                                    limitTimeMove = 90;
                                }
                                else
                                {
                                    if(opponentWithBoard)
                                    {
                                        limitTimeMove = 90;
                                    }
                                    else
                                    {
                                        limitTimeMove = 30;
                                    }
                                }
                            }

                            setAnimationTurn(game.getBoardTurn());
                        }
                        else if(data.charAt(0) == 'X')
                        {
                            if(game.getState() != Constants.WAITING)
                                showFragLoadingBoard();

                            game.setState(Constants.WAITING);

                            hidePause();

                            Log.e(TAG, "---------------------------- X");

                            Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS DURING MOVING SYSTEM =-------------------");

                            refreshTintMovingBadPositions(stringToListPositions(data.substring(1)), firstCoord, secondCoord);

                            if(good)
                            {
                                Log.e(TAG, "-------------------= ALL GOOD DURING MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("fixedBoard");
                            }
                            else
                            {
                                Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("bad");
                            }
                        }
                        else if(data.charAt(0) == 'Y')
                        {
                            Log.e(TAG, "-------------------= CHECK IF BAD POSITIONS FINAL DURING MOVING SYSTEM =-------------------");

                            refreshTintBadPositions(stringToListPositions(data.substring(1)));

                            if(good)
                            {
                                Log.e(TAG, "-------------------= ALL GOOD DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("fixedSystem");
                            }
                            else
                            {
                                Log.e(TAG, "-------------------= SOMETHING IS WRONG DURING FINAL MOVEMENT OF SYSTEM =-------------------");
                                BTCommunication.getInstance().sendData("bad");
                            }
                        }
                        else if(data.charAt(0) == 'E')
                        {
                            BTCommunication.getInstance().sendData("again");
                        }
                        else if(data.equals("OKEND"))
                        {
                            Log.e(TAG, "OK, GOT IT, GAME FINISHED");
                            BTCommunication.getInstance().sendData("YEAH");
                        }
                        else
                        {
                            Log.e(TAG, "ERROR: TRIED TO MOVE IN BOARD ON OPPONENT'S TURN | MOVED FROM BOARD");
                            moderator = ILEGAL_MOVE;
                            BTCommunication.getInstance().sendData("bad");
                        }
                    }
                }
            }
        }
    }

    private void checkData(String data)
    {
        if(data.equals("WIN"))
        {
            BTCommunication.getInstance().sendData("OVER");
            game.setState(Constants.FINISHEDBYIA);
            gotoFragGameFinished(true);
        }
        else if(data.equals("LOSE"))
        {
            BTCommunication.getInstance().sendData("OVER");
            game.setState(Constants.FINISHEDBYP);
            gotoFragGameFinished(true);
        }
        else if(data.equals("DRAW"))
        {
            BTCommunication.getInstance().sendData("OVER");
            game.setState(Constants.DRAWMOVBYIA);
            gotoFragGameFinished(true);
        }
        else if(data.equals("ASK"))
        {
            gotoFragInviteAgain(false, false);
        }
        else if(data.equals("AGAIN"))
        {
            BTCommunication.getInstance().sendData("OKAGAIN");
            timerMainActivity.cancel();
            gotoViewPiecesColorBluetooth();
        }
        else if(data.equals("CANCEL"))
        {
            getSupportFragmentManager().beginTransaction().remove(fragInvitation).commit();
        }
        else if(data.equals("FINISH"))
        {
            gotoFragInviteAgain(true, false);
            gotoFragGameFinished(false);
        }
    }


    private void tintBadInitialPositions(ArrayList<String> aL)
    {
        Coord auxCoord;

        for(int i=0; i<aL.size(); i++)
        {
            auxCoord = new Coord(aL.get(i).charAt(0)-'0', aL.get(i).charAt(1)-'0');
            buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorOrangeYellow);
        }
    }

    private void refreshTintBadPositions(ArrayList<String> aL)
    {
        resetColorButtonsBoard();
        hideComponents();
        tintBadPositions(aL);
        watchaChecksMatesAndDraws();
    }

    private void tintBadPositions(ArrayList<String> aL)
    {
        Coord auxCoord;
        good = true;

        //printArrayListPositions(aL);

        for(int i=0; i<8; i++)
        {
            for(int j=0; j<8; j++)
            {
                auxCoord = new Coord(j, i);

                if(game.getSpot(auxCoord) != null)
                {
                    for(int k=0; k<aL.size(); k++)
                    {
                        if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                        {
                            break;
                        }
                        else
                        {
                            if(k+1 == aL.size())
                            {
                                buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorOrangeYellow);
                                good = false;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    for(int k=0; k<aL.size(); k++)
                    {
                        if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                        {
                            buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorRed);
                            good = false;
                            break;
                        }
                        else
                        {
                            if(k+1 == aL.size())
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void refreshTintMovingBadPositions(ArrayList<String> aL, Coord fCoord, Coord sCoord)
    {
        resetColorButtonsBoard();
        hideComponents();
        tintMovingBadPositions(aL, fCoord, sCoord);
        watchaChecksMatesAndDraws();
    }

    private void tintMovingBadPositions(ArrayList<String> aL, Coord fCoord, Coord sCoord)
    {
        Coord auxCoord;
        good = true;

        //printArrayListPositions(aL);

        for(int i=0; i<8; i++)
        {
            for(int j=0; j<8; j++)
            {
                if((fCoord.x != j || fCoord.y != i) && (sCoord.x != j || sCoord.y != i))
                {
                    auxCoord = new Coord(j, i);

                    if(game.getSpot(auxCoord) != null)
                    {
                        for(int k=0; k<aL.size(); k++)
                        {
                            if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                            {
                                break;
                            }
                            else
                            {
                                if(k+1 == aL.size())
                                {
                                    buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorOrangeYellow);
                                    good = false;
                                    break;
                                }
                            }
                        }
                    }
                    else
                    {
                        for(int k=0; k<aL.size(); k++)
                        {
                            if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                            {
                                buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorRed);
                                good = false;
                                break;
                            }
                            else
                            {
                                if(k+1 == aL.size())
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private ArrayList<String> stringToListPositions(String positions)
    {
        try
        {
            ArrayList<String> aux = new ArrayList<>();

            for(int i=0; i< positions.length(); i+=2)
                aux.add(Character.toString(positions.charAt(i))+Character.toString(positions.charAt(i+1)));

            return aux;
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception MAIN_ACTIVITY stringToListPositions: " + e.toString());
            return null;
        }
    }

    private void printArrayListPositions(ArrayList<String> aL)
    {
        Log.e(TAG, "------- PRINT ARRAY LIST POSITIONS");

        for(int i=0; i<aL.size(); i++)
        {
            Log.e(TAG, "--> " + aL.get(i));
        }

        Log.e(TAG, " ");
    }

    private void hideComponents()
    {
        findViewById(R.id.btnBlackRook).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnWhiteRook).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvBlackName).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvWhiteName).setVisibility(View.INVISIBLE);

        findViewById(R.id.btnBlackRook).setVisibility(View.GONE);
        findViewById(R.id.btnWhiteRook).setVisibility(View.GONE);
        findViewById(R.id.tvBlackName).setVisibility(View.GONE);
        findViewById(R.id.tvWhiteName).setVisibility(View.GONE);

        if(game.getMode() == Constants.PVIA)
        {
            findViewById(R.id.btnBack).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnChronometer).setVisibility(View.INVISIBLE);

            findViewById(R.id.btnBack).setVisibility(View.GONE);
            findViewById(R.id.btnChronometer).setVisibility(View.GONE);
        }
    }

    private void showComponents()
    {
        findViewById(R.id.btnBlackRook).setVisibility(View.VISIBLE);
        findViewById(R.id.btnWhiteRook).setVisibility(View.VISIBLE);
        findViewById(R.id.tvBlackName).setVisibility(View.VISIBLE);
        findViewById(R.id.tvWhiteName).setVisibility(View.VISIBLE);

        if(game.getMode() == Constants.PVIA)
        {
            findViewById(R.id.btnBack).setVisibility(View.VISIBLE);
            findViewById(R.id.btnChronometer).setVisibility(View.VISIBLE);
        }
    }

    private void refreshTintBackPositions(ArrayList<String> aL)
    {
        resetColorButtonsBoard();
        hideComponents();
        tintBackPositions(aL);
        watchaChecksMatesAndDraws();
    }

    private void tintBackPositions(ArrayList<String> aL)
    {
        Coord auxCoord;
        good = true;

        //printArrayListPositions(aL);

        for(int i=0; i<8; i++)
        {
            for(int j=0; j<8; j++)
            {
                auxCoord = new Coord(j, i);

                if(game.getSpot(auxCoord) != null)
                {
                    for(int k=0; k<aL.size(); k++)
                    {
                        if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                        {
                            break;
                        }
                        else
                        {
                            if(k+1 == aL.size())
                            {
                                buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorOrangeYellow);
                                good = false;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    for(int k=0; k<aL.size(); k++)
                    {
                        if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                        {
                            buttonsBoard[auxCoord.x][auxCoord.y].setBackgroundColor(colorRed);
                            good = false;
                            break;
                        }
                        else
                        {
                            if(k+1 == aL.size())
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }

        for(int i=0; i<8; i++)
        {
            for(int j=0; j<8; j++)
            {
                auxCoord = new Coord(j, i);

                if(game.getSpot(auxCoord) != null)
                {
                    for(int k=0; k<aL.size(); k++)
                    {
                        if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                        {
                            break;
                        }
                        else
                        {
                            if(k+1 == aL.size())
                            {
                                tintBackMoves(moveBack1, auxCoord, true);
                                tintBackMoves(moveBack2, auxCoord, false);
                                good = false;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    for(int k=0; k<aL.size(); k++)
                    {
                        if(aL.get(k).equals(String.valueOf(auxCoord.x)+String.valueOf(auxCoord.y)))
                        {
                            tintBackMoves(moveBack1, auxCoord, true);
                            tintBackMoves(moveBack2, auxCoord, false);
                            good = false;
                            break;
                        }
                        else
                        {
                            if(k+1 == aL.size())
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private String getPositionsOfPieces()
    {
        Coord auxCoord;
        String listPositions = "";

        for(int i=0; i<8; i++)
        {
            for(int j=0; j<8; j++)
            {
                auxCoord = new Coord(j, i);

                if(game.getSpot(auxCoord) != null)
                {
                    listPositions += String.valueOf(j)+String.valueOf(i);
                }
            }
        }

        return listPositions;
    }




    private void tintBackMoves(Move m, Coord c, boolean first)
    {
        int colorF;
        int colorS;

        if(first)
        {
            colorF = colorLightBlue;
            colorS = colorNormalBlue;
        }
        else
        {
            colorF = colorLightPurple;
            colorS = colorNormalPurple;
        }

        if (m != null)
        {
            if(c.x == m.getX1() && c.y == m.getY1())
            {
                buttonsBoard[m.getX1()][m.getY1()].setBackgroundColor(colorF);
            }
            else if(c.x == m.getX2() && c.y == m.getY2())
            {
                buttonsBoard[m.getX2()][m.getY2()].setBackgroundColor(colorS);
            }
            else if(m.getRook() != null)
            {
                if(c.x == m.getRook().getX1() && c.y == m.getRook().getY1())
                {
                    buttonsBoard[m.getRook().getX1()][m.getRook().getY1()].setBackgroundColor(colorF);
                }
                else if(c.x == m.getRook().getX2() && c.y == m.getRook().getY2())
                {
                    buttonsBoard[m.getRook().getX2()][m.getRook().getY2()].setBackgroundColor(colorS);
                }
            }
        }
    }


    //----------------------------------------------------------------------------------------------------------

    public Button btnChat, btnBadgeCounter;

    public static Timer tNewMessages;

    private Interface_BackPressed interface_backPressed;

    public int type;

    public static boolean fChatOpened = true;
    public static int auxId = 0, idMssg;
    public static int rLength, hmNewMssgs;

    public void setInterface_backPressed(Interface_BackPressed interface_backPressed)
    {
        this.interface_backPressed = interface_backPressed;
    }

    public void showFragment_Chat()
    {
        hideBadgeCounter();

        fChatOpened = true;
        btnChat.setEnabled(false);

        tNewMessages.cancel();

        Bundle b = new Bundle();
        b.putString("emitter", pG.getPlayerName());
        b.putString("receiver", pG.getOponentName());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_Chat fragment_chat = new Fragment_Chat();
        fragment_chat.setArguments(b);
        fragmentTransaction.replace(R.id.fragmentChat, fragment_chat);
        fragmentTransaction.commit();
    }

    public void hideBadgeCounter()
    {
        hmNewMssgs = 0;
        btnBadgeCounter.setVisibility(View.INVISIBLE);
    }

    public void timerChat()
    {
        newTimer();
        timerNewMessages();
    }

    public void newTimer()
    {
        tNewMessages = new Timer();
    }

    public void timerNewMessages()
    {
        try
        {
            final Handler handler = new Handler();
            tNewMessages.schedule(new TimerTask()
            {
                public void run()
                {
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            MySQL.query(MainActivity.this).UnseenMessages(CP.get().getNamePlayer()); //unseenMessages
                        }
                    });
                }
            },0, 4500);
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR VIEW_GAME INTERFACE timerNewMessages " + e.toString());
        }
    }

    public void quitChat()
    {
        if(interface_backPressed != null)
        {
            interface_backPressed.onBackPressed();
        }

        hideChat();
        hideBadgeCounter();
        fChatOpened = false;
        tNewMessages.cancel();
    }


    // EXAMPLE OF ITERATING THROUGH ELEMENTS OF A LAYOUT (DO NOT FOR ANY REASON DELETE THIS)

    /*RelativeLayout layout = findViewById(R.id.view_ia);

    f = new ArrayList<>();

    for (int i = 0; i < layout.getChildCount(); i++)
    {
        View vv = layout.getChildAt(i);

        if (vv instanceof Button)
        {
            String idButton = vv.getResources().getResourceEntryName(vv.getId());

            if(!idButton.equals("btnTime") && !idButton.equals("btnReply") && !idButton.equals("btnPause") &&
                    !idButton.equals("btnBlackRook") && !idButton.equals("btnWhiteRook") && !idButton.equals("btnBackground"))
            {
                lastColor = ((ColorDrawable)vv.getBackground()).getColor();
                f.add(lastColor);
            }
        }
    }*/

    // LAMBDA EXPRESSION IN ONCLICK LISTENERS

    /*findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    verifyLogin();
                }
            });*/

    //findViewById(R.id.btnNext).setOnClickListener((View v) -> verifyLogin());


    // This is for simulating an user
            /*if(pG.getMode() != PVIA)
            {
                Random rnd = new Random();
                int time = rnd.nextInt(7000-2000+1) + 2000; // De 2 a 7 segundos

                rnbl = () -> {
                    Random rndB = new Random();
                    boolean color = rndB.nextBoolean(); // True or false like throwing a penny

                    respondOponent = true;

                    if(color)
                        findViewById(R.id.btnWhiteColor).performClick();
                    else
                        findViewById(R.id.btnBlackColor).performClick();
                };

                handler = new Handler();
                handler.postDelayed(rnbl, time);
            }*/

}

