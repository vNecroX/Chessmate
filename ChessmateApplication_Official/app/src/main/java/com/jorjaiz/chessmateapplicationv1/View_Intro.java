package com.jorjaiz.chessmateapplicationv1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class View_Intro extends AppCompatActivity
{
    ImageView iVCrown;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__intro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        iVCrown = findViewById(R.id.ivCrown);

        Handler hand = new Handler();
        hand.postDelayed(
                () ->
                {
                    Intent i = new Intent(this, View_ApplicationStart.class);
                    this.startActivity(i);

                }, 6500);

        /*final RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(1);
        rotate.setInterpolator(new OvershootInterpolator());
        iVCrown.setAnimation(rotate);
        rotate.start();*/

        Handler handler = new Handler();
        handler.postDelayed(
                () ->
                {
                    //rotate.cancel();

                    ObjectAnimator animX = ObjectAnimator.ofFloat(iVCrown, "scaleX", 1.5f, 1f);
                    animX.setDuration(500);
                    animX.setRepeatMode(ValueAnimator.RESTART);
                    animX.setRepeatCount(1);
                    animX.setInterpolator(new LinearInterpolator());

                    ObjectAnimator animY = ObjectAnimator.ofFloat(iVCrown, "scaleY", 1.5f, 1f);
                    animY.setDuration(500);
                    animY.setRepeatMode(ValueAnimator.RESTART);
                    animY.setRepeatCount(1);
                    animY.setInterpolator(new LinearInterpolator());

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animX, animY);
                    animatorSet.start();

                }, 2000);

        /*Handler handler2 = new Handler();
        handler2.postDelayed(
                () ->
                {
                    MediaPlayer mP = MediaPlayer.create(this, R.raw.netflixopening);
                    mP.start();

                }, 600);*/
    }

    @Override
    public void onBackPressed()
    {

    }
}
