package com.dollarandtrump.angelcar.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by humnoy on 16/2/59.
 */
public class ResizeHeight extends Animation {
    private int targetHeight;
    private int startHeight;
    private View view;
    private int initialHeight;
    private boolean isExpand = true;

    public ResizeHeight(View view, int targetHeight,boolean isExpand) {
        this.targetHeight = targetHeight;
        this.view = view;
        this.startHeight = view.getHeight();
        initialHeight = 1000;//view.getMeasuredHeight();
        this.isExpand = isExpand;

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        if (isExpand) {
            if (view.getVisibility() == View.GONE){
                view.setVisibility(View.VISIBLE);
            }
            view.getLayoutParams().height = (int) (startHeight + targetHeight * interpolatedTime);
            view.requestLayout();
        }

        if (!isExpand){
            if (interpolatedTime == 1){
                view.setVisibility(View.GONE);
            }else {
                view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                view.requestLayout();
            }
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
