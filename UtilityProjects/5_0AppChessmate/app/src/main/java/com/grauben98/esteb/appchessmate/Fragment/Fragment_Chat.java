package com.grauben98.esteb.appchessmate.Fragment;

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
import com.grauben98.esteb.appchessmate.Adapter.Adapter_Chat;
import com.grauben98.esteb.appchessmate.Data.Data_ChatSendMessages;
import com.grauben98.esteb.appchessmate.Interface.Interface_BackPressed;
import com.grauben98.esteb.appchessmate.POJO.POJO_Chat;
import com.grauben98.esteb.appchessmate.R;
import com.grauben98.esteb.appchessmate.View_GI_PvPOnline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG1;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.URL_DATA;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.cas;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.emitter;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.receptor;

public class Fragment_Chat extends Fragment implements Interface_BackPressed {

    //Objects.
        //Activity.
        public View_GI_PvPOnline view_gi_pvPOnline;
        //RecyclerView.
        private RecyclerView recyclerView;
        //Button.
        Button btnSendMssg;
        //POJO List.
        private List<POJO_Chat> list;
        //Timer.
        private Timer timer = new Timer();
        //Adapter.
        private Adapter_Chat adapter;
        //EditText.
        private EditText etWriteMssg;

    //Variables.
        //String.
        public String mssg = "", time;
        public String jsonRTime = "";
        //int.
        public static int type;
        public static int auxResLength = 0, resLength;
        public static int contOp = 0;
        //boolean.
        public static boolean msggOp = false;
        public boolean chatLimit = true;

    public Fragment_Chat() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment__chat, container, false);

        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rvMessages);
        etWriteMssg = view.findViewById(R.id.etChatMessage);
        btnSendMssg = view.findViewById(R.id.btnSend);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new Adapter_Chat(list, getActivity());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapterItemCount();
                    }
                }, 1);
            }
        });

        btnSendMssg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mssg = etWriteMssg.getText().toString().trim();
                if (!mssg.isEmpty()) {
                    sendMessage();
                    adapterItemCount();
                    etWriteMssg.setText("");
                } else {
                    etWriteMssg.setError("Coloque un mensaje.");
                }
            }
        });

        //getExtra();
        showToast("Hola " + emitter + "!. Tu receptor es: " + receptor);

        if(resLength > 49){
            disableChat();
        }

        receiveMessage();
        timerReceive();
        auxResLength = 0;

        return view;
    }

    public void adapterItemCount(){
        if(adapter.getItemCount() > 1){
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public void getExtra() { //Here U Receive User Names From Previous Activities.
        //Intent intent. .
        //emitter = "sgdf";
        //receptor = "dzfh";
    }

    public void sendMessage() {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonHour = new JSONObject(response);
                    jsonRTime = jsonHour.getString("message_time");
                    newMessage(mssg, 1, jsonRTime);
                } catch (JSONException e) {
                    e.getMessage();
                }
            }
        };

        if(resLength > 49){
            disableChat();
        }

        Data_ChatSendMessages data_chatSendMessages = new Data_ChatSendMessages(emitter, receptor, mssg, response);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(data_chatSendMessages);
    }

    public void timerReceive(){
        final Handler handler = new Handler();
        timer.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        receiveMessage();
                    }
                });
            }
        },500, 5000);
    }

    public void receiveMessage() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_DATA + "&case=" + cas,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        resLength = response.length();
                        if (resLength > 49) {
                            disableChat();
                        }

                        if (auxResLength != resLength) {
                            list.clear();
                            JSONObject jsonObject;

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    jsonObject = response.getJSONObject(i);

                                    mssg = jsonObject.getString("message");
                                    type = jsonObject.getInt("message_type");
                                    time = jsonObject.getString("message_time");
                                    Log.w(TAG1, "resLength: " + resLength);
                                    Log.e(TAG1, "idMssg: " + View_GI_PvPOnline.idMssg);

                                    if(!View_GI_PvPOnline.fChatOpened){
                                        if (resLength > auxResLength) {
                                            if (type == 2) {
                                                Log.i(TAG1, "rival: " + "Here!");
                                                msggOp = true;
                                                contOp++;
                                                Log.e(TAG1, "contOp: " + contOp);
                                                auxResLength = resLength;
                                            }
                                        }
                                    }
                                    else{
                                        if(resLength > auxResLength){
                                            contOp = 0;
                                            auxResLength = resLength;
                                            View_GI_PvPOnline.fChatOpened = false;
                                        }
                                    }

                                    newMessage(mssg, type, time);
                                } catch (JSONException e) { /*e.printStackTrace();*/ }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { /*showToast("" + error.getMessage());*/ }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    public void newMessage(String message, int type, String time) {
        POJO_Chat pojo_chat = new POJO_Chat();
        pojo_chat.setMessage(message);
        pojo_chat.setMessageTime(time);
        pojo_chat.setMessType(type);
        list.add(pojo_chat);
        adapter.notifyDataSetChanged();
    }

    public void disableChat() {
        etWriteMssg.setText(R.string.messagesLimit);
        etWriteMssg.setGravity(Gravity.CENTER);
        etWriteMssg.setInputType(InputType.TYPE_NULL);
        etWriteMssg.setClickable(false);
        etWriteMssg.setLongClickable(false);
        btnSendMssg.setVisibility(View.INVISIBLE);
        btnSendMssg.setClickable(false);

        if (chatLimit) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            chatLimit = false;
        }
    }

    public void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof View_GI_PvPOnline){
            ((View_GI_PvPOnline) context).setInterface_backPressed(this);
        }
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        view_gi_pvPOnline = (View_GI_PvPOnline) getActivity();
        view_gi_pvPOnline.btnChat.setEnabled(true);
        getFragmentManager().beginTransaction().remove(Fragment_Chat.this).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() instanceof View_GI_PvPOnline){
            ((View_GI_PvPOnline)getActivity()).setInterface_backPressed(null);
        }
    }
}