package com.dollarandtrump.angelcar.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dollarandtrump.angelcar.Adapter.CarSubGridAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.PostActivity;
import com.dollarandtrump.angelcar.dao.CarSubCollectionDao;
import com.dollarandtrump.angelcar.dialog.YearDialog;
import com.dollarandtrump.angelcar.interfaces.OnSelectData;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.InformationCarModel;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CarSubFragment extends Fragment {
    private static String SAVE_INSTANCE_USER_INFO = "SAVE_INSTANCE_USER_INFO";
    private static String SAVE_INSTANCE_CAT_TYPE_SUB = "SAVE_INSTANCE_CAT_TYPE_SUB";
    private static String TAG = "CarSubFragment";
    private static int DIALOG_YEAR = 99;
    
    
    @Bind(R.id.grid_sub_model) GridView mGridView;

    private CarSubGridAdapter mAdapter;
    private InformationCarModel carModel;
    private CarSubCollectionDao dao;

    public static CarSubFragment newInstance() {
        CarSubFragment fragment = new CarSubFragment();
//        Bundle args = new Bundle();
//        args.putInt("position",position);
//        fragment.setArguments(args);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_type, container, false);
        initInstances(rootView,savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        carModel = new InformationCarModel();
        dao = new CarSubCollectionDao();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        mAdapter = new CarSubGridAdapter();
        mAdapter.setDao(dao);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(selectTypeListener);
        if (savedInstanceState == null)
            loadCarSubDetail();
    }

    private void loadCarSubDetail() {
        if (carModel.getBrandDao() == null) return;
            Call<CarSubCollectionDao> call = HttpManager.getInstance()
                    .getService().loadDataBrandSub(carModel.getBrandDao().getBrandId());
            call.enqueue(carSubCollectionDaoCallback);

    }

    private void dialogSelectYear() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("YearDialog");
        if (fragment != null){
            ft.remove(fragment);
        }
        ft.addToBackStack(null);
        YearDialog dialog = new YearDialog();
        dialog.setTargetFragment(this,DIALOG_YEAR);
        dialog.show(getFragmentManager(),"YearDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DIALOG_YEAR && resultCode == Activity.RESULT_OK && data != null){
            carModel.setYear(data.getIntExtra(YearDialog.ARG_YEAR,2016));
            MainThreadBus.getInstance().post(carModel);
            OnSelectData onSelectData = (OnSelectData) getActivity();
            onSelectData.onSelectedCallback(PostActivity.CALL_CAR_TYPE);
        }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void eventBusProduceData(InformationCarModel carModel){
        this.carModel = carModel;
        loadCarSubDetail();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_INSTANCE_USER_INFO,Parcels.wrap(carModel));
        outState.putParcelable(SAVE_INSTANCE_CAT_TYPE_SUB,Parcels.wrap(dao));
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        carModel = Parcels.unwrap(savedInstanceState.getParcelable(SAVE_INSTANCE_USER_INFO));
        dao = Parcels.unwrap(savedInstanceState.getParcelable(SAVE_INSTANCE_CAT_TYPE_SUB));
    }

    /***************
     * Listener Zone
     * *************/
    AdapterView.OnItemClickListener selectTypeListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            carModel.setSubDao(dao.getCarSubDao().get(position));
            dialogSelectYear();
        }
    };

    Callback<CarSubCollectionDao> carSubCollectionDaoCallback = new Callback<CarSubCollectionDao>() {
        @Override
        public void onResponse(Call<CarSubCollectionDao> call, Response<CarSubCollectionDao> response) {
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
}






