package com.dollarandtrump.angelcar.listener;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.dollarandtrump.angelcar.Adapter.ConversationRecyclerAdapter;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.interfaces.ItemTouchHelperAdapter;

public class ItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    private ItemTouchHelperAdapter adapter;

    Drawable background;
    Drawable xMark;
    int xMarkMargin;
    boolean initiated;

    Context mContext;


    public ItemTouchHelperCallback(Context context,ItemTouchHelperAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.mContext = context;
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


/**extend ItemTouchHelper.Callback**/
/**
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START ;//| ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
**/

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        /**return 0 delete == false , จะเลื่อนตัวเก่าไม่ได้**/
        int position = viewHolder.getAdapterPosition();
        ConversationRecyclerAdapter recyclerAdapter = (ConversationRecyclerAdapter) recyclerView.getAdapter();
        if (!recyclerAdapter.isDelete() || recyclerAdapter.isPendingRemoval(position)){
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw red background
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = xMark.getIntrinsicWidth();
        int intrinsicHeight = xMark.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void init() {
        background = new ColorDrawable(Color.RED);//Color.parseColor("#FFB13D"));
        xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_bin_delete_white_24dp);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) mContext.getResources().getDimension(R.dimen.ic_clear_margin);
        initiated = true;
    }
}
