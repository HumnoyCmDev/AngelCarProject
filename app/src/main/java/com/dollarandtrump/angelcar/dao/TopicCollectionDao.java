package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 3/3/59. เวลา 16:09
 ***************************************/
public class TopicCollectionDao {

    @SerializedName("topic") @Expose List<TopicDao> topic;
    public List<TopicDao> getTopic() {
        return topic;
    }

    public void setTopic(List<TopicDao> topic) {
        this.topic = topic;
    }
}
