package com.dollarandtrump.angelcar.view;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.SingleViewImageActivity;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/********************************************
 * Created by HumNoy Developer on 29/6/2559.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class ImageViewGlide extends ImageView{

    private String mUrl;
    public ImageViewGlide(Context context) {
        super(context);
        initInstances();
    }

    public ImageViewGlide(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithAttrs(attrs);
        initInstances();
    }

    public ImageViewGlide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithAttrs(attrs);
        initInstances();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageViewGlide(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWithAttrs(attrs);
        initInstances();
    }

    private void initWithAttrs(AttributeSet attrs) {
//        if ( attrs != null){
//            int attributeIds[] = { android.R.attr.src };
//            TypedArray a = getContext().obtainStyledAttributes(attributeIds);
//            int resourceId = a.getResourceId(0, 0);
//            a.recycle();
//            setImageResource(resourceId);
//
//        }
    }

    private void initInstances() {
        setClickable(true);

    }

    public void setImageUrl(final FragmentActivity activity, String url){
        mUrl = url;
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.icon_logo)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SingleViewImageActivity.class);
                i.putExtra("url",mUrl.replace("shoplogo","shoplogofull"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.startActivity(i, ActivityOptions.makeSceneTransitionAnimation(activity, v, "image").toBundle());
                }else {
                    activity.startActivity(i);
                }
            }
        });
    }
}
