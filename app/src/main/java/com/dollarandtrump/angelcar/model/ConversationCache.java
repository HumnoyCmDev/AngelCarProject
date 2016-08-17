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

    public ConversationCache(String carId, MessageDao message, String user) {
        this.carId = carId;
        this.message = message;
        User = user;
    }

    @Column(name = "CarId")
    public String carId;

    @Column(name = "User")
    public String User;

    @Column(name = "Message")
    public MessageDao message;


    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public MessageDao getMessage() {
        return message;
    }

    public void setMessage(MessageDao message) {
        this.message = message;
    }
}
