package com.dollarandtrump.angelcar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ConversationActivity;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 7/6/59.11:23น.
 *
 * @AngelCarProject
 */
public class FireBaseMessaging extends FirebaseMessagingService{
    private static final String TAG = "FireBaseMessaging";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
            MainThreadBus.getInstance().post(remoteMessage);
        sendNotification(remoteMessage);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("carid",remoteMessage.getData().get("carid"));
        intent.putExtra("roomid",remoteMessage.getData().get("roomid"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carhorn);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Message")
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
