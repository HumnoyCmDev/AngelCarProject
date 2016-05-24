package com.dollarandtrump.angelcar.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by humnoy Developer on 16/3/59. 18:08
 */
public class CountCarCollectionDao {

    @SerializedName("rows") @Expose List<CountCarGao> rows;

    public List<CountCarGao> getRows() {
        return rows;
    }

    public void setRows(List<CountCarGao> rows) {
        this.rows = rows;
    }

    public class CountCarGao{
        @SerializedName("countday") @Expose String countDay;
        @SerializedName("countmonth") @Expose String countMonth;
        @SerializedName("countall") @Expose String countAll;

        public String getCountDay() {
            return countDay;
        }

        public void setCountDay(String countDay) {
            this.countDay = countDay;
        }

        public String getCountMonth() {
            return countMonth;
        }

        public void setCountMonth(String countMonth) {
            this.countMonth = countMonth;
        }

        public String getCountAll() {
            return countAll;
        }

        public void setCountAll(String countAll) {
            this.countAll = countAll;
        }
    }
}
