package com.dollarandtrump.angelcar.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.PostActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class NoticeFragment extends Fragment {

    private final int REQUEST_CODE = 991;

    public NoticeFragment() {
        super();
    }

    public static NoticeFragment newInstance() {
        NoticeFragment fragment = new NoticeFragment();
        return fragment;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notice, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState == null) {

        }

    }

    @OnClick(R.id.btnPost)
    public void intentPostActivity(){
        Intent i = new Intent(getActivity(), PostActivity.class);
        startActivityForResult(i,REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE
                && resultCode == Activity.RESULT_OK
                && data != null){


        }
    }
}