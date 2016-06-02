package com.dollarandtrump.angelcar.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.manager.PhotoLoad;
import com.dollarandtrump.angelcar.view.RecyclerGridAutoFit;
import com.hndev.library.view.Transformtion.ScalingUtilities;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 30/5/59.16:44น.
 *
 * @AngelCarProject
 */
public class SimpleGalleryActivity extends AppCompatActivity {

    @Bind(R.id.recyclerGallery)
    RecyclerGridAutoFit gallery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_gallery);
        ButterKnife.bind(this);
        gallery.setLayoutManager(new GridLayoutManager(this,3));

        AdapterGallery adapterGallery = new AdapterGallery();
        adapterGallery.setPathImage(getAllShownImagesPath());

        gallery.setAdapter(adapterGallery);

    }


    private ArrayList<String> getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data;//, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = getContentResolver().query(uri, projection, null,
                null, null);

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
       /* column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);*/
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    class AdapterGallery extends RecyclerView.Adapter<AdapterGallery.ViewHolder>{
        ArrayList<String>  pathImage;
        Context mContext;
        public void setPathImage(ArrayList<String> pathImage) {
            this.pathImage = pathImage;
        }

        @Override
        public AdapterGallery.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_gallery,parent,false);
            mContext = parent.getContext();
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final AdapterGallery.ViewHolder holder, int position) {

            Glide.with(mContext)
                    .load(pathImage.get(position))
                    .error(R.drawable.ic_hndeveloper)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            if (pathImage == null) return 0;
            return pathImage.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.imageGallery) ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }


        }
    }

}
