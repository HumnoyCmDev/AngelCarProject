package com.dollarandtrump.angelcar.model.realm;


import io.realm.RealmObject;

public class MessageRealmObject extends RealmObject{
    private int messageId;
    private boolean isDelete;

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
