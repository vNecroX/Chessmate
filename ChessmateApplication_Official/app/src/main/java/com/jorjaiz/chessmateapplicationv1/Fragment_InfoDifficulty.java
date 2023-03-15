package com.jorjaiz.chessmateapplicationv1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;


public class Fragment_InfoDifficulty extends Fragment implements Constants
{
    EditText etDifficultyTitle;
    TextView tVEasy, tVIntermediate, tVDifficult;
    int difficulty;

    public Fragment_InfoDifficulty()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            Bundle parameters = getArguments();
            difficulty = parameters.getInt("difficulty");
        }
        Log.i(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        Log.i(TAG, "FRAG_INFO DIFFICULTY ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment__info_difficulty, container, false);

        etDifficultyTitle = view.findViewById(R.id.etDifficultyTitle);

        view.findViewById(R.id.btnDifficulty).setOnClickListener(v -> gotoOk());

        tVEasy = view.findViewById(R.id.tVEasyInfo);
        tVIntermediate = view.findViewById(R.id.tVIntermediateInfo);
        tVDifficult = view.findViewById(R.id.tVDifficultInfo);

        tVEasy.setVisibility(View.INVISIBLE);
        tVIntermediate.setVisibility(View.INVISIBLE);
        tVDifficult.setVisibility(View.INVISIBLE);

        switch(difficulty)
        {
            case EASY:
                etDifficultyTitle.setText("Fácil");
                tVEasy.setVisibility(View.VISIBLE);
                break;

            case INTERMEDIATE:
                etDifficultyTitle.setText("Intermedio");
                tVIntermediate.setVisibility(View.VISIBLE);
                break;

            case DIFFICULT:
                etDifficultyTitle.setText("Difícil");
                tVDifficult.setVisibility(View.VISIBLE);
                break;
        }

        etDifficultyTitle.setTextColor(getResources().getColor(R.color.colorTurquoise0));

        return view;
    }

    public void gotoOk()
    {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

}
