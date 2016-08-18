package com.dollarandtrump.angelcar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ConversationActivity;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.model.LoadConversation;
import com.dollarandtrump.angelcar.utils.RxNotification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 7/6/59.11:23น.
 *
 * @AngelCarProject
 */
public class FireBaseMessaging extends FirebaseMessagingService{
    private static final String TAG = "FireBaseMessaging";

    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    LoadConversation loadConversation;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        ((MainApplication) getApplication()).getStoreComponent().inject(this);
        //inti notification icon

        String type = remoteMessage.getData().get("type");
        if (type.equals("chatfinance") || type.equals("chatrefinance") || type.equals("chatpawn") || type.equals("chatcar")){
            sharedPreferences.edit().putBoolean("notification_chat",true).apply();
//            RxNotification.with(getBaseContext())
//                    .isNotification(true);
            loadConversation.load();
            MainThreadBus.getInstance().post(remoteMessage);
            sendNotification(remoteMessage);
        }

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
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingIntent,true)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Message")
//                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder = notificationBuilder.setContent(getCustomView(remoteMessage.getNotification().getBody()));
        }else {
            notificationBuilder = notificationBuilder.setContentTitle("AngelCar Message");
            notificationBuilder = notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private RemoteViews getCustomView(String message){
        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
        notificationView.setTextViewText(R.id.text_title,"AngelCar Message");
        notificationView.setTextViewText(R.id.text_message,message);
        return notificationView;
    }
}
