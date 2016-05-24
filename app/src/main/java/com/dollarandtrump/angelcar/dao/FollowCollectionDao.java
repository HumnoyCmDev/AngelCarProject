package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by humnoyDeveloper on 26/3/59. 11:31
 */
public class FollowCollectionDao {
    @SerializedName("rows") @Expose
    List<FollowDao> rows;

    public List<FollowDao> getRows() {
        return rows;
    }

    public void setRows(List<FollowDao> rows) {
        this.rows = rows;
    }
}
