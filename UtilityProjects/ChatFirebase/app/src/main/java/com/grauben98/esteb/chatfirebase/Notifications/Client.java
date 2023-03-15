package com.grauben98.esteb.chatfirebase.Notifications;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url)
    {
        Log.e("TAG", "CLIENT RETROFIT URL: " + url);

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
