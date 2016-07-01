package com.dollarandtrump.angelcar.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ShopActivity;
import com.dollarandtrump.angelcar.utils.PhotoLoad;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.model.CacheShop;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.view.ImageViewGlide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.functions.Action1;


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

    @Bind(R.id.image_test_cache)
    ImageView imageTestCache;

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
        ButterKnife.bind(this,rootView);


        if (mCacheShop != null) {
            mImageProfile.setImageUrl(getActivity(),mCacheShop.getProfileDao().getUrlShopLogo());
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

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    @OnClick(R.id.image_test_cache)
    public void imageTest(){

        RxImagePicker.with(getContext()).requestImage(Sources.GALLERY).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                PhotoLoad load = new PhotoLoad();
                load.loadBitmap(FileUtils.getPath(getContext(), uri), new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        Log.d("cache image", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.d("cache image", "onNext: ");
                        imageTestCache.setImageBitmap(bitmap);
                    }
                });
            }
        });

    }

}
