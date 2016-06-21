package com.dollarandtrump.angelcar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dollarandtrump.angelcar.Adapter.CarSubDetailAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.PostActivity;
import com.dollarandtrump.angelcar.dao.CarSubCollectionDao;
import com.dollarandtrump.angelcar.interfaces.OnSelectData;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;


public class CarSubDetailFragment extends Fragment {

//    private ProgressDialog mProgressDialog;

     @Bind(R.id.listCarDetail) ListView mlListView;

    private CarSubCollectionDao dao;
    private InfoCarModel carModel;
    private CarSubDetailAdapter mAdapter;
    private static final String TAG = "CarSubDetailFragment";

    public static CarSubDetailFragment newInstance() {
        CarSubDetailFragment fragment = new CarSubDetailFragment();
        return fragment;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_detail, container, false);
        initInstances(rootView,savedInstanceState);
        return rootView;

    }

    private void init(Bundle savedInstanceState) {
        dao = new CarSubCollectionDao();
        carModel = new InfoCarModel();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        mAdapter = new CarSubDetailAdapter();
        mAdapter.setDao(dao);
        mlListView.setAdapter(mAdapter);
        mlListView.setOnItemClickListener(onItemClickListener);

        if (savedInstanceState == null)
            loadCarSubDetail();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SAVE_DAO",Parcels.wrap(dao));
        outState.putParcelable("SAVE_CAR_MODEL",Parcels.wrap(carModel));
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        dao = Parcels.unwrap(savedInstanceState.getParcelable("SAVE_DAO"));
        carModel = Parcels.unwrap(savedInstanceState.getParcelable("SAVE_CAR_MODEL"));
    }

    @Override
    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }


    @Subscribe
    public void getProduceData(InfoCarModel carModel) {
        this.carModel = carModel;
        loadCarSubDetail();
    }

//    @Produce
//    public InfoCarModel onProduceInfo() {
//        return carModel;
//    }

    private void loadCarSubDetail() {
        if (carModel.getSubDao() == null) return;
        Call<CarSubCollectionDao> call =
                HttpManager.getInstance().getService().loadDataBrandSubDetail(carModel.getSubDao().getSubId());
        call.enqueue(carSubCollectionDaoCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    /***************
    * Listener Zone
    * *************/
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            carModel.setSubDetailDao(dao.getCarSubDao().get(position));
            MainThreadBus.getInstance().post(carModel);
            OnSelectData onSelectData = (OnSelectData) getActivity();
            onSelectData.onSelectedCallback(PostActivity.CALL_CAR_TYPE_DETAIL,carModel);

        }
    };

    Callback<CarSubCollectionDao> carSubCollectionDaoCallback = new Callback<CarSubCollectionDao>() {
        @Override
        public void onResponse(Call<CarSubCollectionDao> call, retrofit2.Response<CarSubCollectionDao> response) {
            if (response.isSuccessful()) {
                dao = response.body();
                mAdapter.setDao(dao);
                mAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "onResponse: " + response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<CarSubCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    /***************
     * InnerClass Zone
     * *************/
}