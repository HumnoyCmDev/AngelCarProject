package com.dollarandtrump.angelcar.fragment;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.dollarandtrump.angelcar.interfaces.InterNetInterface;
import com.dollarandtrump.angelcar.network.ReactiveNetwork;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Kotlin
 **/
public abstract class BaseFragment extends Fragment implements InterNetInterface {

//    protected abstract void onInternetConnectivity(Boolean isConnectedToInternet);
//    protected abstract Snackbar onCreateSnackBar();

    private Subscription internetConnectivitySubscription;
    private boolean isConnectInternet;
    private Snackbar snackbar;
    @Override
    public void onResume() {
        super.onResume();
        initCheckInternet();
        snackbar = onCreateSnackBar();
    }

    private void initCheckInternet(){
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

    public void internetConnectivity(Boolean isConnectedToInternet){

    }

    @Override
    public boolean isConnectInternet() {
        return isConnectInternet;
    }

    @Override
    public void onPause() {
        super.onPause();
        safelyUnSubscribe(internetConnectivitySubscription);
    }

    private void safelyUnSubscribe(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }
}
