package com.dollarandtrump.angelcar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.DepthTransformer;
import com.flyco.banner.widget.Banner.base.BaseBanner;
import com.hndev.library.view.BaseCustomViewGroup;
import com.hndev.library.view.sate.BundleSavedState;

import org.parceler.Parcels;

import java.text.DecimalFormat;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ItemCarDetails extends BaseCustomViewGroup {
    ImageView mImageProfile;
    ImageBanner mBanner;
    TextView mTitle, mBrand, mDetail, mTime, mCallMe, mYear, mPhone, mPrice;
    TextView mFollow;
    boolean isFollow = false;
    PictureCollectionDao mPictureDao ;
    PostCarDao mPostCarDao;

    public ItemCarDetails(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public ItemCarDetails(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public ItemCarDetails(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ItemCarDetails(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.custom_view_item_car_details, this);

        setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initInstances() {
        mBanner = (ImageBanner) findViewById(R.id.custom_view_imageBanner);
        mTitle = (TextView) findViewById(R.id.custom_view_text_title);
        mBrand = (TextView) findViewById(R.id.custom_view_text_brand);
        mDetail = (TextView) findViewById(R.id.custom_view_text_detail);
        mImageProfile = (ImageView) findViewById(R.id.custom_view_icon_profile);
        mTime = (TextView) findViewById(R.id.custom_view_text_time);
        mCallMe = (TextView) findViewById(R.id.custom_view_cell_text);

        mFollow = (TextView) findViewById(R.id.text_follow);
        mYear = (TextView) findViewById(R.id.custom_view_text_year);
        mPhone = (TextView) findViewById(R.id.custom_view_text_phone);
        mPrice = (TextView) findViewById(R.id.custom_view_text_price);

    }

    public void bindCellViewHolder(PictureCollectionDao images, PostCarDao postCar){
        mPostCarDao = postCar;
        if (images != null) {
            mPictureDao = images;
            mTime.setText(AngelCarUtils.formatTimeAndDay(getContext(),postCar.getCarModifyTime()));
            try {
                mBanner
                        .setTransformerClass(DepthTransformer.class)
                        .setSelectAnimClass(ZoomInEnter.class)
                        .setSource(mPictureDao.getListPicture())
                        .startScroll();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
        if (mPostCarDao != null) {
            bindDataPostCar(mPostCarDao);
            setIconProfile(mPostCarDao.getFullPathShopLogo());

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
                                            contains(String.valueOf(mPostCarDao.getCarId()))) {
                                        isFollow = true;
                                        mFollow.setText(!isFollow ? R.string.follow : R.string.un_follow);
                                    }
                                }
                            }
                        }
                    });

        mFollow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFollow = !isFollow;
                mFollow.setText(!isFollow ? R.string.follow : R.string.un_follow);
                if (isFollow) {
                    //Add Follow
                    Call<ResponseDao> callAddFollow = HttpManager.getInstance().getService()
                            .follow("add",String.valueOf(mPostCarDao.getCarId()),
                                    Registration.getInstance().getShopRef());
                    callAddFollow.enqueue(callbackAddOrRemoveFollow);
                }else {
                    //Delete Follow
                    Call<ResponseDao> callDelete = HttpManager.getInstance().getService()
                            .follow("delete",String.valueOf(mPostCarDao.getCarId()),
                                    Registration.getInstance().getShopRef());
                    callDelete.enqueue(callbackAddOrRemoveFollow);

                }
            }
        });


        }
    }

    private void setIconProfile(String url){
        Glide.with(getContext()).load(url)
                .placeholder(com.hndev.library.R.drawable.icon_logo)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mImageProfile);

