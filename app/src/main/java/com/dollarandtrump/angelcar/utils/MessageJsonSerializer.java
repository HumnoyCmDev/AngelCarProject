package com.dollarandtrump.angelcar.utils;

import com.activeandroid.serializer.TypeSerializer;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/********************************************
 * Created by HumNoy Developer on 29/7/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/

public class MessageJsonSerializer extends TypeSerializer {
    private static final Gson gson = new Gson();

    @Override
    public Class<?> getDeserializedType() {
        return List.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return MessageDao.class;
    }

    @Override
    public Object serialize(Object data) {
       if (data == null) return null;
        return gson.toJson(data);
    }

    @Override
    public Object deserialize(Object data) {
        if (null == data) return null;
        List<MessageDao> strings = new ArrayList<>();
        final MessageDao message = gson.fromJson(data.toString(), MessageDao.class);
        strings.add(message);
        return strings;

    }
}
