package com.jorjaiz.chessmateapplicationv1;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class Fragment_PauseGame extends Fragment implements Constants, Query.OnResponseDatabase
{
    private OnFragmentInteractionListener mListener;

    ParamsGame pG;

    boolean touched = false;

    int gameState;

    public Fragment_PauseGame()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            pG = parameters.getParcelable("pG");
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_PAUSE GAME ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view;

        if(pG.getMode() == PVIA && CP.get().getConn() == ONLINE && CP.get().getNumGamesSaved() < 10)
        {
            view = inflater.inflate(R.layout.fragment_fragment__pause_game, container, false);
            view.findViewById(R.id.btnPvIaPause).setOnClickListener(v -> gotoMainInterface(S_PAUSED));
        }
        else if(pG.getMode() == PVIA && CP.get().getConn() == ONLINE && pG.getNewSaved().equals(SAVED))
        {
            view = inflater.inflate(R.layout.fragment_fragment__pause_game, container, false);
            view.findViewById(R.id.btnPvIaPause).setOnClickListener(v -> gotoMainInterface(S_PAUSED));
        }
        else
        {
            view = inflater.inflate(R.layout.fragment_fragment__pause_game_pvp, container, false);
        }

        view.findViewById(R.id.btnPvIaResume).setOnClickListener(v -> gotoGameInterface());
        view.findViewById(R.id.btnPvIaResign).setOnClickListener(v -> gotoMainInterface(S_FINISHED));

        return view;
    }

    public void gotoMainInterface(int gameState)
    {
        if(!touched)
        {
            touched = true;

            this.gameState = gameState;

            if(pG.getSaveGame() == SAVEGAME && CP.get().getConn() == ONLINE)
            {
                MySQL.queryFrag(getContext(), this)
                        .setGame(gameState, pG.getStringMoves(), pG.getSecondsLeft(), pG.getRewinds(), pG.getIdGame());
            }
            else
            {
                finishGame();
            }
        }
    }

    public void finishGame()
    {
        if(gameState == S_FINISHED)
        {
            removeBLayer();
            setFragFinished();
            getFragmentManager().beginTransaction().remove(this).commit();
        }
        else
        {
            if(pG.getKindOfLocal() == IS_LOCALIA_T)
                BTCommunication.getInstance().sendData("PAUSE");

            Toast.makeText(getContext(), "Partida pausada con exito", Toast.LENGTH_SHORT).show();
            removeBLayer();
            Intent i = new Intent(getContext(), View_MainInterface.class);
            i.putExtra("idPlayer", CP.get().getIdPlayer());
            i.putExtra("namePlayer", CP.get().getNamePlayer());
            this.startActivity(i);
        }
    }

    public void gotoGameInterface()
    {
        if(!touched)
        {
            touched = true;
            removeBLayer();
            setChronoOfActivity(pG.getSecondsLeft());
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragResumeChrono(long secondsLeft);
        void onFragGotoFragGameFinished();
        void onFragRemoveBLayerPause();
    }

    public void setChronoOfActivity(long secondsLeft)
    {
        if (mListener != null)
            mListener.onFragResumeChrono(secondsLeft);
    }

    public void setFragFinished()
    {
        if (mListener != null)
            mListener.onFragGotoFragGameFinished();
    }

    public void removeBLayer()
    {
        if (mListener != null)
            mListener.onFragRemoveBLayerPause();
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, "FATAL ERROR (DATA NULL) FRAGMENT_PAUSEGAME getResponseDB");
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
                        case "setGame":
                            finishGame();
                            break;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception FRAGMENT_PAUSEGAME getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                Toast.makeText(getContext(), "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERROR TO CONNECT TO DATABASE");
            }
        }
    }


}
