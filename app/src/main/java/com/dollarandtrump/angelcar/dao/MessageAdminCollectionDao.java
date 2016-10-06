
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

    // Convert MessageAdmin To MessageCollectionDao
    public MessageCollectionDao convertToMessageCollectionDao() {
        List<MessageDao> listMessage = new ArrayList<>();
           for (MessageAdminDao ado : messageAdmin){
               for (MessageDao mDao : ado.getMessage())
               listMessage.add(mDao);
           }
        MessageCollectionDao gao = new MessageCollectionDao();
        gao.setListMessage(listMessage);
            return gao;
    }
}