//        Glide.with(getContext()).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(ic_Profile) {
//            @Override
//            protected void setResource(Bitmap resource) {
//                RoundedBitmapDrawable circularBitmapDrawable =
//                        RoundedBitmapDrawableFactory.create(getResources(), resource);
//                circularBitmapDrawable.setCircular(true);
//                circularBitmapDrawable.setBounds(1,1,1,1);
//                ic_Profile.setImageDrawable(circularBitmapDrawable);
//            }
//        });
    }

    private void bindDataPostCar(PostCarDao postCarDao){
        double amount = Double.parseDouble(postCarDao.getCarPrice());
        DecimalFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(amount);

        String topic = postCarDao.getCarTitle();
        mTitle.setText(topic);
        String carBrand = postCarDao.getCarName() + " " +
                postCarDao.getCarSub() + " " + postCarDao.getCarSubDetail();

//        String details = "<br> ปี "+ AngelCarUtils.textFormatHtml("#FFB13D",String.valueOf(postCarDao.getCarYear())) +
//                " ราคา " + AngelCarUtils.textFormatHtml("#FFB13D",price) +" บาท";
//        mBrand.setText(Html.fromHtml(carBrand+details+"<br>"+ AngelCarUtils.subDetail(postCarDao.getCarDetail()).replaceAll("\n"," ")));

        mBrand.setText(Html.fromHtml(carBrand));
        mYear.setText(Html.fromHtml(AngelCarUtils.textFormatHtml("#FFFFFF",String.valueOf(postCarDao.getCarYear()))));
        mPrice.setText(Html.fromHtml(AngelCarUtils.textFormatHtml("#FFFFFF",price)));


        if (postCarDao.getName() != null && postCarDao.getPhone() != null) {
            String name = postCarDao.getName().equals("NULL") ? "ไม่มีข้อมูล" : postCarDao.getName();
            String phone = postCarDao.getPhone().equals("NULL") ? "ไม่มีข้อมูล" : postCarDao.getPhone();
            mDetail.setText(name);
            mPhone.setText(phone);
        }else {
            mDetail.setText("-ไม่มีข้อมูล");
        }

        //
        String info = AngelCarUtils.textFormatHtml("#FF0000","Tip: ")+
                "แชทกับผู้ขายเพื่อต่อรองราคาได้ทันทีและกด <img src ='ic_custom_header_person.png'> " +
                "เพื่อสอบถามยอดจัดจากธนาคาร";
        Spanned spanned = Html.fromHtml(info, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = 0;

                if(source.equals("ic_custom_header_person.png")){
                    id = R.drawable.ic_custom_header_person;
                }
                LevelListDrawable d = new LevelListDrawable();
                Drawable empty = getResources().getDrawable(id);
                d.addLevel(0, 0, empty);
                assert empty != null;
                d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                return d;
            }
        }, null);
        mCallMe.setText(spanned);
    }

    public void setFollow(boolean isFollow){
        this.isFollow = isFollow;
    }

    public void setOnClickItemBannerListener(BaseBanner.OnItemClickL onItemClickL) {
        mBanner.setOnItemClickL(onItemClickL);
    }

    public void setOnClickItemBannerListener(final OnClickItemHeaderChatListener onItemClickL) {
        mBanner.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                onItemClickL.onItemClickBanner(position);
            }
        });
        mPhone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickL.onItemClickPhone(mPostCarDao.getPhone());
            }
        });
    }

    public interface OnClickItemHeaderChatListener {
        void onItemClickBanner(int position);
//        void onItemClickFollow(boolean isFollow);
        public void onItemClickPhone(String phone);

    }

    private void initWithAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /*
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleableName,
                defStyleAttr, defStyleRes);

        try {

        } finally {
            a.recycle();
        }
        */
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);
        savedState.getBundle().putParcelable("picDao", Parcels.wrap(mPictureDao));
        savedState.getBundle().putParcelable("postDao",Parcels.wrap(mPostCarDao));
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
        mPictureDao = Parcels.unwrap(bundle.getParcelable("picDao"));
        mPostCarDao = Parcels.unwrap(bundle.getParcelable("postDao"));
    }

    Callback<ResponseDao> callbackAddOrRemoveFollow = new Callback<ResponseDao>() {
        @Override
        public void onResponse(Call<ResponseDao> call, Response<ResponseDao> response) {
            if (response.isSuccessful()) {
//                Log.i(TAG, "onResponse:" + response.body().success);
            } else {
//                try {
//                    Log.i(TAG, "onResponse: " + response.errorBody().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
        @Override
        public void onFailure(Call<ResponseDao> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
        }
    };
}
