package com.dollarandtrump.angelcar.sql;


import android.support.annotation.NonNull;

import com.activeandroid.util.SQLiteUtils;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.MessageQueryInterface;

import java.util.List;

public class QueryMessage extends MessageQueryInterface<MessageDao> {

    private MessageDao mOldMessage;
    private MessageDao mNewMessage;

    @Override
    public MessageDao cacheMessageSingle(@NonNull String type,MessageDao newMessage) {
        this.mNewMessage = newMessage;
            this.mOldMessage = SQLiteUtils.rawQuerySingle(MessageDao.class,
                    "SELECT * FROM Conversation " +
                            "INNER JOIN MessageDao " +
                            "ON Conversation.Message = MessageDao.Id " +
                            "WHERE Conversation.ConversationType = '" + type + "' " +
                            "AND MessageDao.MessageId = '" + newMessage.getMessageId() + "'", null);

        return mOldMessage;
    }

    @Override
    public List<MessageDao> cacheMessageList(String type) {
        List<MessageDao> message = SQLiteUtils.rawQuery(MessageDao.class,
                "SELECT * FROM Conversation " +
                        "INNER JOIN MessageDao " +
                        "ON Conversation.Message = MessageDao.Id " +
                        "WHERE Conversation.ConversationType = '" + type + "' " +
                        "AND MessageDao.isDelete = 0", null);
        return message;
    }

    @Override
    public boolean deleteMessage(String type,String carId) {
        MessageDao message = SQLiteUtils.rawQuerySingle(MessageDao.class,
                "SELECT * FROM Conversation " +
                        "INNER JOIN MessageDao " +
                        "ON Conversation.Message = MessageDao.Id " +
                        "WHERE Conversation.ConversationType = '" + type + "' AND MessageDao.MessageCarId ="+carId, null);
        message.setDelete(true);
        message.save();
        return message.isDelete();
    }

    @Override
    public boolean findReadMessage(String type, String messageBy) {
        List<MessageDao> cache = SQLiteUtils.rawQuery(MessageDao.class,
                "SELECT * FROM Conversation " +
                        "INNER JOIN MessageDao ON Conversation.Message = MessageDao.Id " +
                        "WHERE Conversation.ConversationType = '" + type + "' " +
                        "And MessageDao.MessageBy = '" + messageBy + "'  " +
                        "AND MessageDao.MessageStatus = 0", null);
        return cache != null && cache.size() > 0;
    }


}
