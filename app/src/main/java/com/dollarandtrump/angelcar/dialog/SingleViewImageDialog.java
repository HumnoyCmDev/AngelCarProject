package com.dollarandtrump.angelcar.dialog;//package com.beta.cls.angelcar.dialog;
//
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.DialogFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.beta.cls.angelcar.R;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.davemorrissey.labs.subscaleview.ImageSource;
//import com.davemorrissey.labs.subscaleview.ImageViewState;
//import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * Created by humnoyDeveloper on 11/4/59. 14:36
// */
//public class SingleViewImageDialog extends DialogFragment{
//    private static String ARGS_PICTURE = "ARGS_PICTURE";
//    private static final String BUNDLE_STATE = "ImageViewState";
//
//    @Bind(R.id.pictureFull) SubsamplingScaleImageView scaleImageView;
//
//    private String src;
//    private Target target;
//
//    public static SingleViewImageDialog newInstance(String fullUrl) {
//        Bundle args = new Bundle();
//        SingleViewImageDialog fragment = new SingleViewImageDialog();
//        args.putString(ARGS_PICTURE,fullUrl);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (savedInstanceState != null)
//            onRestoreInstanceState(savedInstanceState);
//    }
//
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_view_picture,container,false);
//        initInstance(v,savedInstanceState);
//        return v;
//    }
//
//    private void initInstance(View v, Bundle savedInstanceState) {
//        ButterKnife.bind(this,v);
//        src = getArguments().getString(ARGS_PICTURE);
//
//
//        ImageViewState imageViewState = null;
//        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE)) {
//            imageViewState = (ImageViewState)savedInstanceState.getSerializable(BUNDLE_STATE);
//        }
//
//        target = new Target(imageViewState);
//        Glide.with(this).load(src)
//                .asBitmap()
//                .placeholder(R.drawable.loading)
//                .into(target);
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Glide.clear(target);
//    }
//
////    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
////        @Override
////        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
////            scaleImageView.setImage(ImageSource.cachedBitmap(resource));
////        }
////    };
//
//    private class Target extends SimpleTarget<Bitmap>{
//        ImageViewState imageViewState;
//
//        public Target(ImageViewState imageViewState) {
//            this.imageViewState = imageViewState;
//        }
//
//        @Override
//        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//            scaleImageView.setImage(ImageSource.cachedBitmap(resource),imageViewState);
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(ARGS_PICTURE,src);
//
//        View rootView = getView();
//        if (rootView != null) {
//            ImageViewState state = scaleImageView.getState();
//            if (state != null) {
//                outState.putSerializable(BUNDLE_STATE, state);
//            }
//        }
//    }
//
//    private void onRestoreInstanceState(Bundle savedInstanceState) {
//        src = savedInstanceState.getString(ARGS_PICTURE);
//    }
//
//}
