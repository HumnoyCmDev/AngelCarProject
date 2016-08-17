package com.dollarandtrump.angelcar.manager;

import com.dollarandtrump.angelcar.dao.MessageCollectionDao;

import rx.subjects.PublishSubject;
@Deprecated
public class RxMessageObservable {

    private static RxMessageObservable instance;

    public static synchronized RxMessageObservable with() {
        if (instance == null)
            instance = new RxMessageObservable();
        return instance;
    }

    private PublishSubject<MessageCollectionDao> publishSubject;
    private RxMessageObservable() {
    }

    public PublishSubject<MessageCollectionDao> publishSubject(){
        publishSubject = PublishSubject.create();
        return publishSubject;
    }

    public void onInitMessage(MessageCollectionDao dao){
        if (publishSubject != null){
            publishSubject.onNext(dao);
//            publishSubject.onCompleted();
        }
    }

    public void onDestroy() {
        if (publishSubject != null) {
            publishSubject.onCompleted();
        }
    }
}
