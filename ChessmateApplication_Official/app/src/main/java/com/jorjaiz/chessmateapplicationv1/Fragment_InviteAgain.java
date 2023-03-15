package com.jorjaiz.chessmateapplicationv1;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;


public class Fragment_InviteAgain extends Fragment implements Constants
{
    private OnFragmentInteractionListener mListener;

    ParamsGame pG;

    TextView tvInvitation;
    Button btnPlayAgain, btnCancelAndLeave;

    DatabaseReference reference;

    boolean oponentDisconnected;

    boolean inviting;

    public Fragment_InviteAgain()
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
            oponentDisconnected = parameters.getBoolean("oponentDisconnected", false);
            inviting = parameters.getBoolean("inviting");
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_INVITE AGAIN ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__invite_again, container, false);

        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(2000);
        anim.setInterpolator(new BounceInterpolator());
        view.startAnimation(anim);

        AnimationDrawable animDraw = (AnimationDrawable) getResources().getDrawable(R.drawable.rounded_list);
        view.setBackground(animDraw);
        animDraw.setEnterFadeDuration(2000);
        animDraw.setExitFadeDuration(2000);
        animDraw.start();

        if(oponentDisconnected)
        {
            view.findViewById(R.id.btnPlayAgain).setOnClickListener(v -> cancelAndLeave());
            view.findViewById(R.id.btnCancelAndLeave).setVisibility(View.INVISIBLE);

            TextView tv = view.findViewById(R.id.tvInvitation);
            tv.setText("Jugador "+ pG.getOponentName() + " se ha desconectado");
        }
        else
        {
            if(inviting)
            {
                view.findViewById(R.id.btnPlayAgain).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.btnCancelAndLeave).setOnClickListener(v -> cancelAndLeave());

                TextView tv = view.findViewById(R.id.tvInvitation);
                tv.setText("Esperando respuesta del jugador ...");
            }
            else
            {
                view.findViewById(R.id.btnPlayAgain).setOnClickListener(v -> playAgain());
                view.findViewById(R.id.btnCancelAndLeave).setVisibility(View.INVISIBLE);

                TextView tv = view.findViewById(R.id.tvInvitation);
                tv.setText(pG.getOponentName() + " te ha invitado de nuevo a una partida ... aceptas el reto?");
            }
        }

        return view;
    }

    public void playAgain()
    {
        if(pG.getMode() == PVPONLINE && CP.get().getConn() == ONLINE)
        {
            if(pG.getKindPlayer() == MASTER)
                CommunicationInterface.getInstance().respondAsMaster("AGAIN");
            else
                CommunicationInterface.getInstance().respondAsSlave("AGAIN");
        }
        else if(pG.getMode() == PVPLOCAL)
        {
            BTCommunication.getInstance().sendData("AGAIN");
        }

        setPlayAgain();
    }

    public void cancelAndLeave()
    {
        if(!oponentDisconnected)
        {
            if(pG.getMode() == PVPONLINE && CP.get().getConn() == ONLINE)
            {
                if(pG.getKindPlayer() == MASTER)
                    CommunicationInterface.getInstance().respondAsMaster("CANCEL");
                else
                    CommunicationInterface.getInstance().respondAsSlave("CANCEL");
            }
            else
            {
                BTCommunication.getInstance().sendData("CANCEL");
            }
        }

        setCancelInvitation();
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
        void onPlayAgain();
        void onCancelInvitation();
    }

    public void setPlayAgain()
    {
        if (mListener != null)
            mListener.onPlayAgain();
    }

    public void setCancelInvitation()
    {
        if (mListener != null)
            mListener.onCancelInvitation();
    }

}
