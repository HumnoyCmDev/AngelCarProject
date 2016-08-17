package com.dollarandtrump.angelcar.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.dollarandtrump.angelcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InfoActivity extends AppCompatActivity{

//    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.web_view_info) WebView mWebViewInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

//        setSupportActionBar(mToolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        mWebViewInfo.getSettings().setTextZoom(110);
        mWebViewInfo.loadUrl("file:///android_asset/about.html");

    }


}
