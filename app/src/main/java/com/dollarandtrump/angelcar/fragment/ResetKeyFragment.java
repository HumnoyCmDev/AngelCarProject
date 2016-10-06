package com.dollarandtrump.angelcar.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


@SuppressWarnings("unused")
public class ResetKeyFragment extends Fragment {
    private static final String TAG = ResetKeyFragment.class.getSimpleName();

    @Bind(R.id.edit_text_old_password) EditText mOldPassword;
    @Bind(R.id.edit_text_password) EditText mPassword;
    @Bind(R.id.edit_text_confirm_password) EditText mConfirmPassword;
    @Bind(R.id.button_reset_password) Button mButtonResetPass;
    @Bind(R.id.text_status) TextView mStatus;

    private String shopId;
    private String shopNumber;

    public ResetKeyFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ResetKeyFragment newInstance(String shopId,String shopNumber) {
        ResetKeyFragment fragment = new ResetKeyFragment();
        Bundle args = new Bundle();
        args.putString("shopid",shopId);
        args.putString("shopnumber",shopNumber);
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
        View rootView = inflater.inflate(R.layout.fragment_reset_key, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        shopId = getArguments().getString("shopid");
        shopNumber = getArguments().getString("shopnumber");
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        Observable<Boolean> observableChangePass = RxTextView.textChangeEvents(mPassword).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        });
        Observable<Boolean> observableChangeConfirmPass = RxTextView.textChangeEvents(mConfirmPassword).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        });

        Observable.combineLatest(observableChangePass, observableChangeConfirmPass, new Func2<Boolean, Boolean, Boolean>() {

            @Override
            public Boolean call(Boolean aBoolean, Boolean aBoolean2) {
                return aBoolean && aBoolean2;
            }
        }).doOnNext(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
                    mButtonResetPass.setEnabled(true);
                    mStatus.setVisibility(View.GONE);
                } else {
                    if (aBoolean) {
                        mStatus.setVisibility(View.VISIBLE);
                        mStatus.setText("รหัสผ่านไม่ตรงกันค่ะ");
                        mButtonResetPass.setEnabled(false);
                    }else {
                        mStatus.setVisibility(View.GONE);
                        mButtonResetPass.setEnabled(true);
                    }
                }
            }
        }).subscribe();

    }

    @OnClick(R.id.button_reset_password)
    public void onButtonResetPass(){
        String message = shopId+"||"+shopNumber+"||"+mPassword.getText().toString()+"||"+mOldPassword.getText().toString();
        HttpManager.getInstance().getService().updateKey(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Error", throwable);
                    }
                })
                .doOnNext(new Action1<ResponseDao>() {
                    @Override
                    public void call(ResponseDao responseDao) {
                        Log.d(TAG, "call: "+responseDao.getResult());
                        if (responseDao.getResult().equals("success")) {
                            alertDialog("เปลี่ยนรหัสผ่านเรียบร้อยแล้วค่ะ",true);
                        }else {
                            alertDialog("รหัสผ่านเก่าไม่ถูกต้อง",false);
                        }
                    }
                }).subscribe();
    }

    private void alertDialog(String title , final boolean isBackPressed) {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.alert)
                .setMessage(title)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isBackPressed)
                            getActivity().onBackPressed();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
