package com.dollarandtrump.angelcar.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PictureDao;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoyDeveloper on 6/4/59. 15:31
 */
public class ViewPictureFragment extends Fragment {
    private static final String ARGS_PICTURE = "base_url";
    private static final String BUNDLE_STATE = "ImageViewState";

    @Bind(R.id.pictureFull) SubsamplingScaleImageView mImageView;
    @Bind(R.id.progressbar_load) ProgressBar mProgressBar;
    private Target mTarget;

    public static ViewPictureFragment newInstance(String url) {
        Bundle args = new Bundle();
        ViewPictureFragment fragment = new ViewPictureFragment();
        args.putString(ARGS_PICTURE, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sub_sampling_scale_image_view,container,false);
        initInstance(v,savedInstanceState);
        return v;
    }

    private void initInstance(View v, Bundle savedInstanceState) {
        ButterKnife.bind(this,v);

        ImageViewState imageViewState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
            imageViewState = (ImageViewState)savedInstanceState.getSerializable(BUNDLE_STATE);
        }

        mImageView.setPanEnabled(true);
        mImageView.setZoomEnabled(true);
//        mImageView.setDoubleTapZoomDpi(160);
//        mImageView.setMinimumDpi(80);

        String src  = getArguments().getString(ARGS_PICTURE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTarget = new Target(imageViewState);
        Glide.with(this).load(src)
                .asBitmap()
                .placeholder(R.drawable.icon_logo)
                .into(mTarget);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.clear(mTarget);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class Target extends SimpleTarget<Bitmap>{
        ImageViewState imageViewState;

        public Target(ImageViewState imageViewState) {
            this.imageViewState = imageViewState;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            mImageView.setImage(ImageSource.cachedBitmap(resource),imageViewState);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        View rootView = getView();
        if (rootView != null) {
            ImageViewState state = mImageView.getState();
            if (state != null) {
                outState.putSerializable(BUNDLE_STATE, state);
            }
        }

    }
}
