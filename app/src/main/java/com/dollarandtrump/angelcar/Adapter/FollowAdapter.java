package com.dollarandtrump.angelcar.Adapter;

/**
 * Created by Developer on 12/15/2015. 14:35
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.hndev.library.view.AngelCarPost;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FollowAdapter extends BaseAdapter {

    private PostCarCollectionDao dao;
    private int lastPositionInteger = -1;
    private Context mContext;

    public FollowAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDao(PostCarCollectionDao dao) {
        this.dao = dao;
    }

    public int getCount() {
        if (dao == null) return 0;
        if (dao.getListCar() == null) return 0;
        return dao.getListCar().size();
    }


    public PostCarDao getItem(int position) {
        return dao.getListCar().get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
//        return position % 2 == 0 ? 1 : 0;
    return 1;
    }

    public View getView(int position, View view, ViewGroup parent) {
        int switching_position = getItemViewType(position);
        switch (switching_position){
            case 0: view = inflaterLayoutRight(view,parent,getItem(position),position);
                break;
            case 1: view = inflaterLayoutLeft(view,parent,getItem(position),position);
                break;
        }

         if(position > lastPositionInteger){
             Animation anim = AnimationUtils.loadAnimation(parent.getContext(),
                     R.anim.up_from_bottom);
             view.startAnimation(anim);
             lastPositionInteger = position;
         }

        return view;
    }

    private View inflaterLayoutLeft(View view, ViewGroup parent, PostCarDao postCarDao,int position){
        ViewHolderItemLeft holder;
        if(view != null) {
            holder = (ViewHolderItemLeft) view.getTag();
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_left, parent, false);
            holder = new ViewHolderItemLeft(view);
            view.setTag(holder);
        }
        bindDataPostCar(holder,postCarDao,"#ff0000","#ff0000",position);
        return view;
    }

    private View inflaterLayoutRight(View view, ViewGroup parent, PostCarDao postCarDao,int position){
        ViewHolderItemRight holder;
        if(view != null) {
            holder = (ViewHolderItemRight) view.getTag();
        }else {
            view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_right, parent, false);
            holder = new ViewHolderItemRight(view);
            view.setTag(holder);
        }
        bindDataPostCar(holder,postCarDao,"#FFB13D","#FFB13D",position);
        return view;
    }

    private void bindDataPostCar(ViewHolderPost holderPost,PostCarDao dao,String color1,String color2,int position){
        String topic = dao.getCarTitle();
        holderPost.angelCarPost.setPictureProfile(dao.getFullPathShopLogo());
        holderPost.angelCarPost.setPictureProduct(dao.getCarImageThumbnailPath());
        holderPost.angelCarPost.setTitle(topic);
        double amount = Double.parseDouble(dao.getCarPrice());
//        DecimalFormat formatter = new DecimalFormat("#,###").format(amount);
        String price = new DecimalFormat("#,###").format(amount);
        String strTitle = dao.getCarName() + " " +
                dao.getCarSub() + " " + dao.getCarSubDetail();
        String strDetail = "ปี "+ AngelCarUtils.textFormatHtml(color1,""+dao.getCarYear())+
                " ราคา "+ AngelCarUtils.textFormatHtml(color2, price) +" บาท";
        holderPost.angelCarPost.setDetails(strTitle);
        holderPost.angelCarPost.setDetails2Html(strDetail);
        String datetime = AngelCarUtils.formatTimeAndDay(mContext,dao.getCarModifyTime());
        holderPost.angelCarPost.setTime(datetime);
    }

    public class ViewHolderItemLeft extends ViewHolderPost{
        public ViewHolderItemLeft(View v) {
            super(v);
        }
    }

    public class ViewHolderItemRight extends ViewHolderPost{
        public ViewHolderItemRight(View v) {
            super(v);
        }
    }

    public abstract class ViewHolderPost {
        @Bind(R.id.item_post) protected AngelCarPost angelCarPost;
        public ViewHolderPost(View v) {
            ButterKnife.bind(this,v);
        }

    }

}
