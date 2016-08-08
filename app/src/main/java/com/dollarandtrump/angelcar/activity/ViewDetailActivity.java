package com.dollarandtrump.angelcar.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.banner.ImageBanner;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.manager.Permission;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.utils.ViewFindUtils;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.DepthTransformer;
import com.flyco.banner.widget.Banner.base.BaseBanner;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/********************************************
 * Created by HumNoy Developer on 22/6/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class ViewDetailActivity extends AppCompatActivity {
    private static final String TAG = "ViewDetailActivity";

    private boolean isShop;
    private PostCarDao mDao;

    @Bind(R.id.tv_topic_car) TextView mTopicCar;
    @Bind(R.id.tv_address) TextView mAddress;
    @Bind(R.id.tv_detail) TextView mDetail;
    @Bind(R.id.tv_topic) TextView mTopic;
    @Bind(R.id.tv_phone) TextView mPhone;
    @Bind(R.id.tv_gear) TextView mGear;
    @Bind(R.id.tv_name) TextView mName;
    @Bind(R.id.button_edit) Button mButtonEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);
        ButterKnife.bind(this);

        // get Intent
        Bundle args = getIntent().getExtras();
        isShop = args.getBoolean("is_shop");
        mDao = Parcels.unwrap(args.getParcelable("dao"));
        mButtonEdit.setVisibility(isShop ? View.VISIBLE : View.GONE);
        //load image
        if (savedInstanceState == null) {
            loadImage();
        }
        initInstance();
    }

    private void initInstance() {
        mTopicCar.setText(mDao.toTopicCar());
        mGear.setText(mDao.getGear() == 1 ? "ออโต้" : "ธรรมดา");
        mTopic.setText(mDao.getCarTitle());
        mDetail.setText(AngelCarUtils.convertLineUp(mDao.getCarDetail()));
        mName.setText(mDao.getName());
        mAddress.setText(mDao.getProvince());
        mPhone.setText(mDao.getPhone());
    }

    private void loadImage() {
        Log.d(TAG, "loadImage: "+mDao.getCarId());
        HttpManager.getInstance().getService().observableLoadAllImage(String.valueOf(mDao.getCarId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PictureCollectionDao>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(PictureCollectionDao pictureCollectionDao) {
                        pictureCarDetail(pictureCollectionDao);
                    }
                });

//         /*load image*/
//        Callback<PictureCollectionDao> loadPictureCallback = new Callback<PictureCollectionDao>() {
//            @Override
//            public void onResponse(Call<PictureCollectionDao> call, Response<PictureCollectionDao> response) {
//                if (response.isSuccessful()) {
//                    //Picture banner
//                    pictureCarDetail(response.body());
//                } else {
//                    try {
//                        Log.i("Dialog", "onResponse: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PictureCollectionDao> call, Throwable t) {
//                Log.e("Dialog", "onFailure: ", t);
//            }
//        };
//        Call<PictureCollectionDao> callLoadPictureAll =
//                HttpManager.getInstance().getService().loadAllPicture(String.valueOf(mDao.getCarId()));
//        callLoadPictureAll.enqueue(loadPictureCallback);
    }

    private void pictureCarDetail(final PictureCollectionDao dao) {
        if (dao.getListPicture() != null && dao.getListPicture().size() > 0) {
            ImageBanner sib = ViewFindUtils.find(getWindow().getDecorView(), R.id.image_banner);
            sib
                    .setTransformerClass(DepthTransformer.class)
                    .setSelectAnimClass(ZoomInEnter.class)
                    .setSource(dao.getListPicture())
                    .startScroll();

            sib.setOnItemClickL(new BaseBanner.OnItemClickL() {
                @Override
                public void onItemClick(int position) {

                    Intent intent = new Intent(ViewDetailActivity.this, ViewPictureActivity.class);
                    intent.putExtra("PICTURE_DAO", Parcels.wrap(dao));
                    intent.putExtra("POSITION", position);
                    startActivity(intent);

                }
            });
        }
    }

    @OnClick({R.id.button_edit, R.id.button_close})
    public void buttonListener(View v) {
        if (v.getId() == R.id.buttonEdit) {
            // code
            Intent intent = new Intent(ViewDetailActivity.this,PostActivity.class);
            intent.putExtra("isEdit",true);
            intent.putExtra("carModel", Parcels.wrap(mDao));
            startActivity(intent);
            return;
        }
        finish();
    }

    @OnClick(R.id.button_phone)
    public void callPhone() {
        if (Permission.callPhone(this)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mDao.getPhone().trim().replaceAll(" ", "")));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }

    }

    /***************
     *Listener zone*
     ***************/

}
