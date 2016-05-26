package com.dollarandtrump.angelcar.Adapter;

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
    List<String> brand;

    public void setDao(PostCarCollectionDao dao) {
        this.dao = dao;
        if(dao != null && dao.getListCar() != null &&dao.getListCar().size() > 0){
            /*Collections.sort(this.dao.getListCar(), new Comparator<PostCarDao>() {
                @Override
                public int compare(PostCarDao lhs, PostCarDao rhs) {
                    return lhs.getCarName().compareTo(rhs.getCarName());
                }
            });*/
           /* List<String> _brand = new LinkedList<>();
            for (PostCarDao d : dao.getListCar()){
                _brand.add(d.getCarName());
            }*/
//            this.dao.sortBrand();
            brand = this.dao.findDuplicates();
        }


    }

   /* public Set<String> findDuplicates(List<String> listContainingDuplicates) {
        Set<String> setToReturn = new HashSet<String>();
//        Set<String> set1 = new HashSet<String>();
        for (String yourInt : listContainingDuplicates) {
//            if (!set1.add(yourInt)) {
                setToReturn.add(yourInt);
//            }
        }
        return setToReturn;
    }*/

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
            holder.carHashTag.setColorBackground("#F44336");
            holder.carHashTag.setColorSelected("#2196F3");
            holder.carHashTag.setColorUnSelected("#F44336");
//            holder.carHashTag.setEnabled(true);
        }else {
            if (dao != null && dao.getListCar() != null
                    && brand != null && brand.size() > 0) {
//                PostCarDao item = dao.getListCar().get(position-1);
                holder.carHashTag.setText(brand.get(position-1));
                holder.carHashTag.setColorSelected("#2196F3");
                holder.carHashTag.setCornerRadius(5f);
            }
        }
    }

    @Override
    public int getItemCount() {
//        if (dao == null) return 1;
//        if (dao.getListCar() == null) return 1;
//        return dao.getListCar().size()+1;
        return (brand != null ? brand.size()+1 : 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AngelCarHashTag carHashTag;
        public ViewHolder(View itemView) {
            super(itemView);
            carHashTag = (AngelCarHashTag) itemView;
            carHashTag.setOnClickHashTagListener(new AngelCarHashTag.OnClickHashTagListener() {
                @Override
                public void onClickItem(boolean isSelected) {
                    Log.i("ShopAdapter", "Selected : "+isSelected);
//                    notifyDataSetChanged();

                    if (onItemClickHashTagListener != null && brand != null && brand.size() > 0)
                        onItemClickHashTagListener
                                .onItemClick(isSelected,getAdapterPosition(),
                                        brand.get(getAdapterPosition()-1));
                }
            });
        }
    }
}
