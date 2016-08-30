package com.dollarandtrump.angelcar.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.InfoActivity;
import com.dollarandtrump.angelcar.activity.SettingsActivity;
import com.dollarandtrump.angelcar.activity.ShopActivity;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.RxUploadFile;
import com.dollarandtrump.angelcar.model.CacheShop;
import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.ImageModel;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.utils.Log;
import com.dollarandtrump.angelcar.view.ImageViewGlide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;



@SuppressWarnings("unused")
public class MenuFragment extends Fragment {

    @Bind(R.id.image_view_glide_profile) ImageViewGlide mImageProfile;
    @Bind(R.id.text_name) TextView mName;
    @Bind(R.id.text_description) TextView mDescription;

//    @Bind(R.id.text_emoji)  TextView textEmoji;
//    @Bind(R.id.emoji) EditText emoji;
//    @OnClick(R.id.btn_emoji)
//    public void onClickButtonEmoji(){
//        textEmoji.setText(emoji.getText());
//    }

    private ProfileDao mProfile;

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
        mProfile = SQLiteUtils.rawQuerySingle(ProfileDao.class,"SELECT * FROM Profile",null);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);

        if (mProfile != null) {
            mImageProfile.setImageUrl(getActivity(), mProfile.getUrlShopLogo());
            mName.setText(mProfile.getShopName());
            mDescription.setText(mProfile.getShopDescription());
        }

    }


    @OnClick(R.id.menu_button_profile)
    public void showProfileActivity(){
        Intent i = new Intent(getActivity(), ShopActivity.class);
        i.putExtra("user", Registration.getInstance().getUserId());
        i.putExtra("shop", Registration.getInstance().getShopRef());
        startActivity(i);
    }

    @OnClick(R.id.text_button_info)
    public void onClickInfo(){
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_button_setting)
    public void onClickSetting(){
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
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

}
