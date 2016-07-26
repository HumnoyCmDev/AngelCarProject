package com.dollarandtrump.angelcar.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ShopActivity;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.model.CacheShop;
import com.dollarandtrump.angelcar.view.ImageViewGlide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public class MenuFragment extends Fragment {

    @Bind(R.id.image_view_glide_profile) ImageViewGlide mImageProfile;
    @Bind(R.id.text_name) TextView mName;
    @Bind(R.id.text_description) TextView mDescription;

    NotificationManagerCompat mNotification;

    CacheShop mCacheShop;
    public MenuFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mCacheShop = new Select().from(CacheShop.class).executeSingle();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);

        if (mCacheShop != null) {
            mImageProfile.setImageUrl(getActivity(), mCacheShop.getProfileDao().getUrlShopLogo());
            mName.setText(mCacheShop.getProfileDao().getShopName());
            mDescription.setText(mCacheShop.getProfileDao().getShopDescription());
        }
    }


    @OnClick(R.id.menu_button_profile)
    public void showProfileActivity(){
        Intent i = new Intent(getActivity(), ShopActivity.class);
        i.putExtra("user",Registration.getInstance().getUserId());
        i.putExtra("shop",Registration.getInstance().getShopRef());
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @OnClick(R.id.text_view_show_case)
    public void showNotification(){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AngelCar")
                .setContentText("Tset")
                .setAutoCancel(true)
                ;
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
