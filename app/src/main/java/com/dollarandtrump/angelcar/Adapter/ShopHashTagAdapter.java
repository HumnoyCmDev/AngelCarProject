package com.dollarandtrump.angelcar.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.hndev.library.view.AngelCarHashTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by humnoyDeveloper on 19/5/59. 11:06
 */
public class ShopHashTagAdapter extends RecyclerView.Adapter<ShopHashTagAdapter.ViewHolder>{

    public interface OnItemClickHashTagListener{
        void onItemClick(boolean isSelected,int position,String brand);
    }

    private OnItemClickHashTagListener onItemClickHashTagListener;
    private PostCarCollectionDao dao;

    private List<PostCarDao> childBrand;
//    List<String> brand;

    public void setDao(PostCarCollectionDao dao) {
        this.dao = dao;
//        if(this.dao != null && this.dao.getListCar() != null && this.dao.getListCar().size() > 0){
//            brand = this.dao.findDuplicates();
//        }
    }

    public void setChildBrand(List<PostCarDao> childBrand) {
//        if (this.childBrand != null && this.childBrand.size() > 0)
//        this.childBrand.clear();
        this.childBrand = childBrand;
    }

    public void setOnItemClickHashTagListener(OnItemClickHashTagListener onItemClickHashTagListener){
        this.onItemClickHashTagListener = onItemClickHashTagListener;
    }


    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @Override
    public ShopHashTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AngelCarHashTag angelCarHashTag = new AngelCarHashTag(parent.getContext());
        return new ViewHolder(angelCarHashTag);
    }

    @Override
    public void onBindViewHolder(ShopHashTagAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0){
            holder.carHashTag.setText("ทั้งหมด");
            holder.carHashTag.setCornerRadius(5f);
            holder.carHashTag.setTextColor(Color.WHITE);
            holder.carHashTag.setColorBackground("#2196F3");
            holder.carHashTag.setColorSelected("#2196F3");
            holder.carHashTag.setColorUnSelected("#F44336");
        }else {
            if (dao != null && dao.getListCar() != null) {
                PostCarDao item = dao.getListCar().get(position-1);
                holder.carHashTag.setText(item.getCarName());
                holder.carHashTag.setColorSelected("#2196F3");
                holder.carHashTag.setCornerRadius(5f);
                if (childBrand != null && childBrand.size() > 0) {
                    String lastCarSub = "";
                    for (PostCarDao d : childBrand) {
                        if (d.getCarName().contains(item.getCarName())) {
                            if (!lastCarSub.contains(d.getCarSub())) {
                                holder.carHashTag.addChildCarSub(d.getCarSub());
                                lastCarSub = d.getCarSub();
                            }
                        }

                    }
                }
            }
        }
    }
//    public void clearData() {
//        if (dao != null && dao.getListCar() != null && dao.getListCar().size() > 0) {
//            int size = dao.getListCar().size();
//            if (size > 0) {
//                for (int i = 0; i < size; i++) {
////                    this.dao.getListCar().remove(0);
//                }
////                this.notifyItemRangeRemoved(0, size);
//                this.notifyItemChanged(1);
//            }
//        }
//    }

    @Override
    public int getItemCount() {
        if (dao == null) return 1;
        if (dao.getListCar() == null) return 1;
        return dao.getListCar().size()+1;
//        return (brand != null ? brand.size()+1 : 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AngelCarHashTag carHashTag;
        public ViewHolder(View itemView) {
            super(itemView);
            carHashTag = (AngelCarHashTag) itemView;
            carHashTag.setOnClickHashTagListener(new AngelCarHashTag.OnClickHashTagListener() {
                @Override
                public void onClickItem(boolean isSelected) {
//                    if (getAdapterPosition() == 0) return;
                    if (onItemClickHashTagListener != null && dao != null && dao.getListCar().size() > 0) {
                        if (getAdapterPosition() == 0){
                            onItemClickHashTagListener
                                    .onItemClick(isSelected, getAdapterPosition(),
                                            "");
                            return;
                        }
                        onItemClickHashTagListener
                                .onItemClick(isSelected, getAdapterPosition(),
                                        dao.getListCar().get(getAdapterPosition() - 1).getCarName());
                    }
                }
            });
        }
    }
}
