package com.jorjaiz.chessmateapplicationv1.Chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.MainActivity;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Fragment_Chat extends Fragment implements Interface_BackPressed, Constants, Query.OnResponseDatabase
{
    public MainActivity gameInterfaceOnline;

    private List<POJO_Chat> list;
    private RecyclerView recyclerView;
    private Adapter_Chat adapter;

    private EditText etWriteMssg;
    private Button btnSendMssg;

    private Timer timer = new Timer();

    public String mssg = "", time = "", jsonRTime = "";

    public boolean chatLimit = true;

    public static int type = 0;
    public static int auxResLength = 0, resLength = 0;

    public static int contOp = 0;
    public static boolean msggOp = false;

    private String emitter;
    private String receiver;

    public Fragment_Chat()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            this.emitter = parameters.getString("emitter");
            this.receiver = parameters.getString("receiver");
        }

        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAGMENT_CHAT ON CREATE");

        Log.w(TAG, "Emitter: " + emitter);
        Log.w(TAG, "Receiver: " + receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__chat, container, false);

        recyclerView = view.findViewById(R.id.rvMessages);
        etWriteMssg = view.findViewById(R.id.etChatMessage);
        btnSendMssg = view.findViewById(R.id.btnSend);

        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Adapter_Chat(list, getActivity());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapterItemCount();
                    }
                }, 1);
            }
        });

        btnSendMssg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mssg = etWriteMssg.getText().toString().trim();

                if (!mssg.isEmpty())
                {
                    sendMessage();
                    adapterItemCount();
                    etWriteMssg.setText("");
                }
                else
                {
                    etWriteMssg.setError("Coloque un mensaje.");
                }
            }
        });

        if(resLength > 49)
            disableChat();

        MySQL.queryFrag(getContext(), Fragment_Chat.this).SeeAllMessages(CP.get().getNamePlayer()); //receiveMessage

        timerReceive();
        auxResLength = 0;

        return view;
    }

    public void adapterItemCount()
    {
        if(adapter.getItemCount() > 1)
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    public void sendMessage()
    {
        Response.Listener<String> response = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonHour = new JSONObject(response);
                    jsonRTime = jsonHour.getString("message_time");
                    newMessage(mssg, 1, jsonRTime);
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "FRAGMENT_CHAT sendMessage exception: " + e.toString());
                }
            }
        };

        if(resLength > 49)
            disableChat();

        Data_ChatSendMessages data_chatSendMessages = new Data_ChatSendMessages(emitter, receiver, mssg, response);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(data_chatSendMessages);
    }

    public void newMessage(String message, int type, String time)
    {
        POJO_Chat pojo_chat = new POJO_Chat();
        pojo_chat.setMessage(message);
        pojo_chat.setMessType(type);
        pojo_chat.setMessageTime(time);
        list.add(pojo_chat);
        adapter.notifyDataSetChanged();
    }

    public void timerReceive()
    {
        final Handler handler = new Handler();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        MySQL.queryFrag(getContext(), Fragment_Chat.this).SeeAllMessages(CP.get().getNamePlayer()); //receiveMessage
                    }
                });
            }
        },500, 5000);
    }

    public void disableChat()
    {
        etWriteMssg.setText(R.string.messagesLimit);
        etWriteMssg.setGravity(Gravity.CENTER);
        etWriteMssg.setInputType(InputType.TYPE_NULL);
        etWriteMssg.setClickable(false);
        etWriteMssg.setLongClickable(false);
        btnSendMssg.setVisibility(View.INVISIBLE);
        btnSendMssg.setClickable(false);

        if (chatLimit)
        {
            View view = getActivity().getCurrentFocus();

            if (view != null)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            chatLimit = false;
        }
    }

    // INTERFACES IMPLEMENTATION

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(context instanceof MainActivity)
        {
            ((MainActivity) context).setInterface_backPressed(this);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(getActivity() instanceof MainActivity)
        {
            ((MainActivity)getActivity()).setInterface_backPressed(null);
        }
    }

    @Override
    public void onBackPressed()
    {
        timer.cancel();

        gameInterfaceOnline = (MainActivity) getActivity();
        gameInterfaceOnline.btnChat.setEnabled(true);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, "FATAL ERROR (DATA NULL) FRAGMENT_CHAT getResponseDB");
        }
        else
        {
            if(data.containsKey("DATA"))
            {
                try
                {
                    JSONArray jA = null;

                    if(data.get("DATA")!=null)
                        jA = data.get("DATA");

                    switch(purpose)
                    {
                        case "SeeAllMessages":

                            if(jA != null)
                            {
                                resLength = jA.length();

                                if (resLength > 49)
                                    disableChat();

                                if (auxResLength != resLength)
                                {
                                    list.clear();
                                    JSONObject jsonObject;

                                    for (int i = 0; i < jA.length(); i++)
                                    {
                                        try
                                        {
                                            jsonObject = jA.getJSONObject(i);

                                            mssg = jsonObject.getString("message");
                                            type = jsonObject.getInt("message_type");
                                            time = jsonObject.getString("message_time");

                                            //Log.w(TAG, "LENGTH OF RES: " + resLength);
                                            //Log.i(TAG, "ID OF MESSAGE: " + MainActivity.idMssg);

                                            if(!MainActivity.fChatOpened)
                                            {
                                                if (resLength > auxResLength)
                                                {
                                                    if (type == 2)
                                                    {
                                                        //Log.i(TAG, "rival: " + "Here!");

                                                        msggOp = true;
                                                        contOp++;

                                                        //Log.e(TAG, "contOp: " + contOp);

                                                        auxResLength = resLength;
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                if(resLength > auxResLength)
                                                {
                                                    contOp = 0;
                                                    auxResLength = resLength;
                                                    MainActivity.fChatOpened = false;
                                                }
                                            }

                                            newMessage(mssg, type, time);
                                        }
                                        catch (JSONException e)
                                        {
                                            Log.e(TAG, "FRAGMENT_CHAT receive message exception: " + e.toString());
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception FRAGMENT_CHAT getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                Toast.makeText(getContext(), "CHAT: No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ERROR TO CONNECT TO DATABASE");
            }
        }
    }
}
