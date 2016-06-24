package com.dollarandtrump.angelcar.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by humnoyDeveloper on 28/3/59. 16:41
 */
public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private PostCarCollectionDao dao;
    private PostCarCollectionDao originalDao;
    private Filter planetFilter;
    private Context context;
    private RecyclerOnItemClickListener recyclerOnItemClickListener;

    private List<Integer> positionHeader;

    private boolean isShop = true;

    public boolean isHeader(int position) {
        for (int i : positionHeader){
            if (i == position) return true;
        }
        return false ;
    }

    @Override
    public int getItemCount() {
        if (dao == null) return 0;
        if (dao.getListCar() == null) return 0;
//        if (positionHeader != null)
//        return dao.getListCar().size()+positionHeader.size();
        return dao.getListCar().size();
    }

    public void setShop(boolean shop) {
        isShop = shop;
    }

    public void setDao(PostCarCollectionDao dao) {
        this.dao = dao;
        originalDao = dao;
//        this.dao.sortBrand();
        positionHeader = this.dao.findPositionHeader();
//        listNameHeader = this.dao.findDuplicates();

        /*add position Header to Dao*/
        if (this.dao != null && this.dao.getListCar() != null &&
                positionHeader != null) {
            for (int i = 0; i < positionHeader.size(); i++) {
                this.dao.getListCar().add(positionHeader.get(i), new PostCarDao());
            }
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
                    .inflate(R.layout.item_shop_line_separator,parent,false);
            return new HeaderViewHolder(v);
        }

        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_shop,parent,false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder.getItemViewType() == ITEM_VIEW_TYPE_HEADER){
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;

            if (dao.getListCar() != null &&
                    dao.getListCar().get(position+1).getCarName() != null)
            viewHolder.tvTitleHeader.setText(
                    dao.getListCar().get(position+1).getCarName().toUpperCase());
        }

        if (holder.getItemViewType() == ITEM_VIEW_TYPE_ITEM) {
            ViewHolder viewHolder = (ViewHolder) holder;

                PostCarDao item = dao.getListCar()
                        .get(position);
                Glide.with(context)
                        .load(item.getCarImageThumbnailPath())
                        .placeholder(R.drawable.loading)
                        .into(viewHolder.shopImage);
            String carName = item.getCarSub()+" "+item.getCarSubDetail();
                viewHolder.carName.setText(carName);
            viewHolder.shopSetting.setVisibility(isShop ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter(this,dao.getListCar());
        return planetFilter;
    }

    public void reSetDao(){
        if (originalDao != null && originalDao.getListCar() != null)
            setDao(originalDao);
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

        //todo Check Place Item
        @Override
        public void onClick(View v) {
            if (recyclerOnItemClickListener != null) {
                if (!findPlaceHeader(getAdapterPosition())) {
                    if (v instanceof ImageView) { // setting
                        recyclerOnItemClickListener
                                .OnClickSettingListener(v, getAdapterPosition());
                    } else { // item
                        recyclerOnItemClickListener
                                .OnClickItemListener(v, getAdapterPosition());
                    }
                }

            }
        }

        public boolean findPlaceHeader(int position){
            for (int i : positionHeader){
                if (i == position) return true;
            }
            return false;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_line_separator) TextView tvTitleHeader;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    /******************
     *Inner Class Zone*
     ******************/
    public class PlanetFilter extends Filter {
        private ShopAdapter shopAdapter;
        private  List<PostCarDao> originalDao;
        private  List<PostCarDao> daoFilterList;

        public PlanetFilter(ShopAdapter shopAdapter, List<PostCarDao> originalDao) {
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