package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.fragment.ShopFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShopActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        Bundle args = getIntent().getExtras();
        if (args != null && savedInstanceState == null){
            String user = args.getString("user");
            String shop = args.getString("shop");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, ShopFragment.newInstance(user,shop)).commit();
        }


    }
}
