package com.dollarandtrump.angelcar.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarDataTypeDao;
import com.dollarandtrump.angelcar.dao.CarSubCollectionDao;
import com.dollarandtrump.angelcar.dao.CarSubDao;

import java.util.List;

public class CarSubGridAdapter extends BaseAdapter {

    private CarSubCollectionDao dao;

    public void setDao(CarSubCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if (dao == null) return 0;
        if (dao.getCarSubDao() == null) return 0;
        return dao.getCarSubDao().size();
    }

    @Override
    public CarSubDao getItem(int position) {
        return dao.getCarSubDao().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_row, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.cartype_sub = (TextView) convertView.findViewById(R.id.name_cartype);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.cartype_sub.setText(getItem(position).getSubName());

        return convertView;
    }

    private static class ViewHolder {
        TextView cartype_sub;

    }
}