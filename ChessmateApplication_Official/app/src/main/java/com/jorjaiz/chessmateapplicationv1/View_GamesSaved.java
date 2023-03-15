package com.jorjaiz.chessmateapplicationv1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jorjaiz.chessmateapplicationv1.Adapters.Adapter_GamesSaved;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class View_GamesSaved extends AppCompatActivity implements Constants,
        Fragment_EditNameGame.OnFragmentInteractionListener, Fragment_StartGame.OnFragmentInteractionListener,
        Adapter_GamesSaved.MyAdapterListener, Query.OnResponseDatabase, FireQuery.OnResponseFireQuery
{

    ParamsGame paramsGame;

    Adapter_GamesSaved adapter;
    ArrayList<Game> listGames;
    RecyclerView rVGamesSaved;

    public static int itemIndex;
    public static String itemMode;
    public static String itemDifficulty;
    public static String itemColorPlayer;
    public static String itemGameName;
    public static String itemOponentName;

    boolean touched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__games_saved);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_GAMES SAVED ON CREATE");

            findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);

            Bundle parameters = getIntent().getExtras();
            paramsGame = parameters.getParcelable("pG");

            itemIndex = 0;

            listGames = new ArrayList<>();

            rVGamesSaved = findViewById(R.id.rvSavedGames);

            findViewById(R.id.btnEditGameName).setOnClickListener(
                    view ->
                    {
                        paramsGame.setResume(0);
                        Log.w(TAG, "< EDIT NAME OF GAME SAVED >");
                        changeNameGame();
                    });

            findViewById(R.id.btnRestartGame).setOnClickListener(
                    view ->
                    {
                        Log.w(TAG, "< RESUME GAME SAVED >");
                        paramsGame.setResume(1);
                        which();
                    });

            findViewById(R.id.btnReviewGame).setOnClickListener(
                    view ->
                    {
                        Log.w(TAG, "< REVIEW GAME SAVED >");
                        paramsGame.setResume(0);
                        which();
                    });

            findViewById(R.id.btnDeleteGame).setOnClickListener(
                    view ->
                    {
                        paramsGame.setResume(0);
                        Log.w(TAG, "< DELETE GAME SAVED >");
                        deleteGame();
                    });

            MySQL.query(this).getSavedGames(paramsGame.getIdPlayer());

        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMESSAVED onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, View_MainInterface.class);
        i.putExtra("idPlayer", CP.get().getIdPlayer());
        i.putExtra("namePlayer", CP.get().getNamePlayer());
        this.startActivity(i);
    }

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {

    }

    public void which()
    {
        try
        {
            if(itemIndex != 0)
            {
                if(!touched)
                {
                    touched = true;

                    paramsGame.setIdGame(itemIndex);

                    if(itemMode.equals("pvia"))
                        paramsGame.setMode(PVIA);
                    else if(itemMode.equals("pvplocal"))
                        paramsGame.setMode(PVPLOCAL);
                    else
                        paramsGame.setMode(PVPONLINE);

                    if(itemDifficulty.equals("Facil"))
                        paramsGame.setDifficulty(EASY);
                    else if(itemDifficulty.equals("Intermedio"))
                        paramsGame.setDifficulty(INTERMEDIATE);
                    else if(itemDifficulty.equals("Dificil"))
                        paramsGame.setDifficulty(DIFFICULT);
                    else
                        paramsGame.setDifficulty(EASY);

                    MySQL.query(this).getSavedGamesInfo(paramsGame.getIdGame());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMESSAVED which: " + e.toString());
        }
    }

    public void gotoResume(int colorPlayer)
    {
        try
        {
            paramsGame.setColorPlayer(colorPlayer);

            if(CP.get().isBoard() && paramsGame.getMode() == PVIA)
                paramsGame.setKindOfLocal(IS_LOCALIA_T);
            else
                paramsGame.setKindOfLocal(IS_NOLOCAL);

            Bundle b = new Bundle();
            b.putParcelable("pG", paramsGame);
            b.putBoolean("waitingToPlay", false);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment_StartGame frag = new Fragment_StartGame();
            frag.setArguments(b);
            transaction.replace(R.id.fragmentPlace, frag);
            transaction.commit();

            findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMESSAVED gotoResume: " + e.toString());
        }
    }

    public void changeNameGame()
    {
        if(itemIndex != 0)
        {
            Bundle b = new Bundle();
            b.putString("gameName", itemGameName);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment_EditNameGame frag = new Fragment_EditNameGame();
            frag.setArguments(b);
            transaction.replace(R.id.fragmentPlace, frag);
            transaction.commit();

            findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
        }
    }

    public void deleteGame()
    {
        if(itemIndex != 0)
        {
            int i = 0;

            for(Game g : listGames)
            {
                if(g.getIdGame() == itemIndex)
                    break;

                i++;
            }

            listGames.remove(i);
            adapter.notifyItemRemoved(i);

            MySQL.query(this).deleteGame(itemIndex);

            itemIndex = 0;
        }

    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onFragEditNameGame(String gameName)
    {
        int i = 0;

        for(Game g : listGames)
        {
            if(g.getIdGame() == itemIndex)
                break;

            i++;
        }

        Game game = listGames.get(i);
        game.setNameOfGame(gameName);

        listGames.set(i, game);

        adapter.notifyItemChanged(i);

        itemGameName = gameName;

        MySQL.query(this).setNameOfGame(itemGameName, itemIndex);
    }

    @Override
    public void onFragRemoveBLayerEditNameGame()
    {
        findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFragRemoveBLayer()
    {
        findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFragOpponentDisconnected()
    {

    }

    @Override
    public void onFragErrorWithServer()
    {

    }

    @Override
    public void onFragLoading()
    {
        Bundle b = new Bundle();
        b.putBoolean("waitingToPlay", true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_StartGame frag = new Fragment_StartGame();
        frag.setArguments(b);
        transaction.replace(R.id.fragmentPlace2, frag);
        transaction.commit();

        findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragQuitLoading()
    {

    }

    @Override
    public void onItemViewGameSavedClick(String mode, String lastState)
    {
        if(!mode.equals("pvia") || lastState.equals("Finalizada"))
            findViewById(R.id.btnRestartGame).setVisibility(View.INVISIBLE);
        else
            findViewById(R.id.btnRestartGame).setVisibility(View.VISIBLE);
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, " Error fatal DATA NULL VIEW_GAMES SAVED getResponseDB");
        }
        else
        {
            if(data.containsKey("DATA"))
            {
                try
                {
                    JSONArray jA = null;

                    if(data.get("DATA")!=null)
                        jA = data.get("DATA");

                    switch(purpose)
                    {
                        case "getSavedGames":
                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                JSONObject object = jA.getJSONObject(i);

                                Game game = new Game();

                                game.setIdGame(Integer.valueOf(object.getString("idGame")));
                                game.setMode(object.getString("mode"));
                                game.setDifficulty(object.getString("difficulty"));
                                game.setLastState(object.getString("lastState"));
                                game.setNameOfGame(object.getString("nameGame"));
                                game.setPlayerName(object.getString("playerName"));
                                game.setColorPlayer(object.getString("colorPlayer"));
                                game.setOponentName(object.getString("oponentName"));
                                game.setOponentColor(object.getString("colorOponent"));

                                String s = object.getString("moves");

                                ArrayList<String> arrayListMoves = new ArrayList<>();

                                StringBuilder sb;
                                for (int x = 0; x < s.length(); x++)
                                {
                                    if (s.charAt(x) == '@')
                                    {
                                        sb = new StringBuilder();

                                        sb.append(s.charAt(x + 1)).append(s.charAt(x + 2)).append(s.charAt(x + 3))
                                                .append(s.charAt(x + 4)).append(s.charAt(x + 5)).append(s.charAt(x + 6))
                                                .append(s.charAt(x + 7)).append(s.charAt(x + 8)).append(s.charAt(x + 9));

                                        arrayListMoves.add(sb.toString());
                                    }
                                }

                                int idUser = 0;
                                char consequence = 'n';

                                if(arrayListMoves.size() != 0)
                                {
                                    idUser = Character.getNumericValue(arrayListMoves.get(arrayListMoves.size()-1).charAt(0));
                                    consequence = arrayListMoves.get(arrayListMoves.size()-1).charAt(8);
                                }

                                if((consequence == 'j' || consequence == 'n' || consequence == 'k')
                                        && game.getLastState().equals("Finalizada"))
                                {
                                    game.setPlayerStatus(idUser==paramsGame.getIdPlayer()?"Ganador":"Perdedor");
                                    game.setOponentStatus(idUser==paramsGame.getIdPlayer()?"Perdedor":"Ganador");
                                }
                                else
                                {
                                    switch (consequence)
                                    {
                                        case 'j':
                                            game.setPlayerStatus(idUser==paramsGame.getIdPlayer()?"--":"En Jaque");
                                            game.setOponentStatus(idUser==paramsGame.getIdPlayer()?"En Jaque":"--");
                                            break;

                                        case 'k':
                                            game.setPlayerStatus(idUser==paramsGame.getIdPlayer()?"Ganador":"Perdedor");
                                            game.setOponentStatus(idUser==paramsGame.getIdPlayer()?"Perdedor":"Ganador");
                                            break;

                                        case 'l':
                                        case 'm':
                                            game.setPlayerStatus("Empate");
                                            game.setOponentStatus("Empate");
                                            break;

                                        case 'n':
                                            game.setPlayerStatus("--");
                                            game.setOponentStatus("--");
                                    }
                                }

                                listGames.add(game);
                            }

                            adapter = new Adapter_GamesSaved(listGames, this, this);

                            LinearLayoutManager l = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

                            rVGamesSaved.setLayoutManager(l);
                            rVGamesSaved.setAdapter(adapter);
                            break;

                        case "getSavedGamesInfo":
                            int idUser = 2;
                            int idOponent = 1;
                            int colorPlayer;

                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                JSONObject object = jA.getJSONObject(i);
                                idUser = Integer.parseInt(object.getString("idUser"));
                                idOponent = Integer.parseInt(object.getString("idOponent"));
                            }

                            if(itemColorPlayer.equals("B"))
                            {
                                paramsGame.setpOneId(idUser);
                                paramsGame.setpTwoId(idOponent);
                                paramsGame.setpOneWhite(1);
                                paramsGame.setpTwoBlack(0);
                                colorPlayer = 1;
                            }
                            else
                            {
                                paramsGame.setpOneId(idOponent);
                                paramsGame.setpTwoId(idUser);
                                paramsGame.setpOneWhite(0);
                                paramsGame.setpTwoBlack(1);
                                colorPlayer = 0;
                            }

                            paramsGame.setOponentName(itemOponentName);

                            gotoResume(colorPlayer);
                            break;

                        case "setNameOfGame":
                            break;

                        case "deleteGame":
                            break;
                    }
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Exception VIEW_GAMESSAVED getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                transaction.replace(R.id.fragmentPlace, frag);
                transaction.commit();
                touched = false;
            }
        }
    }

}
