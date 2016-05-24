package com.dollarandtrump.angelcar.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.view.HeaderAdminChatCar;
import com.dollarandtrump.angelcar.view.HeaderChatCar;
import com.hndev.library.view.AngelCarMessage;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        Log.i("Adapter", "setShowDate: "+positionShowDate);
        if (viewTypeShowDate == View.GONE)
            viewTypeShowDate = View.VISIBLE;
        else
            viewTypeShowDate = View.GONE;
    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    HeaderChatCar.OnClickItemHeaderChatListener onClickItemHeaderChatListener;

    public ChatAdapter(String messageBy) {
        this.messageBy = messageBy;
    }


    public void setOnClickItemBannerListener(HeaderChatCar.OnClickItemHeaderChatListener onClickItemHeaderChatListener) {
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

        Log.i("Adapter", "ps1 "+position);
        if (getItemViewType(position) == 2){
            HeaderChatCar headerChatCar;
            if (convertView != null) {
                headerChatCar = (HeaderChatCar) convertView;
            }else {
                headerChatCar = new HeaderChatCar(parent.getContext());
            }

            if (postCarDao != null) {
                headerChatCar.setIconProfile("http://cls.paiyannoi.me/profileimages/default.png");
                headerChatCar.setImageBanner(pictureDao);
                headerChatCar.setDataPostCar(postCarDao);
                if (onClickItemHeaderChatListener != null)
                    headerChatCar.setOnClickItemBannerListener(onClickItemHeaderChatListener);
                headerChatCar.setFollow(isFollow);
                headerChatCar.setTimeString(dateFormat.format(postCarDao.getCarModifyTime()) + " น.");
            }
            return headerChatCar;
        }

        Log.i("Adapter", "ps2 "+position);
        if (getItemViewType(position) == 3){
            HeaderAdminChatCar headerAdminChatCar;
            if (convertView != null){
                headerAdminChatCar = (HeaderAdminChatCar) convertView;
            }else {
                headerAdminChatCar = new HeaderAdminChatCar(parent.getContext());
            }
            headerAdminChatCar.setIconProfile("http://cls.paiyannoi.me/profileimages/default.png");
            return headerAdminChatCar;
        }

        MessageDao message = getItem(position);
        int msBy = getItemViewType(position);
        positionShowDate = getCount();
        switch (msBy){
            case 0 : convertView = inflateLayoutChatLeft(convertView,parent,message,position);
                break;
            case 1 : convertView = inflateLayoutChatRight(convertView,parent,message,position);
                break;
        }
        return convertView;
    }

    //inflate layout
    private View inflateLayoutChatRight(View view, ViewGroup parent, MessageDao message, int position) {
        TextRightViewHolder holder;
        if (view != null) {
            holder = (TextRightViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            holder = new TextRightViewHolder(view);
            view.setTag(holder);
        }
        //coding
        holder.angelCarMessage.setMessage(message.getMessageText());
        holder.angelCarMessage.setIconProfile(message.getUserProfileImage());

        // Test Date Time
        if (positionShowDate == position) {
            Log.i("Adapter", "true : "+position);
            holder.angelCarMessage.inflateDateTime(viewTypeShowDate);
        }else {
            Log.i("Adapter", "false : "+position);
        }

        return view;
    }

    private View inflateLayoutChatLeft(View view, ViewGroup parent, MessageDao message, int position) {
        TextLeftViewHolder holder;
        if (view != null) {
            holder = (TextLeftViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            holder = new TextLeftViewHolder(view);
            view.setTag(holder);
        }
        //coding
        holder.angelCarMessage.setMessage(message.getMessageText());
        holder.angelCarMessage.setIconProfile(message.getUserProfileImage());
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
