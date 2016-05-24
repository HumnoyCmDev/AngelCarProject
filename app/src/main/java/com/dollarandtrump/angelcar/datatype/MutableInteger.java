package com.dollarandtrump.angelcar.datatype;

import android.os.Bundle;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 29/2/59. เวลา 23:09
 ***************************************/
public class MutableInteger {
    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Bundle onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putInt("value",value);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle saveInstanceSate){
        value = saveInstanceSate.getInt("value");
    }
}
