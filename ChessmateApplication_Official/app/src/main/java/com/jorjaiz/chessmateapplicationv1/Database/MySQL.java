package com.jorjaiz.chessmateapplicationv1.Database;

import android.content.Context;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

public class MySQL implements Constants
{
    static
    {

    }

    public static Query query(Context ctx)
    {
        return new Query(ctx);
    }

    public static Query queryFrag(Context ctx, Query.OnResponseDatabase listener)
    {
        return new Query(ctx, listener);
    }

}
