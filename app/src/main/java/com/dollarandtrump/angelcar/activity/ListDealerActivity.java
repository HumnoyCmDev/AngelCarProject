package com.dollarandtrump.angelcar.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dollarandtrump.angelcar.Adapter.ListDealerAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.DealerCollection;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ListDealerActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.list_all_dealer) RecyclerView list;

    private ListDealerAdapter adapter;
    private DealerCollection collection;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dealer);
        ButterKnife.bind(this);

        initToolbar();
        collection = new DealerCollection();
        adapter = new ListDealerAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter.setCollection(collection);
        list.setLayoutManager(manager);
        list.setAdapter(adapter);

        adapter.setListener(new ListDealerAdapter.OnClickDealerListener() {
            @Override
            public void onItemClick(String shop, String user) {
                Intent i = new Intent(ListDealerActivity.this, ShopActivity.class);
                i.putExtra("user",user);
                i.putExtra("shop",shop);
                startActivity(i);
            }
        });

        if (savedInstanceState == null)
            loadViewDealer();


    }

    private void loadViewDealer() {
        subscription = HttpManager.getInstance().getService().observableViewDealer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("ListDealer",throwable);
                    }
                })
                .doOnNext(new Action1<DealerCollection>() {
                    @Override
                    public void call(DealerCollection dealerCollection) {
                        adapter.setCollection(dealerCollection);
                        adapter.notifyDataSetChanged();
                    }
                })
                .subscribe();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
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
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
