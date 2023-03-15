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


public class Fragment_UnknownError extends Fragment implements Constants
{
    public Fragment_UnknownError()
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
        Log.i(TAG, "FRAG_UNKNOWN ERROR ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  =  inflater.inflate(R.layout.fragment_fragment__unkown_error, container, false);
        view.findViewById(R.id.OkUnknown).setOnClickListener(v -> gotoOk());
        return view;
    }

    public void gotoOk()
    {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
