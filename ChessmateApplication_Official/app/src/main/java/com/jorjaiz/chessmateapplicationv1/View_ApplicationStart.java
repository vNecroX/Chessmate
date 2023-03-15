package com.jorjaiz.chessmateapplicationv1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;

public class View_ApplicationStart extends AppCompatActivity implements Constants
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__application_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.w(TAG, "----------------------------------------------------------------------");
        Log.w(TAG, "VIEW_APPLICATION START ON CREATE");

        try
        {
            findViewById(R.id.btnLogin).setOnClickListener(view -> gotoLogin());
            findViewById(R.id.btnCreateAcc).setOnClickListener(view -> gotoCreateAccount());
            findViewById(R.id.btnNoAcc).setOnClickListener(view -> gotoContinue());
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_APPLICATIONSTART onCreate: " + e.toString());
        }
    }

    public void gotoLogin()
    {
        try
        {
            this.startActivity(new Intent(this, View_Login.class));
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_APPLICATIONSTART gotoLogin: " + e.toString());
        }
    }

    public void gotoCreateAccount()
    {
        try
        {
            this.startActivity(new Intent(this, View_EditProfile.class).putExtra("case", "createAccount"));
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_APPLICATIONSTART gotoCreateAccount: " + e.toString());
        }
    }

    public void gotoContinue()
    {
        try
        {
            View_MainInterface.firstUserAccess = true;
            Intent i = new Intent(this, View_MainInterface.class);
            i.putExtra("idPlayer", 0);
            i.putExtra("namePlayer", "Anonimo");
            this.startActivity(i);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_APPLICATIONSTART gotoContinue: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, " ");
        Log.e(TAG, " ");
        Log.e(TAG, "* * * * * * * APP HAS BEEN CLOSED * * * * * * *");
        Log.e(TAG, " ");
        Log.e(TAG, " ");
        super.onDestroy();
    }
}
