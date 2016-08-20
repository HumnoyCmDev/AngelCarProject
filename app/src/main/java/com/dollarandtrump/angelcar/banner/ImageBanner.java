package com.dollarandtrump.angelcar.banner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PictureDao;
import com.dollarandtrump.angelcar.utils.ViewFindUtils;
import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.view.AdjustableImageView;
import com.dollarandtrump.angelcar.view.PhotoBanner;
import com.flyco.banner.widget.Banner.BaseIndicatorBanner;

public class ImageBanner extends BaseIndicatorBanner<PictureDao, ImageBanner> {
    private ColorDrawable colorDrawable;

    public ImageBanner(Context context) {
        this(context, null, 0);
    }

    public ImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    @Override
    public void onTitleSlect(TextView tv, int position) {
//        final PictureDao item = mDatas.get(position)
//        tv.setText(item.getTopic());
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = View.inflate(mContext, R.layout.adapter_simple_image, null);
//        ImageView iv = ViewFindUtils.find(inflate, R.id.iv);
        PhotoBanner iv  = ViewFindUtils.find(inflate,R.id.iv);

        final PictureDao item = mDatas.get(position);
//        int itemWidth = mDisplayMetrics.widthPixels;
//        int itemHeight = (int) (itemWidth * 360 * 1.0f / 640);
//        iv.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));
        String BASE_URL_THUMBNAIL = item.getCarImageFullHDPath();
//        String urlImage ="http://angelcar.com/"+item.getCarImagePath()
//                .replace("carimages","thumbnailcarimages");

        if (!TextUtils.isEmpty(BASE_URL_THUMBNAIL)) {
            Glide.with(mContext)
                    .load(BASE_URL_THUMBNAIL)
//                    .override(itemWidth, itemHeight)
                    .crossFade()
                    .centerCrop()
                    .placeholder(colorDrawable)
                    .into(iv);
        } else {
            iv.setImageDrawable(colorDrawable);
        }

        return inflate;
    }


}
