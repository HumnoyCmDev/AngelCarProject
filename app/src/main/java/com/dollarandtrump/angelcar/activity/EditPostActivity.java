package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.fragment.PostFragment;

import org.parceler.Parcels;

/**
 * Created by humnoyDeveloper on 25/4/59. 15:43
 */
public class EditPostActivity  extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        PostCarDao dao = Parcels.unwrap(getIntent().getParcelableExtra("postCarDao"));
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, PostFragment.newInstanceEdit(dao))
                    .commit();
        }
    }


}
