package com.dollarandtrump.angelcar.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.fragment.DealerManagerFragment;
import com.dollarandtrump.angelcar.fragment.RegisterKeyFragment;
import com.dollarandtrump.angelcar.fragment.ResetKeyFragment;
import com.dollarandtrump.angelcar.fragment.SingInFragment;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.module.Register;
import com.dollarandtrump.angelcar.utils.Log;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class FormLogInActivity extends AppCompatActivity {

    private String shopRank;
    private String shopNumber;
    private boolean isCheckKeyShop;
    private String shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        shopRank = getIntent().getExtras().getString("shoprank", "normal");
        shopNumber = getIntent().getExtras().getString("shopnumber", "NULL");
        isCheckKeyShop = getIntent().getExtras().getBoolean("checkkeyshop", false);
        shopId = Registration.getInstance().getShopRef();

        initFragment();
    }

    private void initFragment() {
        Fragment fragment;
        if (shopRank.equals("dealer")) {
            //check isSign in คือการใช้ key login
            boolean isSignin = Registration.getInstance().isSignIn();
            if (isCheckKeyShop && isSignin) { // login แล้ว
                 fragment = DealerManagerFragment.newInstance(shopId, shopNumber);
            } else {
                fragment = RegisterKeyFragment.newInstance(shopNumber);
            }
        } else {
            fragment = SingInFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }


    public void addFragmentResetKey() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, ResetKeyFragment.newInstance(shopId, shopNumber))
                .addToBackStack(null).commit();
    }
}
