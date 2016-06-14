package com.dollarandtrump.angelcar.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.manager.ActivityResultEvent;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpUploadManager;
import com.dollarandtrump.angelcar.model.ImageModel;
import com.dollarandtrump.angelcar.model.ShopImageModel;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;


/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 31/5/59.13:43น.
 *
 * @AngelCarProject
 */
public class ShopUpLoadDialog extends DialogFragment {
    private static final String TAG = "ShopUpLoad";

    public final static int REQUEST_CODE = 899;
    @Bind(R.id.recyclerShopAllPicture)
    RecyclerView recyclerShop;


//    List<File> listPicture;
    ShopUpLoadAdapter adapter;
    ShopImageModel imageModel;
    List<ImageModel> imageModelList;
    int index = 0;

    public static ShopUpLoadDialog newInstance() {
        Bundle args = new Bundle();
        ShopUpLoadDialog fragment = new ShopUpLoadDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
//        listPicture = new ArrayList<>();
        MainThreadBus.getInstance().register(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_shop_all_picture, container, false);
        initInstance(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    private void initInstance(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        imageModel = new ShopImageModel();
        imageModelList = new ArrayList<>();

        adapter = new ShopUpLoadAdapter();
        recyclerShop.setAdapter(adapter);
        adapter.setOnItemClickListener(new ShopUpLoadAdapter.OnItemClickListener() {
            @Override
            public void onItemClickSelect() {
                if (imageModel != null && imageModel.getImageModels() != null){
                    if (imageModel.getImageModels().size() > 3)
                        return;
                }
                intentLoadPictureExternalStore();
            }

            @Override
            public void onItemClickDelete(int position) {
//                listPicture.remove(position);
                imageModel.getImageModels().remove(position);
                adapter.notifyDataSetChanged();
            }
        });


    }

    private void intentLoadPictureExternalStore() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(i, REQUEST_CODE);
    }

    @Subscribe
    public void onActivityResultReceived(ActivityResultEvent event) {
        int requestCode = event.getRequestCode();
        int resultCode = event.getResultCode();
        Intent data = event.getData();

        onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
            String picturePath = AngelCarUtils.getFilesPath(getContext(),data);
            ImageModel model = new ImageModel();
            model.setFileImage(new File(picturePath));
            model.setIndex(String.valueOf(index));
            imageModelList.add(model);
            imageModel.setImageModels(imageModelList);
            index++;
//
//            listPicture.add(new File(picturePath));
            adapter.setListPicture(imageModel);
            adapter.notifyDataSetChanged();
        }

    }

    @OnClick(R.id.btnShopUpLoad)
    public void shopUpLoad(){
        HttpUploadManager.uploadFileShop(imageModel, Registration.getInstance().getShopRef(),
                new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext: "+s);
            }
        });

    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        BusProvider.getInstance().unregister(this);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            intentLoadPictureExternalStore();
        } else {
            dismiss();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        MainThreadBus.getInstance().unregister(this);
    }

    /******************
     *Listener zone*
     ******************/


    /******************
     * Inner class zone*
     ******************/
    public static class ShopUpLoadAdapter extends RecyclerView.Adapter<ShopUpLoadAdapter.ViewHolder> {
//        List<File> listPicture;
        ShopImageModel imageModel;
        OnItemClickListener onItemClickListener;
        Context mContext;

        public void setListPicture(ShopImageModel imageModel) {
            this.imageModel = imageModel;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public ShopUpLoadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_gallery, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ShopUpLoadAdapter.ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                Glide.with(mContext)
                        .load(imageModel.getImageModels().get(position - 1).getFileImage())
                        .placeholder(R.drawable.ic_image)
                        .into(holder.imageGallery);
            }
        }

        @Override
        public int getItemCount() {
            if (imageModel == null) return 1;
            if (imageModel.getImageModels() == null) return 1;

            return imageModel.getImageModels().size() + 1;

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.imageGallery)
            ImageView imageGallery;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                imageGallery.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {

                    if (getAdapterPosition() == 0)
                        onItemClickListener.onItemClickSelect();
                    else
                        onItemClickListener.onItemClickDelete(getAdapterPosition() - 1);
                }
            }
        }

        public interface OnItemClickListener {
            void onItemClickSelect();

            void onItemClickDelete(int position);
        }
    }

}