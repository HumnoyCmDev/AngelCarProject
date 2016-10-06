package com.dollarandtrump.angelcar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ConversationActivity;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.LoadConversation;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 7/6/59.11:23น.
 *
 * @AngelCarProject
 */
public class FireBaseMessaging extends FirebaseMessagingService{
    private static final String TAG = "FireBaseMessaging";

    @Inject
    SharedPreferences sharedPreferences;
    @Inject @Named("default")
    SharedPreferences preferencesDefault;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        //inti notification icon


        if (preferencesDefault.getBoolean("notifications_new_message",true)) {
            String type = remoteMessage.getData().get("type");
            if (type.equals("chatfinance") || type.equals("chatrefinance") || type.equals("chatpawn") || type.equals("chatcar")) {
                sharedPreferences.edit().putBoolean("notification_chat", true).apply();
                new LoadConversation().load(null);
                MainThreadBus.getInstance().post(remoteMessage);

                String strId;
                if (remoteMessage.getData().containsKey("carid")){
                    strId = remoteMessage.getData().get("carid");
                }else if (remoteMessage.getData().containsKey("topicid")){
                    strId = remoteMessage.getData().get("topicid");
                }else {
                    strId = "0";
                }

                int id = Integer.parseInt(strId);
                Log.d("carid","Integer -> "+id);
                sendNotification(id,remoteMessage);
            }
        }

    }


    private void sendNotification(int id, RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("carid",remoteMessage.getData().get("carid"));
        intent.putExtra("roomid",remoteMessage.getData().get("roomid"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setFullScreenIntent(pendingIntent,true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.RED,3000,3000)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setContent(getCustomView(remoteMessage.getNotification().getBody()));
        }else {
            notificationBuilder.setContentTitle("AngelCar Message");
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notificationBuilder.build());
    }

    private RemoteViews getCustomView(String message){
        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
        notificationView.setTextViewText(R.id.text_title,"AngelCar Message");
        notificationView.setTextViewText(R.id.text_message,message);
        return notificationView;
    }


}
