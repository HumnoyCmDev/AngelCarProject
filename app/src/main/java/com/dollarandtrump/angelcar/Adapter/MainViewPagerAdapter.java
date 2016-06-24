package com.dollarandtrump.angelcar.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dollarandtrump.angelcar.fragment.FeedPostFragment;
import com.dollarandtrump.angelcar.fragment.TabFinanceFragment;
import com.dollarandtrump.angelcar.fragment.MenuFragment;
import com.dollarandtrump.angelcar.fragment.ShopFragment;
import com.dollarandtrump.angelcar.manager.Registration;

/********************************************
 * Created by HumNoy Developer on 20/1/59.
 * ผู้คร่ำหวอดในกวงการ Android มากกว่า 1 ปี
 * AngelCarProject
 ********************************************/
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return FeedPostFragment.newInstance();
            case 1: return TabFinanceFragment.newInstance();
            case 2: return ShopFragment.newInstance(Registration.getInstance().getUserId(),
                    Registration.getInstance().getShopRef());
            default: return MenuFragment.newInstance();
//            case 3: return MenuFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
