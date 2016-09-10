package com.dollarandtrump.angelcar.configuration;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.dollarandtrump.angelcar.utils.ReduceSizeImage;
import com.hndev.library.view.Transformtion.ScalingUtilities;

public class GlideTargetBitmap extends SimpleTarget<Bitmap> {
    private ImageView mImage;

    public GlideTargetBitmap(ImageView mImage) {
        this.mImage = mImage;
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        final Target<?> that = this;
        ReduceSizeImage.Size newSize = ReduceSizeImage.solution(ReduceSizeImage.SIZE_SMALL, resource.getWidth(), resource.getHeight());
        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(resource, newSize.getWidth(), newSize.getHeight(), ScalingUtilities.ScalingLogic.CROP);
        mImage.setImageBitmap(scaledBitmap);
//                                resource.recycle();
        Glide.clear(that);
    }
}
