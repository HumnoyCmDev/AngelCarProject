package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.fragment.ViewPictureFragment;

/**
 * Created by humnoyDeveloper on 11/4/59. 14:36
 */
public class SingleViewImageActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_view_image);

        Bundle arg = getIntent().getExtras();
        String url = arg.getString("url");

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, ViewPictureFragment.newInstance(url)).commit();
        }

    }


}
