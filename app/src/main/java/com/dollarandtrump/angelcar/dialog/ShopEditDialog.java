package com.dollarandtrump.angelcar.dialog;

import android.net.Uri;
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
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.HttpUploadManager;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.utils.FileUtils;
import com.dollarandtrump.angelcar.utils.Log;
import com.github.siyamed.shapeimageview.CircularImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class ShopEditDialog extends DialogFragment {
    public final static int REQUEST_CODE = 100;
    public interface EditShopCallback{
        void onSuccess();
        void onFail();
    }

    @Bind(R.id.editShopName) EditText editShopName;
    @Bind(R.id.editShopDescription) EditText editShopDescription;
    @Bind(R.id.text_view_shop_number) TextView tvShopNumber;
    @Bind(R.id.shopProfileImage) CircularImageView profileImage;

    EditShopCallback editShopCallback;

    String shopName, shopDescription,shopNumber , logoShop;
    Uri mUri = null;

//    @Inject
//    @Named("default")
//    SharedPreferences preferencesDefault;

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
            HttpUploadManager.uploadLogoShop(FileUtils.getFile(getActivity(), mUri), Registration.getInstance().getShopRef())
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Log.d("upload image shop success ::"+s);
                        }
                    })
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e("error dialog shop",throwable);
                        }
                    }).subscribe();
        }


        dismiss();
    }

//    @Subscribe
//    public void onActivityResultReceived(ActivityResultEvent event) {
//        int requestCode = event.getRequestCode();
//        int resultCode = event.getResultCode();
//        Intent data = event.getData();
//        onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
//            picturePath = AngelCarUtils.getFilesPath(getContext(),data);
//            Glide.with(this).load(new File(picturePath))
//                    .placeholder(com.hndev.library.R.drawable.loading)
//                    .bitmapTransform(new CropCircleTransformation(getActivity()))
//                    .into(profileImage);
//        }
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
//        MainThreadBus.getInstance().unregister(this);
    }

    /******************
     *Listener zone*
     ******************/


    /******************
     *Inner class zone*
     ******************/


}
