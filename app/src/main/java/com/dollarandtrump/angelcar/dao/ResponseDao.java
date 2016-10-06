package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ResponseDao {
    /*
    *Message ตอบกลับจาก Server ผลลัพที่ได้เป็น Json
    *@เพื่อเช็ค Status การทำงาน
    */

    @SerializedName("return") @Expose String result;

    @SerializedName("success") @Expose public String success;

    @SerializedName("approve_id") @Expose public String approveId;

    @SerializedName("carid") @Expose public String carId;

    @SerializedName("shopid") @Expose String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getApproveId() {
        return approveId;
    }

    public void setApproveId(String approveId) {
        this.approveId = approveId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
