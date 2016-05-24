package com.dollarandtrump.angelcar.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.Selection;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Deprecated
public class SampleUploadFileFragment extends Fragment {

    private ImageView ivPhoto;
    String picturePath;

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public SampleUploadFileFragment() {

    }

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sample_upload_file, container, false);
        findViewRootView(v);
        return v;

    }

    private static final String TAG = "HelpFragment1";

    public int getCurrentCursorLine(EditText editText){
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();
        if (!(selectionStart == -1)) {
            return layout.getLineForOffset(selectionStart);
        }
        return -1;
    }

    private void findViewRootView(View v) {
        ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);
        final FrameLayout post = (FrameLayout) v.findViewById(R.id.testAnim);
        final Button button = (Button) v.findViewById(R.id.button);

        final EditText editText = (EditText) v.findViewById(R.id.editText);
        final TextView editText2 = (TextView) v.findViewById(R.id.editText2);

        Button btnCheckEd = (Button) v.findViewById(R.id.btnCheckEdit);
        btnCheckEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int  i = getCurrentCursorLine(editText);
                String allString = editText.getText().toString();
//                String Subline = allString.replaceAll("\n","<n>");
                String SublineUp = AngelCarUtils.formatLineUp(allString);
                String lineUp = AngelCarUtils.convertLineUp(SublineUp);
                String topic = AngelCarUtils.subTopic("1rrrrr1``ssdfsdjkfh");

                String d = AngelCarUtils.getPostCollection("1rrrrr1``ssdfsdjkfh<n>kdfjgkdfg<n>").getTopic();
                String d2 = AngelCarUtils.getPostCollection("1rrrrr1``ssdfsdjkfh<n>kdfjgkdfg<n>").getDetail();

                Log.i(TAG, "onClick: "+ SublineUp);
                Log.i(TAG, "onClick: "+ lineUp);
                Log.i(TAG, "onClick: "+ topic);
                Log.i(TAG, "onClick: "+ d);
                Log.i(TAG, "onClick: "+ d2);

                String s = "tt``tt";
                Log.i(TAG, "onClick: "+s.contains("``"));

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(i, RESULT_LOAD_IMAGE);


            }
        });


        Button up = (Button) v.findViewById(R.id.button_upload);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(picturePath);
                Toast.makeText(getContext(),"Put :: "+file.getName(),Toast.LENGTH_SHORT).show();
                final OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userfile", file.getName(),
                                RequestBody.create(MEDIA_TYPE_PNG, file))
                        .build();

                final Request request = new Request.Builder()
//                        .url("http://ga.paiyannoi.me/gachatcarimageupload.php")
                        .url("http://angelcar.com/gachatcarimageupload.php")
                        .post(requestBody)
                        .build();

                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Response r = client.newCall(request).execute();
                            if (r.isSuccessful()){
                                Log.i(TAG, "doInBackground:api paiyannoi "+r.body().string());
                            }else {
                                Log.i(TAG, "doInBackground: api paiyannoi false");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();

            }
        });


        Button upOkhttp = (Button) v.findViewById(R.id.button_upload3);
        upOkhttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            File f = new File(picturePath);
                            Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

                            byte[] array = stream.toByteArray();
                            String encoded_string = Base64.encodeToString(array, Base64.DEFAULT);


                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("image_name","Okhttp1_"+f.getName())
                                    .addFormDataPart("encoded_string",encoded_string)
                                    .build();/*ทำงานได้*/

                            RequestBody formBody = new FormBody.Builder()
                                    .add("image_name", "Okhttp2_"+f.getName())
                                    .add("encoded_string", encoded_string)
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://www.usedcar.co.th/imgupload.php")
                                    .post(requestBody)
                                    .build();

                            OkHttpClient client = new OkHttpClient
                                    .Builder()
                                    .readTimeout(60*1000,TimeUnit.MILLISECONDS).build();

                            Response r = client.newCall(request).execute();
                            if (r.isSuccessful()){
                                Log.i(TAG, "doInBackground: "+r.body().string());
                            }else {
                                Log.i(TAG, "doInBackground: false");

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();

            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    int RESULT_LOAD_IMAGE = 99;
    int RESULT_OK = -1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


            File imgFile = new File(picturePath);
            Uri uri = Uri.fromFile(imgFile);

            Bitmap myBitmap = BitmapFactory.decodeFile(uri.getPath());
            ivPhoto.setImageBitmap(myBitmap);

            Log.i(TAG, "onActivityResult: "+imgFile.getName());


        }
    }



}