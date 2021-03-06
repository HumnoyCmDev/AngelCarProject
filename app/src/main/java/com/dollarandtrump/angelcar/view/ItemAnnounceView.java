package com.dollarandtrump.angelcar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.hndev.library.view.BaseCustomViewGroup;
import com.hndev.library.view.sate.BundleSavedState;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ItemAnnounceView extends BaseCustomViewGroup {
    ImageView ic_Profile;
    TextView detail;
    public ItemAnnounceView(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public ItemAnnounceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public ItemAnnounceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ItemAnnounceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.custom_view_item_announce, this);

    }

    private void initInstances() {
        ic_Profile = (ImageView) findViewById(R.id.custom_view_icon_profile);
        detail = (TextView) findViewById(R.id.custom_view_cell_text);


        String d = AngelCarUtils.textFormatHtml("#FF0000","Tip: ")+"แชทกับผู้ขายเพื่อต่อรองราคาได้ทันทีและกด <img src ='ic_custom_header_person.png'> " +
                "เพื่อสอบถามยอดจัดจากธนาคาร";
        Spanned spanned = Html.fromHtml(d, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = 0;

                if(source.equals("ic_custom_header_person.png")){
                    id = R.drawable.ic_custom_header_person;
                }
                LevelListDrawable d = new LevelListDrawable();
                Drawable empty = getResources().getDrawable(id);
                d.addLevel(0, 0, empty);
                assert empty != null;
                d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                return d;
            }
        }, null);
        detail.setText(spanned);
    }


    public void setIconProfile(String url){
        Glide.with(getContext()).load(url)
                .placeholder(com.hndev.library.R.drawable.icon_logo)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(ic_Profile);
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

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
////        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int newWidth = (width / 8) * 7;
//        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth,MeasureSpec.EXACTLY);
//        // หลอกขนาดของลูก
//        super.onMeasure(newWidthMeasureSpec,heightMeasureSpec);
//        // หลอกขนาดของแม่
////        setMeasuredDimension(width,heightMeasureSpec);
//    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

//        Bundle bundle = ss.getBundle();
    }

}
