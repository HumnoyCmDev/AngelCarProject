package com.dollarandtrump.angelcar.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarBrandCollectionDao;
import com.dollarandtrump.angelcar.dao.CarBrandDao;
import com.dollarandtrump.angelcar.fragment.FeedPostFragment;
import com.dollarandtrump.angelcar.manager.http.HttpManager;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by humnoy on 24/2/59.
 */
public class FilterBrandDialog extends DialogFragment {
    private static final String TAG = "FilterBrandDialog";

    @Bind(R.id.list_view) ListView listView;
    ListViewAdapter adapter;
    private CarBrandCollectionDao dao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        dao = new CarBrandCollectionDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_layout,container,false);
        initInstance(view,savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    private void initInstance(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this,view);
        adapter = new ListViewAdapter();
        adapter.setData(dao);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getActivity().getIntent();
                CarBrandDao model = dao.getBrandDao().get(position);
                intent.putExtra(FeedPostFragment.ARG_BRAND, Parcels.wrap(model));
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
                dismiss();
            }
        });

        if(savedInstanceState == null)
            loadBrand();

    }

    private void loadBrand() {
        Call<CarBrandCollectionDao> call = HttpManager.getInstance().getService().loadDataBrand();
        call.enqueue(carBrandCollectionDaoCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /******************
     *Listener zone*
     ******************/
    Callback<CarBrandCollectionDao> carBrandCollectionDaoCallback = new Callback<CarBrandCollectionDao>() {
        @Override
        public void onResponse(Call<CarBrandCollectionDao> call, Response<CarBrandCollectionDao> response) {
            if (response.isSuccessful()) {
                dao = response.body();
                adapter.setData(dao);
                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "onResponse: " + response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<CarBrandCollectionDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
        }
    };

    /******************
     *Inner class zone*
     ******************/
    public class ListViewAdapter extends BaseAdapter{

        private CarBrandCollectionDao dao;

        public void setData(CarBrandCollectionDao dao) {
            this.dao = dao;
        }

        @Override
        public int getCount() {
            if (dao == null) return 0;
            if (dao.getBrandDao() == null) return 0;
            return dao.getBrandDao().size();
        }

        @Override
        public CarBrandDao getItem(int position) {
            return dao.getBrandDao().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null){
                holder = (ViewHolder) convertView.getTag();
            }else {
               convertView = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.adapter_item_filter,parent,false);
               holder = new ViewHolder(convertView);
               convertView.setTag(holder);
            }
            holder.iconFilter.setImageResource(R.drawable.toyota);//TODO Show ALL LOGO
            holder.tvFilter.setText(getItem(position).getBrandName());

            return convertView;
        }

        public class ViewHolder {
            @Bind(R.id.filter_icon) ImageView iconFilter;
            @Bind(R.id.filter_name) TextView tvFilter;
            public ViewHolder(View view) {
                ButterKnife.bind(this,view);
            }
        }



    }

}
