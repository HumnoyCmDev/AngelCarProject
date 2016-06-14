package com.dollarandtrump.angelcar.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.manager.http.HttpUploadManager;
import com.dollarandtrump.angelcar.model.InformationCarModel;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.hndev.library.view.Transformtion.ScalingUtilities;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import rx.Subscriber;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 5/2/59. เวลา 10:41
 ***************************************/
public class PostFragment extends Fragment {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int REQUEST_CODE_LOAD_IMAGE = 191;
    @Bind({
            R.id.post_photo_1,R.id.post_photo_2,
            R.id.post_photo_3,R.id.post_photo_4,
            R.id.post_photo_5,R.id.post_photo_6,
            R.id.post_photo_7,R.id.post_photo_8,
    }) List<ImageView> photo;

    @Bind(R.id.fragment_all_post_etDescription) EditText editTextDescription;
    @Bind(R.id.fragment_all_post_tgGear) ToggleButton tgGear;
    @Bind(R.id.fragment_all_post_etName) EditText editTextName;
    @Bind(R.id.fragment_all_post_etPrice) EditText editTextPrice;
    @Bind(R.id.fragment_all_post_etRegister) EditText editTextRegister;
    @Bind(R.id.fragment_all_post_etTelephone) EditText editTextTelephone;
    @Bind(R.id.fragment_all_post_etTopic) EditText editTextTopic;

    @Bind(R.id.tvTopicCar) TextView tvTopicCar;
    @Bind(R.id.spinnerProvince) Spinner spinnerProvince;
    @Bind(R.id.fragment_all_post_ButtonPost) Button btnPost;
    @Bind({R.id.contentPhoto1,R.id.contentPhoto2}) List<LinearLayout> content;

    private static final String TAG = "PostFragment";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private int idPhoto = 0;
    private int id_province = 1;
    private HashMap<Integer, File> filesPhotoList;
    private InformationCarModel carModel;
//    private Subscription subscription;
    private PostCarDao dao;

    public PostFragment() {
        super();
    }

