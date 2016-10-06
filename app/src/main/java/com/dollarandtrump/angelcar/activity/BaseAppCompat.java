package com.dollarandtrump.angelcar.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.interfaces.InterNetInterface;
import com.dollarandtrump.angelcar.network.Connectivity;
import com.dollarandtrump.angelcar.network.ReactiveNetwork;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Kotlin
 **/

public abstract class BaseAppCompat extends AppCompatActivity implements InterNetInterface {
    private Snackbar snackbar;

    private Subscription internetConnectivitySubscription;
    private Subscription networkConnectivitySubscription;
    private boolean isConnectInternet = true;
    @Override
    public void onResume() {
        super.onResume();
        initCheckInternet();
        snackbar = onCreateSnackBar();
    }

    @Override
    public Snackbar onCreateSnackBar() {
        return null;
    }

    private void initCheckInternet(){
        networkConnectivitySubscription =
                ReactiveNetwork.observeNetworkConnectivity(this)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Connectivity>() {
                            @Override public void call(final Connectivity connectivity) {
//                                final NetworkInfo.State state = connectivity.getState();
//                                final String name = connectivity.getName();
                                onNetworkConnectivity(connectivity);
                            }
                        });

        internetConnectivitySubscription = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isConnectedToInternet) {
                        isConnectInternet = isConnectedToInternet;
                        if (snackbar != null) {
                            if (isConnectedToInternet)
                                snackbar.dismiss();
                            else
                                snackbar.show();
                        }
                        internetConnectivity(isConnectedToInternet);
                    }
                });
    }
    @Override
    public void internetConnectivity(Boolean isConnectedToInternet){

    }

    @Override
    public void onNetworkConnectivity(Connectivity connectivity) {

    }

    @Override
    public boolean isConnectInternet() {
        return isConnectInternet;
    }

    @Override
    public void onPause() {
        super.onPause();
        safelyUnSubscribe(internetConnectivitySubscription,networkConnectivitySubscription);
    }

    private void safelyUnSubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }
}
