package com.dollarandtrump.angelcar.dialog;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class InformationDialog extends DialogFragment{

    @Inject
    @Named("default")
    SharedPreferences preferencesDefault;

    @Bind(R.id.edit_text_name) EditText mInputName;
    @Bind(R.id.edit_text_telephone) EditText mInputTel;
    @Bind(R.id.button_save) Button mButtonSave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_information,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainApplication) getActivity().getApplication()).getApplicationComponent().inject(this);


        mInputName.setText(preferencesDefault.getString("pre_name",null));
        mInputTel.setText(preferencesDefault.getString("pre_phone",null));

        Observable<Boolean> isName = RxTextView.textChangeEvents(mInputName).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                boolean b = textViewTextChangeEvent.text().length() > 0;
                return b;
            }
        });
        Observable<Boolean> isTel = RxTextView.textChangeEvents(mInputTel).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                boolean b = textViewTextChangeEvent.text().length() > 0;
                return b;
            }
        });

        Observable.combineLatest(isName, isTel, new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean, Boolean aBoolean2) {
                return aBoolean && aBoolean2;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mButtonSave.setEnabled(aBoolean);
//                mButtonSave.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });

    }

    @OnClick(R.id.button_save)
    public void onSaveInformation(){
        preferencesDefault.edit().putString("pre_name",mInputName.getText().toString().trim()).apply();
        preferencesDefault.edit().putString("pre_phone",mInputTel.getText().toString().trim()).apply();
        dismiss();
    }
}
