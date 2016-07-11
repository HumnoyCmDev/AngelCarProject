package com.dollarandtrump.angelcar.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by humnoy on 26/1/59.
 */
//// TODO: 20/2/59 New Adapter...
public class ConversationAdapter extends BaseAdapter{

    private List<MessageDao> mListDao;
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;
    Context context;
    public ConversationAdapter() {
    }

    public void setDao(List<MessageDao> mListDao) {
        this.mListDao = mListDao;

        context = Contextor.getInstance().getContext();
        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);

        Collections.sort(mListDao, new Comparator<MessageDao>() {
            @Override
            public int compare(MessageDao lhs, MessageDao rhs) {
                return rhs.getMessagesTamp().compareTo(lhs.getMessagesTamp());
            }
        });
    }

    @Override
    public int getCount() {
        if (mListDao == null) return 0;
        return mListDao.size();
    }

    @Override
    public MessageDao getItem(int position) {
        return mListDao.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.angelcar_conversation_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        MessageDao message = getItem(position);

        Glide.with(parent.getContext())
                .load(message.getUserProfileImage())
                .bitmapTransform(new CropCircleTransformation(parent.getContext()))
                .error(R.drawable.ic_hndeveloper)
                .into(holder.avatar);

        String msg = message.getMessageText();

        holder.title.setText(message.getDisplayName());
        if (msg.contains("<img>") && msg.contains("</img>")){
                holder.lastMessage.setText("รูปภาพ 1 รูป");
        }else {
            holder.lastMessage.setText(message.getMessageText());
        }
//        String time = AngelCarUtils.formatTimeDay(parent.getContext(),message.getMessagesTamp())
//                .replaceAll(",","");
        String time = AngelCarUtils.formatTime(context, message.getMessagesTamp(), mTimeFormat, mDateFormat);
        holder.time.setText(time);
        return convertView;
    }


    public class ViewHolder{
        @Bind(R.id.avatar) ImageView avatar;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.last_message) TextView lastMessage;
        @Bind(R.id.time) TextView time;

        public ViewHolder(View v) {
            ButterKnife.bind(this,v);
        }
    }

}
