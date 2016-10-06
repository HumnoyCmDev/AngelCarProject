package com.dollarandtrump.angelcar.manager;


import android.support.annotation.Nullable;

import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.sql.QueryMessage;
import com.dollarandtrump.angelcar.sql.MessageSqlTemplate;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.Log;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class LoadConversation {

    public interface CallBack {
        void call(MessageManager manager);
    }

    private Subscription subscriptionMessage;
    private Subscription subscriptionCar;

    /** @CallBack Nullable**/
    public void load(@Nullable final CallBack callBack){
        subscriptionCar = HttpManager.getInstance().getService()
                .observableLoadCarId(Registration.getInstance().getShopRef())
                .subscribeOn(Schedulers.newThread())
                .doOnError(onError)
                .doOnNext(new Action1<CarIdDao>() {
                    @Override
                    public void call(CarIdDao carIdDao) {
                        loadAllMessage(carIdDao,callBack);
                    }
                })
                .subscribe();
    }

    private void loadAllMessage(final CarIdDao carIdDao,@Nullable final CallBack callBack) {
        Observable<MessageAdminCollectionDao> rxCallLoadTopic =
                HttpManager.getInstance().getService().observableConversationTopic(carIdDao.getTopicId());

        Observable<MessageAdminCollectionDao> rxCallLoadMessageSell =
                HttpManager.getInstance().getService()
                        .observableMessageAdmin(carIdDao.getAllCarId());

        Observable<MessageCollectionDao> rxCallLoadMessageBuy = HttpManager.getInstance()
                .getService().observableMessageClient(Registration.getInstance().getUserId());

        Log.d(""+carIdDao.getTopicId());
//        Log.d(""+carIdDao.getAllCarId());
//        Log.d(""+Registration.getInstance().getUserId());

        subscriptionMessage = Observable.zip(rxCallLoadMessageSell, rxCallLoadMessageBuy, rxCallLoadTopic, new Func3<MessageAdminCollectionDao,
                MessageCollectionDao, MessageAdminCollectionDao, MessageManager>() {
            @Override
            public MessageManager call(MessageAdminCollectionDao messageAdminCollectionDao, MessageCollectionDao dao, MessageAdminCollectionDao messageAdminCollectionDao2) {
                MessageManager manager = new MessageManager();
                manager.setConversationSell(messageAdminCollectionDao.convertToMessageCollectionDao());
                manager.setConversationBuy(dao);
                manager.setConversationTopic(messageAdminCollectionDao2.convertToMessageCollectionDao());
                manager.setProductIds(carIdDao);
                insertConversation(messageAdminCollectionDao, dao, messageAdminCollectionDao2);
                return manager;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(onError)
                .doOnNext(new Action1<MessageManager>() {
                    @Override
                    public void call(MessageManager manager) {
                        if (callBack != null) {
                            callBack.call(manager);
                        }
                    }
                }).subscribe();
    }

    private void insertConversation(MessageAdminCollectionDao adminDao1, MessageCollectionDao dao, MessageAdminCollectionDao adminDao2) {

        MessageSqlTemplate<MessageDao> messageSql = new MessageSqlTemplate<>();
        QueryMessage queryMessage = new QueryMessage();

        //update topic
        messageSql.setType("Topic");
        messageSql.insertMessageEmpty(adminDao2.convertToMessageCollectionDao(),queryMessage);
        //update message buy
        messageSql.setType("Buy");
        messageSql.insertMessageEmpty(dao,queryMessage);
        //update message sell
        messageSql.setType("Sell");
        messageSql.insertMessageEmpty(adminDao1.convertToMessageCollectionDao(),queryMessage);

    }

    public void unSubscribe(){
        unSubscription(subscriptionCar,subscriptionMessage);
    }

   private void unSubscription(Subscription... subscriptions){
       for (Subscription s : subscriptions){
           if (s != null && s.isUnsubscribed()){
               s.unsubscribe();
           }
       }
   }

    Action1<Throwable> onError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e("ERROR CONVERSATION",throwable);
        }
    };

}
