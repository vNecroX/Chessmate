package com.jorjaiz.chessmateapplicationv1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

import java.util.Hashtable;
import java.util.Map;

public class Fragment_EditNameGame extends Fragment implements Constants
{
    EditText eTEditGameName;
    String gameName;

    private Fragment_EditNameGame.OnFragmentInteractionListener mListener;
    boolean touched = false;

    public Fragment_EditNameGame()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            gameName = parameters.getString("gameName");
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_EDIT NAME GAME ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__edit_name_game, container, false);

        eTEditGameName = view.findViewById(R.id.etEditGameName);

        view.findViewById(R.id.bCancelEditGameName).setOnClickListener(v -> cancelEditGameName());
        view.findViewById(R.id.bEditGameName).setOnClickListener(v -> editGameName());

        return view;
    }


    public void editGameName()
    {
        if(!touched)
        {
            if(gameName.equals(eTEditGameName.getText().toString()))
            {
                Toast.makeText(getContext(), "Error, mismo nombre de partida", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(eTEditGameName.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(), "Favor, ingresar el nuevo nombre de la partida", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(eTEditGameName.getText().toString().charAt(0) == ' ')
                    {
                        Toast.makeText(getContext(), "Error, espacio inválido como primer caracter", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (mListener != null)
                        {
                            touched = true;

                            removeBLayer();
                            mListener.onFragEditNameGame(eTEditGameName.getText().toString());
                            getFragmentManager().beginTransaction().remove(this).commit();
                        }
                    }
                }
            }
        }
    }

    public void cancelEditGameName()
    {
        if(!touched)
        {
            touched = true;
            removeBLayer();
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (Fragment_EditNameGame.OnFragmentInteractionListener) activity;
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
        void onFragEditNameGame(String gameName);
        void onFragRemoveBLayerEditNameGame();
    }

    public void removeBLayer()
    {
        if (mListener != null)
            mListener.onFragRemoveBLayerEditNameGame();
    }
}
