package com.dollarandtrump.angelcar.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.hndev.library.view.AngelCarMessage;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 3/2/59. เวลา 10:56
 ***************************************/
public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<MessageDao> messagesGao;
    private String messageBy ;


    public RecyclerChatAdapter(String messageBy) {
        this.messageBy = messageBy;
    }

    public void setMessagesGao(List<MessageDao> messagesGao) {
        this.messagesGao = messagesGao;
    }

    @Override
    public int getItemViewType(int position) {
        String messageByGao = messagesGao.get(position).getMessageBy();
        return byUser(messageBy,messageByGao);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0:
                View viewLeft = inflater.inflate(R.layout.item_chat_them,parent,false);
                return new ViewHolderLeft(viewLeft);
            default:
                View viewRight = inflater.inflate(R.layout.item_chat_me,parent,false);
                return new ViewHolderRight(viewRight);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // get item
        MessageDao message = messagesGao.get(position);

        switch (holder.getItemViewType()){
            case 0 :
                ViewHolderLeft holderLeft = (ViewHolderLeft) holder;
                holderLeft.angelCarMessage.setMessage(message.getMessageText());
                holderLeft.angelCarMessage.setIconProfile(message.getUserProfileImage());

                if (position == 0)
                   holderLeft.angelCarMessage.setBackground(Color.parseColor("#50E3C2"));
                else if (position == 1)
                    holderLeft.angelCarMessage.setBackground(Color.parseColor("#7ED321"));
                break;

            case 1:
                ViewHolderRight holderRight = (ViewHolderRight) holder;
                holderRight.angelCarMessage.setMessage(message.getMessageText());
                holderRight.angelCarMessage.setIconProfile(message.getUserProfileImage());

                if (position == 0)
                    holderRight.angelCarMessage.setBackground(Color.parseColor("#50E3C2"));
                else if (position == 1)
                    holderRight.angelCarMessage.setBackground(Color.parseColor("#7ED321"));
                break;
        }



    }

    public class ViewHolderLeft extends RecyclerView.ViewHolder {
        @Bind(R.id.item_chat_left)
        AngelCarMessage angelCarMessage;
        public ViewHolderLeft(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderRight extends RecyclerView.ViewHolder {
        @Bind(R.id.item_chat_right)
        AngelCarMessage angelCarMessage;
        public ViewHolderRight(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        if (messagesGao == null) return 0;
        return messagesGao.size();
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
