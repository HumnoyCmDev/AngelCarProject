package com.dollarandtrump.angelcar.fragment;

/**
 * Created by ABaD on 12/15/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.R;

public class FilterFragment extends Fragment {

    public FilterFragment() {
        super();
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {

// Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_filter, container, false);

    }

}