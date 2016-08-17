package com.dollarandtrump.angelcar.dao;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Table(name ="Province")
public class ProvinceDao extends Model{
    @SerializedName("province_id") @Expose @Column(name = "ProvinceId") int provinceId;
    @SerializedName("province_name") @Expose @Column(name = "ProvinceName") String province;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
