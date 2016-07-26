package com.dollarandtrump.angelcar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.banner.ImageBanner;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.DepthTransformer;
import com.flyco.banner.widget.Banner.base.BaseBanner;
import com.hndev.library.view.BaseCustomViewGroup;
import com.hndev.library.view.sate.BundleSavedState;

import org.parceler.Parcels;

import java.text.DecimalFormat;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ItemCarDetailView extends BaseCustomViewGroup {
    ImageView ic_Profile;
    ImageBanner banner;
    TextView title,brand,profile,time;/*detail,*/
    ToggleButton btnFollow;
    PictureCollectionDao pictureDao = new PictureCollectionDao();

    public ItemCarDetailView(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public ItemCarDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public ItemCarDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ItemCarDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.custom_view_item_car_detail_new, this);
    }

    private void initInstances() {
        banner = (ImageBanner) findViewById(R.id.custom_view_imageBanner);
        title = (TextView) findViewById(R.id.custom_view_text_title);
//        detail = (TextView) findViewById(R.id.custom_view_chat_header_tvDetails);
        brand = (TextView) findViewById(R.id.custom_view_text_brand);
        profile = (TextView) findViewById(R.id.custom_view_text_detail);
        ic_Profile = (ImageView) findViewById(R.id.custom_view_icon_profile);
        btnFollow = (ToggleButton) findViewById(R.id.custom_view_button_follow);
        time = (TextView) findViewById(R.id.custom_view_text_time);
    }

    public void setPictureDao(PictureCollectionDao pictureDao) {
        this.pictureDao = pictureDao;
    }

    public void setImageBanner(PictureCollectionDao pictureDao){
        setPictureDao(pictureDao);
        try {
            banner
                    .setTransformerClass(DepthTransformer.class)
                    .setSelectAnimClass(ZoomInEnter.class)
                    .setSource(pictureDao.getListPicture())
                    .startScroll();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }


//    public void setTextTitle(String str){
//        title.setText(str);
//    }

//    public void setTextDetail(String str){
//        detail.setText(str);
//    }

    public void setIconProfile(String url){
        Glide.with(getContext()).load(url)
                .placeholder(com.hndev.library.R.drawable.loading)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(ic_Profile);

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

    public void setDataPostCar(PostCarDao postCarDao){
        double amount = Double.parseDouble(postCarDao.getCarPrice());
        DecimalFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(amount);

        String topic = postCarDao.getCarTitle();
        title.setText(topic);
        String strTitle = postCarDao.getCarName() + " " +
                postCarDao.getCarSub() + " " + postCarDao.getCarSubDetail();
        String d = "<br> ปี "+ AngelCarUtils.textFormatHtml("#FFB13D",String.valueOf(postCarDao.getCarYear())) +
                " ราคา " + AngelCarUtils.textFormatHtml("#FFB13D",price) +" บาท";
        brand.setText(Html.fromHtml(strTitle+d+"<br>"+ AngelCarUtils.subDetail(postCarDao.getCarDetail()).replaceAll("\n"," ")));
//        detail.setText("");

        if (postCarDao.getName() != null && postCarDao.getPhone() != null) {
            String name = postCarDao.getName().contains("NULL") ? "ไม่มีชื่อ" : postCarDao.getName();
            String phone = postCarDao.getPhone().contains("NULL") ? "ไม่มีเบอร์" : postCarDao.getPhone();
            profile.setText(name + " เบอร์โทร. " + phone);
        }else {
            profile.setText("-ไม่มีข้อมูล");
        }


    }

    public void setTimeString(String time){
        this.time.setText(time);
    }

    public void setFollow(boolean isFollow){
        btnFollow.setChecked(isFollow);
    }

    public void setOnClickItemBannerListener(BaseBanner.OnItemClickL onItemClickL) {
        banner.setOnItemClickL(onItemClickL);
    }

    public void setOnClickItemBannerListener(final OnClickItemHeaderChatListener onItemClickL) {
        banner.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                onItemClickL.onItemClickBanner(position);
            }
        });
        btnFollow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickL.onItemClickFollow(btnFollow.isChecked());
            }
        });
    }

    public interface OnClickItemHeaderChatListener {
        void onItemClickBanner(int position);
        void onItemClickFollow(boolean isFollow);

    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
//        int height = MeasureSpec.getSize(widthMeasureSpec);
//        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
//        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(width,height);
//    }

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
        savedState.getBundle().putParcelable("picDao", Parcels.wrap(pictureDao));
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
        pictureDao = Parcels.unwrap(bundle.getParcelable("picDao"));
    }

}
