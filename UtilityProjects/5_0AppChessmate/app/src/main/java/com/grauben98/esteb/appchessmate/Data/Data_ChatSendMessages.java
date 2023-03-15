package com.grauben98.esteb.appchessmate.Data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Data_ChatSendMessages extends StringRequest {
    private static final String path = "https://estudiantescrazys.000webhostapp.com/Send_Messages.php";
    private Map<String, String> params;
    public Data_ChatSendMessages(String emitter, String receptor, String message, Response.Listener<String>listener){
        super(Request.Method.POST, path, listener, null);
        params = new HashMap<>();
        params.put("emitter",emitter+"");
        params.put("receptor",receptor+"");
        params.put("message",message+"");
    }
    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
