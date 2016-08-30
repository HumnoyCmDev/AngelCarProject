package com.dollarandtrump.angelcar.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PhotoBanner extends ImageView{

    public PhotoBanner(Context context) {
        super(context);

    }

    public PhotoBanner(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PhotoBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 2 / 3;
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
        setMeasuredDimension(width,height);

    }
}
