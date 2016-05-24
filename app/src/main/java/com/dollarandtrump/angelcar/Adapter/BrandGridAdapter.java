package com.dollarandtrump.angelcar.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarBrandCollectionDao;
import com.dollarandtrump.angelcar.dao.CarBrandDao;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BrandGridAdapter extends BaseAdapter {
    CarBrandCollectionDao dao;

    public void setDao(CarBrandCollectionDao dao) {
        this.dao = dao;
    }

    public int getCount() {
        if (dao == null) return 0;
        if (dao.getBrandDao() == null) return 0;
        return dao.getBrandDao().size();
    }

    public CarBrandDao getItem(int position) {
        return dao.getBrandDao().get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_row,parent,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        holder.imgBrand.setBackgroundResource(R.drawable.toyota);
        holder.tvName.setText(getItem(position).getBrandName());

        return view;
    }

    public static class ViewHolder{
        @Bind(R.id.imageGrid) ImageView imgBrand;
        @Bind(R.id.name_cartype) TextView tvName;

        public ViewHolder(View v) {
            ButterKnife.bind(this,v);
        }
    }
}