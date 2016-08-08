package com.dollarandtrump.angelcar.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 7/6/59.11:18น.
 *
 * @AngelCarProject
 */
public class FireBaseInstanceID extends FirebaseInstanceIdService{
    private static final String TAG = "FirebaseInstanceID";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Token: "+refreshedToken);
        Log.i(TAG, "Token Id: "+FirebaseInstanceId.getInstance().getId());
    }
}
