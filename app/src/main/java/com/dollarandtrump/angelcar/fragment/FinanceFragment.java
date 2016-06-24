package com.dollarandtrump.angelcar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย Humnoy Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@SuppressWarnings("unused")
public class FinanceFragment extends Fragment {

    @Bind(R.id.recycler_list_chat) RecyclerView mListChat;

    public FinanceFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static FinanceFragment newInstance() {
        FinanceFragment fragment = new FinanceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View rootView = inflater.inflate(R.layout.fragment_finance, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        mListChat.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    // Adapter
    public class FeedChatAdapter extends RecyclerView.Adapter<FeedChatAdapter.ViewHolder>{

        @Override
        public FeedChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            return null;
        }

        @Override
        public void onBindViewHolder(FeedChatAdapter.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

            public void bindViewHolder(){

            }
        }
    }

}
