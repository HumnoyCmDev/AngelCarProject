package com.dollarandtrump.angelcar.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;

import com.dollarandtrump.angelcar.manager.bus.BusProvider;
import com.dollarandtrump.angelcar.utils.RegistrationResult;

import java.util.regex.Pattern;

/**
 * Created by humnoyDeveloper on 3/18/2016 AD. 11:50
 */
public class RegistrationAlertFragment extends DialogFragment {
    public static final int REGISTRATION_OK = 0;
    public static final int REGISTRATION_CANCEL = 1;
    private int isResult = 1;
    private String EMAIL_ADDRESS = "ไม่พบ Email";


    public static RegistrationAlertFragment newInstance() {
//        Bundle args = new Bundle();
        RegistrationAlertFragment fragment = new RegistrationAlertFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // GET Email // registration
        //TODO Nexus5 Not Email!
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                if (account.name.contains("@gmail.com")) {
                    EMAIL_ADDRESS = account.name;
                    isResult = REGISTRATION_OK;
                    return;
                }else {
                    EMAIL_ADDRESS = "ไม่พบบัญชีของ google";
                    isResult = REGISTRATION_CANCEL;
                }
            }else {
                isResult = REGISTRATION_CANCEL;
            }
        }


//        try {
//            Account[] as = AccountManager.get(getContext()).getAccountsByType("com.google");
//            for (Account account : as) {
//                Log.i("GET Email", account.type+" : "+account.name);
//            }
//        } catch (Exception e) {
//            Log.i("Exception", "Exception:" + e);
//        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("ระบบลงทะเบียนด้วย Email")
                .setMessage("ลงทะเบียนด้วยบัญชี Email: "+EMAIL_ADDRESS)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BusProvider.getInstance().post(new RegistrationResult(isResult,EMAIL_ADDRESS));
                    }
                })

                .create();
    }


}
