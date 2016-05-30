package com.hndev.library.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hndev.library.R;
import com.hndev.library.view.sate.BundleSavedState;

public class AngelCarHashTag extends BaseCustomViewGroup {
    private boolean isSelected = false;
    private String colorSelected = "#FFB13D";
    private String colorUnSelected = "#FFB13D";
//    private float radius = 15f;


    private LinearLayout hashTagGroupBrand;

    public interface OnClickHashTagListener{
        void onClickItem(boolean isSelected);
    }

    private TextView tvHashTag;
    private GradientDrawable gradientDrawable;
    public AngelCarHashTag(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public AngelCarHashTag(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public AngelCarHashTag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public AngelCarHashTag(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.custom_veiw_hashtag, this);
        hashTagGroupBrand = (LinearLayout) findViewById(R.id.custom_view_hash_tag_group_brand);
        tvHashTag = (TextView) findViewById(R.id.custom_view_hash_tag_brand);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#FFB13D"));
        gradientDrawable.setCornerRadius(dp2px(15f));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            tvHashTag.setBackground(gradientDrawable);
        }else {
            tvHashTag.setBackgroundDrawable(gradientDrawable);
        }
//        hashTagGroupBrand.addView(createViewHasTag("AngelCar1",15f,"#FFB13D"));
//        hashTagGroupBrand.addView(createViewHasTag("AngelCar",15f,"#FFB13D"));
        hashTagGroupBrand.setVisibility(GONE);
    }

    public void addChildCarSub(String carSub){
        hashTagGroupBrand.addView(createViewHasTag(carSub,10f,"#9E9E9E"));
    }

    public TextView createViewHasTag(String brand,float radius,String color){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_veiw_childe_brand_hashtag,null);
        TextView tvHashTag = (TextView) v.findViewById(R.id.customViewChildBrandHashTag);
        tvHashTag.setText(brand);
//        TextView tvHashTag = new TextView(getContext());
//        tvHashTag.setText(brand);
//        tvHashTag.setPadding(dp2px(4),dp2px(4),dp2px(4),dp2px(4));
//        tvHashTag.setTextColor(Color.BLACK);
////        tvHashTag.setTypeface(null, Typeface.BOLD);
//        tvHashTag.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(2,2,2,2);
        tvHashTag.setLayoutParams(param);
//        tvHashTag.setLayoutParams(param);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor(color));
        gradientDrawable.setCornerRadius(radius);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            tvHashTag.setBackground(gradientDrawable);
        }else {
            tvHashTag.setBackgroundDrawable(gradientDrawable);
        }
        return tvHashTag;
    }



    private void initInstances() {
        // findViewById here
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

    public void setTextSize(float size){
        tvHashTag.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }

    public void setText(String s){
        tvHashTag.setText(s);
    }

    public void setDrawableLeft(int drawableLeft){
        tvHashTag.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,0,0,0);
    }

    public void setDrawablePadding(int padding){
        tvHashTag.setCompoundDrawablePadding(padding);
    }

    public void setCornerRadius(float dp){
        gradientDrawable.setCornerRadius(dp2px(dp));
    }

    public void setColorBackground(String color){
        gradientDrawable.setColor(Color.parseColor(color));
    }

    public void setColorSelected(String colorSelected){
        this.colorSelected = colorSelected;
//        gradientDrawable.setColor(Color.parseColor(colorSelected));
    }

    public void setColorUnSelected(String colorUnSelected){
        this.colorUnSelected = colorUnSelected;
//        gradientDrawable.setColor(Color.parseColor(colorUnSelected));
    }

    public void setEnabled(){
        tvHashTag.setEnabled(true);
    }

    public void setTextColor(int color){
        tvHashTag.setTextColor(color);
    }

    public void setOnClickHashTagListener(final OnClickHashTagListener onClickHashTagListener){
        tvHashTag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelected) {
                    showChildSubCar();
                }else {
                    hideChildSubCar();
                }
                onClickHashTagListener.onClickItem(isSelected);
            }
        });

    }

    public void hideChildSubCar() {
        setColorBackground(colorUnSelected);
//                    setCornerRadius(radius);
        hashTagGroupBrand.setVisibility(GONE);
        Animation anim = AnimationUtils
                .loadAnimation(getContext(), R.anim.hash_tag_slide_right_out);
        hashTagGroupBrand.startAnimation(anim);
        isSelected = false;
    }

    public void showChildSubCar() {
        setColorBackground(colorSelected);
//                    setCornerRadius(5f);
        hashTagGroupBrand.setVisibility(VISIBLE);
        Animation anim = AnimationUtils
                .loadAnimation(getContext(), R.anim.hash_tag_slide_left_in);
        hashTagGroupBrand.startAnimation(anim);
        isSelected = true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);

        savedState.getBundle().putString("colorSelected",colorSelected);
        savedState.getBundle().putString("colorUnSelected",colorUnSelected);
        savedState.getBundle().putBoolean("isSelected",isSelected);

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        colorSelected = bundle.getString("colorSelected");
        colorUnSelected = bundle.getString("colorUnSelected");
        isSelected = bundle.getBoolean("isSelected");

        // Restore State from bundle here
    }

    /** px to dp */
    private float px2dp(float size) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = size / (metrics.densityDpi / 160f);
        return dp;
    }

    /** dp to px */
    protected int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
