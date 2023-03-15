package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.View_Login;
import com.jorjaiz.chessmateapplicationv1.View_MainInterface;

import java.util.HashMap;

public class Firebase implements Constants
{
    private static FirebaseDatabase fire;

    static
    {
        fire = FirebaseDatabase.getInstance();
    }

    public static FireQuery query(Context ctx)
    {
        return new FireQuery(fire, ctx);
    }

    public static FireQuery query(ReplyReceiver ctx)
    {
        return new FireQuery(fire, ctx);
    }

}
