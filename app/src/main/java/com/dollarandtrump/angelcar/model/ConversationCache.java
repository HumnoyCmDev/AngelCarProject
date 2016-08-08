package com.dollarandtrump.angelcar.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.dollarandtrump.angelcar.dao.MessageDao;

import java.util.ArrayList;
import java.util.List;

/********************************************
 * Created by HumNoy Developer on 30/7/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/

@Table(name = "Conversation")
public class ConversationCache extends Model{

    @Column(name = "CarId")
    public String carId ;

    @Column(name = "User")
    public String User;

    @Column(name = "Message")
    public List<MessageDao> message =  new ArrayList<>();


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

    public List<MessageDao> getMessage() {
        return message;
    }

    public void setMessage(List<MessageDao> message) {
        this.message = message;
    }
}
