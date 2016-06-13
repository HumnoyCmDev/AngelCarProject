package com.dollarandtrump.angelcar.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 10/6/59.11:35น.
 *
 * @AngelCarProject
 */
public class AngelCarMessageAdapter extends RecyclerView.Adapter<AngelCarMessageAdapter.ViewHolder> {
    private final static int TYPE_THEM = 0;
    private final static int TYPE_ME = 1;
    private MessageCollectionDao mMessageDao;
    private String mMessageBy;
    private LayoutInflater mLayoutInflater;

    private Context mContext;

    // Dates and Clustering
    private final Map<Integer, Cluster> mClusterCache = new HashMap<>();
//    protected final Handler mUiThreadHandler;
//    private final DateFormat mDateFormat;
    private final DateFormat mTimeFormat;

    public AngelCarMessageAdapter(Context context, String mMessageBy) {
        this.mContext = context;
        this.mMessageBy = mMessageBy;
        mLayoutInflater = LayoutInflater.from(context);
//        mUiThreadHandler = new Handler(Looper.getMainLooper());
//        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    public void setmMessageBy(String mMessageBy) {
        this.mMessageBy = mMessageBy;
    }

    public void setMessageDao(MessageCollectionDao mMessageDao) {
        this.mMessageDao = mMessageDao;
    }

    @Override
    public int getItemViewType(int position) {
        MessageDao item = mMessageDao.getListMessage().get(position);
        return userTypeView(mMessageBy,item.getMessageBy());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        //chat
        int roodId = viewType == TYPE_THEM ? CellViewHolder.RESOURCE_ID_ME : CellViewHolder.RESOURCE_ID_THEM;
        CellViewHolder callViewHolder = new CellViewHolder(mLayoutInflater.inflate(roodId,parent,false));
        return callViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       bindCellViewHolder((CellViewHolder) holder,position);
    }

