package com.dollarandtrump.angelcar.interfaces;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 1/3/59. เวลา 13:26
 ***************************************/
public abstract class WaitMessageOnBackground {
    private  boolean isLoopWaitMessage = true;
    private  long timeSleep = 1000L;

    public abstract void onBackground();// ทำงานเสร็จ จะเข้า onMainThread() ต่อ

    public abstract void onMainThread();

    public void setLoopWaitMessage(boolean loopWaitMessage) {
        isLoopWaitMessage = loopWaitMessage;
    }

    public boolean isLoopWaitMessage() {
        return isLoopWaitMessage;
    }

    public long getTimeSleep() {
        return timeSleep;
    }

    public void setTimeSleep(long timeSleep) {
        this.timeSleep = timeSleep;
    }
}
