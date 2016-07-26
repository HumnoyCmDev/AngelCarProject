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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.datatype.MutableInteger;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.hndev.library.view.AngelCarPost;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedPostCarAdapter extends BaseAdapter implements Filterable {

    private PostCarCollectionDao dao;
    private List<PostCarDao> daoFilter;
    private Filter planetFilter;
    private MutableInteger lastPositionInteger;
    private boolean isLoading = false;
    private Context mContext;
//    @SuppressLint("SimpleDateFormat")
//    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    AngelCarPost.OnClickImageProfile onClickImageProfile;

    public FeedPostCarAdapter(Context mContext,MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
        this.mContext = mContext;
    }

    public void setDao(PostCarCollectionDao dao) {
        this.dao = dao;
        if (dao !=null && dao.getListCar() != null)
            daoFilter = dao.getListCar();
    }

    public void setOnClickImageProfile(AngelCarPost.OnClickImageProfile onClickImageProfile) {
        this.onClickImageProfile = onClickImageProfile;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public int getCount() {
        if (daoFilter == null) return 1;
        return daoFilter.size()+1;
    }

    public void increaseLastPosition(int amount) {
        lastPositionInteger.setValue(lastPositionInteger.getValue() + amount);
    }

    public PostCarDao getItem(int position) {
        return daoFilter.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getViewTypeCount() {
        return 3;
    }

    public int getItemViewType(int position) {
        if (position == getCount()-1) return 2;
        return position % 2 == 0 ? 1 : 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        int switching_position = getItemViewType(position);

        if (switching_position == 2){
            ProgressBar item;
            if (view != null )
                item = (ProgressBar) view;
            else
                item = new ProgressBar(parent.getContext());

            if (isLoading) {
                item.setVisibility(View.VISIBLE);
            }else {
                item.setVisibility(View.GONE);
            }
            return item;
        }

        switch (switching_position){
            case 0: view = inflaterLayoutRight(view,parent,getItem(position),position);
                break;
            case 1: view = inflaterLayoutLeft(view,parent,getItem(position),position);
                break;
        }

         if(position > lastPositionInteger.getValue()){
             Animation anim = AnimationUtils.loadAnimation(parent.getContext(),
                     R.anim.up_from_bottom);
             view.startAnimation(anim);
             lastPositionInteger.setValue(position);
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

        if (onClickImageProfile != null) {
            holderPost.angelCarPost.setOnClickImageProfile(onClickImageProfile,position);
        }
    }

//    private  <T extends ViewHolderPost> T ViewHolder(T t){
//        return t;
//    }

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter();

        return planetFilter;
    }

    /************
    *Inner Class*
    *************/

    public abstract class ViewHolderPost {
        @Bind(R.id.item_post) protected AngelCarPost angelCarPost;
        public ViewHolderPost(View v) {
            ButterKnife.bind(this,v);
        }

    }

    private class PlanetFilter extends Filter {

        public PlanetFilter() {
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = dao.getListCar();
                results.count = dao.getListCar().size();
            }else {
                List<PostCarDao> nGao = new ArrayList<>();
                for (PostCarDao g : daoFilter){
                    if(g.getCarName().toUpperCase().startsWith(constraint.toString().toUpperCase())){
                        nGao.add(g);
                    }
                }

                results.values = nGao;
                results.count = nGao.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                daoFilter = (List<PostCarDao>) results.values;
                notifyDataSetChanged();
            }
        }
    }


}
