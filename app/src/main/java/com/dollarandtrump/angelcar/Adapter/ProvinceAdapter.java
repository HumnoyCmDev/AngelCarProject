package com.dollarandtrump.angelcar.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dollarandtrump.angelcar.dao.ProvinceDao;

import java.util.List;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 9/8/59.14:09น.
 *
 * @AngelCarProject
 */
public class ProvinceAdapter extends BaseAdapter{
    private List<ProvinceDao> mProvince;

    public void setProvince(List<ProvinceDao> province) {
        this.mProvince = province;
    }

    @Override
    public int getCount() {
        if (mProvince == null) return 0;
        return mProvince.size();
    }

    @Override
    public ProvinceDao getItem(int position) {
        return mProvince.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = new TextView(parent.getContext());
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(getItem(position).getProvince());
        holder.textView.setPadding(8,8,8,8);
        return convertView;
    }

    private class ViewHolder {
        TextView textView;

        public ViewHolder(View v) {
            textView = (TextView) v;
        }
    }
}
