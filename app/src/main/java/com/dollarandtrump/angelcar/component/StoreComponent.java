package com.dollarandtrump.angelcar.component;

import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.activity.ConversationActivity;
import com.dollarandtrump.angelcar.activity.MainActivity;
import com.dollarandtrump.angelcar.activity.SplashScreenActivity;
import com.dollarandtrump.angelcar.module.AppModule;
import com.dollarandtrump.angelcar.module.StoreModule;
import com.dollarandtrump.angelcar.service.FireBaseMessaging;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, StoreModule.class})
public interface StoreComponent {
    void inject(SplashScreenActivity activity);
    void inject(MainActivity activity);
    void inject(FireBaseMessaging fireBaseMessaging);
    void inject(ConversationActivity activity);
}
