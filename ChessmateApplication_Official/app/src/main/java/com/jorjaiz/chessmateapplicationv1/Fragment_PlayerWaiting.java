package com.jorjaiz.chessmateapplicationv1;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;


public class Fragment_PlayerWaiting extends Fragment implements Constants
{

    public Fragment_PlayerWaiting()
    {
    }

    boolean playerWaiting;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            playerWaiting = parameters.getBoolean("playerWaiting", false);
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_PLAYER WAITING ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_fragment__player_waiting, container, false);

        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(2000);
        anim.setInterpolator(new BounceInterpolator());
        view.startAnimation(anim);

        TextView tVW1 = view.findViewById(R.id.tVw1);
        TextView tVW2 = view.findViewById(R.id.tVw2);

        if(playerWaiting) // if oponent is ready
        {
            view.setBackground(getResources().getDrawable(R.drawable.gradient11));
            tVW1.setVisibility(View.INVISIBLE);
            tVW1.setVisibility(View.GONE);
        }
        else
        {
            view.setBackground(getResources().getDrawable(R.drawable.gradient14));
            tVW2.setVisibility(View.INVISIBLE);
            tVW2.setVisibility(View.GONE);
        }

        return view;
    }

}
