package com.dollarandtrump.angelcar.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoyDeveloper on 28/3/59. 16:41
 */
public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private PostCarCollectionDao dao;
    private Filter planetFilter;
    private Context context;
//    private final String BASE_URL_THUMBNAIL ="http://angelcar.com/ios/data/gadata/thumbnailcarimages/";
    private RecyclerOnItemClickListener recyclerOnItemClickListener;
//    ArrayList<String> newCarName;


    public boolean isHeader(int position) {
//        return false;
        return position == 0 || position == 4 || position == 6 ;
    }

    @Override
    public int getItemCount() {
//        if (dao == null) return 5;
//        if (dao.getListCar() == null) return 5;
//        return dao.getListCar().size()+5;
        return 5+3;
    }

    public void setDao(PostCarCollectionDao dao) {
        this.dao = dao;

        if ((this.dao != null && this.dao.getListCar() != null)
                && this.dao.getListCar().size() > 0){
            Collections.sort(this.dao.getListCar(), new Comparator<PostCarDao>() {
                @Override
                public int compare(PostCarDao lhs, PostCarDao rhs) {
                    return lhs.getCarName().compareTo(rhs.getCarName());
                }
            });
        }


        // หาตำแหน่งหัวข้อยี่ห้อ
        if(dao != null && dao.getListCar() != null &&dao.getListCar().size() > 0){
            List<String> carName = new ArrayList<>();
            for (PostCarDao d : dao.getListCar()){
                carName.add(d.getCarName());
            }
            ArrayList<String> newCarName = new ArrayList<>(findDuplicates(carName));
            Log.i("ShopAdapter", "size :"+newCarName.size());
            Log.i("ShopAdapter", "size :"+dao.getListCar().size());
//            for (int i = 0; i < newCarName.size(); i++) {
//                for (int j = 0; j < dao.getListCar().size(); j++) {
//                    if (newCarName.get(i)
//                            .contains(dao.getListCar().get(j).getCarName())){
//
//
//                    }
//                }
//            }
        }
    }

    public void setOnclickListener(RecyclerOnItemClickListener recyclerOnItemClickListener){
        this.recyclerOnItemClickListener = recyclerOnItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_line_brand,parent,false);
            return new HeaderViewHolder(v);
        }

        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // coding
//        if (holder.getItemViewType() == ITEM_VIEW_TYPE_ITEM) {
//            ViewHolder viewHolder = (ViewHolder) holder;
//            PostCarDao item = dao.getListCar().get(position-1);
//            Glide.with(context)
//                    .load(item.getCarImageThumbnailPath())
//                    .placeholder(R.drawable.loading)
//                    .into(viewHolder.shopImage);
//            viewHolder.carName.setText(item.getCarName());
//        }
    }

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter(this,dao.getListCar());
        return planetFilter;
    }

    public Set<String> findDuplicates(List<String> listContainingDuplicates) {
         Set<String> setToReturn = new HashSet<String>();
//         Set<String> set1 = new HashSet<String>();
        for (String yourInt : listContainingDuplicates) {
//            if (!set1.add(yourInt)) {
                setToReturn.add(yourInt);
//            }
        }
        return setToReturn;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.itemShopSetting) ImageView shopSetting;
        @Bind(R.id.itemShopImage) ImageView shopImage;
        @Bind(R.id.itemShopCar) TextView carName;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            shopSetting.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerOnItemClickListener != null) {
                if (v instanceof ImageView) { // setting
                    recyclerOnItemClickListener
                            .OnClickSettingListener(v, getAdapterPosition());
                } else { // item
                    recyclerOnItemClickListener
                            .OnClickItemListener(v,getAdapterPosition());
                }
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    /******************
     *Inner Class Zone*
     ******************/
    private class PlanetFilter extends Filter {
        private ShopAdapter shopAdapter;
        private  List<PostCarDao> originalDao;
        private  List<PostCarDao> daoFilterList;

        public PlanetFilter(ShopAdapter shopAdapter,  List<PostCarDao> originalDao) {
            this.shopAdapter = shopAdapter;
            this.originalDao = originalDao;
            daoFilterList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            daoFilterList.clear();
            FilterResults results = new FilterResults();
            if (constraint.length() == 0){
                daoFilterList.addAll(originalDao);
            }else {
                for (PostCarDao g : originalDao){
                    if(g.getCarName().toUpperCase().startsWith(constraint.toString().toUpperCase())){
                        daoFilterList.add(g);
                    }
                }
            }
            results.values = daoFilterList;
            results.count = daoFilterList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shopAdapter.dao.getListCar().clear();
            PostCarCollectionDao dao = new PostCarCollectionDao();
            dao.setListCar((List<PostCarDao>) results.values);
            shopAdapter.setDao(dao);
            shopAdapter.notifyDataSetChanged();
        }
    }

    public interface RecyclerOnItemClickListener{
        void OnClickItemListener(View v, int position);
        void OnClickSettingListener(View v, int position);
    }

}