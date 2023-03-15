package com.grauben98.esteb.chatfirebase.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.grauben98.esteb.chatfirebase.Adapter.UserAdapter;
import com.grauben98.esteb.chatfirebase.Model.Chat;
import com.grauben98.esteb.chatfirebase.Model.Chatlist;
import com.grauben98.esteb.chatfirebase.Model.User;
import com.grauben98.esteb.chatfirebase.Notifications.Token;
import com.grauben98.esteb.chatfirebase.R;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<Chatlist> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("TAG", " ");
        Log.d("TAG", "ONCREATE VIEW - CHATSFRAGMENT");


        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("TAG", "ONDATACHANGE - addValueEventListener - CHATLIST - CHATSFRAGMENT");

                usersList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token){

        Log.w("TAG", " ");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);

        databaseReference.child(fuser.getUid()).setValue(token1);

        Log.d("TAG", "TOKEN OF YOU USER HAS BEEN CHANGED IN RAMA TOKENS");

        Log.d("TAG", "UPDATETOKEN - CHATSFRAGMENT");

        Log.e("TAG", "NEW TOKEN: " + token1.getToken());

        Log.e("TAG", "ID OF YOU USER: " + fuser.getUid());

        Log.d("TAG", " ");
    }

    private void chatList() {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("TAG", "ONDATACHANGE - chatlist - USERS - CHATSFRAGMENT");

                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    for(Chatlist chatlist : usersList)
                    {
                        if(user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
