package com.jorjaiz.chessmateapplicationv1.Firebase;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYOnyZA4:APA91bGFtZLnplkUfkTCysmIFSK5Y0Y3Oom84IyJ2Nn9ItbS0cNpHeHDH5G9SY_nGRESagWKvuYTko1JXRm5OlbZthv1eHBPMD-MlHss3Pk13OpXnUO41Rf6rctdYgRbpizCFUGT8AL2"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
