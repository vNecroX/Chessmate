package com.jorjaiz.chessmateapplicationv1.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.R;
import com.jorjaiz.chessmateapplicationv1.User;
import com.jorjaiz.chessmateapplicationv1.View_GamersList;

import java.util.List;

public class Adapter_GamersList extends RecyclerView.Adapter<Adapter_GamersList.GamersHolder> implements Constants
{

    private List<User> list;
    private Context ctx;

    public static View lastView;
    public static User lastUser;

    private static AnimatorSet animatorSet;
    private AnimationDrawable animDraw;

    private int superIndex;

    public MyAdapterGamersListListener myAdapterListener;

    public interface MyAdapterGamersListListener
    {
        void onItemViewGamersListClicked(boolean enableButton);
    }

    public Adapter_GamersList(List<User> list, Context ctx, MyAdapterGamersListListener listener)
    {
        this.list = list;
        this.ctx = ctx;
        this.superIndex = 0;
        this.myAdapterListener = listener;
    }

    @NonNull
    @Override
    public GamersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_gamerslist, viewGroup, false);
        return new GamersHolder(view, ctx);
    }

    @Override
    public void onBindViewHolder(@NonNull GamersHolder gamersHolder, int i)
    {
        gamersHolder.tvGamerName.setText(list.get(i).getPlayerName());
        gamersHolder.tvNationName.setText(list.get(i).getNation());

        setFadeAnimation(gamersHolder.itemView);

        final int index = i;

        if(gamersHolder.itemView != lastView ||
                !gamersHolder.tvGamerName.getText().toString().equals(lastUser==null?"":lastUser.getPlayerName()))
        {
            superIndex++;

            /*Log.w(TAG, " ");
            Log.w(TAG, "=-------------- New itemview in the scope -----------------=");
            Log.w(TAG, " ");*/

            if(animatorSet != null && superIndex>7)
                animatorSet.cancel();

            gamersHolder.itemView.setBackground(ctx.getResources().getDrawable(R.drawable.gradient6));
            gamersHolder.tvGamerName.setTextColor(ctx.getResources().getColor(R.color.colorTurquoise0));
            gamersHolder.tvNationName.setTextColor(ctx.getResources().getColor(R.color.colorTurquoise0));
        }
        else
        {
            superIndex = 0;

            /*Log.i(TAG, " ");
            Log.i(TAG, "===------------ Item selected in the scope ---------------===");
            Log.i(TAG, " ");*/

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

                    ObjectAnimator auxX = ObjectAnimator.ofFloat(gamersHolder.itemView, "scaleX", 1.0f);
                    auxX.setDuration(100);
                    auxX.setRepeatCount(1);
                    auxX.setInterpolator(new LinearInterpolator());

                    ObjectAnimator auxY = ObjectAnimator.ofFloat(gamersHolder.itemView, "scaleY", 1.0f);
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

        if(list.get(i).getConnection() == ONLINE)
        {
            gamersHolder.itemView.setBackground(ctx.getResources().getDrawable(R.drawable.gradient11));
            gamersHolder.tvGamerName.setTextColor(ctx.getResources().getColor(R.color.colorLima));
            gamersHolder.tvNationName.setTextColor(ctx.getResources().getColor(R.color.colorLima));
        }
        else if(list.get(i).getConnection() == BUSSY)
        {
            gamersHolder.itemView.setBackground(ctx.getResources().getDrawable(R.drawable.gradient14));
            gamersHolder.tvGamerName.setTextColor(ctx.getResources().getColor(R.color.colorLightOrange));
            gamersHolder.tvNationName.setTextColor(ctx.getResources().getColor(R.color.colorLightOrange));
        }

        gamersHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.w(TAG, "---> CLICK IN THE ITEM VIEW #" + index);

                View_GamersList.indexItemView = index;

                if(lastView != null)
                {
                    animatorSet.cancel();
                    lastView.setBackground(ctx.getResources().getDrawable(R.drawable.gradient6));
                }

                ObjectAnimator animX = ObjectAnimator.ofFloat(gamersHolder.itemView, "scaleX", 1.0f, 0.9f);
                animX.setDuration(2000);
                animX.setRepeatMode(ValueAnimator.REVERSE);
                animX.setRepeatCount(Animation.INFINITE);
                animX.setInterpolator(new LinearInterpolator());

                ObjectAnimator animY = ObjectAnimator.ofFloat(gamersHolder.itemView, "scaleY", 1.0f, 0.9f);
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

                        ObjectAnimator auxX = ObjectAnimator.ofFloat(gamersHolder.itemView, "scaleX", 1.0f);
                        auxX.setDuration(100);
                        auxX.setRepeatCount(1);
                        auxX.setInterpolator(new LinearInterpolator());

                        ObjectAnimator auxY = ObjectAnimator.ofFloat(gamersHolder.itemView, "scaleY", 1.0f);
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
                gamersHolder.itemView.setBackground(animDraw);
                animDraw.setEnterFadeDuration(2000);
                animDraw.setExitFadeDuration(2000);
                animDraw.start();

                lastView = gamersHolder.itemView;

                lastUser = new User();

                lastUser.setPlayerName(gamersHolder.tvGamerName.getText().toString());

                if(list.get(index).getConnection() == OFFLINE || list.get(index).getConnection() == BUSSY)
                {
                    myAdapterListener.onItemViewGamersListClicked(false);
                }
                else
                {
                    myAdapterListener.onItemViewGamersListClicked(true);
                }
            }
        });
    }

    private void setFadeAnimation(View view)
    {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class GamersHolder extends RecyclerView.ViewHolder
    {
        TextView tvGamer;
        TextView tvNation;
        TextView tvGamerName;
        TextView tvNationName;

        ViewGroup.LayoutParams layoutParams;

        GamersHolder(View itemView, Context ctx)
        {
            super(itemView);
            tvGamer = itemView.findViewById(R.id.tvPlayer);
            tvNation = itemView.findViewById(R.id.tvNation);
            tvGamerName = itemView.findViewById(R.id.tvPlayerName);
            tvNationName = itemView.findViewById(R.id.tvNationName);

            layoutParams = itemView.getLayoutParams();

        }
    }
}
