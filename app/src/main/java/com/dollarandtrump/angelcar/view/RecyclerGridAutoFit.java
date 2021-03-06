package com.dollarandtrump.angelcar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.dollarandtrump.angelcar.R;


public class RecyclerGridAutoFit extends RecyclerView{
    private int columnWidth;
    private GridLayoutManager manager;

    public RecyclerGridAutoFit(Context context) {
        super(context);

    }

    public RecyclerGridAutoFit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RecyclerGridAutoFit(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(
                    attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        manager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(manager);

        spacing = getResources().getDimensionPixelSize(R.dimen.bottom_image_title_spacing);
    }

    int thumbnailSize;
    int spacing;
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            manager.setSpanCount(spanCount);
        }

        /*// new
        int width  = MeasureSpec.getSize(widthSpec);
        float density = getResources().getDisplayMetrics().density;
        final int numColumns = (int) (width / (columnWidth * density));
        thumbnailSize = Math.round((width - ((numColumns - 1) * spacing)) / 3.0f);
        manager.setSpanCount(numColumns);
        super.onMeasure(widthSpec, heightSpec);*/
    }

    public int getThumbnailSize() {
        return thumbnailSize;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }
}
