package com.dollarandtrump.angelcar.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.TopicCollectionDao;
import com.dollarandtrump.angelcar.dao.TopicDao;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.utils.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private final static int TYPE_THEM = 0;
    private final static int TYPE_ME = 1;
    private TopicCollectionDao mTopicDao;
    private String mUserId;

    private Context mContext;
    // Dates and Clustering
    private final Map<Integer, Cluster> mClusterCache = new HashMap<>();
    private final Handler mUiThreadHandler;
    private final DateFormat mTimeFormat;
    private OnClickItemChatListener onClickItemChatListener;

    public TopicAdapter(Context context, String userId) {
        this.mContext = context;
        this.mUserId = userId;
        mUiThreadHandler = new Handler(Looper.getMainLooper());
//        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    public void setmUserId(String userId) {
        this.mUserId = userId;
    }

    public void setOnClickItemChatListener(OnClickItemChatListener onClickItemChatListener){
        this.onClickItemChatListener = onClickItemChatListener;
    }

    public void setTopicDao(TopicCollectionDao topicDao) {
        this.mTopicDao = topicDao;
    }

    @Override
    public int getItemViewType(int position) {
        TopicDao item = mTopicDao.getTopic().get(position);
        return userTypeView(mUserId,item.getUserId());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case TYPE_ME :
                View viewMe = inflater.inflate(CellViewHolder.RESOURCE_ID_ME,parent,false);
                return new CellViewHolder(viewMe);
            default:
                View viewThem = inflater.inflate(CellViewHolder.RESOURCE_ID_THEM,parent,false);
                return new CellViewHolder(viewThem);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       bindCellViewHolder((CellViewHolder) holder,position);
    }

    public void bindCellViewHolder(CellViewHolder viewHolder, int position) {
         TopicDao topic  = mTopicDao.getTopic().get(position);

        boolean oneOnOne = false; //getParticipants(mMessageDao); // User == 2 = true ; > 2 = false;
        //Clustering and dates
        Cluster cluster = getClustering(mTopicDao,position);
        if (cluster.mClusterWithPrevious == null){
            viewHolder.mClusterSpaceGap.setVisibility(View.GONE);
            viewHolder.mTimeGroup.setVisibility(View.GONE);
        }else if (cluster.mDateBoundaryWithPrevious || cluster.mClusterWithPrevious == ClusterType.MORE_THAN_HOUR) {
            // Crossed into a new day, or > 1hr lull in conversation
            Date receivedAt = topic.getStamp();
            if (receivedAt == null) receivedAt = new Date();
            String timeBarDayText = AngelCarUtils.formatTimeDay(mContext, receivedAt);
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


        if (viewHolder.getItemViewType() == TYPE_ME){

            if (AngelCarUtils.isMessageText(topic.getMessage())) {
                viewHolder.mCallText.setText(topic.getMessage());
            }else {
                viewHolder.mCallText.setText("คุณส่งรูปภาพ 1 รูป");
            }
            viewHolder.mCallText.setBackgroundResource(R.drawable.topic_item_call_me);

        }else {

            if (AngelCarUtils.isMessageText(topic.getMessage())) {
                viewHolder.mCallText.setText(topic.getMessage());
            }else {
                viewHolder.mCallText.setText(topic.getUserId() + "ส่งรูปภาพ 1 รูป");
            }
            viewHolder.mCallText.setTextColor(Color.WHITE);
            viewHolder.mCallText.setBackgroundResource(R.drawable.topic_item_call_them);

            // Sender name, only for first message in cluster // User > 2
            if (!oneOnOne && (cluster.mClusterWithPrevious == null || cluster.mClusterWithPrevious == ClusterType.NEW_SENDER)) {
                TopicDao topicDao = mTopicDao.getTopic().get(position);
                if (topicDao != null) {
                    viewHolder.mUserName.setText("สมาชิก "+topicDao.getUserId());
                } else {
                    TopicDao nameProvider = mTopicDao.getTopic().get(position+1);
                    viewHolder.mUserName.setText(nameProvider != null ? "*สมาชิก " : "Unknown user");
                }
                viewHolder.mUserName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mUserName.setVisibility(View.GONE);
            }

            // Avatars
            if (oneOnOne) { // TODO เช็ค user 1-1
                // Not in one-on-one conversations
                viewHolder.mAvatar.setVisibility(View.GONE);
            } else if (cluster.mClusterWithNext == null || cluster.mClusterWithNext != ClusterType.LESS_THAN_MINUTE) {
                // Last message in cluster
                viewHolder.mAvatar.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(R.drawable.icon_logo)
                        .placeholder(R.drawable.icon_logo)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(viewHolder.mAvatar);
            } else {
                // Invisible for clustered messages to preserve proper spacing
                viewHolder.mAvatar.setVisibility(View.INVISIBLE);
            }

        }

    }



    @Override
    public int getItemCount() {
        if (mTopicDao == null) return 0;
        if (mTopicDao.getTopic() == null) return 0;
        return mTopicDao.getTopic().size();
    }

    private int userTypeView(String userId, String userIdDao) {
        return userId.equals(userIdDao) ? TYPE_ME : TYPE_THEM;
    }

    class ViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener{
        public ViewHolder(View itemView) {
            super(itemView);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            if (Log.isLoggable(Log.DEBUG)) Log.d(""+getAdapterPosition());
//
//            if (onClickItemChatListener != null){
//                onClickItemChatListener.onSelectItem(mTopicDao.getTopic().get(getAdapterPosition()),getAdapterPosition());
//            }
//        }
    }

    //==============================================================================================
    // การจัดกลุ่ม
    //==============================================================================================

    private Cluster getClustering(TopicCollectionDao mTopicDao, int position) {
        TopicDao topic = mTopicDao.getTopic().get(position);
        Cluster result = mClusterCache.get(topic.getId());
        if (result == null) {
            result = new Cluster();
            mClusterCache.put(topic.getId(), result);
        }

        int previousPosition = position - 1;
        TopicDao previousMessage = (previousPosition >= 0) ? mTopicDao.getTopic().get(previousPosition) : null;
        if (previousMessage != null) {
            result.mDateBoundaryWithPrevious = isDateBoundary(previousMessage.getStamp(), topic.getStamp());
            result.mClusterWithPrevious = ClusterType.fromMessages(previousMessage, topic);

            Cluster previousCluster = mClusterCache.get(previousMessage.getId());
            if (previousCluster == null) {
                previousCluster = new Cluster();
                mClusterCache.put(previousMessage.getId(), previousCluster);
            } else {
                // does the previous need to change its clustering?
                if ((previousCluster.mClusterWithNext != result.mClusterWithPrevious) ||
                        (previousCluster.mDateBoundaryWithNext != result.mDateBoundaryWithPrevious)) {
                    requestUpdate(previousMessage, previousPosition);
//                    notifyItemChanged(previousPosition,previousMessage);
                }
            }
            previousCluster.mClusterWithNext = result.mClusterWithPrevious;
            previousCluster.mDateBoundaryWithNext = result.mDateBoundaryWithPrevious;
        }

        int nextPosition = position + 1;
        TopicDao nextMessage = (nextPosition < getItemCount()) ? mTopicDao.getTopic().get(nextPosition) : null;
        if (nextMessage != null) {
            result.mDateBoundaryWithNext = isDateBoundary(topic.getStamp(),nextMessage.getStamp());
            result.mClusterWithNext = ClusterType.fromMessages(topic, nextMessage);

            Cluster nextCluster = mClusterCache.get(nextMessage.getId());
            if (nextCluster == null) {
                nextCluster = new Cluster();
                mClusterCache.put(nextMessage.getId(), nextCluster);
            } else {
                // does the next need to change its clustering?
                if ((nextCluster.mClusterWithPrevious != result.mClusterWithNext) ||
                        (nextCluster.mDateBoundaryWithPrevious != result.mDateBoundaryWithNext)) {
                    requestUpdate(nextMessage, nextPosition);
//                    notifyItemChanged(nextPosition,nextMessage);
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

    private void requestUpdate(final TopicDao topicDao, final int lastPosition) {
        mUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(lastPosition,topicDao);
            }
        });
    }

    private static class Cluster {
        public boolean mDateBoundaryWithPrevious;
        public ClusterType mClusterWithPrevious;

        public boolean mDateBoundaryWithNext;
        public ClusterType mClusterWithNext;
    }

    class CellViewHolder extends ViewHolder {
        public final static int RESOURCE_ID_ME = R.layout.angelcar_topic_item_me;
        public final static int RESOURCE_ID_THEM = R.layout.angelcar_topic_item_them;

        // View cache
        protected TextView mUserName;
        protected View mTimeGroup;
        protected TextView mTimeGroupDay;
        protected TextView mTimeGroupTime;
        protected Space mClusterSpaceGap;
        protected ImageView mAvatar;
//        protected TextView mCell;
//        protected FrameLayout mCell;

        protected TextView mCallText;

        public CellViewHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.sender);
            mTimeGroup = itemView.findViewById(R.id.time_group);
            mTimeGroupDay = (TextView) itemView.findViewById(R.id.time_group_day);
            mTimeGroupTime = (TextView) itemView.findViewById(R.id.time_group_time);
            mClusterSpaceGap = (Space) itemView.findViewById(R.id.cluster_space);
//            mCell = (FrameLayout) itemView.findViewById(R.id.cell);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);

            mCallText = (TextView) itemView.findViewById(R.id.call_text);

            mCallText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItemChatListener != null){
                        onClickItemChatListener.onSelectItem(mTopicDao.getTopic().get(getAdapterPosition()),getAdapterPosition());
                    }
                }
            });
        }
    }

    private enum ClusterType {
        NEW_SENDER,
        LESS_THAN_MINUTE,
        LESS_THAN_HOUR,
        MORE_THAN_HOUR;

        private static final long MILLIS_MINUTE = 60 * 1000;
        private static final long MILLIS_HOUR = 60 * MILLIS_MINUTE;

        public static ClusterType fromMessages(TopicDao older, TopicDao newer) {
            // Different users?
            if (!older.getUserId().equals(newer.getUserId())) return NEW_SENDER;

            // Time clustering for same user?
            Date oldReceivedAt = older.getStamp();
            Date newReceivedAt = newer.getStamp();
            if (oldReceivedAt == null || newReceivedAt == null) return LESS_THAN_MINUTE;
            long delta = Math.abs(newReceivedAt.getTime() - oldReceivedAt.getTime());
            if (delta <= MILLIS_MINUTE) return LESS_THAN_MINUTE;
            if (delta <= MILLIS_HOUR) return LESS_THAN_HOUR;
            return MORE_THAN_HOUR;
        }
    }

    public interface OnClickItemChatListener{
        public void onSelectItem(TopicDao topicDao, int position);
    }
}
