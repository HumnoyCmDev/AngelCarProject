package com.dollarandtrump.angelcar.sql;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.MessageQueryInterface;
import com.dollarandtrump.angelcar.model.ConversationCache;

import java.util.List;

public class MessageSqlTemplate<T extends Model> {

    private String mType;

    public MessageSqlTemplate() {
    }


    public MessageSqlTemplate(String mType) {
        this.mType = mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public void insertMessageEmpty(MessageCollectionDao dao, MessageQueryInterface<T> mapper){
        ActiveAndroid.beginTransaction();
        try {
            for( MessageDao newMessage : dao.getListMessage()){

                // Select car id
                MessageDao queryMessage = (MessageDao) mapper.cacheMessageSingle(mType,newMessage);
                if (queryMessage == null) { // insert
                    newMessage.save();
                    new ConversationCache(mType, newMessage).save();
                    android.util.Log.d("TAG MESSAGE", "insert");
                }else { //update
                    if (newMessage.getMessageStamp().after(queryMessage.getMessageStamp())
                            || newMessage.getMessageStatus() == 1) {
                        android.util.Log.d("TAG MESSAGE", "update  "+newMessage.getMessageId());
                        queryMessage.setMessageId(newMessage.getMessageId());
                        queryMessage.setMessageStamp(newMessage.getMessageStamp());
                        queryMessage.setUserProfileImage(newMessage.getUserProfileImage());
                        queryMessage.setMessageText(newMessage.getMessageText());
                        queryMessage.setMessageStatus(newMessage.getMessageStatus());

                        /*check delete == true ข้อความที่เคยลบ เป็น false เพื่อให้กลับมาแสดง*/
                        if (queryMessage.isDelete())
                            queryMessage.setDelete(false);

                        queryMessage.save();
                    }
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public List<T> queryMessage(String type,MessageQueryInterface<T> mapper){
        return mapper.cacheMessageList(type);
    }
    public List<T> queryMessage(MessageQueryInterface<T> mapper){
        return mapper.cacheMessageList(mType);
    }

    public boolean deleteMessage(String carId,MessageQueryInterface<T> mapper){
        return mapper.deleteMessage(mType,carId);
    }

    public boolean findReadMessage(String type,String messageBy,MessageQueryInterface<T> mapper){
        return mapper.findReadMessage(type,messageBy);
    }

}
