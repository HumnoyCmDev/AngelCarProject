package com.dollarandtrump.angelcar.rx_picker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/********************************************
 * Created by HumNoy Developer on 16/6/2559.
 * AngelCarProject
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 ********************************************/
public class HiddenActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener{
    public static String RX_PICKER_SOURCE = "rx_picker_source";

    private static String TAG = "RxImagePicker";

    private static final int SELECT_PHOTO = 100;
    private static final int TAKE_PHOTO = 101;
    private static final int LOCATION = 102;

    private Uri cameraPictureUrl;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleIntent(getIntent());
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHOTO:
                    RxImagePicker.with(this).onImagePicked(data.getData());
                    break;
                case TAKE_PHOTO:
                    RxImagePicker.with(this).onImagePicked(cameraPictureUrl);
                    break;
                case LOCATION:
                    RxLocationPicker.with(this).onLocationPicked(PlacePicker.getPlace(this,data));
                    break;
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxImagePicker.with(this).onDestroy();
        RxLocationPicker.with(this).onDestroy();
    }

    private void handleIntent(Intent intent) {
        Sources sourceType = Sources.values()[intent.getIntExtra(RX_PICKER_SOURCE, 0)];
        int chooseCode = 0;
        Intent pickerIntent = null;
        switch (sourceType) {
            case CAMERA:
                cameraPictureUrl = Uri.fromFile(createImageFile());
                pickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl);
                chooseCode = TAKE_PHOTO;
                break;
            case GALLERY:
                if (!checkPermission()) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    pickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    pickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    pickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    pickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                pickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickerIntent.setType("image/*");
                chooseCode = SELECT_PHOTO;
                break;
            case LOCATION:
                    try {
                        pickerIntent = new PlacePicker.IntentBuilder().build(this);
                        chooseCode = LOCATION;
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        Log.e(TAG, "handleIntent: ", e);
                    }
                break;
        }

        startActivityForResult(pickerIntent, chooseCode);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(HiddenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HiddenActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            return false;
        } else {
            return true;
        }
    }

    private File createImageFile() {
        File imageTempFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(null);
        try {
            imageTempFile = File.createTempFile(
                    timeStamp,
                    ".jpg",
                    storageDir
            );
        } catch (IOException ex) {
            Log.e(TAG, ex.toString());
        }
        return imageTempFile;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: "+connectionResult.getErrorMessage());
    }
}
