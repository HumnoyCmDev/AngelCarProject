package com.dollarandtrump.angelcar.model;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class LoadConversation {

    public void load(){
        HttpManager.getInstance().getService()
                .observableLoadCarId(Registration.getInstance().getShopRef())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<CarIdDao>() {
                    @Override
                    public void call(CarIdDao carIdDao) {
                        loadAllMessage(carIdDao);
                    }
                });
    }

    private void loadAllMessage(final CarIdDao carIdDao) {
        Observable<MessageAdminCollectionDao> rxCallLoadTopic =
                HttpManager.getInstance().getService().observableConversationTopic(carIdDao.getTopicId());

        Observable<MessageAdminCollectionDao> rxCallLoadMessageSell =
                HttpManager.getInstance().getService()
                        .observableMessageAdmin(carIdDao.getAllCarId());

        Observable<MessageCollectionDao> rxCallLoadMessageBuy = HttpManager.getInstance()
                .getService().observableMessageClient(Registration.getInstance().getUserId());

        Observable.zip(rxCallLoadMessageSell, rxCallLoadMessageBuy, rxCallLoadTopic, new Func3<MessageAdminCollectionDao,
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
//                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
    }

    private void insertConversation(MessageAdminCollectionDao messageAdminCollectionDao, MessageCollectionDao dao, MessageAdminCollectionDao messageAdminCollectionDao2) {
        new Delete().from(ConversationCache.class).execute();
        new Delete().from(MessageDao.class).execute();

        MessageManager managerSqlite = new MessageManager();
        managerSqlite.setMessageDao(messageAdminCollectionDao2.convertToMessageCollectionDao());
        managerSqlite.appendDataToBottomPosition(messageAdminCollectionDao.convertToMessageCollectionDao());
        managerSqlite.appendDataToBottomPosition(dao);

        //test insert to db
        ActiveAndroid.beginTransaction();
        try {
            for( MessageDao m :managerSqlite.getMessageDao().getListMessage()){
                m.save();
                new ConversationCache(m.getMessageCarId(),m,m.getMessageFromUser()).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

}
