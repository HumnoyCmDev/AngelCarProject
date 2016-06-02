package com.dollarandtrump.angelcar.manager.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by humnoy on 5/2/59.
 */
public class ActivityResultBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private static ActivityResultBus instance;

    public static ActivityResultBus getInstance() {
        if (instance == null)
            instance = new ActivityResultBus();
        return instance;
    }

    @Override
    public void post(final Object event) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ActivityResultBus.getInstance().post(event);
                }
            });
    }
}
