package com.dollarandtrump.angelcar.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dollarandtrump.angelcar.rx_picker.HiddenActivity;
import com.dollarandtrump.angelcar.rx_picker.Sources;

import rx.Observable;
import rx.subjects.PublishSubject;


public class RxNotification {
    private static RxNotification instance;

    public static synchronized RxNotification with(Context context) {
        if (instance == null) {
            instance = new RxNotification(context.getApplicationContext());
        }
        return instance;
    }

    private Context context;
    private PublishSubject<Boolean> publishSubject;

    private RxNotification(Context context) {
        this.context = context;
    }

    public Observable<Boolean> observableNotification() {
        publishSubject = PublishSubject.create();
        return publishSubject;
    }

    public void isNotification(boolean aBoolean) {
        if (publishSubject != null) {
            publishSubject.onNext(aBoolean);
//            publishSubject.onCompleted();
        }
    }

    public void onDestroy() {
        if (publishSubject != null) {
            publishSubject.onCompleted();
        }
    }
}
