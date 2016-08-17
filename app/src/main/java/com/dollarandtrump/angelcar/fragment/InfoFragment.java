package com.dollarandtrump.angelcar.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.dollarandtrump.angelcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InfoFragment extends Fragment{
    @Bind(R.id.web_view_info)
    WebView mWebViewInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_info,container,false);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebViewInfo.getSettings().setTextZoom(110);
        mWebViewInfo.loadUrl("file:///android_asset/about.html");

    }
}
