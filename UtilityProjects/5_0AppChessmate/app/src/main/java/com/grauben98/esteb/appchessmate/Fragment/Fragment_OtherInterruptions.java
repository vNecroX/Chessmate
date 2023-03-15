package com.grauben98.esteb.appchessmate.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grauben98.esteb.appchessmate.R;

public class Fragment_OtherInterruptions extends Fragment {

    public Fragment_OtherInterruptions() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment__other_interruptions, container, false);
    }
}
