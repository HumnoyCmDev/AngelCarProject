package com.dollarandtrump.angelcar.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.model.CacheShop;
import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.utils.Log;
import com.hndev.library.view.BaseCustomViewGroup;
import com.hndev.library.view.sate.BundleSavedState;

import org.parceler.Parcels;

public class PhotoView extends BaseCustomViewGroup {
    public interface OnClickListener {
        public void onClickListener(Uri uri, int position);
    }
    private ImageView mPhoto;
    private RecyclerView mPhotoList;
    private Gallery mGallery;
    private PhotoAdapter mAdapter;
    private OnClickListener clickListener;
    public PhotoView(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.custom_list_photo, this);
    }

    private void initInstances() {
        mPhoto = (ImageView) findViewById(R.id.image_photo);
        mPhotoList = (RecyclerView) findViewById(R.id.recycler_list_photo);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mPhotoList.setLayoutManager(manager);

        mAdapter = new PhotoAdapter();
        mAdapter.setGallery(mGallery);
        mPhotoList.setAdapter(mAdapter);
    }

    public void onBindingData(Gallery gallery){
        mGallery = gallery;
        Glide.with(getContext())
                .load(mGallery.getListGallery().get(0).getUri())
                .crossFade()
                .into(mPhoto);
        mAdapter.setGallery(mGallery);
        mAdapter.notifyDataSetChanged();

        clickListener = new OnClickListener() {
            @Override
            public void onClickListener(Uri uri, int position) {
                Glide.with(getContext())
                        .load(uri)
                        .crossFade()
                        .into(mPhoto);
            }
        };

    }


    private void initWithAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {

    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * 2 / 3;
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
        setMeasuredDimension(width,height);

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        savedState.getBundle().putParcelable("gallery", Parcels.wrap(mGallery));

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        Bundle bundle = ss.getBundle();
        mGallery = Parcels.unwrap(bundle.getParcelable("gallery"));
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder>{
        private Gallery mGallery;

        public void setGallery(Gallery gallery) {
            this.mGallery = gallery;
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_gallery, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, final int position) {
            Glide.with(getContext())
                    .load(this.mGallery.getListGallery().get(position).getUri())
                    .crossFade()
                    .into(holder.mImageChildPhoto);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null){
                        clickListener.onClickListener(mGallery.getListGallery().get(position).getUri(),position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (this.mGallery == null)return 0;
            if (this.mGallery.getListGallery() == null) return 0;
            return this.mGallery.getListGallery().size();
        }
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView mImageChildPhoto;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            mImageChildPhoto = (ImageView) itemView.findViewById(R.id.imageGallery);
        }
    }
}
