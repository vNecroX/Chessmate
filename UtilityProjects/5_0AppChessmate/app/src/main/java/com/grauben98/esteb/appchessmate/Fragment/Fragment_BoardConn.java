package com.grauben98.esteb.appchessmate.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.grauben98.esteb.appchessmate.Data.Data_MainConfiguration;
import com.grauben98.esteb.appchessmate.R;
import com.grauben98.esteb.appchessmate.View_BluetoothDevice;
import com.grauben98.esteb.appchessmate.View_ConnectBoard;
import com.grauben98.esteb.appchessmate.View_MainConfiguration;
import com.grauben98.esteb.appchessmate.View_MainInterface;
import com.grauben98.esteb.appchessmate.View_PeerLocalBluetooth;

import static android.app.Activity.RESULT_OK;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.cSetBoardConfiguration;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.idUser;

public class Fragment_BoardConn extends Fragment {

    //Objects
        //Button
        Button btnBoardConnDecline, btnBoardConnAccept;
        //BluetoothAdapter
        BluetoothAdapter bluetoothAdapter;

    //Constants
        //int
        private static final int REQUEST_ENABLE_BT = 0;

    public Fragment_BoardConn() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_fragment__board_conn, container, false);

        btnBoardConnDecline = view.findViewById(R.id.btnBoardConnDecline);
        btnBoardConnAccept = view.findViewById(R.id.btnBoardConnAccept);

        bluetoothAdapter = View_MainInterface.getBluetoothAdapter();

        btnBoardConnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View_MainConfiguration.board = 0;
                closeFragmentBoardConn();
            }
        });

        btnBoardConnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isEnabled()){
                    View_MainConfiguration.board = 0;
                    gotoConnectBoard();
                }
                else{
                    turningOnBluetooth();
                }
            }
        });

        return view;
    }

    public void turningOnBluetooth(){
        Intent intentEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intentEnable, REQUEST_ENABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    btnBoardConnAccept.performClick();
                }
                else{
                    showToast("Active el Bluetooth para continuar...");
                }
                break;
        }
    }

    public void closeFragmentBoardConn(){
        if(View_MainInterface.board == 1 || View_MainConfiguration.board == 1){
            Response.Listener<String> response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) { }
            };

            Data_MainConfiguration data_mainConfiguration = new Data_MainConfiguration(cSetBoardConfiguration, 0, idUser, response);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(data_mainConfiguration);
        }
        getFragmentManager().beginTransaction().remove(Fragment_BoardConn.this).commit();
    }

    public void gotoConnectBoard(){
        Intent intentConnectBoard = new Intent(getActivity(), View_ConnectBoard.class);
        startActivity(intentConnectBoard);
    }

    public void showToast(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
