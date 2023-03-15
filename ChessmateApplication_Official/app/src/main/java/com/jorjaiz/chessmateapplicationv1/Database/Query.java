package com.jorjaiz.chessmateapplicationv1.Database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Query implements Constants
{
    public interface OnResponseDatabase
    {
        void getResponseDB(HashMap<String, JSONArray> data, String purpose);
    }

    private OnResponseDatabase dbListener;
    private String purpose;
    private Context ctx;

    private HashMap<String, JSONArray> data;

    public Query(Context ctx)
    {
        this.ctx = ctx;
        this.dbListener = (OnResponseDatabase)ctx;
    }

    public Query(Context ctx, OnResponseDatabase listener)
    {
        this.ctx = ctx;
        this.dbListener = listener;
    }

    private void letsQuery(Map<String, String> map, final String pp)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATABASE,
                response ->
                {
                    try
                    {
                        if(!purpose.equals("getNations"))
                        {
                            Log.i(TAG, " ");
                            Log.i(TAG, ">>>>> SUCCESFUL QUERY: " + response);
                        }

                        getResponseInJSONArray(response);

                        dbListener.getResponseDB(data, pp);

                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Exception MYSQL letsQuery: " + e.toString());
                        dbListener.getResponseDB(null, purpose);
                    }
                },
                error ->
                {
                    Toast.makeText(ctx, "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();

                    Log.e(TAG, " ");
                    Log.e(TAG, ">>>>> ERROR CONNECTING TO DB");

                    data = new HashMap<>();
                    data.put("ERROR", null);

                    dbListener.getResponseDB(data, purpose);
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void getResponseInJSONArray(String response)
    {
        data = new HashMap<>();

        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            data.put("DATA", jsonArray);
        }
        catch (Exception e)
        {
            Log.w(TAG, "QUERY returns DONE");
            data.put("DATA", null);
        }
    }

    // VIEW_LOGIN

    public void verifyLogin(String username, String password)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "verifyLogin");
        map.put("userName", username);
        map.put("password", password);
        purpose = "verifyLogin";
        letsQuery(map, purpose);
    }


    // VIEW_GAMES SAVED

    public void getSavedGames(int idPlayer)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getSavedGames");
        map.put("idUser", idPlayer+"");
        purpose = "getSavedGames";
        letsQuery(map, purpose);
    }

    // VIEW_GAMES SAVED

    public void getSavedGamesInfo(int idGame)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getSavedGamesInfo");
        map.put("idGame", idGame+"");
        purpose = "getSavedGamesInfo";
        letsQuery(map, purpose);
    }

    // VIEW_GAMES SAVED

    public void setNameOfGame(String itemGameName, int itemIndex)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "setNameOfGame");
        map.put("nameOfGame", itemGameName);
        map.put("idGame", itemIndex+"");
        purpose = "setNameOfGame";
        letsQuery(map, purpose);
    }

    // VIEW_GAMES SAVED

    public void deleteGame(int itemIndex)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "deleteGame");
        map.put("idGame", itemIndex+"");
        purpose = "deleteGame";
        letsQuery(map, purpose);
    }

    // VIEW_EDIT PROFILE, VIEW_GAMERS LIST

    public void getPlayersList(int idPlayer)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getPlayersList");
        map.put("idUser", idPlayer+"");
        purpose = "getPlayersList";
        letsQuery(map, purpose);
    }

    // VIEW_EDIT PROFILE

    public void createAccount(String nation, String userName, String mail, String password)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "createAccount");
        map.put("nation", nation+"");
        map.put("userName", userName+"");
        map.put("mail", mail+"");
        map.put("password", password+"");
        purpose = "createAccount";
        letsQuery(map, purpose);
    }

    // VIEW_EDIT PROFILE

    public void getNations()
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getNations");
        purpose = "getNations";
        letsQuery(map, purpose);
    }

    // VIEW_EDIT PROFILE

    public void setPersonalData(String nation, String userName, String mail, String password, int idPlayer)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "setPersonalData");
        map.put("nation", nation+"");
        map.put("userName", userName+"");
        map.put("mail", mail+"");
        map.put("password", password+"");
        map.put("idUser", idPlayer+"");
        purpose = "setPersonalData";
        letsQuery(map, purpose);
    }

    // VIEW_EDIT PROFILE

    public void getPersonalData(int idPlayer)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getPersonalData");
        map.put("idUser", idPlayer+"");
        purpose = "getPersonalData";
        letsQuery(map, purpose);
    }





    // VIEW_GAME INTERFACE

    public void addNewMove(String stringMoves, long secondsLeft, int idGame)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "addNewMove");
        map.put("stringMoves", stringMoves);
        map.put("secondsLeft", secondsLeft+"");
        map.put("idGame", idGame+"");
        purpose = "addNewMove";
        letsQuery(map, purpose);
    }

    // VIEW_GAME INTERFACE

    public void deleteMoves(String stringMoves, int rewinds, int idGame)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "deleteMoves");
        map.put("stringMoves", stringMoves);
        map.put("rewinds", rewinds+"");
        map.put("idGame", idGame+"");
        purpose = "deleteMoves";
        letsQuery(map, purpose);
    }

    // VIEW_GAME INTERFACE

    public void setGame(int gameState, String stringMoves, long secondsLeft, int rewinds, int idGame)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "setGame");
        map.put("idGameState", gameState+"");
        map.put("stringMoves", stringMoves);
        map.put("secondsLeft", secondsLeft+"");
        map.put("rewinds", rewinds+"");
        map.put("idGame", idGame+"");
        purpose = "setGame";
        letsQuery(map, purpose);
    }



    // FRAG_START GAME

    public void createGame(int mode, int difficulty, int idPlayer, String oponentName, long secondsLeft, int pOneId, int rewinds)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "createGame");
        map.put("idMode", mode+"");
        map.put("idDifficulty", difficulty+"");
        map.put("idUser", idPlayer+"");
        map.put("oponentName", oponentName);
        map.put("idGameState", 3+"");
        map.put("name", "New Game");
        map.put("secondsLeft", secondsLeft+"");
        map.put("color", idPlayer==pOneId?"B":"N");
        map.put("colorOponent", idPlayer==pOneId?"N":"B");
        map.put("rewinds", rewinds+"");
        purpose = "createGame";
        letsQuery(map, purpose);
    }

    // FRAG_START GAME

    public void getIDLastGame(int idPlayer)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getIDLastGame");
        map.put("idUser", idPlayer+"");
        purpose = "getIDLastGame";
        letsQuery(map, purpose);
    }

    // FRAG_START GAME

    public void getSavedMoves(int idGame)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getSavedMoves");
        map.put("idGame", idGame+"");
        purpose = "getSavedMoves";
        letsQuery(map, purpose);
    }


    // VIEW_MAIN INTERFACE

    public void checkWifi()
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "checkWifi");
        purpose = "checkWifi";
        //letsCheckConnection(map);
        letsQuery(map, purpose);
    }



    // FRAGMENT_START GAME

    public void CreateTablesChat(String emitter, String receptor)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "CreateTablesChat");
        map.put("emitter", emitter);
        map.put("receptor", receptor);
        purpose = "CreateTablesChat";
        letsQuery(map, purpose);
    }

    // FRAGMENT_CHAT

    public void SeeAllMessages(String emitter)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "SeeAllMessages");
        map.put("emitter", emitter);
        purpose = "SeeAllMessages";
        letsQuery(map, purpose);
    }

    // VIEW_GAME INTERFACE

    public void UnseenMessages(String emitter)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "UnseenMessages");
        map.put("emitter", emitter);
        purpose = "UnseenMessages";
        letsQuery(map, purpose);
    }

    // VIEW_MAIN INTERFACE

    public void DropTable(String emitter)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "DropTable");
        map.put("emitter", emitter);
        purpose = "DropTable";
        letsQuery(map, purpose);
    }

    // VIEW_MAIN CONFIGURATION

    public void getConfiguration(String idUser)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getConfiguration");
        map.put("idUser", idUser);
        purpose = "getConfiguration";
        letsQuery(map, purpose);
    }


    // FRAG_START GAME

    public void getNumOfGamesSaved(String idUser)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "getNumOfGamesSaved");
        map.put("idUser", idUser);
        purpose = "getNumOfGamesSaved";
        letsQuery(map, purpose);
    }


    // MAIN INTERFACE

    public void setBoardConfiguration(String board, String idUser)
    {
        Map<String, String> map = new Hashtable<>();
        map.put("case", "setBoardConfiguration");
        map.put("board", board);
        map.put("idUser", idUser);
        purpose = "setBoardConfiguration";
        letsQuery(map, purpose);
    }

}
