package com.grauben98.esteb.appchessmate.Data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.URL;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.idUser;

public class Data_MainConfiguration extends StringRequest {
    private Map<String, String> params;

    public Data_MainConfiguration(String c , int autos, int board, int chat, int notif, String idUser, Response.Listener<String>listener){
        super(Request.Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("case",c);
        params.put("autosave",autos+"");
        params.put("board",board+"");
        params.put("chat",chat+"");
        params.put("notif",notif+"");
        params.put("idUser",idUser);
    }

    public Data_MainConfiguration(String c, int board, String idUser, Response.Listener<String>listener){
        super(Request.Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("case",c);
        params.put("board",board+"");
        params.put("idUser",idUser);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
