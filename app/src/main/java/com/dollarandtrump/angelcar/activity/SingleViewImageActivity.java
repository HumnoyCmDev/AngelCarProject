package com.dollarandtrump.angelcar.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dollarandtrump.angelcar.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoyDeveloper on 11/4/59. 14:36
 */
public class SingleViewImageActivity extends AppCompatActivity {
    public static String ARGS_PICTURE = "ARGS_PICTURE";
    private static final String BUNDLE_STATE = "ImageViewState";

    @Bind(R.id.pictureFull) SubsamplingScaleImageView scaleImageView;

    String src;
    private Target target;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view_image);
        ButterKnife.bind(this);

        initInstance(savedInstanceState);
    }


    private void initInstance(Bundle savedInstanceState) {
        src = getIntent().getStringExtra(ARGS_PICTURE);

        ImageViewState imageViewState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
            imageViewState = (ImageViewState)savedInstanceState.getSerializable(BUNDLE_STATE);
        }

        target = new Target(imageViewState);
        Glide.with(this).load(src)
                .asBitmap()
                .placeholder(R.drawable.loading)
                .into(target);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.clear(target);
    }

    private class Target extends SimpleTarget<Bitmap>{
        ImageViewState imageViewState;

        public Target(ImageViewState imageViewState) {
            this.imageViewState = imageViewState;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            scaleImageView.setImage(ImageSource.cachedBitmap(resource),imageViewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARGS_PICTURE,src);

        View rootView = getWindow().getDecorView();
        if (rootView != null) {
            ImageViewState state = scaleImageView.getState();
            if (state != null) {
                outState.putSerializable(BUNDLE_STATE, state);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        src = savedInstanceState.getString(ARGS_PICTURE);
    }

}
