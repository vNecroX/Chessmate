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

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;


public class Fragment_LoadingBoard extends Fragment implements Constants
{
    public Fragment_LoadingBoard()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {

        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_LOADING BOARD ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__loading_board, container, false);

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

        return view;
    }
}
