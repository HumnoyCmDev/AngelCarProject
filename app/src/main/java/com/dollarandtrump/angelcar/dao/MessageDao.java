
package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class MessageDao {

    @SerializedName("messageid")        @Expose int messageId;
    @SerializedName("messagecarid")     @Expose String messageCarId;
    @SerializedName("messagefromuser")  @Expose String messageFromUser;
    @SerializedName("messagetext")      @Expose String messageText;
    @SerializedName("displayname")      @Expose String displayName;
    @SerializedName("messageby")        @Expose String messageBy;
    @SerializedName("userprofileimage") @Expose String userProfileImage;
    @SerializedName("messagestamp")     @Expose Date messagesTamp;

    // Topic
    @SerializedName("messagetopicid") @Expose String messageTopId;

    public String getMessageTopId() {
        return messageTopId;
    }

    public void setMessageTopId(String messageTopId) {
        this.messageTopId = messageTopId;
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

    public Date getMessagesTamp() {
        return messagesTamp;
    }

    public void setMessagesTamp(Date messagesTamp) {
        this.messagesTamp = messagesTamp;
    }
}
