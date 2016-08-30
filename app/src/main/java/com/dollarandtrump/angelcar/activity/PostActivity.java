package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.fragment.BrandFragment;
import com.dollarandtrump.angelcar.fragment.CarSubDetailFragment;
import com.dollarandtrump.angelcar.fragment.CarSubFragment;
import com.dollarandtrump.angelcar.fragment.ListImageFragment;
import com.dollarandtrump.angelcar.fragment.PostCarFragment;
import com.dollarandtrump.angelcar.interfaces.OnScrolling;
import com.dollarandtrump.angelcar.interfaces.OnSelectData;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.dollarandtrump.angelcar.view.AngelCarViewPager;
import com.squareup.otto.Produce;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PostActivity extends AppCompatActivity implements OnSelectData ,OnScrolling {
    public static final int CALL_BRAND = 1;
    public static final int CALL_CAR_TYPE = 2;
    public static final int CALL_CAR_TYPE_DETAIL = 3;
    public static final int CALL_GALLERY_OK = 4;
    public static final int CALL_GALLERY_CANCEL = -4;
    public static final int CALL_GALLERY_NEXT = -3;
    public static final int CALL_FINISH_POST = 5;


    private int lastPosition = 0;

    @Bind(R.id.post_viewpager) AngelCarViewPager pager;
    @Bind(R.id.btnNext) Button next;
    @Bind(R.id.btnPrevious) Button previous;
    @Bind(R.id.group_button) LinearLayout mGroupButton;
    @Bind(R.id.toolbar) Toolbar toolbar;

    InfoCarModel infoCarModel;
    List<Fragment> mFragment;
    private PostAdapterViewpager adapter;
    boolean isEdit = false;
    int category;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initToolbar();
        MainThreadBus.getInstance().register(this);
        ButterKnife.bind(this);
        initInstance();


    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initInstance(){
        mFragment = new ArrayList<>(4);
        mFragment.add(BrandFragment.newInstance());
        mFragment.add(CarSubFragment.newInstance());
        mFragment.add(CarSubDetailFragment.newInstance());
        infoCarModel = new InfoCarModel();
        Bundle arg = getIntent().getExtras();
        if (arg != null && arg.getBoolean("isEdit",false)){ // edit
            isEdit = arg.getBoolean("isEdit",false);
            PostCarDao modelCar = Parcels.unwrap(arg.getParcelable("carModel"));
            infoCarModel.setEditInfo(true);
            infoCarModel.setPostCarDao(modelCar);
            MainThreadBus.getInstance().post(onProduceInfo());
        }

        if (arg != null) {
            category = arg.getInt("category", -1);
        }

        adapter = new PostAdapterViewpager(getSupportFragmentManager());
        adapter.setFragmentList(mFragment);
        pager.setOffscreenPageLimit(0);
        pager.setAdapter(adapter);
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
    public void next(View v) {
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
        this.infoCarModel.setCategory(category);
        if (callback != CALL_BRAND && callback != CALL_CAR_TYPE) {
            switch (callback) {
                case CALL_CAR_TYPE_DETAIL:
                    if (!isEdit)
                        mFragment.add(ListImageFragment.newInstance(infoCarModel));
                    mFragment.add(PostCarFragment.newInstance());
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

    @Override
    public void onScrollingUp() {
        Animation animation = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.group_button_slide_up);
        mGroupButton.startAnimation(animation);
        mGroupButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScrollingDown() {
        mGroupButton.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.group_button_slide_down);
        mGroupButton.startAnimation(animation);
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
