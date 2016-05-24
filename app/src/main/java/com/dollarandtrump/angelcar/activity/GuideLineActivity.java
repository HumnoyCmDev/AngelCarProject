package com.dollarandtrump.angelcar.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.banner.SimpleGuideBanner;
import com.dollarandtrump.angelcar.utils.ViewFindUtils;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.DepthTransformer;

import java.util.ArrayList;

public class GuideLineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_line);

        View decorView = getWindow().getDecorView();
        sgb(decorView);

    }

    private void sgb(View rootView) {
        SimpleGuideBanner sgb = ViewFindUtils.find(rootView, R.id.sgb);
        sgb
                .setIndicatorWidth(6)
                .setIndicatorHeight(6)
                .setIndicatorGap(12)
                .setIndicatorCornerRadius(3.5f)
                .setSelectAnimClass(ZoomInEnter.class)
                .setTransformerClass(DepthTransformer.class)
                .barPadding(0, 10, 0, 10)
                .setSource(geUsertGuides())
                .startScroll();

        sgb.setOnJumpClickL(new SimpleGuideBanner.OnJumpClickL() {
            @Override
            public void onJumpClick() {
                finish();
            }
        });
    }

    public static ArrayList<Integer> geUsertGuides() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide_line_1);
        list.add(R.drawable.guide_line_2);
        list.add(R.drawable.guide_line_3);
        list.add(R.drawable.guide_line_4);
        list.add(R.drawable.guide_line_5);
        list.add(R.drawable.guide_line_6);
        list.add(R.drawable.guide_line_7);
        return list;
    }
}
