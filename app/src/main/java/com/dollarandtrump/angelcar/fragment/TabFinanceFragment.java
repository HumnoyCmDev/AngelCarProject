package com.dollarandtrump.angelcar.fragment;

/**
 * Created by ABaD on 12/15/2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.R;
import com.flyco.tablayout.SegmentTabLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TabFinanceFragment extends Fragment {

    @Bind(R.id.tabLayoutSegment) SegmentTabLayout segmentTabLayout;


    public TabFinanceFragment() {
        super();
    }

    public static TabFinanceFragment newInstance() {
        return new TabFinanceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finance_tab, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        if (savedInstanceState == null) {
            ArrayList<Fragment> mFragments = new ArrayList<>();
            mFragments.add(FinanceFragment.newInstance());
            mFragments.add(FinanceFragment.newInstance());
            segmentTabLayout.setTabData(new String[]{"จัดไฟแนนซ์", "รีไฟแนนซ์"}, getActivity(), R.id.fl_change, mFragments);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}