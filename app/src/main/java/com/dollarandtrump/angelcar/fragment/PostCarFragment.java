package com.dollarandtrump.angelcar.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.Adapter.ProvinceAdapter;
import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ShopActivity;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.ProvinceDao;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.dialog.EvidenceDialog;
import com.dollarandtrump.angelcar.interfaces.OnScrolling;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxUploadFile;
import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.utils.Log;
import com.dollarandtrump.angelcar.view.PhotoCollectionView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func5;
import rx.schedulers.Schedulers;


@SuppressWarnings("unused")
public class PostCarFragment extends Fragment {

    @Bind(R.id.image_icon_profile) ImageView mProfile;
    @Bind(R.id.text_shop_name) TextView mShopName;
    @Bind(R.id.text_status) TextView mStatus;
    @Bind(R.id.text_brand) TextView mBrand;
    @Bind(R.id.photo)
    PhotoCollectionView mPhoto;
    @Bind(R.id.toggle_button_gear) ToggleButton mGear;
    @Bind(R.id.edit_text_price) EditText mPrice;
    @Bind(R.id.edit_text_details) EditText mDetails;
    @Bind(R.id.edit_text_name) EditText mName;
    @Bind(R.id.edit_text_topic) EditText mTopic;
    @Bind(R.id.edit_text_telephone) EditText mTel;
    @Bind(R.id.spinner_province) Spinner mProvince;
    @Bind(R.id.button_post) Button mButtonPost;
    @Bind(R.id.text_evidence) TextView mEvidence;

    @Bind(R.id.scroll_view) NestedScrollView mScroll;

    private ProgressDialog mProgressDialog;

    private List<ProvinceDao> mListProvince;
    private int mIdProvince = 0;
    private InfoCarModel mCarModel;
    private Gallery mImageEvidence;

    private String[] mTypeEvidence = {"owner","delegate","dealers"};

    /**control scroll up down**/
    private int mLastPositionScroll = 0;
    private boolean isControlScroll = true;
    private ProfileDao mProfileDB;

    @Inject
    @Named("default") SharedPreferences preferencesDefault;

