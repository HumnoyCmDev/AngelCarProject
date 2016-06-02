package com.dollarandtrump.angelcar.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoy on 26/1/59.
 */
//// TODO: 20/2/59 New Adapter...
public class MessageAdapter extends BaseAdapter{

    private List<MessageDao> dao;
    private SimpleDateFormat sf;


    @SuppressLint("SimpleDateFormat")
    public MessageAdapter() {
        sf = new SimpleDateFormat("HH:mm:ss");
    }

    public void setDao(List<MessageDao> dao) {
        this.dao = dao;
        Collections.sort(dao, new Comparator<MessageDao>() {
            @Override
            public int compare(MessageDao lhs, MessageDao rhs) {
                return rhs.getMessagesTamp().compareTo(lhs.getMessagesTamp());
            }
        });
    }

    @Override
    public int getCount() {
        if (dao == null) return 0;
        return dao.size();
    }

    @Override
    public MessageDao getItem(int position) {
        return dao.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_message_adapter, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        MessageDao message = getItem(position);

        Glide.with(parent.getContext())
                .load(message.getUserProfileImage())
                .error(R.drawable.ic_hndeveloper)
                .into(holder.icon);

        String msg = message.getMessageText();

        holder.txtDisPlayName.setText(message.getDisplayName());
        if (msg.contains("<img>") && msg.contains("</img>")){
                holder.txtMessage.setText("รูปภาพ 1 รูป");
        }else {
            holder.txtMessage.setText(message.getMessageText());
        }
        holder.txtTime.setText(sf.format(message.getMessagesTamp()));
        return convertView;
    }


    public class ViewHolder{
        @Bind(R.id.chat_item_message_dis_play_name) TextView txtDisPlayName;
        @Bind(R.id.chat_item_message_image) CircularImageView icon;
        @Bind(R.id.chat_item_message_message) TextView txtMessage;
        @Bind(R.id.chat_item_message_time) TextView txtTime;

        public ViewHolder(View v) {
            ButterKnife.bind(this,v);
        }
    }

}
