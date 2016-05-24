package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 3/3/59. เวลา 16:10
 ***************************************/
public class TopicDao {

    @SerializedName("topicid")      @Expose int topicId;
    @SerializedName("userid")       @Expose String userId;
    @SerializedName("topicmessage") @Expose String topicMessage;
    @SerializedName("topicstatus")  @Expose String topicStatus;
    @SerializedName("topicstamp")   @Expose Date topicStamp;
    @SerializedName("topictype")    @Expose String topicType;


    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicMessage() {
        return topicMessage;
    }

    public void setTopicMessage(String topicMessage) {
        this.topicMessage = topicMessage;
    }

    public String getTopicStatus() {
        return topicStatus;
    }

    public void setTopicStatus(String topicStatus) {
        this.topicStatus = topicStatus;
    }

    public Date getTopicStamp() {
        return topicStamp;
    }

    public void setTopicStamp(Date topicStamp) {
        this.topicStamp = topicStamp;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }
}