    public static PostFragment newInstance() {
        Bundle args = new Bundle();
        PostFragment fragment = new PostFragment();
        args.putBoolean("isPost",true);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostFragment newInstanceEdit(PostCarDao dao) {
        Bundle args = new Bundle();
        PostFragment fragment = new PostFragment();
        args.putBoolean("isPost",false);
        args.putParcelable("carDao",Parcels.wrap(dao));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if(!getArguments().getBoolean("isPost"))
            dao = Parcels.unwrap(getArguments().getParcelable("carDao"));

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
        filesPhotoList = new HashMap<>();
    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        // Text Format
        editTextTelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        editTextDescription.setOnEditorActionListener(onEditorActionListener);
        initDataProvince();

        //init data (Edit)
        if (dao != null){
            content.get(0).setVisibility(View.GONE);
            content.get(1).setVisibility(View.GONE);
            tvTopicCar.setText(dao.toTopicCar());
            tgGear.setChecked(dao.getGear() == 0);
            spinnerProvince.setSelection(dao.getProvinceId()); // make
            editTextRegister.setText(dao.getPlate());
            editTextTelephone.setText(dao.getPhone());
            editTextName.setText(dao.getName());
            editTextPrice.setText(dao.getCarPrice());
            editTextTopic.setText(dao.getCarTitle());
            editTextDescription.setText(AngelCarUtils.convertLineUp(dao.getCarDetail()));
            btnPost.setText("SAVE");
        }
        editTextPrice.addTextChangedListener(AutoCommaListener);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SAVE_CAR_MODEL",Parcels.wrap(carModel));

    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        carModel = Parcels.unwrap(savedInstanceState.getParcelable("SAVE_CAR_MODEL"));
    }



    private void initDataProvince() {
        List<String> list = new ArrayList<String>();
        getResources().getStringArray(R.array.province);
        Collections.addAll(list, getResources().getStringArray(R.array.province));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(dataAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(parent.getContext(),parent.getItemAtPosition(position).toString(),
//                        Toast.LENGTH_LONG).show();
                id_province = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.fragment_all_post_ButtonPost)
    public void onClickPost(){

       String shopPref = Registration.getInstance().getShopRef(); // 1
       String carName = carModel.getBrandDao().getBrandName().toUpperCase() ; // toyota
       String topic = editTextTopic.getText().toString();
       String detail = editTextDescription.getText().toString().trim();
//       String appendCarTopDetail = AngelCarUtils.append(topic,detail); // ชื่อสั้นๆ
//       int carYear = carModel.getYear(); // ปีรถ
       String carPrice = editTextPrice.getText().toString().trim();// ราคารถ
//       String carStatus = "wait";//wait,online,offline
       String province = String.valueOf(id_province).trim(); // 1 - 77
       String gear = tgGear.isChecked() ? "1":"2"; // 0 or 1
       String plate = editTextRegister.getText().toString().trim(); // text ทะเบียนน
       String name = editTextName.getText().toString().trim(); // ชื่อ นามสกุล
       String phone = editTextTelephone.getText().toString().trim();

        //isEmpty = true หากมีค่าว่าง

        if (filesPhotoList.size() < 3) {
            Toast.makeText(getContext(), "กรุณาใส่รูปมากกว่า 3", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEmpty(shopPref)   ||  isEmpty(carName)    ||
            isEmpty(topic)      ||  isEmpty(carPrice)   ||
            isEmpty(detail)     ||  isEmpty(phone)      ||
            isEmpty(province)   ||  isEmpty(gear)       ||
            isEmpty(plate)      ||  isEmpty(name)){
            Toast.makeText(getContext(),"กรุณากรอกข้อมูลให้ครบ!",Toast.LENGTH_SHORT).show();
            return;
        }

            Call<Results> call = HttpManager.getInstance().getService().postCar(shopPref,
                    carModel.getBrandDao().getBrandId(),
                    carModel.getSubDao().getSubId(),
                    carModel.getSubDetailDao().getSubId(),
                    topic, detail, carModel.getYear(),
                    carPrice, "online", province, gear, plate, name, phone);
            call.enqueue(postCallback);

//            OnSelectData onSelectData = (OnSelectData) getActivity();
//            onSelectData.onSelectedCallback(PostActivity.CALLBACK_ALL_POST);

    }

    private boolean isEmpty(String str){
        if (str.isEmpty()) return true;
        if (str.equals("")) return true;
        return false;
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
                        RequestBody.create(MEDIA_TYPE_PNG, filesPhotoList.get(i))).build();

                Request request = new Request.Builder()
                        .url("http://www.angelcar.com/ios/data/gadata/imgupload.php")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(responseCallbackUpFile);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Uri selectedImage = data.getData();
                Cursor cursor = getActivity().getContentResolver()
                        .query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                // add path
                addFilesPhotoList(idPhoto,picturePath);
        }

    }

    private void addFilesPhotoList(int idPhoto,String picturePath){
        // list photo
        filesPhotoList.put(idPhoto,new File(picturePath));
        Bitmap scaledBitmap = ScalingUtilities
                .createScaledBitmap(decodeFile(filesPhotoList.get(idPhoto)),
                        90, 90, ScalingUtilities.ScalingLogic.CROP);
        photo.get(idPhoto).setImageBitmap(scaledBitmap);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (subscription != null)
//            subscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({
            R.id.post_photo_1,R.id.post_photo_2,
            R.id.post_photo_3,R.id.post_photo_4,
            R.id.post_photo_5,R.id.post_photo_6,
            R.id.post_photo_7,R.id.post_photo_8,
    })
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post_photo_1 : idPhoto = 0 ; break ;
            case R.id.post_photo_2 : idPhoto = 1 ; break ;
            case R.id.post_photo_3 : idPhoto = 2 ; break ;
            case R.id.post_photo_4 : idPhoto = 3 ; break ;
            case R.id.post_photo_5 : idPhoto = 4 ; break ;
            case R.id.post_photo_6 : idPhoto = 5 ; break ;
            case R.id.post_photo_7 : idPhoto = 6 ; break ;
            case R.id.post_photo_8 : idPhoto = 7 ; break ;
        }
        addOrRemovePhotoList(idPhoto);
    }

    private void addOrRemovePhotoList(int id_photo){
        // เช็คกรณี หากมีรูปอยู่แล้ว กดอีกครั้งให้ลบออก
        if (filesPhotoList.containsKey(id_photo)){
            filesPhotoList.remove(id_photo);
            photo.get(id_photo).setImageResource(R.drawable.ic_photo);
        }else{
            if (!checkPermissionApi23()){ //ต่ำกว่า Android 23
                intentLoadPictureExternalStore();
            }
        }
    }

    /*Permission*/
    private boolean checkPermissionApi23(){
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //กรณีไม่ให้สิทธิ์// แสดงรายการคำขอ ผลหากไม่ให้สิท ขอ Permission ผ่าน Dialog
                    showMessageOKCancel("AngelCar ต้องการขอสิทธิ์ในการเข้าถึงรูปภาพ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    });

                } else {
                    // ขอสิทธิ์เข้าถึง
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);

                }
            }else {
                intentLoadPictureExternalStore();
            }
            return true;
        }
            return false;
    }

    private void intentLoadPictureExternalStore(){
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        if (Build.VERSION.SDK_INT >= 18)
//            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(i, REQUEST_CODE_LOAD_IMAGE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

   /* //TODO Method ไม่ทำงาน
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: "+requestCode);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentLoadPictureExternalStore();
                    Log.i(TAG, "onRequestPermissionsResult: true");

                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

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
    public void eventBusProduceData(InformationCarModel carModel){
        this.carModel = carModel;
//init data
        String topic = carModel.getBrandDao().getBrandName().toUpperCase()+" "+
                carModel.getSubDao().getSubName()+" "+
                carModel.getSubDetailDao().getSubName()+" ปี"+
                carModel.getYear();
        tvTopicCar.setText(topic);
    }

    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    /*************
    *Listener Zone
    **************/
    Callback<Results> postCallback = new Callback<Results>() {
        @Override
        public void onResponse(Call<Results> call, Response<Results> response) {
            if (response.isSuccessful()) {
                // upload picture
//                uploadPicture(response.body().getSuccess(),filesPhotoList, responseCallbackUpFile);

                List<File> fileList = new ArrayList<>();
                for (int i = 0; i < filesPhotoList.size(); i++) {
                    fileList.add(filesPhotoList.get(i));
                }
                
                HttpUploadManager.uploadFilePostCar(fileList, response.body().getSuccess(),
                        new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ",e );
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: "+s);
                    }
                });

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

    okhttp3.Callback responseCallbackUpFile = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.e(TAG, "onFailure: ", e);
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) {
            if (response.isSuccessful()) {
                try {
                    Log.i(TAG, "UpFile Completed: " + response.body().string());
                } catch (IOException e) {
//                    e.printStackTrace();
                    Log.e(TAG, "UpFile Error: ", e);
                }
            } else {
                Log.i(TAG, "onResponse: ");
            }
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

    TextWatcher AutoCommaListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if (s != null) {
                try {
                    editTextPrice.removeTextChangedListener(this);
                    String givenString = s.toString();
                    if (givenString.contains(",")) {
                        givenString = givenString.replaceAll(",", "");
                    }
                    double doubleValue = Double.parseDouble(givenString);
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String formattedString = formatter.format(doubleValue);
                    editTextPrice.setText(formattedString);

                } catch (NumberFormatException e) {
                }
                    editTextPrice.addTextChangedListener(this);
            }
        }
    };

}
