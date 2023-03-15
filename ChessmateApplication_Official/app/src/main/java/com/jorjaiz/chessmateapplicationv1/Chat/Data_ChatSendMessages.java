package com.jorjaiz.chessmateapplicationv1.Chat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

import java.util.HashMap;
import java.util.Map;

public class Data_ChatSendMessages extends StringRequest implements Constants
{
    private Map<String, String> params;

    public Data_ChatSendMessages(String emitter, String receptor, String message, Response.Listener<String>listener)
    {
        super(Request.Method.POST, URL_DATABASE, listener, null);

        params = new HashMap<>();
        params.put("case", "SendMessages");
        params.put("emitter", emitter+"");
        params.put("receptor", receptor+"");
        params.put("message", message+"");
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
