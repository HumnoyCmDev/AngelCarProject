package com.dollarandtrump.angelcar.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.interfaces.ItemTouchHelperAdapter;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.utils.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ConversationRecyclerAdapter extends RecyclerView.Adapter<ConversationRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
//    List<Integer> itemIndex;
    private List<String> items;
    private HashMap<String, Runnable> pendingRunnables = new HashMap<>();
    private List<String> itemsPendingRemoval;
    private boolean isDelete = false;
    private HashMap<String,Integer> itemSelected = new HashMap<>();

    private Context mContext;
    private List<MessageDao> mListConversation;
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;

    private String[] mProduceIds = null;
    private boolean isTopic;

    private OnClickItemConversationListener onClickItemConversationListener;

    public ConversationRecyclerAdapter(Context mContext, boolean isTopic) {
        this.mContext = mContext;
        this.isTopic = isTopic;

        itemsPendingRemoval = new ArrayList<>();
    }

    public void setOnClickItemConversationListener(OnClickItemConversationListener onClickItemConversationListener) {
        this.onClickItemConversationListener = onClickItemConversationListener;
    }

    public void setListConversation(List<MessageDao> listConversation) {
        this.mListConversation = listConversation;

        mDateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(mContext);

        Collections.sort(this.mListConversation, new Comparator<MessageDao>() {
            @Override
            public int compare(MessageDao lhs, MessageDao rhs) {
                return rhs.getMessageStamp().compareTo(lhs.getMessageStamp());
            }
        });

        items = new ArrayList<>();
        for (MessageDao dao : mListConversation){
            items.add(""+dao.getMessageId());
        }


    }

    public void setProduct(CarIdDao produceIds){
        mProduceIds = produceIds.getAllCarId().split("\\|\\|");
    }

    @Override
    public ConversationRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.angelcar_conversation_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ConversationRecyclerAdapter.ViewHolder holder, int position) {
            MessageDao message = mListConversation.get(position);

        final String item = items.get(position);

        if (itemsPendingRemoval.contains(item)) {
            holder.mGroup.setVisibility(View.GONE);
            holder.undo.setBackgroundResource(R.drawable.selector_follow);
            holder.undo.setVisibility(View.VISIBLE);
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(item));
                }
            });
        }else {
            holder.mGroup.setVisibility(View.VISIBLE);
            holder.undo.setVisibility(View.GONE);
            holder.undo.setOnClickListener(null);
        }
        holder.mSelectedDeleteMessage.setVisibility(isDelete() ? View.VISIBLE : View.GONE);



        /*Read message Bold*/
        if (!isTopic && mProduceIds != null) {
            for (String id : mProduceIds){
                    /*message to  me(Shop)*/
                if (id.equals(message.getMessageCarId()) && "user".equals(message.getMessageBy())){
                    if (message.getMessageStatus() == 0){
                        holder.title.setTypeface(null, Typeface.BOLD);
                        holder.lastMessage.setTypeface(null,Typeface.BOLD);
                    }
                   /*message to me(user)*/
                }else if (!id.equals(message.getMessageCarId()) && "shop".equals(message.getMessageBy())){
                    if (message.getMessageStatus() == 0) {
                        holder.title.setTypeface(null, Typeface.BOLD);
                        holder.lastMessage.setTypeface(null,Typeface.BOLD);
                    }
                }
            }
        }else {
            //Topic
            if (!message.getMessageBy().equals("user") && message.getMessageStatus() == 0){
                holder.title.setTypeface(null, Typeface.BOLD);
                holder.lastMessage.setTypeface(null,Typeface.BOLD);
            }

        }

        Glide.with(mContext)
                .load(message.getUserProfileImage())
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(holder.avatar);

        String msg = message.getMessageText();

        holder.title.setText(message.getDisplayName());
        if (msg.contains("<img>") && msg.contains("</img>")){
            holder.lastMessage.setText("รูปภาพ 1 รูป");
        }else {
            holder.lastMessage.setText(message.getMessageText());
        }
        String time = AngelCarUtils.formatTime(mContext, message.getMessageStamp(), mTimeFormat, mDateFormat);
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        if (mListConversation == null) return 0;
        return mListConversation.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mListConversation, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mListConversation, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
            pendingRemoval(position);
    }

    public void pendingRemoval(final int position) {
        final String item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(position);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    private void remove(int position) {
        int newPosition = items.size() > position ? position : position-1;
        String item = items.get(newPosition);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            removeItem(newPosition);
        }

    }

    private void removeItem(int position){
        mListConversation.remove(position);
        notifyItemRemoved(position);
        items.remove(position);
    }

    public void delete(){
        ArrayList<String> keys = new ArrayList<>(itemSelected.keySet());
        ListIterator<String> iterator = keys.listIterator(keys.size());
        while (iterator.hasPrevious()){
            String key = iterator.previous();
            Log.d(key);
            if (itemSelected.containsKey(key)){
                removeItem(itemSelected.get(key));
                itemSelected.remove(key);
            }
        }
    }




    public boolean isPendingRemoval(int position) {
        String item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    public void setIsDelete(boolean d){
        this.isDelete = d;
        notifyDataSetChanged();
    }

    public boolean isDelete() {
        return isDelete;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        @Bind(R.id.avatar) ImageView avatar;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.last_message) TextView lastMessage;
        @Bind(R.id.time) TextView time;
        @Bind(R.id.swipeable) LinearLayout mGroup;
        @Bind(R.id.check_box_delete) CheckBox mSelectedDeleteMessage;
        @Bind(R.id.undo_button) TextView undo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mGroup.setBackgroundResource(R.drawable.selector_overlay);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mSelectedDeleteMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        itemSelected.put(String.valueOf(getAdapterPosition()), getAdapterPosition());
                    }else {
                        if (itemSelected.containsKey(String.valueOf(getAdapterPosition()))){
                            itemSelected.remove(String.valueOf(getAdapterPosition()));
                        }
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
            if (onClickItemConversationListener != null){
                MessageDao message = mListConversation.get(getAdapterPosition());
                onClickItemConversationListener.onClickItemChat(message,getAdapterPosition());
                if (isDelete()){
                    mSelectedDeleteMessage.setChecked(!mSelectedDeleteMessage.isChecked());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onClickItemConversationListener != null){
                onClickItemConversationListener.onLongClickItemChat();
                return true;
            }
                return false;
        }
    }

    public interface OnClickItemConversationListener {
            void onClickItemChat(MessageDao message,int position);
            void onLongClickItemChat();
    }
}
