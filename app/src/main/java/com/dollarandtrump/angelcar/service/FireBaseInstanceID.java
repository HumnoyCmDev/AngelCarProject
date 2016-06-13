package com.dollarandtrump.angelcar.service;

import android.util.Log;

import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 7/6/59.11:18น.
 *
 * @AngelCarProject
 */
public class FireBaseInstanceID extends FirebaseInstanceIdService{
    private static final String TAG = "FirebaseInstanceID";

    @Override
    public void onTokenRefresh() {
//        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "onTokenRefresh: "+refreshedToken);

//        sendRegistrationToServer(refreshedToken);
    }

//    private void sendRegistrationToServer(String token) {
//        String user = Registration.getInstance().getUserId();
//        HttpManager.getInstance().getService().sendTokenRegistration(user,token)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Observer<Results>() {
//            @Override
//            public void onCompleted() {
//                Log.i(TAG, "onCompleted: ");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "onError: ",e);
//            }
//
//            @Override
//            public void onNext(Results results) {
//                Log.i(TAG, "onNext: "+results);
//            }
//        });
//    }

}
