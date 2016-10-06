package com.dollarandtrump.angelcar.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.utils.CacheData;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 29/2/59. เวลา 11:24
 ***************************************/
public class MessageManager {

    private Context mContext;
    private MessageCollectionDao messageDao;
    private MessageCollectionDao mConversationBuy;
    private MessageCollectionDao mConversationSell;

    private MessageCollectionDao mConversationTopic;

    private MessageCollectionDao mDuplicateMessage;

    private CarIdDao mProductIds;

    public MessageManager() {
        mContext = Contextor.getInstance().getContext();
        //Load CacheData
//        loadCache();
    }

    public MessageCollectionDao getMessageDao() {
        return messageDao;
    }

    public void setMessageDao(MessageCollectionDao messageDao) {
        this.messageDao = messageDao;

        mDuplicateMessage = new MessageCollectionDao();
        mDuplicateMessage.setListMessage(messageDao.getListMessage());
        //Save CacheData
//        saveCache();
    }

    public void setProductIds(CarIdDao productIds) {
        this.mProductIds = productIds;

    }

    public CarIdDao getProductIds() {
        return mProductIds;
    }

    public int getCount(){
        if (messageDao == null) return 0;
        if (messageDao.getListMessage() == null) return 0;

        return messageDao.getListMessage().size();
    }

    public void insertMessageAtTopPosition(MessageCollectionDao newDao){
        if (messageDao == null)
            messageDao = new MessageCollectionDao();
        if (messageDao.getListMessage() == null)
            messageDao.setListMessage(new ArrayList<MessageDao>());
        messageDao.getListMessage().addAll(0,newDao.getListMessage());
        //Save CacheData
//        saveCache();
    }

    public void insertMessageAtTopPosition(List<MessageDao> newDao){
        if (messageDao == null)
            messageDao = new MessageCollectionDao();
        if (messageDao.getListMessage() == null)
            messageDao.setListMessage(new ArrayList<MessageDao>());
        messageDao.getListMessage().addAll(0,newDao);

    }

    public MessageCollectionDao getConversationSell() {
        return mConversationSell;
    }

    public MessageCollectionDao getConversationBuy() {
        return mConversationBuy;
    }

    public MessageCollectionDao getConversationTopic() {
        return mConversationTopic;
    }

    public void setConversationTopic(MessageCollectionDao conversationTopic) {
        this.mConversationTopic = conversationTopic;
    }

    public void setConversationBuy(MessageCollectionDao conversationBuy) {
        this.mConversationSell = conversationBuy;
    }

    public void setConversationSell(MessageCollectionDao conversationSell) {
        this.mConversationBuy = conversationSell;
    }

    public void appendDataToBottomPosition(MessageCollectionDao dao){
        if (messageDao == null){
            messageDao = new MessageCollectionDao();
        }
        if (messageDao.getListMessage() == null){
            messageDao.setListMessage(new ArrayList<MessageDao>());
        }
        messageDao.getListMessage().addAll(getCount(),dao.getListMessage());


        /**Dup message**/
        if (mDuplicateMessage == null){
            mDuplicateMessage = new MessageCollectionDao();
        }
        if (mDuplicateMessage.getListMessage() == null){
            mDuplicateMessage.setListMessage(new ArrayList<MessageDao>());
        }

        mDuplicateMessage.getListMessage().addAll(mDuplicateMessage.getListMessage().size(),dao.getListMessage());

    }

    public void updateMessageThem(MessageDao dao){
        if (messageDao == null){
            messageDao = new MessageCollectionDao();
        }
        if (messageDao.getListMessage() == null){
            messageDao.setListMessage(new ArrayList<MessageDao>());
        }
        messageDao.getListMessage().add(getCount(),dao);

//        this.mDuplicateMessage.getListMessage().add(getCount(),dao);
    }

