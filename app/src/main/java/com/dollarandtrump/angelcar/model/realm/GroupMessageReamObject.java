package com.dollarandtrump.angelcar.model.realm;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GroupMessageReamObject extends RealmObject{
    @PrimaryKey
    private String group;
    private RealmList<MessageRealmObject> message = new RealmList<>();

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public RealmList<MessageRealmObject> getMessage() {
        return message;
    }

    public void setMessage(RealmList<MessageRealmObject> message) {
        this.message = message;
    }
}
