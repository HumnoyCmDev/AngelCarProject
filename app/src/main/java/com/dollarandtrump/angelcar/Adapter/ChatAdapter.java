package com.dollarandtrump.angelcar.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.view.ItemAnnounceView;
import com.dollarandtrump.angelcar.view.ItemCarDetailView;
import com.hndev.library.view.AngelCarMessage;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

/**
 * Created by humnoy on 22/1/59.
 */
public class ChatAdapter extends BaseAdapter {
    private List<MessageDao> messages;
    private String messageBy ;
    private PictureCollectionDao pictureDao ;
    private PostCarDao postCarDao;
    boolean isFollow = false;

    int positionShowDate;

    int viewTypeShowDate = View.VISIBLE;

    public void setShowDate(int positionShowDate) {
        this.positionShowDate = positionShowDate;
        if (viewTypeShowDate == View.GONE)
            viewTypeShowDate = View.VISIBLE;
        else
            viewTypeShowDate = View.GONE;
    }

    ItemCarDetailView.OnClickItemHeaderChatListener onClickItemHeaderChatListener;

    public ChatAdapter(String messageBy) {
        this.messageBy = messageBy;
    }


    public void setOnClickItemBannerListener(ItemCarDetailView.OnClickItemHeaderChatListener onClickItemHeaderChatListener) {
        this.onClickItemHeaderChatListener = onClickItemHeaderChatListener;
    }

    public void setMessages(List<MessageDao> messages) {
        this.messages = messages;
    }

    public void setPictureDao(PictureCollectionDao pictureDao) {
        this.pictureDao = pictureDao;
    }

    public void setPostCarDao(PostCarDao postCarDao) {
        this.postCarDao = postCarDao;
    }

    @Override
    public int getCount() {
        if (messages == null) return 2;
        return messages.size()+2;
    }

    @Override
    public MessageDao getItem(int position) {
        return messages.get(position-2);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 2;
        if (position == 1) return 3;
        String messageByGao = getItem(position).getMessageBy();
        return byUser(messageBy,messageByGao);
    }

    public void setFollow(boolean isFollow){
        this.isFollow = isFollow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == 2){
            ItemCarDetailView itemCarDetailView;
            if (convertView != null) {
                itemCarDetailView = (ItemCarDetailView) convertView;
            }else {
                itemCarDetailView = new ItemCarDetailView(parent.getContext());
            }

            if (postCarDao != null) {
                itemCarDetailView.setIconProfile("http://cls.paiyannoi.me/profileimages/default.png");
                itemCarDetailView.setImageBanner(pictureDao);
                itemCarDetailView.setDataPostCar(postCarDao);
                if (onClickItemHeaderChatListener != null)
                    itemCarDetailView.setOnClickItemBannerListener(onClickItemHeaderChatListener);
                itemCarDetailView.setFollow(isFollow);
                itemCarDetailView.setTimeString(AngelCarUtils.formatTimeAndDay(parent.getContext(),postCarDao.getCarModifyTime()));
            }
            return itemCarDetailView;
        }

        if (getItemViewType(position) == 3){
            ItemAnnounceView itemAnnounceView;
            if (convertView != null){
                itemAnnounceView = (ItemAnnounceView) convertView;
            }else {
                itemAnnounceView = new ItemAnnounceView(parent.getContext());
            }
            itemAnnounceView.setIconProfile("http://cls.paiyannoi.me/profileimages/default.png");
            return itemAnnounceView;
        }

        MessageDao message = getItem(position);
        int msBy = getItemViewType(position);
        positionShowDate = getCount();
        switch (msBy){
            case 0 : convertView = inflateLayoutChatCellThem(convertView,parent,message,position);
                break;
            case 1 : convertView = inflateLayoutChatCellMe(convertView,parent,message,position);
                break;
        }
        return convertView;
    }

    //inflate layout
    private View inflateLayoutChatCellMe(View view, ViewGroup parent, MessageDao message, int position) {
        TextRightViewHolder holder;
        if (view != null) {
            holder = (TextRightViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
            holder = new TextRightViewHolder(view);
            view.setTag(holder);
        }
        //coding
        holder.angelCarMessage.setMessage(message.getMessageText());
        holder.angelCarMessage.setIconProfile(message.getUserProfileImage());

        // Test Date Time
        if (positionShowDate == position) {
            holder.angelCarMessage.inflateDateTime(viewTypeShowDate);
        }
        return view;
    }

    private View inflateLayoutChatCellThem(View view, ViewGroup parent, MessageDao message, int position) {
        TextLeftViewHolder holder;
        if (view != null) {
            holder = (TextLeftViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_them, parent, false);
            holder = new TextLeftViewHolder(view);
            view.setTag(holder);
        }
        //coding
        holder.angelCarMessage.setMessage(message.getMessageText());
        holder.angelCarMessage.setIconProfile(message.getUserProfileImage());

        if(message.getMessageStatus() == 0){ /* Read Message*/
            HttpManager.getInstance().getService()
                    .observableReadMessage(String.valueOf(message.getMessageId()))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe();
        }


        return view;
    }

    // View Holder
    public class TextLeftViewHolder {
        @Bind(R.id.item_chat_left)
        AngelCarMessage angelCarMessage;
        public TextLeftViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public class TextRightViewHolder {
        @Bind(R.id.item_chat_right)
        AngelCarMessage angelCarMessage;
        public TextRightViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    private int byUser(String messageBy, String messageByGao) {
        /*0 = ซ้าย || 1 = ขวา*/
        if (messageBy.equals(messageByGao))
            return 1;
        else
            return 0;

        /*@ messageBy ได้มาจาก DetailActivity ส่งเข้ามา
        *ถ้า messageBy ตรงกับใน messageByGao เรียงแชทไว้ขวา*/
    }

}
