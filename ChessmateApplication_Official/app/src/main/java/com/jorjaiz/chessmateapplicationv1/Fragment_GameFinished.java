package com.jorjaiz.chessmateapplicationv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;


public class Fragment_GameFinished extends Fragment implements Constants
{

    TextView tVGameStatus;

    private Fragment_GameFinished.OnFragmentInteractionListener mListener;
    boolean touched = false;
    boolean touched2 = false;

    ParamsGame paramsGame;

    int gameState;

    boolean waitingForPlayer = false;

    boolean enableNewGame;

    public Fragment_GameFinished()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            paramsGame = parameters.getParcelable("pG");
            gameState = parameters.getInt("gameState");
            enableNewGame = parameters.getBoolean("enableNewGame", true);
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_GAME FINISHED ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__game_finished, container, false);

        if(enableNewGame)
            view.findViewById(R.id.btnGameFinishedNewGame).setOnClickListener(v -> gotoPlayAgain());

        view.findViewById(R.id.btnGameFinishedExitGame).setOnClickListener(v -> gotoMainInterface());
        view.findViewById(R.id.btnRewatch).setOnClickListener(v -> gotoRewatch());

        tVGameStatus = view.findViewById(R.id.tvGameStatus);

        switch (gameState)
        {
            case FINISHEDBYIA:
            case PAUSED:
                tVGameStatus.setText("PERDISTE :c");
                break;

            case FINISHEDBYP:
                tVGameStatus.setText("GANASTE!!!");
                break;

            case DRAWMOVBYIA:
            case DRAWMOVBYP:
            case DRAWMAT:
                tVGameStatus.setText("EMPATE .-.");
                break;
        }
        return view;
    }

    public void gotoPlayAgain()
    {
        if(!touched)
        {
            touched = true;

            Intent i = null;

            switch(paramsGame.getMode())
            {
                case PVIA:
                    if(paramsGame.getKindOfLocal() == IS_LOCALIA_T)
                    {
                        BTCommunication.getInstance().sendData("AGAIN");
                    }
                    i = new Intent(getContext(), View_Difficulty.class);
                    break;

                case PVPLOCAL:
                    BTCommunication.getInstance().sendData("ASK");
                    i = new Intent(getContext(), View_PiecesColor_Bluetooth.class);
                    waitingForPlayer = true;
                    break;

                case PVPONLINE:
                    if(CP.get().getConn() == ONLINE)
                    {
                        if(paramsGame.getKindPlayer() == MASTER)
                            CommunicationInterface.getInstance().respondAsMaster("ASK");
                        else
                            CommunicationInterface.getInstance().respondAsSlave("ASK");

                        i = new Intent(getContext(), View_PiecesColor.class);

                        waitingForPlayer = true;
                    }
                    else
                    {
                        i = new Intent(getContext(), View_GamersList.class);
                    }

                    break;
            }

            if(i == null)
            {
                i = new Intent(getContext(), View_MainInterface.class);
                i.putExtra("case", "PauseGame");
            }

            if(waitingForPlayer)
            {
                setInvitationAgain();
            }
            else
            {
                i.putExtra("pG", paramsGame);
                this.startActivity(i);
            }
        }
    }

    public void gotoMainInterface()
    {
        if(!touched2)
        {
            touched2 = true;

            if(paramsGame.getMode() == PVPONLINE && CP.get().getConn() == ONLINE)
            {
                if(paramsGame.getKindOfLocal() == IS_REMOTE_T)
                {
                    BTCommunication.getInstance().sendData("FINISH");

                    if(MainActivity.timerMainActivity != null)
                        MainActivity.timerMainActivity.cancel();
                }

                if(paramsGame.getKindPlayer() == MASTER)
                    CommunicationInterface.getInstance().respondAsMaster("FINISH");
                else
                    CommunicationInterface.getInstance().respondAsSlave("FINISH");

                if(paramsGame.getMode() == PVPONLINE && CP.get().isChat())
                    if(MainActivity.tNewMessages != null)
                        MainActivity.tNewMessages.cancel();
            }
            else if(paramsGame.getMode() == PVPLOCAL)
            {
                if(paramsGame.getKindPlayer() == MASTER)
                    BTCommunication.getInstance().sendData("FINISH");
                else
                    BTCommunication.getInstance().sendData("FINISH");

                if(MainActivity.timerMainActivity != null)
                    MainActivity.timerMainActivity.cancel();
            }

            if(paramsGame.getMode() == PVIA && paramsGame.getKindOfLocal() == IS_LOCALIA_T)
            {
                BTCommunication.getInstance().sendData("FINISH");

                if(MainActivity.timerMainActivity != null)
                    MainActivity.timerMainActivity.cancel();
            }

            Intent i = new Intent(getContext(), View_MainInterface.class);
            i.putExtra("case", "PauseGame");
            i.putExtra("idPlayer", CP.get().getIdPlayer());
            i.putExtra("namePlayer", CP.get().getNamePlayer());
            i.putExtra("tryConn", true);
            this.startActivity(i);
        }
    }

    public void gotoRewatch()
    {
        setRewatch();
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (Fragment_GameFinished.OnFragmentInteractionListener) activity;
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
        void onFragRewatch();
        void onFragInviteAgain();
    }

    public void setRewatch()
    {
        if (mListener != null)
            mListener.onFragRewatch();
    }

    public void setInvitationAgain()
    {
        if (mListener != null)
            mListener.onFragInviteAgain();
    }


}
