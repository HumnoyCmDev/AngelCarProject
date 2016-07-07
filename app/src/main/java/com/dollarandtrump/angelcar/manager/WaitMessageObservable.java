package com.dollarandtrump.angelcar.manager;

import android.util.Log;

import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

/********************************************
 * Created by HumNoy Developer on 30/6/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class WaitMessageObservable implements Observable.OnSubscribe<String> {

    public enum Type {
        CHAT_CAR,CHAT_TOPIC
    }

    private int mIdMessage;
    private String mCarId;
    private String mMessageFromUser;

    private boolean isLoopSuccess= true;
    private final long timeSleep = 1000L;

    private Type mType;

    public WaitMessageObservable(Type type,int mIdMessage, String mCarId, String mMessageFromUser) {
        mType = type;
        this.mIdMessage = mIdMessage;
        this.mCarId = mCarId;
        this.mMessageFromUser = mMessageFromUser;
    }

    @Override
    public void call(Subscriber<? super String> subscriber) {
        if (!isLoopSuccess) {
            subscriber.onCompleted();
            subscriber.unsubscribe();
        }
            while (isLoopSuccess){
                if (mType == Type.CHAT_CAR) {
                    Call<MessageCollectionDao> call =
                            HttpManager.getInstance()
                                    .getService(60 * 1000) // Milliseconds (1 นาที)
                                    .waitMessage(mCarId + "||" + mMessageFromUser + "||" + mIdMessage);
                    try {
                        Response<MessageCollectionDao> result = call.execute();
                        if (result.isSuccessful()) {
                            MessageCollectionDao dao = result.body();
                            if (dao != null && dao.getListMessage().size() > 0) {
//                            RxMessageObservable.with().onInitMessage(dao);
                                MainThreadBus.getInstance().post(dao);
                                mIdMessage = getMaximumId(dao);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }else if (mType == Type.CHAT_TOPIC){
                    Call<MessageCollectionDao> cell = HttpManager.getInstance()
                            .getService(60 * 1000).waitMessageTopic(mCarId+"||"+mMessageFromUser+"||"+mIdMessage);
                    try {
                        Response<MessageCollectionDao> response = cell.execute();
                        if (response.isSuccessful()){
                            MessageCollectionDao dao = response.body();
                            if (dao != null && dao.getListMessage().size() > 0) {
//                            RxMessageObservable.with().onInitMessage(dao);
                                MainThreadBus.getInstance().post(dao);
                                mIdMessage = getMaximumId(dao);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(timeSleep);
                } catch (InterruptedException e) {}
            }

    }

    public int getMaximumId(MessageCollectionDao messageDao){
        if (messageDao == null)
            return 0;
        if (messageDao.getListMessage().size() == 0)
            return 0;
        int maxId = messageDao.getListMessage().get(0).getMessageId();
        for (int i = 0; i < messageDao.getListMessage().size(); i++)
            maxId = Math.max(maxId, messageDao.getListMessage().get(i).getMessageId());
        return maxId;
    }

    public void setIdMessage(int idMessage) {
        this.mIdMessage = idMessage;
    }

    public void unsubscribe() {
        isLoopSuccess = false;
    }
}
