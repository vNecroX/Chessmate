package com.jorjaiz.chessmateapplicationv1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;


public class Fragment_ErrorWithServer extends Fragment implements Constants
{
    public Fragment_ErrorWithServer()
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
        Log.i(TAG, "FRAG_ERROR WITH SERVER ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  =  inflater.inflate(R.layout.fragment_fragment__error_with_server, container, false);
        view.findViewById(R.id.btnErrorWithServer).setOnClickListener(v -> gotoOk());
        return view;
    }

    public void gotoOk()
    {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

}
