package com.dollarandtrump.angelcar.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.DealerCollection;
import com.dollarandtrump.angelcar.dao.ShopDealerDao;
import com.dollarandtrump.angelcar.utils.Log;
import com.dollarandtrump.angelcar.view.PhotoThumb;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kotlin
 **/
public class ListDealerAdapter extends RecyclerView.Adapter<ListDealerAdapter.ViewHolder>{
    private Context mContext;
    private DealerCollection collection;
    private OnClickDealerListener listener;

    public ListDealerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setCollection(DealerCollection collection) {
        this.collection = collection;
    }

    public void setListener(OnClickDealerListener listener) {
        this.listener = listener;
    }

    @Override
    public ListDealerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_dealer,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListDealerAdapter.ViewHolder holder, int position) {
        ShopDealerDao dealerDao = collection.getDealers().get(position);
        Glide.with(mContext).load(dealerDao.getShopLogo())
                .crossFade()
                .centerCrop()
                .placeholder(R.drawable.icon_logo)
                .error(R.drawable.icon_logo)
                .into(holder.mImageProfile);
        holder.mShopName.setText(dealerDao.getShopName());
        holder.mTextCount.setText(dealerDao.getCurrentCar() +" /คัน");
        String viewAndFollow = dealerDao.getShopView() +" | "+dealerDao.getShopFollow() +" Follow";
        holder.mTextView.setText(viewAndFollow);
    }

    @Override
    public int getItemCount() {
        if (collection == null) return 0;
        return collection.getDealers().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_shop) PhotoThumb mImageProfile;
        @Bind(R.id.text_shop_name) TextView mShopName;
        @Bind(R.id.text_count_car) TextView mTextCount;
        @Bind(R.id.text_view) TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        ShopDealerDao item = collection.getDealers().get(getAdapterPosition());
                        listener.onItemClick(String.valueOf(item.getShopId()),item.getUserRef());
                    }
                }
            });
        }
    }

    public interface OnClickDealerListener {
        void onItemClick(String shop,String user);
    }
}
