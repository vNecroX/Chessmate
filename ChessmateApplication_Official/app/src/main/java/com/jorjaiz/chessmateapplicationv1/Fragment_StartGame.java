package com.jorjaiz.chessmateapplicationv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class Fragment_StartGame extends Fragment implements Constants, CommunicationInterface.OnCommunicationListener,
        Query.OnResponseDatabase, BTCommunication.OnBTCommunicationListener
{
    private Fragment_StartGame.OnFragmentInteractionListener mListener;

    ParamsGame pG;

    boolean touched = false;

    boolean waitingToPlay;

    public static Timer listenerDataS;

    public static Timer inactiveTimeS;
    int counterInactiveTime;

    public Fragment_StartGame() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            if (getArguments() != null)
            {
                Bundle parameters = getArguments();
                pG = parameters.getParcelable("pG");
                waitingToPlay = parameters.getBoolean("waitingToPlay");

                if(pG.getNewSaved().equals(NEW))
                {
                    if(!waitingToPlay || pG.getKindPlayer() == SLAVE)
                    {
                        inactiveTimeS = new Timer();

                        if(CP.get().getConn() == ONLINE && pG.getMode() == PVPONLINE)
                        {
                            CommunicationInterface.getInstance().setListener(this);
                            checkInactiveTime();
                        }
                        else if(pG.getMode() == PVPLOCAL)
                        {
                            listenerDataS = new Timer();
                            BTCommunication.getInstance().setListener(this);
                            timerData();
                            checkInactiveTime();
                        }
                    }

                    if(!waitingToPlay && pG.getMode() == PVIA && pG.getKindOfLocal() == IS_LOCALIA_T)
                    {
                        BTCommunication.getInstance().setListener(this);
                        BTCommunication.getInstance().sendData("IS_LOCALIA_T");
                    }

                    if(pG.getKindOfLocal() == IS_REMOTE_T)
                    {
                        if(!waitingToPlay && pG.getMode() == PVPONLINE)
                        {
                            Log.e(TAG, "=*/-*/-");

                            BTCommunication.getInstance().setListener(this);
                            BTCommunication.getInstance().sendData("IS_REMOTE_T");
                        }
                        else if(waitingToPlay && pG.getKindPlayer() == SLAVE)
                        {
                            BTCommunication.getInstance().setListener(this);
                            BTCommunication.getInstance().sendData("IS_REMOTE_T");
                        }
                    }
                }
                else
                {
                    if(pG.getKindOfLocal() == IS_LOCALIA_T && pG.getResume() == 1)
                    {
                        BTCommunication.getInstance().setListener(this);
                        BTCommunication.getInstance().sendData("IS_LOCALIA_T");
                    }
                }
            }
            Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            Log.i(TAG, "FRAG_START GAME ON CREATE");
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception FRAGMENT_STARTGAME onCreate: " + e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view;

        if(waitingToPlay && pG.getKindPlayer() == MASTER)
        {
            view = inflater.inflate(R.layout.fragment_fragment__loading, container, false);
        }
        else if(waitingToPlay)
        {
            view = inflater.inflate(R.layout.fragment_fragment__waiting_game, container, false);
        }
        else
        {
            view = inflater.inflate(R.layout.fragment_fragment__start_game, container, false);

            if(pG.getNewSaved().equals(NEW))
            {
                if(CP.get().getConn() == ONLINE && pG.getMode() == PVPONLINE)
                {
                    if(pG.getKindPlayer() == MASTER)
                    {
                        view.findViewById(R.id.bAccept).setOnClickListener(v -> gotoPlay());
                        view.findViewById(R.id.bCancel).setOnClickListener(v -> back());
                    }
                }
                else if(pG.getMode() == PVPLOCAL)
                {
                    if(pG.getKindPlayer() == MASTER)
                    {
                        view.findViewById(R.id.bAccept).setOnClickListener(v -> gotoPlay());
                        view.findViewById(R.id.bCancel).setOnClickListener(v -> back());
                    }
                }
                else if(pG.getMode() == PVIA)
                {
                    if(pG.getKindPlayer() == MASTER)
                    {
                        view.findViewById(R.id.bAccept).setOnClickListener(v -> gotoPlay());
                        view.findViewById(R.id.bCancel).setOnClickListener(v -> back());
                    }
                }
            }
            else
            {
                view.findViewById(R.id.bAccept).setOnClickListener(v -> gotoPlay());
                view.findViewById(R.id.bCancel).setOnClickListener(v -> back());

                if(pG.getResume() == 0)
                {
                    TextView tVStartGameTitle = view.findViewById(R.id.etStartGameTitle);
                    tVStartGameTitle.setText("Ver movimientos?");
                    tVStartGameTitle.setTextColor(getResources().getColor(R.color.colorTurquoise0));
                    tVStartGameTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    Button bAccept = view.findViewById(R.id.bAccept);
                    bAccept.setText("Ver");
                }
            }
        }

        return view;
    }

    public void gotoPlay()
    {
        if(!touched)
        {
            touched = true;

            if(pG.getNewSaved().equals(NEW))
            {
                setFragLoading();

                if(CP.get().getKindOfUser() == REGISTERED)
                {
                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            ()->
                            {
                                if(CP.get().getConn() == ONLINE)
                                {
                                    if(CP.get().isAutosave())
                                    {
                                        if(pG.getMode() == PVPONLINE)
                                        {
                                            MySQL.queryFrag(getContext(), this).CreateTablesChat(pG.getPlayerName(), pG.getOponentName());
                                        }
                                        else
                                        {
                                            MySQL.queryFrag(getContext(),this).getNumOfGamesSaved(CP.get().getIdPlayer()+"");
                                        }
                                    }
                                    else
                                    {
                                        if(pG.getMode() == PVPONLINE)
                                            CommunicationInterface.getInstance().respondAsMaster("NOSAVE");
                                        else if(pG.getMode() == PVPLOCAL)
                                            BTCommunication.getInstance().sendData("NOSAVE");

                                        pG.setSaveGame(NOSAVEGAME);
                                        createGame(0);
                                    }
                                }

                            }, 1500
                    );
                }
                else
                {
                    if(pG.getKindOfLocal() == IS_LOCALPVP_T)
                        BTCommunication.getInstance().sendData("NOSAVE");

                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            ()->
                            {
                                pG.setSaveGame(NOSAVEGAME);
                                createGame(0);

                            }, 2000
                    );
                }
            }
            else
            {
                Handler hdlr = new Handler();
                hdlr.postDelayed(
                        ()->
                        {
                            MySQL.queryFrag(getContext(), this).getSavedMoves(pG.getIdGame());

                        }, 1500
                );
            }
        }
    }

    public void back()
    {
        if(!touched)
        {
            Handler hdlr = new Handler();
            hdlr.postDelayed(
                    ()->
                    {
                        removeBLayer();
                        getFragmentManager().beginTransaction().remove(this).commit();

                        if(pG.getNewSaved().equals(NEW) && pG.getKindPlayer() == MASTER)
                        {
                            if(CP.get().getConn() == ONLINE && pG.getMode() == PVPONLINE)
                            {
                                CommunicationInterface.getInstance().respondAsMaster("END");
                            }
                            else if(pG.getMode() == PVPLOCAL)
                            {
                                BTCommunication.getInstance().sendData("DIE");
                            }
                            else if(pG.getMode() == PVIA && pG.getKindOfLocal() == IS_LOCALIA_T)
                            {
                                BTCommunication.getInstance().sendData("DIE");
                            }
                        }

                        if(pG.getMode() == PVPLOCAL)
                            listenerDataS.cancel();

                        if(inactiveTimeS != null)
                            inactiveTimeS.cancel();

                        Intent i = new Intent(getContext(), View_MainInterface.class);
                        i.putExtra("idPlayer", CP.get().getIdPlayer());
                        i.putExtra("namePlayer", CP.get().getNamePlayer());
                        this.startActivity(i);

                    }, 1000
            );
        }
    }

    public void createGame(int iG)
    {
        pG.setIdGame(iG);

        if(pG.getMode() == PVIA)
        {
            if(pG.getDifficulty() == EASY)
                pG.setRewinds(INFINITY_REWINDS);
            else if(pG.getDifficulty() == INTERMEDIATE)
                pG.setRewinds(FIVE_REWINDS);
            else if(pG.getDifficulty() == DIFFICULT)
                pG.setRewinds(ZERO_REWINDS);
        }
        else
        {
            pG.setRewinds(ZERO_REWINDS);
        }

        pG.setSecondsLeft(0);
        pG.setStringMoves("");

        removeBLayer();

        if(pG.getMode() == PVPLOCAL)
            listenerDataS.cancel();

        inactiveTimeS.cancel();

        Intent i = new Intent(getActivity().getBaseContext(), MainActivity.class);
        i.putExtra("pG", pG);
        this.startActivity(i);
    }

    public void timerData()
    {
        final Handler handler = new Handler();
        listenerDataS.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        BTCommunication.getInstance().listenForData();
                    }
                });
            }
        },1000,1000);
    }

    public void checkInactiveTime()
    {
        counterInactiveTime = 0;

        final Handler handler = new Handler();
        inactiveTimeS.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.i(TAG, "INACTIVE TIME -  <" + counterInactiveTime + ">");
                        counterInactiveTime++;

                        if(counterInactiveTime >= 20)
                        {
                            if(counterInactiveTime == 30)
                            {
                                Toast.makeText(getContext(), "Tiempo límite de inactividad alcanzado", Toast.LENGTH_LONG).show();

                                if(pG.getMode() == PVPLOCAL)
                                {
                                    BTCommunication.getInstance().sendData("DIE");
                                    listenerDataS.cancel();
                                }

                                inactiveTimeS.cancel();

                                Intent i = new Intent(getContext(), View_MainInterface.class);
                                i.putExtra("idPlayer", CP.get().getIdPlayer());
                                i.putExtra("namePlayer", CP.get().getNamePlayer());
                                getContext().startActivity(i);
                            }
                            else if((50-counterInactiveTime)%2 != 0)
                            {
                                //Toast.makeText(getContext(), 50-counterInactiveTime+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        },0,1000);
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (Fragment_StartGame.OnFragmentInteractionListener) activity;
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
        void onFragRemoveBLayer();
        void onFragOpponentDisconnected();
        void onFragErrorWithServer();
        void onFragLoading();
        void onFragQuitLoading();
    }

    public void removeBLayer()
    {
        if (mListener != null)
            mListener.onFragRemoveBLayer();
    }

    public void setOpponentDisconnected()
    {
        if (mListener != null)
            mListener.onFragOpponentDisconnected();
    }

    public void setErrorWithServer()
    {
        if (mListener != null)
            mListener.onFragErrorWithServer();
    }

    public void setFragLoading()
    {
        if (mListener != null)
            mListener.onFragLoading();
    }

    public void setFragQuitLoading()
    {
        if (mListener != null)
            mListener.onFragQuitLoading();
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, " Error fatal DATA NULL FRAGMENT_STARTGAME getResponseDB");
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

                    JSONObject object;

                    switch(purpose)
                    {
                        case "getNumOfGamesSaved":
                            int numGames = 0;
                            object = jA.getJSONObject(0);
                            numGames = Integer.parseInt(object.getString("numGames"));

                            CP.get().setNumGamesSaved(numGames);

                            if(numGames >= 10)
                            {
                                Toast.makeText(
                                        getContext(), "No se pueden guardar más partidas, se continuará sin guardar esta partida",
                                        Toast.LENGTH_LONG).show();

                                if(pG.getMode() == PVPONLINE)
                                    CommunicationInterface.getInstance().respondAsMaster("NOSAVE");
                                else if(pG.getMode() == PVPLOCAL)
                                    BTCommunication.getInstance().sendData("NOSAVE");

                                pG.setSaveGame(NOSAVEGAME);

                                if(pG.getMode() == PVPLOCAL)
                                    listenerDataS.cancel();

                                inactiveTimeS.cancel();

                                Handler hndlr = new Handler();
                                hndlr.postDelayed(
                                        () ->
                                        {

                                            createGame(0);

                                        }, 1000);

                            }
                            else
                            {
                                MySQL.queryFrag(getContext(), this)
                                        .createGame(pG.getMode(), pG.getDifficulty(), pG.getIdPlayer(),
                                                pG.getOponentName(), pG.getSecondsLeft(), pG.getpOneId(),
                                                pG.getRewinds());
                            }

                            break;

                        case "createGame":
                            MySQL.queryFrag(getContext(), this).getIDLastGame(pG.getIdPlayer());
                            break;

                        case "getIDLastGame":
                            int iG = 0;

                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                object = jA.getJSONObject(i);
                                iG = Integer.parseInt(object.getString("idGame"));
                            }

                            Log.w(TAG, "Game has been created in database, Game number #" + iG);

                            pG.setSaveGame(SAVEGAME);

                            if(pG.getMode() == PVPONLINE)
                                CommunicationInterface.getInstance().respondAsMaster(Integer.toString(iG));
                            else if(pG.getMode() == PVPLOCAL)
                                BTCommunication.getInstance().sendData("@"+iG+"@");

                            createGame(iG);

                            break;

                        case "getSavedMoves":
                            String sM = "";
                            long sL = 0;
                            int r = 0;

                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                object = jA.getJSONObject(i);

                                sM = object.getString("stringMove");
                                sL = Long.parseLong(object.getString("secondsLeft"));
                                r = Integer.parseInt(object.getString("rewinds"));
                            }

                            pG.setStringMoves(sM);
                            pG.setSecondsLeft(sL);
                            pG.setRewinds(r);

                            if(pG.getNewSaved().equals(SAVED) && pG.getResume() == 1)
                            {
                                if(CP.get().isAutosave())
                                    pG.setSaveGame(SAVEGAME);
                                else
                                    pG.setSaveGame(NOSAVEGAME);

                                MySQL.queryFrag(getContext(), this).setGame(3,
                                        pG.getStringMoves(), pG.getSecondsLeft(), pG.getRewinds(), pG.getIdGame());
                            }

                            removeBLayer();

                            Intent i = new Intent(getActivity().getBaseContext(), MainActivity.class);
                            i.putExtra("pG", pG);
                            this.startActivity(i);
                            break;

                        case "CreateTablesChat":
                            MySQL.queryFrag(getContext(),this).getNumOfGamesSaved(CP.get().getIdPlayer()+"");
                            break;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception FRAGMENT_STARTGAME getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                Log.e(TAG, "ERROR TO CONNECT TO DATABASE");
                touched = false;
                setFragQuitLoading();
                setErrorWithServer();
            }
        }
    }

    // COMMUNICATION IMPLEMENTATIONS

    @Override
    public void masterReplays(String data)
    {
        if(pG.getKindPlayer() == SLAVE)
        {
            if(data.equals("END"))
            {
                setOpponentDisconnected();
            }
            else if(data.equals("NOSAVE"))
            {
                pG.setSaveGame(NOSAVEGAME);
                createGame(0);
            }
            else
            {
                pG.setSaveGame(SAVEGAME);
                createGame(Integer.parseInt(data));
            }

        }
    }

    @Override
    public void slaveReplays(String data)
    {

    }

    // COMMUNICATION BLUETOOTH IMPLEMENTATIONS

    @Override
    public void bluetoothReply(String data)
    {
        if(!data.equals(""))
        {
            if(pG.getKindPlayer() == SLAVE)
            {
                if(data.equals("NOSAVE"))
                {
                    BTCommunication.getInstance().sendData("START");

                    pG.setSaveGame(NOSAVEGAME);

                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            () ->
                            {
                                createGame(0);

                            }, 1000
                    );
                }
                else if(data.charAt(0) == '@')
                {
                    BTCommunication.getInstance().sendData("START");

                    ArrayList<String> aL = new ArrayList<>();

                    for(int i=0; i<data.length(); i++)
                    {
                        if(data.charAt(i) == '@')
                        {
                            String aux = "";

                            for(int j=i+1; j<data.length(); j++)
                            {
                                if(data.charAt(j) == '@')
                                {
                                    break;
                                }
                                else
                                {
                                    aux += data.charAt(j);
                                }
                            }

                            aL.add(aux);
                        }
                    }

                    pG.setSaveGame(SAVEGAME);

                    Handler hdlr = new Handler();
                    hdlr.postDelayed(
                            () ->
                            {
                                createGame(Integer.parseInt(aL.get(0)));

                            }, 1000
                    );
                }
                else if(data.equals("DIE"))
                {
                    back();
                }
            }
        }
    }
}
