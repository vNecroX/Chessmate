package com.grauben98.esteb.chatfirebase.Notifications;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.e("TAG", "TUTUTUTUTUTUTUTUITUTUTUTUTUTUTU");

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser != null)
        {
            Log.e("TAG", "TOKEN HAS BEEN RENEWED");
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        databaseReference.child(firebaseUser.getUid()).setValue(token);

        Log.e("TAG", "TOKEN IS BEEN UPDATED!!!!");
    }
}
