package com.jorjaiz.chessmateapplicationv1.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Classes.Notation;
import com.jorjaiz.chessmateapplicationv1.R;

import java.util.List;

public class Adapter_CardMove extends PagerAdapter implements Constants
{
    List<Notation> listMoves;
    LayoutInflater layoutInflater;

    public Adapter_CardMove(List<Notation> listMoves, Context context)
    {
        this.listMoves = listMoves;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return listMoves.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View view = layoutInflater.inflate(R.layout.card_move, container,false);

        ImageView iVPiece = view.findViewById(R.id.iVPiece);
        TextView tVXi = view.findViewById(R.id.tVXi);
        TextView tVYi = view.findViewById(R.id.tVYi);
        ImageView iVAction1 = view.findViewById(R.id.iVAction1);
        ImageView iVPiece2 = view.findViewById(R.id.iVPiece2);
        TextView tVXf = view.findViewById(R.id.tVXf);
        TextView tVYf = view.findViewById(R.id.tVYf);

        iVPiece.setImageResource(getIconByInt(listMoves.get(position).getPiece()));

        tVXi.setText(listMoves.get(position).getXi()+"");
        tVYi.setText(listMoves.get(position).getYi()+"");

        iVAction1.setImageResource(getIconActionByInt(listMoves.get(position).getAction1()));

        iVPiece2.setImageResource(getIconByInt(listMoves.get(position).getPiece2()));

        tVXf.setText(listMoves.get(position).getXf()+"");
        tVYf.setText(listMoves.get(position).getYf()+"");

        container.addView(view);

        return view;
    }

    private int getIconByInt(int ic)
    {
        switch(ic)
        {
            case 0: return Color.TRANSPARENT;
            case 1: return R.drawable.ic_pawnblack;
            case 2: return R.drawable.ic_knightblack;
            case 3: return R.drawable.ic_bishopblack;
            case 4: return R.drawable.ic_rookblack;
            case 5: return R.drawable.ic_queenblack;
            case 6: return R.drawable.ic_kingblack;
            case 11: return R.drawable.ic_pawnwhite;
            case 12: return R.drawable.ic_knightwhite;
            case 13: return R.drawable.ic_bishopwhite;
            case 14: return R.drawable.ic_rookwhite;
            case 15: return R.drawable.ic_queenwhite;
            case 16: return R.drawable.ic_kingwhite;
        }
        return 0;
    }

    private int getIconActionByInt(int ic)
    {
        switch(ic)
        {
            case 0: return R.drawable.ic_displacement;
            case 1: return R.drawable.ic_eaten;
            case 2: return R.drawable.ic_castling;
            case 4: return Color.TRANSPARENT;
        }
        return 0;
    }

}
