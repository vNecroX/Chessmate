package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jorjaiz.chessmateapplicationv1.Adapters.Adapter_GamersList;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.R;
import com.jorjaiz.chessmateapplicationv1.View_Login;
import com.jorjaiz.chessmateapplicationv1.View_MainInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireQuery implements Constants
{
    public interface OnResponseFireQuery
    {
        void getResponseFireQuery(HashMap<String, Object> data, String purpose);
    }

    private OnResponseFireQuery fqListener;

    private String purpose;

    private FirebaseDatabase fire;
    private DatabaseReference ref;

    public FireQuery(FirebaseDatabase fire, Context ctx)
    {
        this.fire = fire;
        this.fqListener = (OnResponseFireQuery)ctx;
    }

    public FireQuery(FirebaseDatabase fire, ReplyReceiver ctx)
    {
        this.fire = fire;
        this.fqListener = ctx;
    }

    // VIEW_LOGIN

    public void registerPlayer(int idPlayer, String namePlayer)
    {
        ref = fire.getReference("Players").child(String.valueOf(idPlayer));

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("idPlayer", String.valueOf(idPlayer));
        hashMap.put("namePlayer", namePlayer);
        hashMap.put("connection", ONLINE);
        purpose = "registerPlayer";

        ref.setValue(hashMap).addOnCompleteListener(
                task ->
                {
                    if(task.isSuccessful())
                        makeToken(FirebaseInstanceId.getInstance().getToken(), idPlayer, namePlayer);
                });
    }

    // VIEW_LOGIN

    public void makeToken(String token, int idPlayer, String namePlayer)
    {
        ref = fire.getReference("Tokens");
        Token token1 = new Token(token);

        Log.i(TAG, " - - - > TOKEN CREATED: " + token1.getToken());

        ref.child(Integer.toString(idPlayer)).setValue(token1).addOnCompleteListener(
                task ->
                {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("idPlayer", idPlayer);
                    map.put("namePlayer", namePlayer);

                    fqListener.getResponseFireQuery(map, purpose);
                });
    }

    // VIEW_LOGIN

    public void updateToken(String token, int idPlayer)
    {
        ref = fire.getReference("Tokens");
        Token token1 = new Token(token);

        Log.i(TAG, " - - - > TOKEN UPDATED: " + token1.getToken());

        ref.child(Integer.toString(idPlayer)).setValue(token1).addOnCompleteListener(
                task ->
                {
                });
    }

    // VIEW_EDIT PROFILE

    public void sendNamePlayer(int idPlayer, String namePlayer)
    {
        ref = fire.getReference("Players").child(String.valueOf(idPlayer));

        HashMap<String, Object> map = new HashMap<>();
        map.put("namePlayer", namePlayer);
        purpose = "sendNamePlayer";

        ref.updateChildren(map);

        fqListener.getResponseFireQuery(null, purpose);
    }

    // VIEW_MAIN INTERFACE, VIEW_APPLICATION START

    public void updateConnPlayer(int idPlayer, int conn)
    {
        ref = fire.getReference("Players").child(String.valueOf(idPlayer));

        ref.child("connection").onDisconnect().setValue(2);

        HashMap<String, Object> map = new HashMap<>();
        map.put("connection", conn);

        purpose = "updateConnPlayer";

        ref.updateChildren(map);
    }

    // VIEW_GAMERSLIST, VIEW_GAMERSLIST, FRAG_START GAME

    public void getPlayers()
    {
        ref = fire.getReference("Players");
        purpose = "getPlayers";

        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashMap<String, Object> map = new HashMap<>();
                map.put("dataSnapshot", dataSnapshot);

                fqListener.getResponseFireQuery(map, purpose);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    // VIEW_GAMERSLIST

    public void prepareNotification(String token, String senderId)
    {
        Log.w(TAG, "INSIDE sendInvitation FUNCTION");

        ref = fire.getReference("Tokens");
        Token token1 = new Token(token);

        purpose = "prepareNotification";

        Log.w(TAG, "RENEWED TOKEN: " + token1.getToken());

        ref.child(senderId).setValue(token1).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.w(TAG, "TOKEN has been updated");

                fqListener.getResponseFireQuery(null, purpose);
            }
        });
    }

    // VIEW_GAMERS LIST

    public void sendNotification(String senderId, String receiverId)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderId);
        hashMap.put("receiver", receiverId);
        hashMap.put("isReplay", false);
        hashMap.put("isAccepted", false);

        purpose = "sendNotification";

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Notifications").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.w(TAG, "PUSH INVITATION COMPLETED");

                fqListener.getResponseFireQuery(null, purpose);

            }
        });
    }

    // VIEW_GAMERSLIST

    public void getNotifications()
    {
        ref = fire.getReference("Notifications");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.w(TAG, "GET INVITATIONS COMPLETED");

                HashMap<String, Object> map = new HashMap<>();
                map.put("dataSnapshot", dataSnapshot);

                purpose = "getNotifications";

                fqListener.getResponseFireQuery(map, purpose);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    // VIEW_GAMERSLIST

    public void getPlayerListener(String senderId)
    {
        purpose = "getPlayerListener";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Players").child(senderId);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                fqListener.getResponseFireQuery(null, purpose);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    // VIEW_GAMERSLIST, REPLY RECEIVER

    public void getTokenOfReceiverListener(String receiverId)
    {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverId);

        purpose = "getTokenOfReceiverListener";

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashMap<String, Object> map = new HashMap<>();
                map.put("dataSnapshot", dataSnapshot);

                fqListener.getResponseFireQuery(map, purpose);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    // VIEW_GAMERSLIST, REPLY RECEIVER

    public void deliverNotification(Sender sender)
    {
        APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        purpose = "deliverNotification";

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>()
                {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
                    {
                        if(response.code() == 200)
                        {
                            if(response.body().success != 1)
                            {
                                Toast.makeText((Context)fqListener, "No se pudo mandar solicitud. Volver a intentar",
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "ERROR SENDING THE NOTIFICATION");
                            }
                            else
                            {
                                Log.w(TAG, "SUCCESS SENDING THE NOTIFICATION");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t)
                    {

                    }
                });
    }


}
