package com.jorjaiz.chessmateapplicationv1.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Fragment_EditNameGame;
import com.jorjaiz.chessmateapplicationv1.Game;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.R;
import com.jorjaiz.chessmateapplicationv1.User;
import com.jorjaiz.chessmateapplicationv1.View_GamersList;
import com.jorjaiz.chessmateapplicationv1.View_GamesSaved;
import com.jorjaiz.chessmateapplicationv1.View_MainInterface;

import java.util.ArrayList;
import java.util.List;

public class Adapter_GamesSaved extends RecyclerView.Adapter<Adapter_GamesSaved.GamesSavedHolder> implements Constants
{
    private ArrayList<Game> list;
    private Context ctx;

    public static View lastView;
    private static Game lastGame;

    private static AnimatorSet animatorSet;
    private AnimationDrawable animDraw;

    public static ArrayList<View> viewList;

    private MyAdapterListener myAdapterListener;

    private int superIndex;

    public interface MyAdapterListener
    {
        void onItemViewGameSavedClick(String mode, String lastState);
    }

    public Adapter_GamesSaved(ArrayList<Game> list, Context ctx, MyAdapterListener myAdapterListener)
    {
        this.list = list;
        this.ctx = ctx;
        this.myAdapterListener = myAdapterListener;
        this.superIndex = 0;
        this.viewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GamesSavedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_savedgames, viewGroup, false);
        return new GamesSavedHolder(view, ctx);
    }

    @Override
    public void onBindViewHolder(@NonNull GamesSavedHolder savedGamesHolder, int i)
    {
        savedGamesHolder.tvGameID.setText(list.get(i).getIdGame()+"");
        savedGamesHolder.tvStatus.setText(list.get(i).getLastState());
        savedGamesHolder.tvName.setText(list.get(i).getNameOfGame());
        savedGamesHolder.tvPlayerOneName.setText(list.get(i).getPlayerName());
        savedGamesHolder.tvPlayerOnePieces.setText(list.get(i).getColorPlayer());
        savedGamesHolder.tvPlayerOneStatus.setText(list.get(i).getPlayerStatus());
        savedGamesHolder.tvPlayerTwoName.setText(list.get(i).getOponentName());
        savedGamesHolder.tvPlayerTwoPieces.setText(list.get(i).getOponentColor());
        savedGamesHolder.tvPlayerTwoStatus.setText(list.get(i).getOponentStatus());
        savedGamesHolder.tvMode.setText(list.get(i).getMode());
        savedGamesHolder.tvDifficulty.setText(list.get(i).getDifficulty());

        if(savedGamesHolder.tvStatus.getText().equals("Interrumpida"))
            savedGamesHolder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorLightRed));
        else if(savedGamesHolder.tvStatus.getText().equals("Finalizada"))
            savedGamesHolder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorLightOrange));
        else
            savedGamesHolder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorLima));

        if(savedGamesHolder.tvPlayerOnePieces.getText().equals("N"))
            savedGamesHolder.tvPlayerOnePieces.setTextColor(ctx.getResources().getColor(R.color.colorGray));
        else
            savedGamesHolder.tvPlayerOnePieces.setTextColor(ctx.getResources().getColor(R.color.colorWhite));

        if(savedGamesHolder.tvPlayerTwoPieces.getText().equals("N"))
            savedGamesHolder.tvPlayerTwoPieces.setTextColor(ctx.getResources().getColor(R.color.colorGray));
        else
            savedGamesHolder.tvPlayerTwoPieces.setTextColor(ctx.getResources().getColor(R.color.colorWhite));

        setScaleAnimation(savedGamesHolder.itemView);

        if(savedGamesHolder.itemView != lastView ||
                !savedGamesHolder.tvGameID.getText().toString().equals(lastGame==null?"":lastGame.getIdGame()+""))
        {
            superIndex++;

            if(animatorSet != null && superIndex>7)
                animatorSet.cancel();

            savedGamesHolder.itemView.setBackground(ctx.getResources().getDrawable(R.drawable.gradient6));
        }
        else
        {
            superIndex = 0;

            if(animatorSet != null)
                animatorSet.cancel();

            if(animDraw != null)
                animDraw.stop();

            ObjectAnimator animX = ObjectAnimator.ofFloat(lastView, "scaleX", 1.0f, 0.9f);
            animX.setDuration(2000);
            animX.setRepeatMode(ValueAnimator.REVERSE);
            animX.setRepeatCount(Animation.INFINITE);
            animX.setInterpolator(new LinearInterpolator());

            ObjectAnimator animY = ObjectAnimator.ofFloat(lastView, "scaleY", 1.0f, 0.9f);
            animY.setDuration(2000);
            animY.setRepeatMode(ValueAnimator.REVERSE);
            animY.setRepeatCount(Animation.INFINITE);
            animY.setInterpolator(new LinearInterpolator());

            animatorSet = new AnimatorSet();
            animatorSet.playTogether(animX, animY);

            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationCancel(Animator animation)
                {
                    super.onAnimationCancel(animation);

                    ObjectAnimator auxX = ObjectAnimator.ofFloat(savedGamesHolder.itemView, "scaleX", 1.0f);
                    auxX.setDuration(100);
                    auxX.setRepeatCount(1);
                    auxX.setInterpolator(new LinearInterpolator());

                    ObjectAnimator auxY = ObjectAnimator.ofFloat(savedGamesHolder.itemView, "scaleY", 1.0f);
                    auxY.setDuration(100);
                    auxY.setRepeatCount(1);
                    auxY.setInterpolator(new LinearInterpolator());

                    AnimatorSet animatorAuxSet = new AnimatorSet();
                    animatorAuxSet.playTogether(auxX, auxY);

                    animatorAuxSet.start();
                }
            });

            animatorSet.start();

            animDraw = (AnimationDrawable) ctx.getResources().getDrawable(R.drawable.gradient_blackblue_list);
            lastView.setBackground(animDraw);
            animDraw.setEnterFadeDuration(2000);
            animDraw.setExitFadeDuration(2000);
            animDraw.start();
        }

    }

    private void setFadeAnimation(View view)
    {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000);
        view.startAnimation(anim);
    }

    private void setScaleAnimation(View view)
    {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(2000);
        anim.setInterpolator(new BounceInterpolator());
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class GamesSavedHolder extends RecyclerView.ViewHolder //implements View.OnClickListener
    {
        TextView tvGameID;
        TextView tvStatus;
        TextView tvName;
        TextView tvPlayerOneName;
        TextView tvPlayerOnePieces;
        TextView tvPlayerOneStatus;
        TextView tvPlayerTwoName;
        TextView tvPlayerTwoPieces;
        TextView tvPlayerTwoStatus;
        TextView tvMode;
        TextView tvDifficulty;

        GamesSavedHolder(View itemView, Context ctx)
        {
            super(itemView);

            tvGameID = itemView.findViewById(R.id.tvGameID);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvName = itemView.findViewById(R.id.tvName);
            tvPlayerOneName = itemView.findViewById(R.id.tvPlayerOneName);
            tvPlayerOnePieces = itemView.findViewById(R.id.tvPlayerOnePieces);
            tvPlayerOneStatus = itemView.findViewById(R.id.tvPlayerOneStatus);
            tvPlayerTwoName = itemView.findViewById(R.id.tvPlayerTwoName);
            tvPlayerTwoPieces = itemView.findViewById(R.id.tvPlayerTwoPieces);
            tvPlayerTwoStatus = itemView.findViewById(R.id.tvPlayerTwoStatus);
            tvMode = itemView.findViewById(R.id.tvMode);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);

            itemView.setOnClickListener(
                    v ->
                    {
                        if(lastView != null)
                        {
                            animatorSet.cancel();
                            lastView.setBackground(ctx.getResources().getDrawable(R.drawable.gradient6));
                        }

                        View_GamesSaved.itemIndex = Integer.valueOf(tvGameID.getText().toString());
                        View_GamesSaved.itemMode = tvMode.getText().toString();
                        View_GamesSaved.itemDifficulty = tvDifficulty.getText().toString();
                        View_GamesSaved.itemColorPlayer = tvPlayerOnePieces.getText().toString();
                        View_GamesSaved.itemGameName = tvName.getText().toString();

                        if(CP.get().getNamePlayer().equals(tvPlayerOneName.getText().toString()))
                            View_GamesSaved.itemOponentName = tvPlayerTwoName.getText().toString();
                        else
                            View_GamesSaved.itemOponentName = tvPlayerOneName.getText().toString();


                        Log.i(TAG, "ID of Game Selected " + View_GamesSaved.itemIndex);


                        ObjectAnimator animX = ObjectAnimator.ofFloat(itemView, "scaleX", 1.0f, 0.9f);
                        animX.setDuration(2000);
                        animX.setRepeatMode(ValueAnimator.REVERSE);
                        animX.setRepeatCount(Animation.INFINITE);
                        animX.setInterpolator(new LinearInterpolator());

                        ObjectAnimator animY = ObjectAnimator.ofFloat(itemView, "scaleY", 1.0f, 0.9f);
                        animY.setDuration(2000);
                        animY.setRepeatMode(ValueAnimator.REVERSE);
                        animY.setRepeatCount(Animation.INFINITE);
                        animY.setInterpolator(new LinearInterpolator());

                        animatorSet = new AnimatorSet();
                        animatorSet.playTogether(animX, animY);

                        animatorSet.addListener(new AnimatorListenerAdapter()
                        {
                            @Override
                            public void onAnimationCancel(Animator animation)
                            {
                                super.onAnimationCancel(animation);

                                ObjectAnimator auxX = ObjectAnimator.ofFloat(itemView, "scaleX", 1.0f);
                                auxX.setDuration(100);
                                auxX.setRepeatCount(1);
                                auxX.setInterpolator(new LinearInterpolator());

                                ObjectAnimator auxY = ObjectAnimator.ofFloat(itemView, "scaleY", 1.0f);
                                auxY.setDuration(100);
                                auxY.setRepeatCount(1);
                                auxY.setInterpolator(new LinearInterpolator());

                                AnimatorSet animatorAuxSet = new AnimatorSet();
                                animatorAuxSet.playTogether(auxX, auxY);

                                animatorAuxSet.start();
                            }
                        });

                        animatorSet.start();

                        AnimationDrawable animDraw = (AnimationDrawable) ctx.getResources().getDrawable(R.drawable.gradient_blackblue_list);
                        itemView.setBackground(animDraw);
                        animDraw.setEnterFadeDuration(2000);
                        animDraw.setExitFadeDuration(2000);
                        animDraw.start();

                        lastView = itemView;

                        lastGame = new Game();

                        lastGame.setIdGame(Integer.parseInt(tvGameID.getText().toString()));

                        if (myAdapterListener != null)
                            myAdapterListener.onItemViewGameSavedClick(tvMode.getText().toString(), tvStatus.getText().toString());

                    });
        }

    }


}