
package com.dollarandtrump.angelcar.dao;

import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(value = Parcel.Serialization.FIELD, analyze = MessageDao.class)
@Table(name = "MessageDao")
public class MessageDao extends Model{

    @SerializedName("messageid")        @Expose @Column(name = "MessageId") int messageId;
    @SerializedName("messagecarid")     @Expose @Column(name = "MessageCarId") String messageCarId;
    @SerializedName("messagefromuser")  @Expose @Column(name = "MessageFromUser") String messageFromUser;
    @SerializedName("messagetext")      @Expose @Column(name = "MessageText") String messageText;
    @SerializedName("displayname")      @Expose @Column(name = "DisplayName") String displayName;
    @SerializedName("messageby")        @Expose @Column(name = "MessageBy") String messageBy;
    @SerializedName("userprofileimage") @Expose @Column(name = "UserProfileImage") String userProfileImage;
    @SerializedName("messagestamp")     @Expose @Column(name = "MessageStamp") Date messageStamp;
    @SerializedName("messagestatus")    @Expose @Column(name = "MessageStatus") int messageStatus;
    @SerializedName("carimage")         @Expose @Column(name = "CarImage") String carImage;

    @Column(name = "isSent") boolean isSent = true;
    @Column(name = "isDelete") boolean isDelete = false;

    //TOPIC
    @SerializedName("topictype") @Expose @Column(name = "TopicType") String topicType;
    @SerializedName("topicstatus") @Expose @Column(name = "TopicStatus") String topicStatus;


    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isTopic = false;

    public boolean isTopic() {
        return isTopic;
    }

    public void setTopic(boolean topic) {
        isTopic = topic;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessageCarId() {
        return messageCarId;
    }

    public void setMessageCarId(String messageCarId) {
        this.messageCarId = messageCarId;
    }

    public String getMessageFromUser() {
        return messageFromUser;
    }

    public void setMessageFromUser(String messageFromUser) {
        this.messageFromUser = messageFromUser;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessageBy() {
        return messageBy;
    }

    public void setMessageBy(String messageBy) {
        this.messageBy = messageBy;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public Date getMessageStamp() {
        return messageStamp;
    }

    public void setMessageStamp(Date messageStamp) {
        this.messageStamp = messageStamp;
    }

    /**TOPIC อาจจะมีค่า Null**/
    public String  getTopicStatus() {
        return topicStatus;
    }

    public void setTopicStatus(String topicStatus) {
        this.topicStatus = topicStatus;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }
}
