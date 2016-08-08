package com.dollarandtrump.angelcar.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dollarandtrump.angelcar.Adapter.FollowAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.SuccessDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.Log;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by humnoyDeveloper on 26/3/59. 09:06
 */
public class FollowActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.listView) ListView mListView;

    private PostCarCollectionDao dao;
    private FollowAdapter adapter;

    private static final String TAG = "FollowActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ButterKnife.bind(this);

        initInstance();
        loadFollowCarModel();
    }

    private void loadFollowCarModel() {
        if (Log.isLoggable(Log.DEBUG)) Log.d(Registration.getInstance().getShopRef()+"");
        Call<PostCarCollectionDao> call = HttpManager.getInstance().getService()
                .loadFollowCarModel(Registration.getInstance().getShopRef());
        call.enqueue(callbackPostCarModel);
    }

    private void initInstance() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        adapter = new FollowAdapter(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setOnItemLongClickListener(onItemLongClickListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**************
    *Listener Zone*
    ***************/
    Callback<PostCarCollectionDao> callbackPostCarModel = new Callback<PostCarCollectionDao>() {
        @Override
        public void onResponse(Call<PostCarCollectionDao> call, Response<PostCarCollectionDao> response) {
            if (response.isSuccessful()) {
                dao = response.body();
                adapter.setDao(dao);
                adapter.notifyDataSetChanged();

            } else {
                try {
                    if (Log.isLoggable(Log.DEBUG)) Log.d(""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PostCarCollectionDao> call, Throwable t) {
           if (Log.isLoggable(Log.ERROR)) Log.e("Error",t);
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PostCarDao item = dao.getListCar().get(position);
            Intent intent = new Intent(FollowActivity.this, ChatCarActivity.class);
            intent.putExtra("PostCarDao", Parcels.wrap(item));
            intent.putExtra("intentForm", 0);
            intent.putExtra("messageFromUser",Registration.getInstance().getUserId());
            startActivity(intent);
        }
    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            Dialog dialog = new AlertDialog.Builder(FollowActivity.this)
                    .setMessage("Delete Follow?")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Delete Follow -server
                            Call<SuccessDao> callDelete = HttpManager.getInstance().getService()
                                    .follow("delete",String.valueOf(dao.getListCar().get(position).getCarId()),
                                            Registration.getInstance().getShopRef());
                            callDelete.enqueue(callbackAddOrRemoveFollow);

                            //Delete Follow -Model
                            dao.getListCar().remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .create();
            dialog.show();

            return true;
        }
    };

    Callback<SuccessDao> callbackAddOrRemoveFollow = new Callback<SuccessDao>() {
        @Override
        public void onResponse(Call<SuccessDao> call, Response<SuccessDao> response) {
            if (response.isSuccessful()) {
                if (Log.isLoggable(Log.DEBUG)) Log.d(""+response.body().success);
            } else {
                try {
                    if (Log.isLoggable(Log.DEBUG)) Log.d(""+response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<SuccessDao> call, Throwable t) {
            if (Log.isLoggable(Log.ERROR)) Log.e("Error",t);
        }
    };
}
