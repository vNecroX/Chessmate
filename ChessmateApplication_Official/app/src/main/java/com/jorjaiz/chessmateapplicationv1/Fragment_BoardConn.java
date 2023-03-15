package com.jorjaiz.chessmateapplicationv1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Settings.Data_MainConfiguration;

import static android.app.Activity.RESULT_OK;


public class Fragment_BoardConn extends Fragment implements Constants
{
    Button btnBoardConnDecline, btnBoardConnAccept;
    BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 0;

    private Fragment_BoardConn.OnFragmentInteractionListener mListener;

    public Fragment_BoardConn()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {

        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_BOARD CONN ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__board_conn, container, false);

        btnBoardConnDecline = view.findViewById(R.id.btnBoardConnDecline);
        btnBoardConnAccept = view.findViewById(R.id.btnBoardConnAccept);

        bluetoothAdapter = View_MainInterface.getBluetoothAdapter();

        btnBoardConnDecline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeFragmentBoardConn();
            }
        });

        btnBoardConnAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(bluetoothAdapter.isEnabled())
                {
                    gotoConnectBoard();
                }
                else
                {
                    turningOnBluetooth();
                }
            }
        });

        return view;
    }

    public void turningOnBluetooth()
    {
        Intent intentEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intentEnable, REQUEST_ENABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:

                if(resultCode == RESULT_OK)
                    btnBoardConnAccept.performClick();
                else
                    showToast("Active el Bluetooth para continuar...");

                break;
        }
    }

    public void closeFragmentBoardConn()
    {
        Response.Listener<String> response = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) { }
        };

        Data_MainConfiguration data_mainConfiguration =
                new Data_MainConfiguration("setBoardConfiguration", 0, CP.get().getIdPlayer()+"", response);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(data_mainConfiguration);

        setFragRemoveBLayer();
        getFragmentManager().beginTransaction().remove(Fragment_BoardConn.this).commit();
    }

    public void gotoConnectBoard()
    {
        setFragRemoveBLayer();
        Intent intentConnectBoard = new Intent(getActivity(), View_ConnectBoard.class);
        startActivity(intentConnectBoard);
    }

    public void showToast(String text)
    {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (Fragment_BoardConn.OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragRemoveBLayerBoardConn();
    }

    public void setFragRemoveBLayer()
    {
        if (mListener != null)
            mListener.onFragRemoveBLayerBoardConn();
    }
}
