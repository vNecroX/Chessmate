package com.jorjaiz.chessmateapplicationv1.Settings;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

import java.util.HashMap;
import java.util.Map;

public class Data_MainConfiguration extends StringRequest implements Constants
{
    private Map<String, String> params;

    public Data_MainConfiguration(String c , int autos, int board, int chat, int notif, String idUser, Response.Listener<String>listener)
    {
        super(Request.Method.POST, URL_DATABASE, listener, null);
        params = new HashMap<>();
        params.put("case",c);
        params.put("autosave",autos+"");
        params.put("board",board+"");
        params.put("chat",chat+"");
        params.put("notif",notif+"");
        params.put("idUser",idUser);
    }

    public Data_MainConfiguration(String c, int board, String idUser, Response.Listener<String>listener)
    {
        super(Request.Method.POST, URL_DATABASE, listener, null);
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
