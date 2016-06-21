package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.fragment.ListImageFragment;

/********************************************
 * Created by HumNoy Developer on 13/6/2559.
 * AngelCarProject
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 ********************************************/
public class ListCarImageActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_center_view);
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content, ListImageFragment.newInstance())
//                    .addToBackStack(null)
//                    .commit();
        }
    }
}
