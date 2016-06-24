package com.dollarandtrump.angelcar.rx_picker;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.places.Place;

import rx.subjects.PublishSubject;

/********************************************
 * Created by HumNoy Developer on 24/6/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class RxLocationPicker {
    private static RxLocationPicker instance;

    public static synchronized RxLocationPicker with(Context context){
        if (instance == null){
            instance = new RxLocationPicker(context.getApplicationContext());
        }
        return instance;
    }

    private Context mContext;
    private PublishSubject<Place> mPublishSubject;

    public RxLocationPicker(Context mContext) {
        this.mContext = mContext;
    }

    public PublishSubject<Place> requestLocation(){
        mPublishSubject = PublishSubject.create();
        startActivityHiddenActivity(Sources.LOCATION.ordinal());
        return mPublishSubject;
    }

    public void onLocationPicked(Place place){
        if (mPublishSubject != null) {
            mPublishSubject.onNext(place);
            mPublishSubject.onCompleted();
        }

    }

    private void startActivityHiddenActivity(int sources) {
        Intent intent = new Intent(mContext, HiddenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(HiddenActivity.RX_PICKER_SOURCE, sources);
        mContext.startActivity(intent);
    }

    public void onDestroy() {
        if (mPublishSubject != null) {
            mPublishSubject.onCompleted();
        }
    }

}