    public void updateMessageMe(int countMessage,MessageDao message){
        if (messageDao == null){
            messageDao = new MessageCollectionDao();
        }
        if (messageDao.getListMessage() == null){
            messageDao.setListMessage(new ArrayList<MessageDao>());
        }
        int removeId = (getCount()-1)-countMessage;
        messageDao.getListMessage().set(removeId,message);

//        this.mDuplicateMessage.getListMessage().set(removeId,message);
    }

    public void addMessageMe(String messageBy,String messageText){
        if (messageDao == null){
            messageDao = new MessageCollectionDao();
        }
        if (messageDao.getListMessage() == null){
            messageDao.setListMessage(new ArrayList<MessageDao>());
        }
        messageDao.getListMessage().add(setMessageMe(messageBy,messageText));

//        this.mDuplicateMessage.getListMessage().add(setMessageMe(messageBy,messageText));
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
        /*MessageCollectionDao cacheDao =
                new MessageCollectionDao();
        if (messageDao != null && messageDao.getListMessage() != null)
            cacheDao.setListMessage(messageDao.getListMessage().subList(0,
                    Math.min(20,messageDao.getListMessage().size())));
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs =
                mContext.getSharedPreferences("message", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("json",json);
        edit.apply();*/
//        String mKeyMessage = String.format("message_json_%s_%s",
//                messageDao.getCarId(),mMessageFromUser);
//        String gson = new Gson().toJson(messageDao);
//        new CacheData().save(mKeyMessage, gson);

    }

    private void loadCache(){
        SharedPreferences prefs =
                mContext.getSharedPreferences("message",Context.MODE_PRIVATE);
        String json = prefs.getString("json",null);
        if (json == null)
            return;
        messageDao = new Gson().fromJson(json,MessageCollectionDao.class);
    }

    private MessageDao setMessageMe(String messageBy, String messageText) {
        MessageDao mMessageMe = new MessageDao();
        mMessageMe.setMessageId(-1);
        mMessageMe.setMessageBy(messageBy);
        mMessageMe.setMessageStatus(3);
        mMessageMe.setMessageText(messageText);
        mMessageMe.setMessageStamp(new Date());
        mMessageMe.setSent(false);
        return mMessageMe;
    }

    /**@จัดการ message model**/
    public MessageCollectionDao messageSubList(){
        final int maxSubLst = 20;
        if (messageDao != null && messageDao.getListMessage() != null){
            int max = messageDao.getListMessage().size();
            if (max > maxSubLst){
                int min = max - maxSubLst;
                LinkedList<MessageDao> sub = new LinkedList<>(messageDao.getListMessage().subList(min,max));
                messageDao.setListMessage(sub);
                //update message duplicate
//                mDuplicateMessage.setListMessage(mDuplicateMessage.getListMessage().subList(0,min));
            }
            return messageDao;
        }
        return new MessageCollectionDao();
    }

    private int additionalSize = 0;
    public boolean addMessageStepAtTopPosition(){
        if (messageDao != null && messageDao.getListMessage() != null) {
            if (messageDao.getListMessage().size() != mDuplicateMessage.getListMessage().size()){
                MessageCollectionDao copy = new MessageCollectionDao();
            copy.setListMessage(mDuplicateMessage.getListMessage());
            int currentMaxMessageCopy = copy.getListMessage().size();
            int currentMaxMessageDao = messageDao.getListMessage().size();

            int maxMessageCopy = currentMaxMessageCopy - currentMaxMessageDao;
            int min = maxMessageCopy - 20;
            if (min < 0) min = 0;
            if (maxMessageCopy < 0) return false;
            LinkedList<MessageDao> sub = new LinkedList<>(copy.getListMessage().subList(min, maxMessageCopy));
            additionalSize = sub.size();
            insertMessageAtTopPosition(sub);
                return true;
        }
            return false;

        }
        return false;
    }

    public int getAdditionalSize() {
        return additionalSize;
    }
}
