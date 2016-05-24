package com.dollarandtrump.angelcar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dollarandtrump.angelcar.manager.Contextor;

/**
 * Created by humnoy on 5/2/59.
 */
public class ConnectionDetector {

    private static ConnectionDetector instance;

    public static ConnectionDetector getInstance() {
        if (instance == null)
            instance = new ConnectionDetector();
        return instance;
    }
    private Context mContext;
    private ConnectionDetector() {
        mContext = Contextor.getInstance().getContext();
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
