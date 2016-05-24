package com.dollarandtrump.angelcar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.hndev.library.view.AngelCarHashTag;


/**
 * Created by humnoyDeveloper on 19/5/59. 09:11
 */
public class ListHashTag extends RecyclerView{
    public ListHashTag(Context context) {
        super(context);
        initInstances();
    }

    public ListHashTag(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInstances();
    }

    public ListHashTag(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initInstances();
    }

    private void initInstances() {
        setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
    }

}
