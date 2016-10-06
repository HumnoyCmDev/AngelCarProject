package com.dollarandtrump.angelcar.fragment.conversation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dollarandtrump.angelcar.Adapter.ConversationRecyclerAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.listener.ItemTouchHelperCallback;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;



@SuppressWarnings("unused")
public abstract class ConversationFactory extends Fragment implements ConversationRecyclerAdapter.OnClickItemConversationListener{
    private static final String TAG = ConversationFactory.class.getSimpleName();

    @Bind(R.id.stub_text_view) ViewStub mStubTextNoResult;
    @Bind(R.id.listConversation) RecyclerView list;
    @Bind(R.id.imageButton_delete) ImageButton mDeleteMessage;
    ConversationRecyclerAdapter conversationAdapter;

    private MessageManager mManager;
    private boolean isTopic = false;

    public ConversationFactory(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conversation,container,false);
        initInstances(v,savedInstanceState);
        return v;
    }

    private void init(Bundle savedInstanceState) {
        mManager = new MessageManager();
        setHasOptionsMenu(true);
    }
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        mDeleteMessage.setVisibility(View.GONE);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        conversationAdapter = new ConversationRecyclerAdapter(getContext(),isTopic);
        conversationAdapter.setOnClickItemConversationListener(this);
        list.setAdapter(conversationAdapter);
        ItemTouchHelper.Callback callback =
                new ItemTouchHelperCallback(getContext(),conversationAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(list);
        list.addItemDecoration(itemDecoration);

    }

    public void onSubScribeMessage(MessageManager msgManager){
        mManager.setMessageDao(getMessageManager(msgManager));
        mManager.setProductIds(msgManager.getProductIds());
        if (mManager.getCount() > 0) {

            list.setVisibility(View.VISIBLE);
            mStubTextNoResult.setVisibility(View.GONE);

            conversationAdapter.setListConversation(mManager.getMessageDao().getListMessage());
            conversationAdapter.setProduct(mManager.getProductIds());
            conversationAdapter.notifyDataSetChanged();

        }else {
           /* mListView.setVisibility(View.GONE);*/
            mStubTextNoResult.setVisibility(View.VISIBLE);

            list.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickItemChat(MessageDao message,int position) {
        if (conversationAdapter.isDelete()){
            // select item delete

        }else {
            /** select item to room chat **/
            if (isTopic) message.setTopic(true);
            OnClickItemMessageListener onClickItemMessageListener =
                    (OnClickItemMessageListener) getActivity();
            onClickItemMessageListener.onClickItemMessage(message);
        }

    }

    @Override
    public void onLongClickItemChat() {
        clearDeleteFlagIfCapable(true);

    }

    @OnClick(R.id.imageButton_delete)
    public void onDeleteMessage(){
        clearDeleteFlagIfCapable(false);
        conversationAdapter.delete();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_conversation,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete){
            boolean isDelete = !conversationAdapter.isDelete();
            clearDeleteFlagIfCapable(isDelete);
            conversationAdapter.delete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearDeleteFlagIfCapable(boolean d){
        if (d)
            showButtonDelete();
        else
            hideButtonDelete();

            conversationAdapter.setIsDelete(d);
    }

    private void showButtonDelete(){
        mDeleteMessage.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_face_in);
        mDeleteMessage.startAnimation(animation);
    }

    private void hideButtonDelete(){
        if (mDeleteMessage.getVisibility() == View.VISIBLE) {
            mDeleteMessage.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(
                    Contextor.getInstance().getContext(),
                    R.anim.zoom_face_out);
            mDeleteMessage.startAnimation(animation);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("message", Parcels.wrap(mManager.getMessageDao()));
    }


    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        MessageCollectionDao messageDao =
                Parcels.unwrap(savedInstanceState.getParcelable("message"));
        mManager.setMessageDao(messageDao);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setTopic(boolean topic) {
        isTopic = topic;
    }

    /**************
     *Listener Zone*
     ***************/
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MessageDao dao = mManager.getMessageDao().getListMessage().get(position);
            if (isTopic) dao.setTopic(true);
            OnClickItemMessageListener onClickItemMessageListener =
                    (OnClickItemMessageListener) getActivity();
            onClickItemMessageListener.onClickItemMessage(dao);

        }

    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), "OnItem Long Click" + position, Toast.LENGTH_LONG).show();
            return true;
        }
    };

    RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {

        Drawable background;
        boolean initiated;

        private void init() {
            background = new ColorDrawable(Color.RED);
            initiated = true;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (!initiated) {
                init();
            }

            if (parent.getItemAnimator().isRunning()) {

                View lastViewComingDown = null;
                View firstViewComingUp = null;

                // this is fixed
                int left = 0;
                int right = parent.getWidth();

                // this we need to find out
                int top = 0;
                int bottom = 0;

                // find relevant translating views
                int childCount = parent.getLayoutManager().getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getLayoutManager().getChildAt(i);
                    if (child.getTranslationY() < 0) {
                        // view is coming down
                        lastViewComingDown = child;
                    } else if (child.getTranslationY() > 0) {
                        // view is coming up
                        if (firstViewComingUp == null) {
                            firstViewComingUp = child;
                        }
                    }
                }

                if (lastViewComingDown != null && firstViewComingUp != null) {
                    // views are coming down AND going up to fill the void
                    top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                    bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                } else if (lastViewComingDown != null) {
                    // views are going down to fill the void
                    top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                    bottom = lastViewComingDown.getBottom();
                } else if (firstViewComingUp != null) {
                    // views are coming up to fill the void
                    top = firstViewComingUp.getTop();
                    bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                }

                background.setBounds(left, top, right, bottom);
                background.draw(c);

            }
            super.onDraw(c, parent, state);
        }
    };

    /*****************
     *Inner Class Zone*
     ******************/
    abstract public MessageCollectionDao getMessageManager(MessageManager manager);
//    abstract public void subScribeMessage(MessageManager manager);
//    abstract public void onItemClickListener(MessageDao dao);

}
