package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.fragment.ViewPictureFragment;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoyDeveloper on 6/4/59. 15:31
 */
public class ViewPictureActivity extends AppCompatActivity {

    @Bind(R.id.viewPager)
    ViewPager page;

    private PictureCollectionDao dao = null;
    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        ButterKnife.bind(this);

        /**สำคัญ**/
        dao = Parcels.unwrap(getIntent().getParcelableExtra("PICTURE_DAO"));
        position = getIntent().getIntExtra("POSITION",0);

        PagerAdapter pagerAdapter =
                new ScreenSlidePagerAdapter(getSupportFragmentManager());
        page.setAdapter(pagerAdapter);
        page.setCurrentItem(position);


    }

//    @Override
//    public void onBackPressed() {
//        if (page.getCurrentItem() == 0) {
//            super.onBackPressed();
//        } else {
//            page.setCurrentItem(page.getCurrentItem() - 1);
//        }
//    }

    /******************
     *Inner Class Zone*
     ******************/
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String url = dao.getListPicture().get(position).getCarImageThumbnailPath();
            return ViewPictureFragment.newInstance(url);
        }

        @Override
        public int getCount() {
            return dao.getListPicture().size();
        }
    }
}
