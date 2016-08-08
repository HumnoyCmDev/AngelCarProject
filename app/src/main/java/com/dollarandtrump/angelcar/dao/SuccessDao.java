package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 2/3/59. เวลา 09:26
 ***************************************/
public class SuccessDao {
    /*
    *Message ตอบกลับจาก Server ผลลัพที่ได้เป็น Json
    *@เพื่อเช็ค Status การทำงาน
    */

    @SerializedName("return") @Expose String result;

    @SerializedName("success") @Expose public String success;

    @SerializedName("approve_id") @Expose public String approveId;

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
