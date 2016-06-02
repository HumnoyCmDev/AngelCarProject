package com.dollarandtrump.angelcar.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarBrandCollectionDao;
import com.dollarandtrump.angelcar.dao.CarBrandDao;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.fragment.FeedPostCarFragment;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.github.siyamed.shapeimageview.CircularImageView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by humnoy on 24/2/59.
 */
public class ShopEditDialog extends DialogFragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
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
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /******************
     *Listener zone*
     ******************/


    /******************
     *Inner class zone*
     ******************/


}
