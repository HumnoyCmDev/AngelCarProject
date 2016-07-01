package com.dollarandtrump.angelcar.Adapter;

/**
 * Created by Developer on 12/15/2015. 14:35
 */
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class FollowAdapter extends BaseAdapter {

    private PostCarCollectionDao dao;
    private int lastPositionInteger = -1;


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
            case 0: view = inflaterLayoutRight(view,parent,getItem(position));
                break;
            case 1: view = inflaterLayoutLeft(view,parent,getItem(position));
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

    private View inflaterLayoutLeft(View view, ViewGroup parent, PostCarDao postItem){
        ViewHolderItemLeft holder;
        if(view != null) {
            holder = (ViewHolderItemLeft) view.getTag();
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_left, parent, false);
            holder = new ViewHolderItemLeft(view);
            view.setTag(holder);
        }
            String topic = postItem.getCarTitle();
            String detail = AngelCarUtils.convertLineUp(postItem.getCarDetail());
            String carName = postItem.getCarName();
            holder.angelCarPost.setPictureProfile("http://cls.paiyannoi.me/profileimages/default.png");
            holder.angelCarPost.setPictureProduct(postItem.getCarImageThumbnailPath());
            holder.angelCarPost.setTitle(topic);
            holder.angelCarPost.setDetails(carName +" "+ detail.replaceAll("<n>"," "));

        return view;
    }

    private View inflaterLayoutRight(View view, ViewGroup parent, PostCarDao postItem){
        ViewHolderItemRight holder;
        if(view != null) {
            holder = (ViewHolderItemRight) view.getTag();
        }else {
            view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_right, parent, false);
            holder = new ViewHolderItemRight(view);
            view.setTag(holder);
        }
            String topic = postItem.getCarTitle();
            String detail = AngelCarUtils.convertLineUp(postItem.getCarDetail());
            String carName = postItem.getCarName();
            holder.angelCarPost.setPictureProduct(postItem.getCarImageThumbnailPath());
            holder.angelCarPost.setTitle(topic);
            holder.angelCarPost.setDetails(carName +" "+ detail.replaceAll("<n>"," "));

        return view;
    }

    public class ViewHolderItemLeft {
        @Bind(R.id.item_post)
        AngelCarPost angelCarPost;
        public ViewHolderItemLeft(View v) {
            ButterKnife.bind(this,v);
        }
    }

    public class ViewHolderItemRight {
        @Bind(R.id.item_post)
        AngelCarPost angelCarPost;
        public ViewHolderItemRight(View v) {
            ButterKnife.bind(this,v);
        }
    }

}
