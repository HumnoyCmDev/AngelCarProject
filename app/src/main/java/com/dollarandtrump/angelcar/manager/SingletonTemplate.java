package com.dollarandtrump.angelcar.manager;

import android.content.Context;

/**
 * Created by humnoy on 5/2/59.
 * - ข้อควรระวัง Singleton ผูกติดกับ Application
 * - ข้อมูลไม่ควรจะใหญ่ เกินไป
 * -**** Out Of Memory , Memory Leak ****
 */
public class SingletonTemplate {

    private static SingletonTemplate instance;

    public static  SingletonTemplate getInstance() {
        if (instance == null)
            instance = new SingletonTemplate();
        return instance;
    }

    private Context mContext;

    private SingletonTemplate() {
        mContext = Contextor.getInstance().getContext();
    }
}
