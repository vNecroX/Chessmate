package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.provider.SyncStateContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.View_MainInterface;

public class MyFirebaseIdService extends FirebaseInstanceIdService implements Constants
{
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String refreshToken)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);

        databaseReference.child(Integer.toString(CP.get().getIdPlayer())).setValue(token);

        Log.e(TAG, "--- UPDATE TOKEN - FIREBASE INSTANCE ID SERVICE ---");
    }
}
