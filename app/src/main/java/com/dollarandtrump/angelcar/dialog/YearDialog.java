package com.dollarandtrump.angelcar.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoy on 24/2/59.
 */
public class YearDialog extends DialogFragment {
    public static final String ARG_YEAR = "TAG_YEAR";
    @Bind(R.id.grid) GridView gridLayout;

    List<String> dataYear;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_year_grid_layout,container,false);
        ButterKnife.bind(this,view);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setTitle("เลือกปี");
        dataYear = new ArrayList<String>();
        for (int i = 2016; i >= 1950; i--) {
            dataYear.add(String.valueOf(i));
        }

        YearAdapter dataAdapter = new YearAdapter();
        dataAdapter.setData(dataYear);
        gridLayout.setAdapter(dataAdapter);
        gridLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getActivity().getIntent();
                intent.putExtra(ARG_YEAR,Integer.valueOf(dataYear.get(position)));
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
                dismiss();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class YearAdapter extends BaseAdapter{

        List<String> data;

        public void setData(List<String> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            if (data == null) return 0;
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView != null){
                textView = (TextView) convertView;
            }else {
                textView = new TextView(parent.getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setPadding(20,20,20,20);
                textView.setTextSize(18);
            }

            textView.setText(getItem(position));

            return textView;
        }
    }

}
