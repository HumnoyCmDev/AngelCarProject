package com.dollarandtrump.angelcar.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.ActivityResultEvent;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.HttpUploadManager;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.otto.Subscribe;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by humnoy on 24/2/59.
 */
public class ShopEditDialog extends DialogFragment {
    public final static int REQUEST_CODE = 100;
    public interface EditShopCallback{
        void onSuccess();
        void onFail();
    }

    @Bind(R.id.editShopName) EditText editShopName;
    @Bind(R.id.editShopDescription) EditText editShopDescription;
    @Bind(R.id.tvShopNumber) TextView tvShopNumber;
    @Bind(R.id.shopProfileImage) CircularImageView profileImage;

    EditShopCallback editShopCallback;

    String shopName, shopDescription,shopNumber , logoShop;
    String picturePath = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        MainThreadBus.getInstance().register(this);
        Bundle args = getArguments();
        if (args != null){
            shopName = args.getString("shopName");
            shopDescription = args.getString("shopDescription");
            shopNumber = args.getString("shopNumber");
            logoShop = args.getString("logoShop");
        }
    }

    public void setEditShopCallback(EditShopCallback editShopCallback) {
        this.editShopCallback = editShopCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_shop,container,false);
        initInstance(view,savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    private void initInstance(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this,view);

        editShopName.setText(shopName);
        editShopDescription.setText(shopDescription);
        tvShopNumber.setText(shopNumber);

        Glide.with(this).load(logoShop)
                .placeholder(com.hndev.library.R.drawable.loading)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(profileImage);

    }


    @OnClick(R.id.tvButtonUpLoadImage)
    public void uploadImage(){
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(i, REQUEST_CODE);
    }

    @OnClick(R.id.btnSaveShop)
    public void saveShop(){

        String shopName = editShopName.getText().toString().trim();
        String shopDescription = editShopDescription.getText().toString().trim();
        if (shopName.equals("") || shopDescription.equals("")){
            Snackbar.make(getActivity().getWindow().getDecorView(),"กรุณาใส่ข้อมูลให้ครบ",Snackbar.LENGTH_SHORT).show();
            return;
        }
        String shopRef = Registration.getInstance().getShopRef();
        String message = shopRef+"||"+shopName+"||"+shopDescription;
        Observable<Results> rxCall = HttpManager.getInstance().getService().editShopObservable(message)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        rxCall.subscribe(new Observer<Results>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (editShopCallback != null)
                    editShopCallback.onFail();
            }

            @Override
            public void onNext(Results results) {
                if (editShopCallback != null)
                    editShopCallback.onSuccess();
            }
        });

        // upload image profiles
        if (picturePath != null) {
            HttpUploadManager.uploadLogoShop(new File(picturePath), Registration.getInstance().getShopRef(), new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            Log.i("ShopEdit", "onCompleted: ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("ShopEdit", "onError: ", e);
                        }

                        @Override
                        public void onNext(String s) {
                            Log.i("ShopEdit", "onNext: "+s);
                        }
                    });
        }
        dismiss();
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
            picturePath = AngelCarUtils.getFilesPath(getContext(),data);
            Glide.with(this).load(new File(picturePath))
                    .placeholder(com.hndev.library.R.drawable.loading)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(profileImage);
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
     *Inner class zone*
     ******************/


}
