package com.dollarandtrump.angelcar.view;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.hndev.library.view.BaseCustomViewGroup;
import com.hndev.library.view.sate.BundleSavedState;
//https://github.com/traex/ExpandableLayout/blob/master/library/src/main/java/com/andexert/expandablelayout/library/ExpandableLayout.java
public class BarStatusView extends BaseCustomViewGroup {

    private TextView mTitle;
    private ProgressBar mProgressBar;
    private LinearLayout mGroupStatus;


    private Boolean isAnimationRunning = false;
    private Boolean isOpened = false;
    private Integer duration;
    private Animation animation;


    public BarStatusView(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public BarStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public BarStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public BarStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.custom_view_bar_status, this);
    }

    private void initInstances() {
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mTitle = (TextView) findViewById(R.id.status_title);
        mProgressBar = (ProgressBar) findViewById(R.id.status_progress_bar);
        mGroupStatus = (LinearLayout) findViewById(R.id.group_status);

        duration = getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

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

    public void setVisivityProgressBar(int v){
        mProgressBar.setVisibility(v);
    }

    public void setTitle(String s){
        mTitle.setText(s);
    }
    public void setTitle(int s){
        mTitle.setText(s);
    }

    public void start(){
        if (!isAnimationRunning) {
            if (mGroupStatus.getVisibility() == VISIBLE)
                collapse(mGroupStatus);
            else
                expand(mGroupStatus);

            isAnimationRunning = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isAnimationRunning = false;
                }
            }, duration);
        }
    }


    private void expand(final View v){
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(VISIBLE);

        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                if (interpolatedTime == 1)
                    isOpened = true;
                v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    private void collapse(final View v){
        final int initialHeight = v.getMeasuredHeight();
        animation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                    isOpened = false;
                }
                else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    public Boolean isOpened()
    {
        return isOpened;
    }

    public void show() {
        invalidate();
        if (!isAnimationRunning) {
            expand(mGroupStatus);
            isAnimationRunning = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isAnimationRunning = false;
                }
            }, duration);
        }
    }

    public void hide() {
        invalidate();
        if (!isAnimationRunning) {
            collapse(mGroupStatus);
            isAnimationRunning = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isAnimationRunning = false;
                }
            }, duration);
        }
    }

    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        animation.setAnimationListener(animationListener);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // savedState.getBundle().putString("key", value);

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
    }

    //    public void close(){
//
//            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -100);
//            translateAnimation.setDuration(1000);
//            translateAnimation.setFillEnabled(true);
//            translateAnimation.setFillBefore(true);
//            translateAnimation.setFillAfter(true);
//            mGruopStatus.startAnimation(translateAnimation);
//            setVisibility(View.GONE);
//    }

//    public void expand(){
//        setVisibility(View.VISIBLE);
//        TranslateAnimation translateAnimation = new TranslateAnimation(0,0,-100,0);
//        translateAnimation.setDuration(1000);
//        translateAnimation.setFillEnabled(true);
//        translateAnimation.setFillBefore(true);
//        translateAnimation.setFillAfter(true);
//        mGruopStatus.startAnimation(translateAnimation);
//
//    }

}
