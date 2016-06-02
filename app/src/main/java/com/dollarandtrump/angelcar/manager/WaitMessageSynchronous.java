package com.dollarandtrump.angelcar.manager;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dollarandtrump.angelcar.interfaces.WaitMessageOnBackground;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 1/3/59. เวลา 11:59
 ***************************************/
public class WaitMessageSynchronous extends AsyncTask<Void,Void,Void>{
    /*************************
    *Template Task Synchronous
    **************************/
    WaitMessageOnBackground waitMessageOnBackground;

    public WaitMessageSynchronous(WaitMessageOnBackground waitMessageOnBackground) {
        this.waitMessageOnBackground = waitMessageOnBackground;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (waitMessageOnBackground.isLoopWaitMessage()){
            try {
                Thread.sleep(waitMessageOnBackground.getTimeSleep());
                waitMessageOnBackground.onBackground();
                Log.i("Synchronous", "doInBackground: run..");
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run(){
                        waitMessageOnBackground.onMainThread();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    return null;
    }

    public void killTask(){
        if (!isCancelled())
            cancel(true);
            waitMessageOnBackground.setLoopWaitMessage(false);
            Log.i("Log.i Message", "killTask: Success!!");
    }


}
