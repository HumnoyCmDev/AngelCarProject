package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.fragment.ShopFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShopActivity extends BaseAppCompat {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        initToolbar();
        Bundle args = getIntent().getExtras();
        if (args != null && savedInstanceState == null){
            String user = args.getString("user");
            String shop = args.getString("shop");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, ShopFragment.newInstance(user,shop)).commit();
        }


    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Snackbar onCreateSnackBar() {
        return Snackbar.make(mToolbar, R.string.status_network, Snackbar.LENGTH_INDEFINITE);
    }
}
