package com.dollarandtrump.angelcar.manager;

import android.util.Log;

import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
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

    private int mCurrentIdMessage;
    private int mCurrentIdStatus;
    private String mCarId;
    private String mMessageFromUser;

    private boolean isLoopSuccess= true;
    private final long timeSleep = 1000L;
    private MessageManager mManager;
    private Type mType;

    public WaitMessageObservable(Type type,int mCurrentIdMessage, String mCarId, String mMessageFromUser,int currentIdStatus) {
        mType = type;
        this.mCurrentIdStatus = currentIdStatus;
        this.mCurrentIdMessage = mCurrentIdMessage;
        this.mCarId = mCarId;
        this.mMessageFromUser = mMessageFromUser;
        mManager = new MessageManager();
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
                                    .waitMessage(mCarId + "||" + mMessageFromUser + "||" + mCurrentIdMessage+"||"+mCurrentIdStatus);
                    try {
                        Response<MessageCollectionDao> result = call.execute();
                        if (result.isSuccessful()) {
                            MessageCollectionDao dao = result.body();
                            if (dao != null && dao.getListMessage().size() > 0) {
                                mManager.setMessageDao(dao);
                                MainThreadBus.getInstance().post(mManager.getMessageDao());
                                mCurrentIdMessage = mManager.getMaximumId();
                                mCurrentIdStatus = mManager.getCurrentIdStatus();
                            }
                        }
                    } catch (IOException e) {
//                        subscriber.onError(e);
                    }
                }else if (mType == Type.CHAT_TOPIC){
                    Call<MessageCollectionDao> cell = HttpManager.getInstance()
                            .getService(60 * 1000).waitMessageTopic(mCarId+"||"+mMessageFromUser+"||"+mCurrentIdMessage+"||"+mCurrentIdStatus);
                    try {
                        Response<MessageCollectionDao> response = cell.execute();
                        if (response.isSuccessful()){
                            MessageCollectionDao dao = response.body();
                            if (dao != null && dao.getListMessage().size() > 0) {
                                mManager.setMessageDao(dao);
                                MainThreadBus.getInstance().post(mManager.getMessageDao());
                                mCurrentIdMessage = mManager.getMaximumId();
                                mCurrentIdStatus = mManager.getCurrentIdStatus();
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

//    private int getMaximumId(MessageCollectionDao messageDao){
//        if (messageDao == null)
//            return 0;
//        if (messageDao.getListMessage().size() == 0)
//            return 0;
//        int maxId = messageDao.getListMessage().get(0).getMessageId();
//        for (int i = 0; i < messageDao.getListMessage().size(); i++)
//            maxId = Math.max(maxId, messageDao.getListMessage().get(i).getMessageId());
//        return maxId;
//    }
//
//    private int getCurrentIdStatus(MessageCollectionDao messageDao){
//        int messageId = getMaximumId(messageDao);
//        if (messageDao != null && messageDao.getListMessage() != null) {
//            for (MessageDao m : messageDao.getListMessage()){
//                if (m.getMessageId() == messageId){
//                    return m.getMessageStatus();
//                }
//            }
//        }
//        return 0;
//    }

    public void setIdMessage(int idMessage) {
        this.mCurrentIdMessage = idMessage;
    }

    public void unsubscribe() {
        isLoopSuccess = false;
    }
}
