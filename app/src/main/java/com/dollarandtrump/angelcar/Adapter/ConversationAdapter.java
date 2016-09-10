package com.dollarandtrump.angelcar.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.manager.Contextor;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.utils.Log;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

@Deprecated
public class ConversationAdapter extends BaseAdapter{

    private List<MessageDao> mListDao;
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;

    String[] mProduceIds = null;
    Context context;
    boolean isTopic;
    public ConversationAdapter(boolean isTopic) {
        this.isTopic = isTopic;
    }

    public void setDao(List<MessageDao> mListDao) {
        this.mListDao = mListDao;

        context = Contextor.getInstance().getContext();
        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);

        Collections.sort(mListDao, new Comparator<MessageDao>() {
            @Override
            public int compare(MessageDao lhs, MessageDao rhs) {
                return rhs.getMessageStamp().compareTo(lhs.getMessageStamp());
            }
        });
    }

    public void setProduct(CarIdDao produceIds){
        mProduceIds = produceIds.getAllCarId().split("\\|\\|");
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


        Glide.with(parent.getContext())
                .load(message.getUserProfileImage())
                .bitmapTransform(new CropCircleTransformation(parent.getContext()))
                .into(holder.avatar);

        String msg = message.getMessageText();

        holder.title.setText(message.getDisplayName());
        if (msg.contains("<img>") && msg.contains("</img>")){
                holder.lastMessage.setText("รูปภาพ 1 รูป");
        }else {
            holder.lastMessage.setText(message.getMessageText());
        }
//        String time = AngelCarUtils.formatTimeDay(parent.getContext(),message.getMessageStamp())
//                .replaceAll(",","");
        String time = AngelCarUtils.formatTime(context, message.getMessageStamp(), mTimeFormat, mDateFormat);
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
