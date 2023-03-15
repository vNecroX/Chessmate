package com.grauben98.esteb.chatfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.grauben98.esteb.chatfirebase.Adapter.MessageAdapter;
import com.grauben98.esteb.chatfirebase.Fragments.APIService;
import com.grauben98.esteb.chatfirebase.Model.Chat;
import com.grauben98.esteb.chatfirebase.Model.User;
import com.grauben98.esteb.chatfirebase.Notifications.Client;
import com.grauben98.esteb.chatfirebase.Notifications.Data;
import com.grauben98.esteb.chatfirebase.Notifications.MyResponse;
import com.grauben98.esteb.chatfirebase.Notifications.Sender;
import com.grauben98.esteb.chatfirebase.Notifications.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid;

    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Log.i("TAG", " ");
        Log.i("TAG", "ONCREATE - MESSAGEACTIVITY");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Log.i("TAG", "BEFORE GETCLIENT FROM APISERVICE");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Log.i("TAG", "AFTER GETCLIENT FROM APISERVICE");

        recyclerView = findViewById(R.id.recycler_view);
        rvSettings();

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvSettings();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.i("TAG", "BTN SEND PRESSED");

                notify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals(""))
                {
                    Log.i("TAG", "SEND MESSAGE");

                    sendMessage(fuser.getUid(), userid, msg);
                }
                else {
                    showToast("You can't send empty message.");
                }
                text_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAG", "ONDATACHANGE - addValueEventListener - onCreate - USERS - MESSAGEACTIVITY");


                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    try{
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    }catch(Exception e){ }
                }

                readMessages(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void rvSettings(){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAG", " ");
                Log.i("TAG", "ONDATACHANGE - addValueEventListener - seenMessage - CHATS - MESSAGEACTIVITY");

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                        Log.i("TAG", "MESSAGE HAS BEEN SEEN");
                        Log.i("TAG", " ");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage(String sender, final String receiver, String message){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        Log.i("TAG", " ");
        Log.i("TAG", "MESSAGE PUSHED ON CHATS - sendMessage - MESSAGEACTIVITY");

        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAG", " ");
                Log.i("TAG", "ONDATACHANGE - addListenerForSingleValueEvent - sendMessage - CHATLIST - MESSAGEACTIVITY");

                if(!dataSnapshot.exists()){
                    chatReference.child("id").setValue(userid);

                    Log.i("TAG", "ID OF THE OTHER USER PUT IN CHATLIST");
                    Log.i("TAG", " ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        //final String auxID = sender;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAG", " ");
                Log.i("TAG", "ONDATACHANGE - addValueEventListener - sendMessage - USERS - MESSAGEACTIVITY");

                User user = dataSnapshot.getValue(User.class);
                if(notify){
                    sendNotification(receiver, user.getUsername(), msg);
                    //sendNotification(auxID, user.getUsername(), msg);
                    Log.i("TAG", "SEND NOTIFICATION - notify = true");
                    Log.i("TAG", " ");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message){

        Log.i("TAG", " ");
        Log.i("TAG", "sendNotification - MESSAGEACTIVITY");
        Log.e("TAG", "ID RECEIVER: " + receiver);
        Log.i("TAG", "ID USERNAME: " + username);

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        Log.i("TAG", " ");
        Log.i("TAG", "QUERY FROM TOKENS IS BEEN MADE");
        Log.i("TAG", " ");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Log.e("TAG", "TOKEN MESSAGEACTIVITY: " + token.getToken());

                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    Log.i("TAG", "BEFORE API SEND NOTIFICATION");

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            showToast("Failed!.");
                                            Log.i("TAG", "SENDING OF NOTIF FAILED");
                                        }
                                        else
                                        {
                                            Log.i("TAG", "SUCESS SENDING THE NOTIFICATION");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }

                Log.e("TAG", " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String myid, final String userid, final String imageurl){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAG", " ");
                Log.i("TAG", "ONDATACHANGE - readMessages - MESSAGEACTIVITY");

                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);

                        Log.i("TAG", " * MENSAJE");
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        Log.i("TAG", "STATUS HA SIDO ACTUALIZADO - status - USERS - MESSAGEACTIVITY");

        reference.updateChildren(hashMap);
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();

        Log.i("TAG", "PREFERENCIAS EDITADAS - MESSAGEACTIVITY");
    }
}
