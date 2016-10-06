package com.dollarandtrump.angelcar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.util.SQLiteUtils;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.InfoActivity;
import com.dollarandtrump.angelcar.activity.ListDealerActivity;
import com.dollarandtrump.angelcar.activity.SettingsActivity;
import com.dollarandtrump.angelcar.activity.ShopActivity;
import com.dollarandtrump.angelcar.activity.SplashScreenActivity;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.interfaces.InterNetInterface;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.view.ImageViewGlide;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
        if (((InterNetInterface) getActivity()).isConnectInternet()) {
            Intent i = new Intent(getActivity(), ShopActivity.class);
            i.putExtra("user", Registration.getInstance().getUserId());
            i.putExtra("shop", Registration.getInstance().getShopRef());
            startActivity(i);
        }
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

    @OnClick(R.id.text_button_view_dealer)
    public void onClickViewDealer(){
        if (((InterNetInterface) getActivity()).isConnectInternet()) {
            startActivity(new Intent(getActivity(), ListDealerActivity.class));
        }
    }

    @OnClick(R.id.text_shop_login)
    public void onClickLogout(){
//        clearApplicationData();
        if(!Registration.getInstance().isSignIn()) {
            ClearApp();
        }else {
            String userOld = Registration.getInstance().getUserOld();
            String token = FirebaseInstanceId.getInstance().getToken();
            HttpManager.getInstance().getService().logout(userOld + "||" + token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    })
                    .doOnNext(new Action1<ResponseDao>() {
                        @Override
                        public void call(ResponseDao responseDao) {
                            ClearApp();
                        }
                    })
                    .subscribe();
        }
    }

    private void ClearApp() {
        Registration.getInstance().clear();
        getActivity().finish();
        startActivity(new Intent(getActivity(), SplashScreenActivity.class));
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
