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
import android.widget.EditText;

import com.dollarandtrump.angelcar.MainApplication;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.FormLogInActivity;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.manager.http.HttpManager;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


@SuppressWarnings("unused")
public class DealerManagerFragment extends Fragment {

    private String shopId;
    private String shopNumber;

    public DealerManagerFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static DealerManagerFragment newInstance(String shopId,String shopNumber) {
        DealerManagerFragment fragment = new DealerManagerFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_manager_dealer, container, false);
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


    }

    @OnClick({R.id.button_reset_password,R.id.button_delete_key})
    public void onClickButton(View view){
        int id = view.getId();
        if (id == R.id.button_reset_password){
            ((FormLogInActivity) getActivity()).addFragmentResetKey();
        }else {
            alertDialogDeleteKey();
        }
    }

    private void alertDialogDeleteKey() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View editPassView = inflater.inflate(R.layout.dialog_edit_text_password,null);
        final EditText pass = (EditText) editPassView.findViewById(R.id.edit_text_password);
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.alert)
                .setMessage("ใส่รหัสยืนยันการยกเลิกใช้งานกุญแจ")
                .setView(editPassView)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                httpManagerDeleteKey(pass.getText().toString());
                            }
                        }
                )
                .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
    }

    private void httpManagerDeleteKey(String pass){
        String message = shopId+"||"+shopNumber+"||"+pass;
        HttpManager.getInstance().getService().deleteKey(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("DealerManager", "Error", throwable);
                    }
                })
                .doOnNext(new Action1<ResponseDao>() {
                    @Override
                    public void call(ResponseDao responseDao) {
                        if (responseDao.getResult().equals("success")){
                            alertDialog("ลบกุญแจเรียบร้อยแล้วค่ะ",true);
                        }else {
                            alertDialog("รหัสผ่านไม่ถูกต้อง กรุณาลองใหม่อีกครั้งค่ะ",false);
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
                            getActivity().finish();
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
