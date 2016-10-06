package com.dollarandtrump.angelcar.interfaces;


import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.dollarandtrump.angelcar.dao.MessageDao;

import java.util.List;

public abstract class MessageQueryInterface<T extends Model> {
    public abstract T cacheMessageSingle(String type,MessageDao dao);
    public abstract List<T> cacheMessageList(String type);
    public abstract boolean deleteMessage(String type,String carId);
    public abstract boolean findReadMessage(String type,String messageBy);
}
