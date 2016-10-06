package com.dollarandtrump.angelcar.fragment;

/**
 * Created by ABaD on 12/15/2015.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.util.SQLiteUtils;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.TopicChatActivity;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.interfaces.InterNetInterface;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.utils.Log;
import com.flyco.tablayout.SegmentTabLayout;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabTopicFragment extends Fragment{

    @Bind(R.id.tabLayoutSegment) SegmentTabLayout segmentTabLayout;
    @Bind(R.id.floating_action_menu_fab) FloatingActionMenu mMenuFab;
    String[] title;
    public TabTopicFragment() {
        super();
    }

    public static TabTopicFragment newInstance() {
        return new TabTopicFragment();
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
        mMenuFab.setClosedOnTouchOutside(true);

        title = new String[]{getString(R.string.text_finance),
                getString(R.string.text_refinance),getString(R.string.text_pawn)};

        if (savedInstanceState == null) {
            ArrayList<Fragment> mFragments = new ArrayList<>();
            mFragments.add(TopicFragment.newInstance(TopicFragment.Type.FINANCE));
            mFragments.add(TopicFragment.newInstance(TopicFragment.Type.REFINANCE));
            mFragments.add(TopicFragment.newInstance(TopicFragment.Type.PAWN));
            segmentTabLayout.setTabData(title,
                    getActivity(), R.id.fl_change, mFragments);
        }
    }

     @OnClick({R.id.fab_finance,R.id.fab_refinance,R.id.fab_pawn})
     public void onClickFabButton(View v){

         if (((InterNetInterface) getActivity()).isConnectInternet()) {

             ProfileDao mProfile = SQLiteUtils.rawQuerySingle(ProfileDao.class, "SELECT * FROM Profile", null);
             if (mProfile == null || mProfile.getShopName() == null || mProfile.getUrlShopLogo().contains("default.png")) {
                 new AlertDialog.Builder(getActivity())
                         .setCancelable(false)
                         .setTitle(R.string.alert)
                         .setMessage(R.string.alert_edit_shop)
                         .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {

                             }
                         })
                         .setIcon(android.R.drawable.ic_dialog_alert)
                         .show();
             } else {

                 if (mMenuFab.isOpened())
                     mMenuFab.close(true);
                 switch (v.getId()) {
                     case R.id.fab_finance:
                         if (Log.isLoggable(Log.DEBUG)) Log.d("Finance");
                         newTopic(TopicFragment.Type.FINANCE.toString(), 0);
                         break;
                     case R.id.fab_refinance:
                         if (Log.isLoggable(Log.DEBUG)) Log.d("ReFinance");
                         newTopic(TopicFragment.Type.REFINANCE.toString(), 1);
                         break;
                     default:
                         if (Log.isLoggable(Log.DEBUG)) Log.d("Pawn");
                         newTopic(TopicFragment.Type.PAWN.toString(), 2);
                         break;
                 }
             }
         }
     }

    @Override
    public void onStart() {
        super.onStart();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainThreadBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onSubscribeTopic(TopicDao topic){
        if (((InterNetInterface) getActivity()).isConnectInternet()) {
            if (topic.getUserId().equals(Registration.getInstance().getUserId())) {
                Intent intent = new Intent(getActivity(), TopicChatActivity.class);
                intent.putExtra("topic", Parcels.wrap(topic));
                intent.putExtra("room", title[segmentTabLayout.getCurrentTab()]);
                startActivity(intent);
            } else {
                alertMessage();
            }
        }
    }

    private void newTopic(String topic,int idRoom){
        Intent intent = new Intent(getActivity(), TopicChatActivity.class);
        intent.putExtra("topic_message",topic);
        intent.putExtra("room",title[idRoom]);
        startActivity(intent);
    }

    private void alertMessage(){
        new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.alert)
                .setMessage(R.string.alert_message_topic)
                .setPositiveButton(R.string.alert_new_topic, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newTopic(title[segmentTabLayout.getCurrentTab()],segmentTabLayout.getCurrentTab());
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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