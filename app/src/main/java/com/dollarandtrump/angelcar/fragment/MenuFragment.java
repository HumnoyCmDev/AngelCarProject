package com.dollarandtrump.angelcar.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.ListCarImageActivity;
import com.dollarandtrump.angelcar.rx_image_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_image_picker.Sources;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;
import rx.functions.Func1;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public class MenuFragment extends Fragment {


    @Bind(R.id.imageTest)
    ImageView img;

    @Bind(R.id.edit_test)
    EditText editText;

    public MenuFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        // Init 'View' instance(s) with rootView.findViewById here

        final Pattern emailPattern = Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        RxTextView.textChangeEvents(editText).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return emailPattern.matcher(textViewTextChangeEvent.text()).matches();
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                editText.setTextColor(aBoolean ? Color.BLACK : Color.RED);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
