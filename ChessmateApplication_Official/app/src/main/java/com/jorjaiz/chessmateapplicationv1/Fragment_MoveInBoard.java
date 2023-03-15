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

public class Fragment_MoveInBoard extends Fragment implements Constants
{
    int moveWho;

    public Fragment_MoveInBoard()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            moveWho = parameters.getInt("moveWho", MOVEIA);
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_MOVE IN BOARD ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__move_in_board, container, false);

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

        TextView tVMoveIA = view.findViewById(R.id.tVMoveIA);
        TextView tVMoveYou = view.findViewById(R.id.tVMoveYou);
        TextView tVMoveOpponent = view.findViewById(R.id.tVMoveOpponent);
        TextView tVMoveInBoard = view.findViewById(R.id.tVMoveInBoard);

        if(moveWho == MOVEIA)
        {
            tVMoveYou.setVisibility(View.INVISIBLE);
            tVMoveYou.setVisibility(View.GONE);
            tVMoveOpponent.setVisibility(View.INVISIBLE);
            tVMoveOpponent.setVisibility(View.GONE);
            tVMoveInBoard.setVisibility(View.INVISIBLE);
            tVMoveInBoard.setVisibility(View.GONE);
        }
        else if(moveWho == MOVEYOU)
        {
            tVMoveIA.setVisibility(View.INVISIBLE);
            tVMoveIA.setVisibility(View.GONE);
            tVMoveOpponent.setVisibility(View.INVISIBLE);
            tVMoveOpponent.setVisibility(View.GONE);
            tVMoveInBoard.setVisibility(View.INVISIBLE);
            tVMoveInBoard.setVisibility(View.GONE);
        }
        else if(moveWho == MOVEOPPONENT)
        {
            tVMoveIA.setVisibility(View.INVISIBLE);
            tVMoveIA.setVisibility(View.GONE);
            tVMoveYou.setVisibility(View.INVISIBLE);
            tVMoveYou.setVisibility(View.GONE);
            tVMoveInBoard.setVisibility(View.INVISIBLE);
            tVMoveInBoard.setVisibility(View.GONE);
        }
        else
        {
            tVMoveIA.setVisibility(View.INVISIBLE);
            tVMoveIA.setVisibility(View.GONE);
            tVMoveYou.setVisibility(View.INVISIBLE);
            tVMoveYou.setVisibility(View.GONE);
            tVMoveOpponent.setVisibility(View.INVISIBLE);
            tVMoveOpponent.setVisibility(View.GONE);
        }

        return view;
    }

}
