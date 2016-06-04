package com.dollarandtrump.angelcar.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dollarandtrump.angelcar.fragment.FeedPostFragment;
import com.dollarandtrump.angelcar.fragment.TabFinanceFragment;
import com.dollarandtrump.angelcar.fragment.MenuFragment;
import com.dollarandtrump.angelcar.fragment.ShopFragment;

/**
 * Created by humnoy on 20/1/59.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return FeedPostFragment.newInstance();
            case 1: return TabFinanceFragment.newInstance();
            case 2: return ShopFragment.newInstance();
            default: return MenuFragment.newInstance();
//            case 3: return MenuFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
