package com.dollarandtrump.angelcar.dialog;

import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.manager.ProgressRequestBody;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.NotResponse;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.utils.Log;
import com.dollarandtrump.angelcar.utils.ReduceSizeImage;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MultipartBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class ShopEditDialog extends DialogFragment implements ProgressRequestBody.UploadCallbacks{
    public final static int REQUEST_CODE = 100;
    private final int NOTIFICATION_ID = 10;

    public interface EditShopCallback{
        void onSuccess();
        void onFail();
    }

    @Bind(R.id.editShopName) EditText editShopName;
    @Bind(R.id.editShopDescription) EditText editShopDescription;
    @Bind(R.id.text_view_shop_number) TextView tvShopNumber;
    @Bind(R.id.shopProfileImage) CircularImageView profileImage;

    private EditShopCallback editShopCallback;

    private String shopName, shopDescription,shopNumber , logoShop;
    private Uri mUri = null;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

//        ((MainApplication) Contextor.getInstance().getContext()).getApplicationComponent().inject(this);
    }

    private void init(Bundle savedInstanceState) {
//        MainThreadBus.getInstance().register(this);
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
                .placeholder(com.hndev.library.R.drawable.icon_logo)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(profileImage);

        notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

       notification = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("อัพโหลดรูปโปรไฟล์")
                .setContentText("กำลังอัพโหลดรูป")
                .setLights(Color.RED,3000,3000)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);

    }

    @OnClick(R.id.tv_button_upload_image)
    public void uploadImage(){
        RxImagePicker.with(getActivity())
                .requestImage(Sources.GALLERY)
                .subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        mUri = uri;
                        Glide.with(getActivity()).load(uri)
                                .placeholder(com.hndev.library.R.drawable.icon_logo)
                                .bitmapTransform(new CropCircleTransformation(getActivity()))
                                .into(profileImage);
                    }
                });
//        Intent i = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        getActivity().startActivityForResult(i, REQUEST_CODE);
    }

    @OnClick(R.id.btnSaveShop)
    public void saveShop(){

        final String shopName = editShopName.getText().toString().trim();
        final String shopDescription = editShopDescription.getText().toString().trim();
        if (shopName.equals("") || shopDescription.equals("")){
            Snackbar.make(editShopName,"กรุณาใส่ข้อมูลให้ครบ",Snackbar.LENGTH_SHORT).show();
            return;
        }
        String shopRef = Registration.getInstance().getShopRef();
        String message = shopRef+"||"+shopName+"||"+shopDescription;
        Observable<ResponseDao> rxCall = HttpManager.getInstance().getService().observableEditShop(message)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        rxCall.subscribe(new Observer<ResponseDao>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (editShopCallback != null)
                    editShopCallback.onFail();
            }

            @Override
            public void onNext(ResponseDao responseDao) {
                Log.d("edit name success ::");
//                preferencesDefault.edit().putString("pre_shop_name",shopName).apply();
//                preferencesDefault.edit().putString("pre_description",shopDescription).apply();

                if (editShopCallback != null)
                    editShopCallback.onSuccess();
                dismiss();
            }
        });

        // upload image profiles
        if (mUri != null) {
//            HttpUploadManager.uploadLogoShop(FileUtils.getFile(getActivity(), mUri), Registration.getInstance().getShopRef())
//                    .doOnNext(new Action1<String>() {
//                        @Override
//                        public void call(String s) {
//                            Log.d("upload image shop success ::"+s);
//                        }
//                    })
//                    .doOnError(new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            Log.e("error dialog shop",throwable);
//                        }
//                    }).subscribe();

            File newFile = new ReduceSizeImage(FileUtils.getFile(getActivity(), mUri))
                    .resizeImageFile(ReduceSizeImage.SIZE_SMALL);
            ProgressRequestBody fileBody = new ProgressRequestBody(newFile, this);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("userfile", newFile.getName(), fileBody);
            HttpManager.getInstance()
                    .getService()
                    .observableUploadLogoShop(Registration.getInstance().getShopRef(),filePart)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Log.e("ERROR Shop Edit",throwable);
                }
            })
            .doOnNext(new Action1<NotResponse>() {
                @Override
                public void call(NotResponse s) {
                    notificationManager.cancel(NOTIFICATION_ID);
                    notification.setProgress(100,100,false);
                    notification.setContentText("อัพโหลดรูปโปรไฟล์สำเร็จ")
                                .setVibrate(new long[]{50,50})
                                /*.setProgress(0,0,false)*/;
                    notificationManager.notify(NOTIFICATION_ID,notification.build());
                }
            }).subscribe();
        }


        dismiss();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
//        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public void onProgressUpdate(int percentage) {
        notification.setProgress(100,percentage,false);
        notificationManager.notify(NOTIFICATION_ID,notification.build());
    }

    /******************
     *Listener zone*
     ******************/


    /******************
     *Inner class zone*
     ******************/


}
