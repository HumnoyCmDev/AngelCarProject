package com.dollarandtrump.angelcar.interfaces;

import android.support.design.widget.Snackbar;

import com.dollarandtrump.angelcar.network.Connectivity;

/**
 * -Created by Kotlin-
 **/

public interface InterNetInterface {
    Snackbar onCreateSnackBar();
    boolean isConnectInternet();
    void onNetworkConnectivity(Connectivity connectivity);
    void internetConnectivity(Boolean isConnectedToInternet);
}
