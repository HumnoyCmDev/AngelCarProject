package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarBrandDao;
import com.dollarandtrump.angelcar.dao.CarSubDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.fragment.ListImageFragment;
import com.dollarandtrump.angelcar.fragment.BrandFragment;
import com.dollarandtrump.angelcar.fragment.CarSubDetailFragment;
import com.dollarandtrump.angelcar.fragment.CarSubFragment;
import com.dollarandtrump.angelcar.fragment.PostFragment;
import com.dollarandtrump.angelcar.interfaces.OnSelectData;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.dollarandtrump.angelcar.utils.ViewFindUtils;
import com.dollarandtrump.angelcar.view.AngelCarViewPager;
import com.flyco.tablayout.SegmentTabLayout;
import com.squareup.otto.Produce;
import com.viewpagerindicator.LinePageIndicator;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 26/2/59. เวลา 10:43
 ***************************************/
public class PostActivity extends AppCompatActivity implements OnSelectData{
    private static final String TAG = "PostActivity";
    public static final int CALL_BRAND = 1;
    public static final int CALL_CAR_TYPE = 2;
    public static final int CALL_CAR_TYPE_DETAIL = 3;
    public static final int CALL_GALLERY_OK = 4;
    public static final int CALL_GALLERY_CANCEL = -4;
    public static final int CALL_GALLERY_NEXT = -3;
    public static final int CALL_FINISH_POST = 5;

    private int lastPosition = 0;

    @Bind(R.id.post_viewpager) AngelCarViewPager pager;
    @Bind(R.id.indicator) LinePageIndicator indicator;
    @Bind(R.id.btnNext) Button next;
    @Bind(R.id.btnPrevious) Button previous;

    InfoCarModel infoCarModel;
    List<Fragment> mFragment;
    private PostAdapterViewpager adapter;
    boolean isEdit = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        MainThreadBus.getInstance().register(this);
        ButterKnife.bind(this);
        initInstance();
        /*String[] mTitles_3 = {"首页", "消息", "联系人", "更多"};
        SegmentTabLayout segment = ViewFindUtils.find(getWindow().getDecorView(),R.id.segment_tab);
        segment.setTabData(mTitles_3);*/

    }

    private void initInstance(){
        mFragment = new ArrayList<>(4);
        mFragment.add(BrandFragment.newInstance());
        mFragment.add(CarSubFragment.newInstance());
        mFragment.add(CarSubDetailFragment.newInstance());

        Bundle arg = getIntent().getExtras();
        if (arg != null && arg.getBoolean("isEdit",false)){
            isEdit = arg.getBoolean("isEdit",false);
            PostCarDao modelCar = Parcels.unwrap(arg.getParcelable("carModel"));
            infoCarModel = new InfoCarModel();
            infoCarModel.setEditInfo(true);
            infoCarModel.setPostCarDao(modelCar);

            MainThreadBus.getInstance().post(onProduceInfo());
        }

        adapter = new PostAdapterViewpager(getSupportFragmentManager());
        adapter.setFragmentList(mFragment);
        pager.setOffscreenPageLimit(0);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        pager.setPagingEnabled(false);
        previous.setEnabled(false);
        next.setEnabled(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                enabledButton(position,lastPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick({R.id.btnNext,R.id.btnPrevious})
    public void next(View v){
        if (v.getId() == R.id.btnNext)
            pager.setCurrentItem(pager.getCurrentItem()+1);
        else
            pager.setCurrentItem(pager.getCurrentItem()-1);
        enabledButton(pager.getCurrentItem(),lastPosition);
    }

    private void enabledButton(int position,int lastPosition) {
        previous.setEnabled(position > 0);
        next.setEnabled(lastPosition > position);
        pager.setPagingEnabled(lastPosition > position);
    }

    @Override
    public void onSelectedCallback(int callback,InfoCarModel infoCarModel) {
        this.infoCarModel = infoCarModel;
        if (callback != CALL_BRAND && callback != CALL_CAR_TYPE) {
            switch (callback) {
                case CALL_CAR_TYPE_DETAIL:
                    if (!isEdit)
                        mFragment.add(ListImageFragment.newInstance(infoCarModel));
                    mFragment.add(PostFragment.newInstance());
                    adapter.notifyDataSetChanged();
                    break;
                case CALL_GALLERY_OK:
                    next.setEnabled(true);
                    MainThreadBus.getInstance().post(onProduceInfo());
                    return;
                case CALL_GALLERY_CANCEL:
                    next.setEnabled(false);
                    return;
                case CALL_GALLERY_NEXT:
                    MainThreadBus.getInstance().post(onProduceInfo());
                    break;
                default: finish();return;
            }
        }
        pager.setCurrentItem(pager.getCurrentItem()+1);
        lastPosition = pager.getCurrentItem();
        enabledButton(pager.getCurrentItem(),lastPosition);
    }

    private class PostAdapterViewpager extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();

        public void setFragmentList(List<Fragment> fragmentList) {
            this.fragmentList = fragmentList;
        }

        public PostAdapterViewpager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


    @Produce
    public InfoCarModel onProduceInfo(){
        return infoCarModel;
    }

}
