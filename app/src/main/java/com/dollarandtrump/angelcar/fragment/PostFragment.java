package com.dollarandtrump.angelcar.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.RxUploadFile;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func6;
import rx.schedulers.Schedulers;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 5/2/59. เวลา 10:41
 ***************************************/
public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";

    @Bind(R.id.edit_text_description) EditText mDescription;
    @Bind(R.id.edit_text_telephone) EditText mTelephone;
    @Bind(R.id.edit_text_register) EditText mRegister;
    @Bind(R.id.edit_text_price) EditText mPrice;
    @Bind(R.id.edit_text_topic) EditText mTopic;
    @Bind(R.id.edit_text_Name) EditText mName;

    @Bind(R.id.spinner_province) Spinner mSpinnerProvince;
    @Bind(R.id.text_view_topic_car) TextView mTopicCar;
    @Bind(R.id.toggle_buuton_gear) ToggleButton mGear;
    @Bind(R.id.button_post) Button mBtnPost;


    private int mIdProvince = 1;
    private InfoCarModel mCarModel;

    public PostFragment() {
        super();
    }

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
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

    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        // Text Format
        mTelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mDescription.setOnEditorActionListener(onEditorActionListener);
        initDataProvince();
        observableEditTextView();
    }

    void observableEditTextView(){

        Observable<Boolean> registerValid = createObservableLength(mRegister,2);
        Observable<Boolean> telephoneValid = createObservableLength(mTelephone,11);
        Observable<Boolean> nameValid = createObservableLength(mName,3);
        Observable<Boolean> priceValid = createObservableLength(mPrice,5);
        Observable<Boolean> topicValid = createObservableLength(mTopic,20);
        Observable<Boolean> descriptionValid = createObservableLength(mDescription,20);

        Observable.combineLatest(registerValid, telephoneValid, nameValid, priceValid, topicValid, descriptionValid,
                new Func6<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean b1, Boolean b2, Boolean b3,
                                        Boolean b4, Boolean b5, Boolean b6) {
                        return b1 && b2 & b3 && b4 && b5 && b6;
                    }
                }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mBtnPost.setEnabled(aBoolean);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SAVE_CAR_MODEL",Parcels.wrap(mCarModel));

    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        mCarModel = Parcels.unwrap(savedInstanceState.getParcelable("SAVE_CAR_MODEL"));
    }



    private void initDataProvince() {
        List<String> list = new ArrayList<String>();
        getResources().getStringArray(R.array.province);
        Collections.addAll(list, getResources().getStringArray(R.array.province));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        mSpinnerProvince.setAdapter(dataAdapter);

        mSpinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIdProvince = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.button_post)
    public void onClickPost(){
        
        String topic = mTopic.getText().toString();
        String detail = AngelCarUtils.formatLineUp(mDescription.getText().toString().trim());
        String carPrice = mPrice.getText().toString().trim();// ราคารถ
        String province = String.valueOf(mIdProvince).trim(); // 1 - 77
        String gear = mGear.isChecked() ? "1" : "2"; // 0 or 1
        String plate = mRegister.getText().toString().trim(); // text ทะเบียนน
        String name = mName.getText().toString().trim(); // ชื่อ นามสกุล
        String phone = mTelephone.getText().toString().trim();

        if (!mCarModel.isEditInfo()) {
            String shopPref = Registration.getInstance().getShopRef(); // 1
            Call<Results> call = HttpManager.getInstance().getService().postCar(shopPref,
                    mCarModel.getBrandDao().getBrandId(),
                    mCarModel.getSubDao().getSubId(),
                    mCarModel.getSubDetailDao().getSubId(),
                    topic, detail, mCarModel.getYear(),
                    carPrice, "online", province, gear, plate, name, phone);
            call.enqueue(postCallback);


        }else {
            Log.d(TAG, "onClickPost: ");
             HttpManager.getInstance().getService()
                     .observableUpdatePostCar(mCarModel.getPostCarDao().getCarId(),
                             mCarModel.getBrandDao().getBrandId(), mCarModel.getSubDao().getSubId(), mCarModel.getSubDetailDao().getSubId(),
                             topic,detail, mCarModel.getYear(),carPrice,province,gear,name,phone)
             .subscribeOn(Schedulers.newThread())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(new Subscriber<Results>() {
                 @Override
                 public void onCompleted() {
                     Log.d(TAG, "onCompleted: ");
                 }

                 @Override
                 public void onError(Throwable e) {
                     Log.e(TAG, "onError: ", e);
                 }

                 @Override
                 public void onNext(Results results) {
                     Log.d(TAG, "onNext: "+results);
                 }
             });
        }
    }

    private static void uploadPicture(final String id, final HashMap<Integer, File> filesPhotoList,
                                      okhttp3.Callback responseCallbackUpFile) {
        final OkHttpClient client = new OkHttpClient();
        //post picture
        for (int i = 0; i < 8; i++) {
            if (filesPhotoList.containsKey(i)) {
                Log.i(TAG, "onClickPost: " + filesPhotoList.get(i).isFile() + "  userfile ::" + (1 + i));
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("carid", id)
                        .addFormDataPart(
                        "userfile",
                        filesPhotoList.get(i).getName(),
                        RequestBody.create(MediaType.parse("image/png"), filesPhotoList.get(i))).build();

                Request request = new Request.Builder()
                        .url("http://www.angelcar.com/ios/data/gadata/imgupload.php")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(responseCallbackUpFile);
            }
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    public void eventBusProduceData(InfoCarModel carModel){
        this.mCarModel = carModel;

        Log.i(TAG, "eventBusProduceData: "+carModel.getBrandDao().getBrandName());
//init data
        String topic = carModel.getBrandDao().getBrandName().toUpperCase()+" "+
                carModel.getSubDao().getSubName()+" "+
                carModel.getSubDetailDao().getSubName()+" ปี"+
                carModel.getYear();
        mTopicCar.setText(topic);
        if (carModel.isEditInfo()){
            //init data (Edit)
                mGear.setChecked(carModel.getPostCarDao().getGear() == 0);
                mSpinnerProvince.setSelection(carModel.getPostCarDao().getProvinceId()); // make
                mRegister.setText(carModel.getPostCarDao().getPlate());
                mTelephone.setText(carModel.getPostCarDao().getPhone());
                mName.setText(carModel.getPostCarDao().getName());
                mPrice.setText(carModel.getPostCarDao().getCarPrice());
                mTopic.setText(carModel.getPostCarDao().getCarTitle());
                mDescription.setText(AngelCarUtils.convertLineUp(carModel.getPostCarDao().getCarDetail()));
                mBtnPost.setText("SAVE");
        }
    }

    /*************
    *Listener Zone
    **************/
    Callback<Results> postCallback = new Callback<Results>() {
        @Override
        public void onResponse(Call<Results> call, Response<Results> response) {
            if (response.isSuccessful()) {
                RxUploadFile.with(getContext())
                        .postCar()
                        .setId(response.body().getSuccess())
                        .setGallery(mCarModel.getGallery())
                        .subscriber();

                Toast.makeText(Contextor.getInstance().getContext(),
                        "Completed"+response.body().getSuccess(), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Toast.makeText(Contextor.getInstance().getContext(),
                            response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: "+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<Results> call, Throwable t) {
            Toast.makeText(Contextor.getInstance().getContext(),
                    t.toString(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onFailure: ",t);
        }
    };

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onClickPost();
                return true;
            }
            return false;
        }
    };

    /*TextWatcher AutoCommaListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if (s != null) {
                try {
                    mPrice.removeTextChangedListener(this);
                    String givenString = s.toString();
                    if (givenString.contains(",")) {
                        givenString = givenString.replaceAll(",", "");
                    }
                    double doubleValue = Double.parseDouble(givenString);
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(doubleValue);
                    mPrice.setText(formattedString);

                } catch (NumberFormatException e) {
                }
                    mPrice.addTextChangedListener(this);
            }
        }
    };*/

}
