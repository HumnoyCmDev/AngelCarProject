package com.dollarandtrump.angelcar.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.dollarandtrump.angelcar.manager.Contextor;

/**
 * Created by humnoy on 5/2/59.
 * - ข้อควรระวัง Singleton ผูกติดกับ Application
 * - ข้อมูลไม่ควรจะใหญ่ เกินไป
 * -**** Out Of Memory , Memory Leak ****
 */
public class IpAddress {

    private static IpAddress instance;

    public static IpAddress getInstance() {
        if (instance == null)
            instance = new IpAddress();
        return instance;
    }

    private Context mContext;
    private String ip;

    private IpAddress() {
        mContext = Contextor.getInstance().getContext();
        WifiManager wifiMan = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));

    }
    public String getIp() {
        return ip;
    }
}
