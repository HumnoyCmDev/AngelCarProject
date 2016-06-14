package com.dollarandtrump.angelcar.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.manager.Permission;
import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.ImageModel;
import com.dollarandtrump.angelcar.model.InformationCarModel;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.view.RecyclerGridAutoFit;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public class ListCarImageFragment extends Fragment {
    private static final String TAG = "ListCarImageFragment";

    @Bind(R.id.listImage) RecyclerGridAutoFit mListImage;
    private InformationCarModel mInformationCarModel;
    Gallery<ImageModel> mGallery;
    ListImageAdapter mAdapter;
    public ListCarImageFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ListCarImageFragment newInstance() {
        ListCarImageFragment fragment = new ListCarImageFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_car_image, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mGallery = new Gallery<>();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        mAdapter = new ListImageAdapter(getContext());
        mAdapter.setGallery(mGallery);
        mListImage.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onSelectItem() {
                if (Permission.storeage(getActivity())) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 44);
                }
            }

            @Override
            public void onUnSelectItem(int position) {
                Log.i(TAG, "onUnSelectItem: ");
                mGallery.removeItem(position);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 44 && resultCode == Activity.RESULT_OK && data != null){
            String picturePath = AngelCarUtils.getFilesPath(getContext(),data);
            Log.i(TAG, "onActivityResult: "+picturePath);
            ImageModel imageModel = new ImageModel(new File(picturePath),"0");
            mGallery.setListGallery(imageModel);

            mAdapter.setGallery(mGallery);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    /******************
     *Inner Class Zone*
     ******************/
    public class ListImageAdapter extends RecyclerView.Adapter<ListImageAdapter.ViewHolder>{

        private Context mContext;
        private OnItemClickListener onItemListener;

        Gallery<ImageModel> mGallery;

        public void setGallery(Gallery<ImageModel> mGallery) {
            this.mGallery = mGallery;
        }

        public ListImageAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setOnItemClickListener(OnItemClickListener onItemListener) {
            this.onItemListener = onItemListener;
        }

        @Override
        public ListImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.adapter_item_gallery, parent, false));
        }

        @Override
        public void onBindViewHolder(ListImageAdapter.ViewHolder holder, int position) {
             if (getItemViewType(position) == 1){
                 Glide.with(mContext)
                         .load(mGallery.getListGallery().get(position-1).getFileImage())
                         .into(holder.mImageGallery);
                 Log.i(TAG, "onBindViewHolder: "+mGallery.getListGallery().get(position-1).getFileImage().getName());
             }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public int getItemCount() {
            if (mGallery == null) return 1;
            return mGallery.getCount() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            @Bind(R.id.imageGallery) ImageView mImageGallery;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                mImageGallery.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (onItemListener != null){
                    if (getAdapterPosition() == 0)
                        onItemListener.onSelectItem();
                    else
                        onItemListener.onUnSelectItem(getAdapterPosition());
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onSelectItem();
        void onUnSelectItem(int position);
    }
}
