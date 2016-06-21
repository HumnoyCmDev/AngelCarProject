package com.dollarandtrump.angelcar.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.PostActivity;
import com.dollarandtrump.angelcar.interfaces.OnSelectData;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.ImageModel;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.dollarandtrump.angelcar.rx_image.RxImagePicker;
import com.dollarandtrump.angelcar.rx_image.Sources;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.view.RecyclerGridAutoFit;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public class  ListImageFragment extends Fragment {
    private static final String TAG = "ListImageFragment";

    @Bind(R.id.listImage) RecyclerGridAutoFit mListImage;
    @Bind(R.id.group_gallery) LinearLayout mGroupGallery;

    InfoCarModel mInfoCarModel;
    Gallery mGallery;
    ListImageAdapter mAdapter;

    int mCount = 0;
    int mLimitImage = 7;

    public ListImageFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ListImageFragment newInstance(InfoCarModel infoCarModel) {
        ListImageFragment fragment = new ListImageFragment();
        Bundle args = new Bundle();
        args.putParcelable("infoCar",Parcels.wrap(infoCarModel));
        fragment.setArguments(args);
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
        mGallery = new Gallery();
        mInfoCarModel = Parcels.unwrap(getArguments().getParcelable("infoCar"));
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        mAdapter = new ListImageAdapter(getContext());
        mAdapter.setGallery(mGallery);
        mListImage.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemListener);
        initVisibility();

    }

    @OnClick(R.id.tvGallery)
    public void onClickGallery(){

        if (mCount > mLimitImage){
            OnSelectData selectData = (OnSelectData) getActivity();
            selectData.onSelectedCallback(PostActivity.CALL_GALLERY_NEXT,mInfoCarModel);
        }else {
            rxPickerPicker(Sources.GALLERY);
        }

//        if (Permission.storeage(getActivity())) {
//            if (id == R.id.tvGallery || (id == R.id.group_gallery &&
//                    mGallery.getListGallery().size() == 0)) {
               /* Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 44);*/
//            }
//        }
    }

    @OnClick(R.id.tvCamera)
    public void onClickCamera(){
        if (mCount > mLimitImage) return;
            rxPickerPicker(Sources.CAMERA);
    }

    private void rxPickerPicker(Sources sources){
        RxImagePicker.with(getActivity()).requestImage(sources)
                .subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                ImageModel imageModel = new ImageModel();
                imageModel.setUri(uri);
                mGallery.setListGallery(imageModel);
                mAdapter.setGallery(mGallery);
                mAdapter.notifyDataSetChanged();
                mCount++;
                initVisibility();
                checkGallery();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 44 && resultCode == Activity.RESULT_OK && data != null){
            String picturePath = AngelCarUtils.getFilesPath(getContext(),data);
            Log.i(TAG, "onActivityResult: "+picturePath);
            ImageModel imageModel = new ImageModel();
            imageModel.setFileImage(new File(picturePath));
            imageModel.setUri(data.getData());
            mGallery.setListGallery(imageModel);
            mAdapter.setGallery(mGallery);
            mAdapter.notifyDataSetChanged();
            mCount++;
            initVisibility();
            checkGallery();

        }
    }

    private void checkGallery(){
        OnSelectData selectData = (OnSelectData) getActivity();
        if (mGallery.getListGallery().size() > 2){
            // post event
            mInfoCarModel.setGallery(mGallery);
            MainThreadBus.getInstance().post(onProduceData());
            selectData.onSelectedCallback(PostActivity.CALL_GALLERY_OK,mInfoCarModel);
        }else {
            selectData.onSelectedCallback(PostActivity.CALL_GALLERY_CANCEL,mInfoCarModel);
        }
    }

    private void initVisibility() {
        if (mGallery.getListGallery().size() == 0) {
            mListImage.setVisibility(View.GONE);
            mGroupGallery.setGravity(Gravity.CENTER_VERTICAL);
            Animation animIn = AnimationUtils.loadAnimation(getContext(),R.anim.zoom_face_in);
            mGroupGallery.setAnimation(animIn);
        } else {
            mListImage.setVisibility(View.VISIBLE);
            mGroupGallery.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    @Produce
    public InfoCarModel onProduceData(){
        return mInfoCarModel;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
//        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MainThreadBus.getInstance().unregister(this);
    }

//    @Subscribe
//    public void eventBusProduceData(InfoCarModel carModel){
//        mInfoCarModel = carModel;
//        Log.d(TAG, "3 : "+carModel.isEditInfo());
//    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       outState.putParcelable("data", Parcels.wrap(mInfoCarModel));
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        mInfoCarModel = Parcels.unwrap(savedInstanceState.getParcelable("data"));
    }

    /*****************
     * Listener Zone *
     *****************/
    OnItemClickListener onItemListener = new OnItemClickListener() {
        @Override
        public void onSelectItem() {
            onClickGallery();
        }

        @Override
        public void onUnSelectItem(int position) {
            mGallery.removeItem(position);
            mAdapter.setGallery(mGallery);
            mAdapter.notifyDataSetChanged();
            initVisibility();
            mCount--;
            checkGallery();
        }
    };

    /******************
     *Inner Class Zone*
     ******************/
    public class ListImageAdapter extends RecyclerView.Adapter<ListImageAdapter.ViewHolder>{

        private Context mContext;
        private OnItemClickListener onItemListener;

        Gallery mGallery;

        public void setGallery(Gallery mGallery) {
            this.mGallery = mGallery;
        }

        public ListImageAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setOnItemClickListener(OnItemClickListener onItemListener) {
            this.onItemListener = onItemListener;
        }

        @Override
        public int getItemViewType(int position) {
            return position == getItemCount()-1 ? 1 : 0;
        }

        @Override
        public ListImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.adapter_item_gallery, parent, false));
        }

        @Override
        public void onBindViewHolder(ListImageAdapter.ViewHolder holder, int position) {
             if (getItemViewType(position) == 0){
                 Glide.with(mContext)
                         .load(mGallery.getListGallery().get(position).getUri())
                         .crossFade()
                         .into(holder.mImageGallery);
             }
        }

        @Override
        public int getItemCount() {
            if (mGallery == null) return 1;
            return mGallery.getCount()+1;
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
                    if (getAdapterPosition() == getItemCount()-1)
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