    public PostCarFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static PostCarFragment newInstance() {
        PostCarFragment fragment = new PostCarFragment();
        Bundle args = new Bundle();
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
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mProfileDB = SQLiteUtils.rawQuerySingle(ProfileDao.class,"SELECT * FROM Profile",null);
        mListProvince = new Select().from(ProvinceDao.class).execute();

        mImageEvidence = new Gallery();




    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(final View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        ((MainApplication) getActivity().getApplication()).getApplicationComponent().inject(this);

        mTel.setText(preferencesDefault.getString("pre_phone",""));
        mName.setText(preferencesDefault.getString("pre_shop_name",""));


//        mTel.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        // init data
        mShopName.setText(mProfileDB.getShopName());

        Glide.with(this)
                .load(mProfileDB.getUrlShopLogo())
                .placeholder(R.drawable.icon_logo)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .crossFade().into(mProfile);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("แจ้งเตือน");
        mProgressDialog.setMessage("ประกาศขายรถ");
        mProgressDialog.setCancelable(false);

        //TODO Show group button next
        mScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                OnScrolling onScrolling = (OnScrolling) getActivity();

                if (scrollY < mLastPositionScroll && !isControlScroll){
                    //scroll up
                    onScrolling.onScrollingUp();
                    isControlScroll = true;
                }else if (scrollY > mLastPositionScroll && isControlScroll){
                    //scroll down
                    onScrolling.onScrollingDown();
                    isControlScroll = false;
                }
                mLastPositionScroll = scrollY;
            }
        });

        ProvinceAdapter mProvinceAdapter = new ProvinceAdapter();
            mProvinceAdapter.setProvince(mListProvince);
            mProvince.setAdapter(mProvinceAdapter);

        mProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIdProvince = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        changeInfoCar();

    }

    private void changeInfoCar() {
        Observable<Boolean> observablePrice = createObservableLength(mPrice,0);
        Observable<Boolean> observableTel = createObservableLength(mTel,0);
        Observable<Boolean> observableDetail = createObservableLength(mDetails,0);
        Observable<Boolean> observableName = createObservableLength(mName,0);
        Observable<Boolean> observableTopic = createObservableLength(mTopic,0);
        Observable.combineLatest(observablePrice, observableTel, observableDetail, observableName, observableTopic,
                new Func5<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean, Boolean aBoolean2, Boolean aBoolean3, Boolean aBoolean4, Boolean aBoolean5) {
                return aBoolean && aBoolean2 && aBoolean3 && aBoolean4 && aBoolean5;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mButtonPost.setEnabled(aBoolean);
            }
        });
    }


    Observable<Boolean> createObservableLength(final EditText v, final int length){
        return RxTextView.textChangeEvents(v).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                boolean b = textViewTextChangeEvent.text().length() > length;
                v.setTextColor(b ? Color.BLACK : Color.RED);
                return b;
            }
        });
    }

    @OnClick(R.id.text_evidence)
    public void onClickEvidence(){

        /**แนบหลักฐาน**/
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Evidence");
        if (fragment != null){
            ft.remove(fragment);
        }
        EvidenceDialog evidenceDialog = EvidenceDialog.newInstance(mCarModel.getCategory(),mImageEvidence);
        evidenceDialog.show(getChildFragmentManager(),"Evidence");

    }

    @OnClick(R.id.button_post)
    public void onPostCar(){
        if (mImageEvidence.getListGallery().size() > 0){
            /**init data**/
            String topic = mTopic.getText().toString();
            String detail = AngelCarUtils.formatLineUp(mDetails.getText().toString().trim());
            String carPrice = mPrice.getText().toString().trim();// ราคารถ
            String province = String.valueOf(mIdProvince).trim(); // 1 - 77
            String gear = mGear.isChecked() ? "1" : "2"; // 0 or 1
            String name = mName.getText().toString().trim(); // ชื่อ นามสกุล
            String phone = mTel.getText().toString().trim();

            if (!mCarModel.isEditInfo()) {
                String shopPref = Registration.getInstance().getShopRef();
                post(topic, detail, carPrice, province, gear, name, phone, shopPref);

            } else {/**update post**/
                HttpManager.getInstance().getService()
                        .observableUpdatePostCar(mCarModel.getPostCarDao().getCarId(),
                                mCarModel.getBrandDao().getBrandId(), mCarModel.getSubDao().getSubId(), mCarModel.getSubDetailDao().getSubId(),
                                topic,detail, mCarModel.getYear(),carPrice,province,gear,name,phone)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseDao>() {
                            @Override
                            public void call(ResponseDao responseDao) {
                                dialogPostSuccess("แจ้งเตือน");
                            }
                        });
            }

        }else {
            Snackbar.make(getView(),"กรุณาแนบหลักฐานด้วยค่ะ",Snackbar.LENGTH_INDEFINITE).setAction("แนบหลักฐาน", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickEvidence();
                }
            }).show();
        }
    }

    public void post(String topic, String detail, String carPrice, String province, String gear, String name, String phone, String shopPref) {
        mProgressDialog.show();
        HttpManager.getInstance().getService().observablePostCar(shopPref, mCarModel.getBrandDao().getBrandId(),
                mCarModel.getSubDao().getSubId(), mCarModel.getSubDetailDao().getSubId(), topic, detail, mCarModel.getYear(),
                carPrice, "online", province, gear, "NULL", name, phone)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<ResponseDao, Boolean>() {
                    @Override
                    public Boolean call(ResponseDao responseDao) {
                        uploadImageCar(responseDao);
                        uploadEvidence(responseDao);
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        mProgressDialog.dismiss();
                        clearText();
                        dialogPostSuccess("แจ้งเตือน");
                    }
                });

    }

    void post(String shopId,String topic,String details,int brandId,int subBrandId,int brandDetailId,int year,String price,String lat,String log,int gear,int province,String name,String tel){
        // post message **new
        String message = shopId+"||"+topic+"||"+details+"||"+brandId+"||"+subBrandId+"||"+brandDetailId+"||"+year+"||"+price+"||"+lat+"||"+log+"||"+gear+"||"+province+"||"+name+"||"+tel;
//        HttpManager.getInstance().getService().observablePost(message)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseDao>() {
//                });
    }

    public void dialogPostSuccess(String title) {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(title)
                .setMessage("ดูรถได้ที่ shop")
                .setPositiveButton("shop", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                        String user = Registration.getInstance().getUserId();
                        String shop = Registration.getInstance().getShopRef();
                        Intent intentShop = new Intent(getActivity(), ShopActivity.class);
                        intentShop.putExtra("user", user);
                        intentShop.putExtra("shop", shop);
                        startActivity(intentShop);
                    }
                })
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void uploadEvidence(ResponseDao responseDao) {
        String shopId = Registration.getInstance().getShopRef();
        HttpManager.getInstance().getService()
                .observableEvidence(responseDao.getSuccess()+"||"+shopId+"||"+mTypeEvidence[mCarModel.getCategory()])
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseDao>() {
                    @Override
                    public void call(ResponseDao responseDao) {
                   /**upload image evidence**/
                        RxUploadFile.with(getContext()).evidence()
                                .setEvidence(responseDao.getApproveId(),mImageEvidence)
                                .subscriber(new Action1<String>() {
                                    @Override
                                    public void call(String s) {

                                    }
                                });
                    }
                });
    }

    public void uploadImageCar(ResponseDao responseDao) {
        RxUploadFile.with(getContext())
                .postCar()
                .setId(responseDao.getSuccess())
                .setGallery(mCarModel.getGallery())
                .subscriber();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void eventBusProduceData(InfoCarModel carModel) {
        this.mCarModel = carModel;

        String topic = carModel.getBrandDao().getBrandName().toUpperCase()+" "+
                carModel.getSubDao().getSubName()+" "+
                carModel.getSubDetailDao().getSubName()+" ปี"+
                carModel.getYear();
        mBrand.setText(topic);
        if (carModel.getGallery() != null && carModel.getGallery().getListGallery() != null) {
            mPhoto.onBindingData(carModel.getGallery());
        }
        /**status**/
        if(mCarModel.getCategory() != -1) {
        int status ;
            if (mCarModel.getCategory() == 0)
                status = R.string.post_owner;
            else if (mCarModel.getCategory() == 1)
                status = R.string.post_delegate;
            else
                status = R.string.post_dealers;
            mStatus.setText(status);
        }


        if (carModel.isEditInfo()){
            //init data (Edit)
            mPhoto.setVisibility(View.GONE);
            mGear.setChecked(carModel.getPostCarDao().getGear() == 0);
            Log.d(""+carModel.getPostCarDao().getProvinceId());
            mProvince.setSelection(carModel.getPostCarDao().getProvinceId()-1);
            mTel.setText(carModel.getPostCarDao().getPhone());
            mName.setText(carModel.getPostCarDao().getName());
            mPrice.setText(carModel.getPostCarDao().getCarPrice());
            mTopic.setText(carModel.getPostCarDao().getCarTitle());
            mDetails.setText(AngelCarUtils.convertLineUp(carModel.getPostCarDao().getCarDetail()));
            mButtonPost.setText("SAVE");
        }
    }

    private void clearText(){
        mTel.setText(null);
        mName.setText(null);
        mPrice.setText(null);
        mTopic.setText(null);
        mDetails.setText(null);
    }

    @Subscribe
    public void onSubscribeEvidence(Gallery imageEvidence){
        this.mImageEvidence = imageEvidence;
        if (imageEvidence.getListGallery().size() > 0){
             mEvidence.setBackgroundResource(R.drawable.selector_menu_bottom_evidence_success);
        }else{
            mEvidence.setBackgroundResource(R.drawable.selector_menu_bottom_evidence_unsuccess);
        }
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


}
