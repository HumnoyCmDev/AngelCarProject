package com.dollarandtrump.angelcar.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.banner.ImageBanner;
import com.dollarandtrump.angelcar.dao.FollowCollectionDao;
import com.dollarandtrump.angelcar.dao.FollowDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.SuccessDao;
import com.dollarandtrump.angelcar.manager.Permission;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.DepthTransformer;
import com.flyco.banner.widget.Banner.base.BaseBanner;

import org.parceler.Parcels;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/********************************************
 * Created by HumNoy Developer on 2/8/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class ViewCarActivity extends AppCompatActivity{
    @Bind(R.id.custom_view_imageBanner) ImageBanner mBanner;
    @Bind(R.id.custom_view_text_title)  TextView mTitle;
    @Bind(R.id.custom_view_text_brand)  TextView mBrand;
    @Bind(R.id.custom_view_text_detail) TextView mDetail;
    @Bind(R.id.custom_view_text_year)   TextView mYear;
    @Bind(R.id.custom_view_text_phone)  TextView mPhone;
    @Bind(R.id.custom_view_text_price)  TextView mPrice;
    @Bind(R.id.text_time) TextView mTime;
    @Bind(R.id.text_shop_name) TextView mShopName;
    @Bind(R.id.text_follow)  TextView mFollow;
    @Bind(R.id.text_deal)  TextView mDeal;
    @Bind(R.id.text_show_count_view)  TextView mShowCountView;
    @Bind(R.id.image_icon_profile) ImageView mImageProfile;
    @Bind(R.id.image_deal) ImageView mImageDeal;
    @Bind(R.id.button_chat) Button mChat;


    @Bind(R.id.group_button_update_car) LinearLayout mGroupButton;

    @Bind(R.id.toolbar) Toolbar mToolbar;

    private boolean isFollow = false;
    private boolean isShop;
    private PostCarDao mCarDao;
    private PictureCollectionDao mImageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_car);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /*get Arguments*/
        Bundle args = getIntent().getExtras();
        isShop = args.getBoolean("is_shop");
        mCarDao = Parcels.unwrap(args.getParcelable("dao"));
        mGroupButton.setVisibility(isShop ? View.VISIBLE : View.GONE);
        mChat.setVisibility(isShop ? View.GONE : View.VISIBLE);
        mFollow.setVisibility(isShop ? View.GONE : View.VISIBLE);/*-กด follow จาก หน้า feed-*/

        if (savedInstanceState == null){
            if (mCarDao != null)
                bindDataPostCar(mCarDao);

            loadImageNewer();
            loadFollow();
        }


    }

    private void loadFollow() {
        if (isShop) return;
        // Load follow
        HttpManager.getInstance().getService().observableLoadFollow(Registration.getInstance().getShopRef())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FollowCollectionDao>() {
                    @Override
                    public void call(FollowCollectionDao followCollectionDao) {
                        if (followCollectionDao != null && followCollectionDao.getRows() != null &&
                                followCollectionDao.getRows().size() > 0) {
                            for (FollowDao dao : followCollectionDao.getRows()) {
                                if (dao.getCarRef().
                                        contains(String.valueOf(mCarDao.getCarId()))) {
                                    isFollow = true;
                                    mFollow.setText(!isFollow ? R.string.follow : R.string.un_follow);
                                }
                            }
                        }
                    }
                });

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFollow = !isFollow;
                mFollow.setText(!isFollow ? R.string.follow : R.string.un_follow);
                if (isFollow) {
                    //Add Follow
                    Call<SuccessDao> callAddFollow = HttpManager.getInstance().getService()
                            .follow("add",String.valueOf(mCarDao.getCarId()),
                                    Registration.getInstance().getShopRef());
                    callAddFollow.enqueue(callbackAddOrRemoveFollow);
                }else {
                    //Delete Follow
                    Call<SuccessDao> callDelete = HttpManager.getInstance().getService()
                            .follow("delete",String.valueOf(mCarDao.getCarId()),
                                    Registration.getInstance().getShopRef());
                    callDelete.enqueue(callbackAddOrRemoveFollow);
                }
            }
        });

    }

    private void loadImageNewer() {
        HttpManager.getInstance().getService().observableLoadAllImage(String.valueOf(mCarDao.getCarId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PictureCollectionDao>() {
                    @Override
                    public void call( PictureCollectionDao dao) {
                        mImageDao = dao;
                        if (mImageDao.getListPicture() != null && mImageDao.getListPicture().size() > 0) {
                            try {
                                mBanner
                                        .setTransformerClass(DepthTransformer.class)
                                        .setSelectAnimClass(ZoomInEnter.class)
                                        .setSource(mImageDao.getListPicture())
                                        .startScroll();
                            }catch (IllegalStateException e){
                                e.printStackTrace();
                            }
                            mBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
                                @Override
                                public void onItemClick(int position) {
                                    Intent intent = new Intent(ViewCarActivity.this, ViewPictureActivity.class);
                                    intent.putExtra("PICTURE_DAO", Parcels.wrap(mImageDao));
                                    intent.putExtra("POSITION", position);
                                    startActivity(intent);

                                }
                            });
                        }
                    }
                });
    }

    private void bindDataPostCar(PostCarDao postCarDao){
        double amount = Double.parseDouble(postCarDao.getCarPrice());
        DecimalFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(amount);

        String topic = postCarDao.getCarTitle();
        mTitle.setText(topic);
        String carBrand = postCarDao.getCarName() + " " +
                postCarDao.getCarSub() + " " + postCarDao.getCarSubDetail();
        String shopName = postCarDao.getShopName() == null || postCarDao.getShopName().equals("") ? "Unknown" : postCarDao.getShopName();
        mShopName.setText(shopName);
        mTime.setText(AngelCarUtils.formatTimeAndDay(this,postCarDao.getCarModifyTime()));
        mBrand.setText(Html.fromHtml(carBrand));
        mYear.setText(Html.fromHtml(AngelCarUtils.textFormatHtml("#FFFFFF",String.valueOf(postCarDao.getCarYear()))));
        mPrice.setText(Html.fromHtml(AngelCarUtils.textFormatHtml("#FFFFFF",price)));
        mShowCountView.setText(postCarDao.getCarView());

        int dealImage ;
        int detailDeal;
        if (postCarDao.getDeal() == 0 ){
            dealImage = R.drawable.shape_deal_low;
            detailDeal = R.string.deal_low;
        }else if (postCarDao.getDeal() == 2){
            dealImage = R.drawable.shape_deal_mid;
            detailDeal = R.string.deal_mid;
        }else {
            dealImage = R.drawable.shape_deal_high;
            detailDeal = R.string.deal_high;
        }
        mImageDeal.setImageResource(dealImage);
        mDeal.setText(detailDeal);

        Glide.with(this)
                .load(postCarDao.getFullPathShopLogo())
                .placeholder(R.drawable.ic_place_holder_2)
                .crossFade()
                .into(mImageProfile);

        if (postCarDao.getName() != null && postCarDao.getPhone() != null) {
            String name = postCarDao.getName().equals("NULL") ? "ไม่มีข้อมูล" : postCarDao.getName();
            String phone = postCarDao.getPhone().equals("NULL") ? "ไม่มีข้อมูล" : postCarDao.getPhone();
            mDetail.setText(name);
            mPhone.setText(phone);
        }else {
            mDetail.setText("-ไม่มีข้อมูล");
        }

    }

    @OnClick(R.id.custom_view_text_phone)
    public void callPhone() {
        if (Permission.callPhone(this)) {
            if (mCarDao.getPhone().equals("NULL")) return;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCarDao.getPhone().trim().replaceAll(" ", "")));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        }

    }

    @OnClick(R.id.button_edit_car)
    public void onEditPost(){
        Intent intent = new Intent(ViewCarActivity.this,PostActivity.class);
        intent.putExtra("isEdit",true);
        intent.putExtra("carModel", Parcels.wrap(mCarDao));
        startActivity(intent);
    }

    @OnClick(R.id.button_chat)
    public void onClickChat(){
        Intent intent = new Intent(ViewCarActivity.this, ChatCarActivity.class);
        intent.putExtra("PostCarDao", Parcels.wrap(mCarDao));
        intent.putExtra("intentForm", 0);
        intent.putExtra("messageFromUser", Registration.getInstance().getUserId());
        startActivity(intent);
    }

    @OnClick(R.id.button_announce)
    public void onUpdateCar(){
        HttpManager.getInstance().getService().observableAnnounce(String.valueOf(mCarDao.getCarId()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SuccessDao>() {
                    @Override
                    public void call(SuccessDao successDao) {
                        Snackbar.make(getWindow().getDecorView(),"เลื่อนประกาศได้อีกครั้ง "+successDao.getResult()+" ชั่วโมง",Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("cardao",Parcels.wrap(mCarDao));
        outState.putParcelable("imagedao",Parcels.wrap(mImageDao));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCarDao = Parcels.unwrap(savedInstanceState.getParcelable("cardao"));
        mImageDao = Parcels.unwrap(savedInstanceState.getParcelable("imagedao"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Callback<SuccessDao> callbackAddOrRemoveFollow = new Callback<SuccessDao>() {
        @Override
        public void onResponse(Call<SuccessDao> call, Response<SuccessDao> response) {
        }
        @Override
        public void onFailure(Call<SuccessDao> call, Throwable t) {
        }
    };
}
