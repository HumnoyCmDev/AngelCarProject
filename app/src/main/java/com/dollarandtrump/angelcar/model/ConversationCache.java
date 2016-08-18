package com.dollarandtrump.angelcar.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.dollarandtrump.angelcar.dao.MessageDao;

import java.util.ArrayList;
import java.util.List;


@Table(name = "Conversation")
public class ConversationCache extends Model {

    public ConversationCache() {
    }

    public ConversationCache(String conversationType,MessageDao message) {
        this.message = message;
        this.conversationType = conversationType;
    }


    @Column(name = "ConversationType")
    public String conversationType;

    @Column(name = "Message")
    public MessageDao message;




    public MessageDao getMessage() {
        return message;
    }

    public void setMessage(MessageDao message) {
        this.message = message;
    }
}
