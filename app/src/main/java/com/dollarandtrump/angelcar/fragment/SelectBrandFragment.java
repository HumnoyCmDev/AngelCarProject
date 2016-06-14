package com.dollarandtrump.angelcar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dollarandtrump.angelcar.Adapter.BrandGridAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.PostActivity;
import com.dollarandtrump.angelcar.dao.CarBrandCollectionDao;
import com.dollarandtrump.angelcar.interfaces.OnSelectData;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.model.InformationCarModel;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class SelectBrandFragment extends Fragment {


    @Bind(R.id.gridview) GridView gridview;

    InformationCarModel carModel;
    CarBrandCollectionDao dao;
    BrandGridAdapter adapter;
    Subscription subscription;

    private static final String TAG = "SelectBrandFragment";

    public static SelectBrandFragment newInstance(){
        SelectBrandFragment fragment = new SelectBrandFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_brand, container, false);
        initInstances(rootView,savedInstanceState);
        return rootView;

    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        carModel = new InformationCarModel();
        dao = new CarBrandCollectionDao();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        adapter = new BrandGridAdapter();
        adapter.setDao(dao);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(selectBrandListener);
        if (savedInstanceState == null)
            loadBrand();

    }

    private void loadBrand() {
        //Rx Android + Retrofit
        subscription = HttpManager.getInstance().getService().observableLoadDataBrand()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(carBrandCollectionDaoAction);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SAVE_CAR_MODEL",Parcels.wrap(carModel));
        outState.putParcelable("SAVE_DAO",Parcels.wrap(dao));
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        carModel = Parcels.unwrap(savedInstanceState.getParcelable("SAVE_CAR_MODEL"));
        dao = Parcels.unwrap(savedInstanceState.getParcelable("SAVE_DAO"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null)
        subscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /***************
     * Listener Zone
     * *************/
    AdapterView.OnItemClickListener selectBrandListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            carModel.setBrandDao(dao.getBrandDao().get(position));
            MainThreadBus.getInstance().post(carModel);

            OnSelectData onSelectData = (OnSelectData) getActivity();
            onSelectData.onSelectedCallback(PostActivity.CALLBACK_BRAND);
        }
    };

    Action1<CarBrandCollectionDao> carBrandCollectionDaoAction = new Action1<CarBrandCollectionDao>() {
        @Override
        public void call(CarBrandCollectionDao carBrandCollectionDao) {
            dao = carBrandCollectionDao;
            adapter.setDao(carBrandCollectionDao);
            adapter.notifyDataSetChanged();
        }
    };

}