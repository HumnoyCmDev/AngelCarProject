package com.dollarandtrump.angelcar.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.fragment.PhotoFragment;

/**
 * Created by ABaD on 12/2/2015.
 */
@Deprecated
public class PictureAdapter extends FragmentPagerAdapter {
    private PictureCollectionDao gao;

    public PictureAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setGao(PictureCollectionDao gao) {
        this.gao = gao;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoFragment.newInstance(gao.getListPicture().get(position));
    }

    @Override
    public int getCount() {
        if (gao == null) return 0;
        if (gao.getListPicture() == null) return 0;
        return gao.getListPicture().size();
    }
}