    public void bindCellViewHolder(CellViewHolder viewHolder, int position) {
         MessageDao messageDao  = mMessageDao.getListMessage().get(position);
        Log.i("Ag", "bindCellViewHolder: "+mMessageDao.getListMessage().size());
        //Clustering and dates
        Cluster cluster = getClustering(mMessageDao,position);
        if (cluster.mClusterWithPrevious == null){
            viewHolder.mClusterSpaceGap.setVisibility(View.GONE);
            viewHolder.mTimeGroup.setVisibility(View.GONE);
        }else if (cluster.mDateBoundaryWithPrevious || cluster.mClusterWithPrevious == ClusterType.MORE_THAN_HOUR) {
            // Crossed into a new day, or > 1hr lull in conversation
            Date receivedAt = messageDao.getMessagesTamp();
            if (receivedAt == null) receivedAt = new Date();
            String timeBarDayText = AngelCarUtils.formatTimeDay(viewHolder.mCell.getContext(), receivedAt);
            viewHolder.mTimeGroupDay.setText(timeBarDayText);
            String timeBarTimeText = mTimeFormat.format(receivedAt.getTime());
            viewHolder.mTimeGroupTime.setText(" " + timeBarTimeText);
            viewHolder.mTimeGroup.setVisibility(View.VISIBLE);
            viewHolder.mClusterSpaceGap.setVisibility(View.GONE);
        } else if (cluster.mClusterWithPrevious == ClusterType.LESS_THAN_MINUTE) {
            // Same sender with < 1m gap
            viewHolder.mClusterSpaceGap.setVisibility(View.GONE);
            viewHolder.mTimeGroup.setVisibility(View.GONE);
        } else if (cluster.mClusterWithPrevious == ClusterType.NEW_SENDER || cluster.mClusterWithPrevious == ClusterType.LESS_THAN_HOUR) {
            // New sender or > 1m gap
            viewHolder.mClusterSpaceGap.setVisibility(View.VISIBLE);
            viewHolder.mTimeGroup.setVisibility(View.GONE);
        }

        //

        if (getItemViewType(position) == TYPE_ME){
//            viewHolder.mReceipt.setVisibility(View.GONE);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor("#FFB13D"));
            gradientDrawable.setCornerRadius(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                viewHolder.mCell.setBackground(gradientDrawable);
            }else {
                viewHolder.mCell.setBackgroundDrawable(gradientDrawable);
            }

            viewHolder.mCell.setText(messageDao.getMessageText());

        }else {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor("#696969"));
            gradientDrawable.setCornerRadius(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                viewHolder.mCell.setBackground(gradientDrawable);
            }else {
                viewHolder.mCell.setBackgroundDrawable(gradientDrawable);
            }

            viewHolder.mCell.setText(messageDao.getMessageText());
//            Glide.with(mContext).load(messageDao.getUserProfileImage()).into(viewHolder.mAvatar);
        }


    }

    @Override
    public int getItemCount() {
        if (mMessageDao == null) return 0;
        if (mMessageDao.getListMessage() == null) return 0;
        return mMessageDao.getListMessage().size();
    }

    private int userTypeView(String messageBy, String daoMessageBy) {
//        if (messageBy.equals(daoMessageBy)) return TYPE_ME;
//        else return TYPE_THEM;
        return messageBy.equals(daoMessageBy) ? TYPE_ME : TYPE_THEM;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    //==============================================================================================
    // การจัดกลุ่ม
    //==============================================================================================

    private Cluster getClustering(MessageCollectionDao mMessageDao, int position) {
        Cluster result = mClusterCache.get(mMessageDao.getListMessage().get(position).getMessageId());
        if (result == null) {
            result = new Cluster();
            mClusterCache.put(mMessageDao.getListMessage().get(position).getMessageId(), result);
        }

        int previousPosition = position - 1;
        MessageDao previousMessage = (previousPosition >= 0) ? mMessageDao.getListMessage().get(position) : null;
        if (previousMessage != null) {

            result.mDateBoundaryWithPrevious = isDateBoundary(mMessageDao.getListMessage().get(previousPosition).getMessagesTamp(),
                    mMessageDao.getListMessage().get(position).getMessagesTamp());
            result.mClusterWithPrevious = ClusterType.fromMessages(mMessageDao.getListMessage().get(previousPosition),
                    mMessageDao.getListMessage().get(position));

            Cluster previousCluster = mClusterCache.get(mMessageDao.getListMessage().get(position).getMessageId());
            if (previousCluster == null) {
                previousCluster = new Cluster();
                mClusterCache.put(mMessageDao.getListMessage().get(position).getMessageId(), previousCluster);
            } else {
                // does the previous need to change its clustering?
                if ((previousCluster.mClusterWithNext != result.mClusterWithPrevious) ||
                        (previousCluster.mDateBoundaryWithNext != result.mDateBoundaryWithPrevious)) {
//                    requestUpdate(previousMessage, previousPosition);
                    Log.i("AngelCar", "getClustering: Update 1");
                }
            }
            previousCluster.mClusterWithNext = result.mClusterWithPrevious;
            previousCluster.mDateBoundaryWithNext = result.mDateBoundaryWithPrevious;
        }

        int nextPosition = position + 1;
        MessageDao nextMessage = (nextPosition < getItemCount()) ? mMessageDao.getListMessage().get(nextPosition) : null;
        if (nextMessage != null) {
            result.mDateBoundaryWithNext = isDateBoundary(mMessageDao.getListMessage().get(position).getMessagesTamp(),
                    mMessageDao.getListMessage().get(nextPosition).getMessagesTamp());
            result.mClusterWithNext = ClusterType.fromMessages(mMessageDao.getListMessage().get(position),
                    mMessageDao.getListMessage().get(nextPosition));

            Cluster nextCluster = mClusterCache.get(mMessageDao.getListMessage().get(position).getMessageId());
            if (nextCluster == null) {
                nextCluster = new Cluster();
                mClusterCache.put(mMessageDao.getListMessage().get(position).getMessageId(), nextCluster);
            } else {
                // does the next need to change its clustering?
                if ((nextCluster.mClusterWithPrevious != result.mClusterWithNext) ||
                        (nextCluster.mDateBoundaryWithPrevious != result.mDateBoundaryWithNext)) {
//                    requestUpdate(nextMessage, nextPosition);
                    Log.i("AngelCar", "getClustering: Update 2");
                }
            }
            nextCluster.mClusterWithPrevious = result.mClusterWithNext;
            nextCluster.mDateBoundaryWithPrevious = result.mDateBoundaryWithNext;
        }

        return result;
    }

    private static boolean isDateBoundary(Date d1, Date d2) {
        if (d1 == null || d2 == null) return false;
        return (d1.getYear() != d2.getYear()) || (d1.getMonth() != d2.getMonth()) || (d1.getDay() != d2.getDay());
    }

//    private void requestUpdate(final MessageDao message, final int lastPosition) {
//        mUiThreadHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                notifyItemChanged(getPosition(message, lastPosition));
//            }
//        });
//    }

    private static class Cluster {
        public boolean mDateBoundaryWithPrevious;
        public ClusterType mClusterWithPrevious;

        public boolean mDateBoundaryWithNext;
        public ClusterType mClusterWithNext;
    }

    static class CellViewHolder extends ViewHolder{
        public final static int RESOURCE_ID_ME = R.layout.angelcar_message_item_me;
        public final static int RESOURCE_ID_THEM = R.layout.angelcer_message_item_them;

        // View cache
        protected TextView mUserName;
        protected View mTimeGroup;
        protected TextView mTimeGroupDay;
        protected TextView mTimeGroupTime;
        protected Space mClusterSpaceGap;
        protected ImageView mAvatar;
        protected TextView mCell;
        protected TextView mReceipt;

        public CellViewHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.sender);
            mTimeGroup = itemView.findViewById(R.id.time_group);
            mTimeGroupDay = (TextView) itemView.findViewById(R.id.time_group_day);
            mTimeGroupTime = (TextView) itemView.findViewById(R.id.time_group_time);
            mClusterSpaceGap = (Space) itemView.findViewById(R.id.cluster_space);
            mCell = (TextView) itemView.findViewById(R.id.cell);
            mReceipt = (TextView) itemView.findViewById(R.id.receipt);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }

    private enum ClusterType {
        NEW_SENDER,
        LESS_THAN_MINUTE,
        LESS_THAN_HOUR,
        MORE_THAN_HOUR;

        private static final long MILLIS_MINUTE = 60 * 1000;
        private static final long MILLIS_HOUR = 60 * MILLIS_MINUTE;

        public static ClusterType fromMessages(MessageDao older, MessageDao newer) {
            // Different users?
//            if (!older.getSender().equals(newer.getSender())) return NEW_SENDER;

            // Time clustering for same user?
            Date oldReceivedAt = older.getMessagesTamp();
            Date newReceivedAt = newer.getMessagesTamp();
            if (oldReceivedAt == null || newReceivedAt == null) return LESS_THAN_MINUTE;
            long delta = Math.abs(newReceivedAt.getTime() - oldReceivedAt.getTime());
            if (delta <= MILLIS_MINUTE) return LESS_THAN_MINUTE;
            if (delta <= MILLIS_HOUR) return LESS_THAN_HOUR;
            return MORE_THAN_HOUR;
        }
    }
}
