package com.dollarandtrump.angelcar.service;

import android.util.Log;

import com.dollarandtrump.angelcar.manager.Registration;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FireBaseInstanceID extends FirebaseInstanceIdService{
    private static final String TAG = FireBaseInstanceID.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.i(TAG, "Token: "+refreshedToken);
//        Log.i(TAG, "Token Id: "+FirebaseInstanceId.getInstance().getId());
//        Registration.getInstance().saveToken(refreshedToken);
    }
}
