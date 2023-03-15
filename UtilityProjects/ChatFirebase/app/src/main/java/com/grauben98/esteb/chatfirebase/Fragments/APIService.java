package com.grauben98.esteb.chatfirebase.Fragments;

import com.grauben98.esteb.chatfirebase.Notifications.MyResponse;
import com.grauben98.esteb.chatfirebase.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAyOfqpPo:APA91bF_sbO8BX7I0N7s_eVyxRqCgzVXL01a6dZETQR-EM2kuAFPx3bCc-1ega1wnXAy7m3AvOVia2JQL9jo2ColpjE5lv8yqBFUUeyqVs3zYKOz0-N2bkUxeQg4hSlN7I4mYaY9zAmy"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

// API SERVICE
