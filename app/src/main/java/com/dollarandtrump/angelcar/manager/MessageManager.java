package com.dollarandtrump.angelcar.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.ArrayList;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 29/2/59. เวลา 11:24
 ***************************************/
public class MessageManager {

    private Context mContext;
    private MessageCollectionDao messageDao;
    private MessageCollectionDao mConversationSell;
    private MessageCollectionDao mConversationBuy;

    public MessageManager() {
        mContext = Contextor.getInstance().getContext();
        //Load Cache
//        loadCache();
    }

    public MessageCollectionDao getMessageDao() {
        return messageDao;
    }

    public void setMessageDao(MessageCollectionDao messageDao) {
        this.messageDao = messageDao;
        //Save Cache
//        saveCache();
    }

    public int getCount(){
        if (messageDao == null) return 0;
        if (messageDao.getListMessage() == null) return 0;

        return messageDao.getListMessage().size();
    }

    public void insertDaoAtTopPosition(MessageCollectionDao newDao){
        if (messageDao == null)
            messageDao = new MessageCollectionDao();
        if (messageDao.getListMessage() == null)
            messageDao.setListMessage(new ArrayList<MessageDao>());
        messageDao.getListMessage().addAll(0,newDao.getListMessage());
        //Save Cache
//        saveCache();
    }

    public MessageCollectionDao getConversationSell() {
        return mConversationBuy;
    }

    public MessageCollectionDao getConversationBuy() {
        return mConversationSell;
    }

    public void unifyDao(MessageAdminCollectionDao mConversationSell, MessageCollectionDao mConversationBuy){
        this.mConversationSell = mConversationSell.convertToMessageCollectionDao();
        this.mConversationBuy = mConversationBuy;
        if (messageDao == null){
            messageDao = new MessageCollectionDao();
        }
        if (messageDao.getListMessage() == null){
            messageDao.setListMessage(new ArrayList<MessageDao>());
        }
        messageDao.getListMessage().addAll(getCount(),this.mConversationSell.getListMessage());
        messageDao.getListMessage().addAll(getCount(),mConversationBuy.getListMessage());

//        saveCache();
    }

    public void appendDataToBottomPosition(MessageCollectionDao dao){
        if (messageDao == null){
            messageDao = new MessageCollectionDao();
        }
        if (messageDao.getListMessage() == null){
            messageDao.setListMessage(new ArrayList<MessageDao>());
        }
        messageDao.getListMessage().addAll(getCount(),dao.getListMessage());
        //Save Cache
//        saveCache();
    }

    public MessageCollectionDao updateReadMessageDao(MessageCollectionDao dao){
        messageDao.getListMessage().remove(getCount()-1);
        appendDataToBottomPosition(dao);
        return messageDao;
    }

    public int getMaximumId(){
        if (messageDao == null)
            return 0;
        if (messageDao.getListMessage().size() == 0)
            return 0;
        int maxId = messageDao.getListMessage().get(0).getMessageId();
        for (int i = 0; i < messageDao.getListMessage().size(); i++)
            maxId = Math.max(maxId, messageDao.getListMessage().get(i).getMessageId());
        return maxId;
    }

    public int getCurrentIdStatus(){
            int messageId = getMaximumId();
        if (messageDao != null && messageDao.getListMessage() != null) {
            for (MessageDao m : messageDao.getListMessage()){
                if (m.getMessageId() == messageId){
                    return m.getMessageStatus();
                }
            }
        }
        return 0;
    }

    public int getMinimumId(){
        if (messageDao == null)
            return 0;
        if (messageDao.getListMessage() == null)
            return 0;
        if (messageDao.getListMessage().size() == 0)
            return 0;

        int minId = messageDao.getListMessage().get(0).getMessageId();
        for (int i = 0; i < messageDao.getListMessage().size(); i++)
            minId = Math.min(minId,messageDao.getListMessage().get(i).getMessageId());
        return minId;
    }

    public Bundle onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", Parcels.wrap(messageDao));
        return bundle;
    }

    public void onRestoreInstanceState(Bundle saveInstanceState){
        messageDao = Parcels.unwrap(saveInstanceState.getParcelable("message"));
    }

    private void saveCache(){

        MessageCollectionDao cacheDao =
                new MessageCollectionDao();
        if (messageDao != null && messageDao.getListMessage() != null)
            cacheDao.setListMessage(messageDao.getListMessage().subList(0,
                    Math.min(20,messageDao.getListMessage().size())));

        String json = new Gson().toJson(cacheDao);

        SharedPreferences prefs =
                mContext.getSharedPreferences("message", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("json",json);
        edit.apply();

    }

    private void loadCache(){
        SharedPreferences prefs =
                mContext.getSharedPreferences("message",Context.MODE_PRIVATE);
        String json = prefs.getString("json",null);
        if (json == null)
            return;
        messageDao = new Gson().fromJson(json,MessageCollectionDao.class);
    }

}
