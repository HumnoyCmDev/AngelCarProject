package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.fragment.ResultSearchFragment;
import com.dollarandtrump.angelcar.fragment.SearchFragment;
import com.dollarandtrump.angelcar.model.InfoCarModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kotlin
 **/
public class SearchActivity extends BaseAppCompat {
    @Bind(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);


        //toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_content, SearchFragment.newInstance())
                    .commit();
        }

    }

    public void addFragmentResult(InfoCarModel infoCarModel){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_content, ResultSearchFragment.newInstance(infoCarModel))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Snackbar onCreateSnackBar() {
        return Snackbar.make(toolbar, R.string.status_network, Snackbar.LENGTH_INDEFINITE);
    }
}
