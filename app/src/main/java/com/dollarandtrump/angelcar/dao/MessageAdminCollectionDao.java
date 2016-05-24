
package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class MessageAdminCollectionDao {

    @SerializedName("messageviewbyadminaray")
    @Expose
    private List<MessageAdminDao> messageAdmin = new ArrayList<MessageAdminDao>();

    public List<MessageAdminDao> getMessageAdmin() {
        return messageAdmin;
    }

    public void setMessageAdmin(List<MessageAdminDao> messageAdmin) {
        this.messageAdmin = messageAdmin;
    }

    public MessageAdminDao getMessageAdminDao(){
        return messageAdmin.get(0);
    }
}
