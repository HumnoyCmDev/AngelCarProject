package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by humnoyDeveloper on 23/3/59. 16:59
 */
public class CarIdDao {
    @SerializedName("allcarid") @Expose String allCarId;

    @SerializedName("alltopicid") @Expose String topicId;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getAllCarId() {
        return allCarId;
    }

    public void setAllCarId(String allCarId) {
        this.allCarId = allCarId;
    }
}
