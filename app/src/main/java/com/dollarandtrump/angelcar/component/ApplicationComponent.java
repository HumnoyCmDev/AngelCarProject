package com.dollarandtrump.angelcar.component;

import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.activity.ChatCarActivity;
import com.dollarandtrump.angelcar.activity.ConversationActivity;
import com.dollarandtrump.angelcar.activity.MainActivity;
import com.dollarandtrump.angelcar.activity.SplashScreenActivity;
import com.dollarandtrump.angelcar.dialog.InformationDialog;
import com.dollarandtrump.angelcar.dialog.ShopEditDialog;
import com.dollarandtrump.angelcar.fragment.PostCarFragment;
import com.dollarandtrump.angelcar.fragment.ShopFragment;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.module.AppModule;
import com.dollarandtrump.angelcar.module.NetworkModule;
import com.dollarandtrump.angelcar.module.StoreModule;
import com.dollarandtrump.angelcar.service.FireBaseMessaging;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, StoreModule.class, NetworkModule.class})
public interface ApplicationComponent {
    void inject(SplashScreenActivity activity);
    void inject(MainActivity activity);
    void inject(FireBaseMessaging fireBaseMessaging);
    void inject(ConversationActivity activity);
    void inject(ChatCarActivity activity);
    void inject(ShopFragment fragment);
    void inject(InformationDialog dialog);
    void inject(PostCarFragment fragment);
    void inject(HttpManager httpManager);


}
