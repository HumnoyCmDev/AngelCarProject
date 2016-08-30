package com.dollarandtrump.angelcar.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PhotoThumb extends ImageView{
    public PhotoThumb(Context context) {
        super(context);
    }

    public PhotoThumb(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoThumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoThumb(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int bothDimensionSpec = widthMeasureSpec;
        super.onMeasure(bothDimensionSpec, bothDimensionSpec);

    }
}
