package com.dollarandtrump.angelcar.manager;

import android.content.Context;

/**
 * Created by humnoy on 5/2/59.
 */
public class Contextor {
    private static Contextor instance;

    public static Contextor getInstance() {
        if (instance == null)
            instance = new Contextor();
        return instance;
    }

    private Context mContext;

    private Contextor() {}

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
