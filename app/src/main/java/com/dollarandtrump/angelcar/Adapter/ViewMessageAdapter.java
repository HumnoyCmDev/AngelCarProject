package com.dollarandtrump.angelcar.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.configuration.GlideTargetBitmap;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.interfaces.OnItemChatClickListener;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.dollarandtrump.angelcar.utils.Log;
import com.dollarandtrump.angelcar.utils.ReduceSizeImage;
import com.dollarandtrump.angelcar.view.ItemCarDetails;
import com.hndev.library.view.Transformtion.ScalingUtilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import rx.schedulers.Schedulers;

public class ViewMessageAdapter extends RecyclerView.Adapter<ViewMessageAdapter.ViewHolder> {

//    public interface OnItemChatClickListener {
//        public void onClickImageChat(String imageUrl,int position);
//    }

    private final static int TYPE_THEM = 0;
    private final static int TYPE_ME = 1;
    private final static int TYPE_HEADER = 2;
    private final String user;
    private MessageCollectionDao mMessageDao;
    private String mMessageBy;

    private PictureCollectionDao pictureDao ;
    private PostCarDao postCarDao;
    boolean isFollow = false;

    int lastPositionCellMe = 0;

    ItemCarDetails.OnClickItemHeaderChatListener onClickItemHeaderChatListener;
    private OnItemChatClickListener onItemChatClickListener;
    private static Context mContext;
    // Dates and Clustering
    private final Map<Integer, Cluster> mClusterCache = new HashMap<>();
    protected final Handler mUiThreadHandler;
    private final DateFormat mTimeFormat;

    public ViewMessageAdapter(Context context, String mMessageBy) {
        this.mContext = context;
        this.mMessageBy = mMessageBy;
        mUiThreadHandler = new Handler(Looper.getMainLooper());
//        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
        user = Registration.getInstance().getUserId();
    }

    public void setMessageBy(String messageBy) {
        this.mMessageBy = messageBy;
    }

    public void setMessageDao(MessageCollectionDao mMessageDao) {
        this.mMessageDao = mMessageDao;

        if (mMessageDao != null){
            lastPositionCellMe = findLastItemCellMe();
        }
    }

    public void setImageCar(PictureCollectionDao pictureDao){
        this.pictureDao = pictureDao;
    }

    public void setCarInfo(PostCarDao postCarDao){
        this.postCarDao = postCarDao;
    }

    public void setOnClickItemBannerListener(ItemCarDetails.OnClickItemHeaderChatListener onClickItemHeaderChatListener) {
        this.onClickItemHeaderChatListener = onClickItemHeaderChatListener;
    }

    public void setOnItemChatClickListener(OnItemChatClickListener onItemChatClickListener) {
        this.onItemChatClickListener = onItemChatClickListener;
    }

    public void setFollow(boolean isFollow){
        this.isFollow = isFollow;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }else {
            MessageDao item = mMessageDao.getListMessage().get(position - 1);
            return userTypeView(mMessageBy, item.getMessageBy());
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageDao == null) return 1;
        if (mMessageDao.getListMessage() == null) return 1;
        return mMessageDao.getListMessage().size()+1;
    }

    private int findLastItemCellMe(){
        int messageId = 0;
        for(MessageDao message : mMessageDao.getListMessage()){
            if (mMessageBy.equals(message.getMessageBy())){
                messageId = message.getMessageId();
            }
        }
        return messageId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case TYPE_HEADER :
                ItemCarDetails itemCarDetails = new ItemCarDetails(parent.getContext());
                return new ItemCarDetailViewHolder(itemCarDetails);
            case TYPE_ME :
                View viewMe = LayoutInflater.from(parent.getContext()).inflate(CellViewHolder.RESOURCE_ID_ME,parent,false);
                return new CellViewHolder(viewMe);
            default:
                View viewThem = LayoutInflater.from(parent.getContext()).inflate(CellViewHolder.RESOURCE_ID_THEM,parent,false);
                return new CellViewHolder(viewThem);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            if (holder.getItemViewType() == TYPE_HEADER){
                ItemCarDetailViewHolder viewHolder = (ItemCarDetailViewHolder) holder;
                viewHolder.itemCarDetails.bindCellViewHolder(pictureDao,postCarDao);
                viewHolder.itemCarDetails.setFollow(isFollow);
                if (onClickItemHeaderChatListener != null) {
                    viewHolder.itemCarDetails.setOnClickItemBannerListener(onClickItemHeaderChatListener);
                }
                if (getItemCount() > 2 ){
//                    Log.d("> 2 |"+getItemCount());
                    viewHolder.itemCarDetails.setTextLoadMessageBeforeVisibility(View.INVISIBLE);
                }
               if (getItemCount() > 20){
//                   Log.d("> 21 |"+getItemCount());
                    viewHolder.itemCarDetails.setTextLoadMessageBeforeVisibility(View.VISIBLE);
                    viewHolder.itemCarDetails.setTextLoadMessageBefore("โหลดข้อความเก่ากว่า");
                }

            }else{
                bindCellViewHolder((CellViewHolder) holder, position-1);
            }
    }

    public void bindCellViewHolder(CellViewHolder viewHolder, int position) {
         MessageDao message  = mMessageDao.getListMessage().get(position);
        boolean oneOnOne = getParticipants(mMessageDao); // User == 2 = true ; > 2 = false;
        //Clustering and dates
        Cluster cluster = getClustering(mMessageDao,position);
        if (cluster.mClusterWithPrevious == null){
            viewHolder.mClusterSpaceGap.setVisibility(View.GONE);
            viewHolder.mTimeGroup.setVisibility(View.GONE);
        }else if (cluster.mDateBoundaryWithPrevious || cluster.mClusterWithPrevious == ClusterType.MORE_THAN_HOUR) {
            // Crossed into a new day, or > 1hr lull in conversation
            Date receivedAt = message.getMessageStamp();
            if (receivedAt == null) receivedAt = new Date();
            String timeBarDayText = AngelCarUtils.formatTimeDay(mContext, receivedAt);
            viewHolder.mTimeGroupDay.setText(timeBarDayText);
            String timeBarTimeText = mTimeFormat.format(receivedAt.getTime());
            viewHolder.mTimeGroupTime.setText(" " +timeBarTimeText);
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

        viewHolder.mCell.removeAllViews();

        if (viewHolder.getItemViewType() == TYPE_ME){ /*CellMe*/

            if (message.getMessageId() == findLastItemCellMe()) {
                int read = message.getMessageStatus(); // 1 -> read message
                viewHolder.mReceipt.setVisibility(read == 1 ? View.VISIBLE : View.GONE);
                viewHolder.mReceipt.setText(R.string.read);

            } else {
                viewHolder.mReceipt.setVisibility(View.GONE);
            }


            if (AngelCarUtils.isMessageText(message.getMessageText())){ // text
                TextView textMessage = new TextView(mContext);
                textMessage.setBackgroundResource(R.drawable.message_item_call_me);
                textMessage.setTextColor(Color.BLACK);
                textMessage.setText(message.getMessageText());
                viewHolder.mCell.addView(textMessage);
            }else {

                ImageView img = new ImageView(mContext);
                Picasso.with(mContext)
                        .load(AngelCarUtils.subUrlMessage(message.getMessageText()))
                        .transform(new PictureReSize())
                        .into(img);
                viewHolder.mCell.addView(img);

                /**Sample Glide**/
//                final ImageView img = new ImageView(mContext);
//                Glide.with(mContext)
//                        .load(AngelCarUtils.subUrlMessage(message.getMessageText()))
//                        .asBitmap()
//                        .transform(new RoundedCornersTransformation(mContext,20,0))
//                        .into(new GlideTargetBitmap(img));
//                viewHolder.mCell.addView(img);


            }

            /**Sent view alpha*/
            if (message.isSent()){ // Success
                viewHolder.mCell.setAlpha(1.0f);
            }else {
                viewHolder.mCell.setAlpha(0.5f);
            }

        }else { /**Cell Them*/

            if(message.getMessageStatus() == 0){ /** Update Read Message*/
                HttpManager.getInstance().getService()
                        .observableReadMessage(String.valueOf(message.getMessageId()))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe();
            }

            if (AngelCarUtils.isMessageText(message.getMessageText())){ // text

                TextView textMessage = new TextView(mContext);
                textMessage.setBackgroundResource(R.drawable.message_item_call_them);
                textMessage.setTextColor(Color.WHITE);
                textMessage.setText(message.getMessageText());
                viewHolder.mCell.addView(textMessage);
            }else {

                ImageView imageView = new ImageView(mContext);
                Picasso.with(mContext)
                        .load(AngelCarUtils.subUrlMessage(message.getMessageText()))
                        .transform(new PictureReSize())
                        .into(imageView);
                viewHolder.mCell.addView(imageView);

                /**Sample Glide**/
//                final ImageView img = new ImageView(mContext);
//                Glide.with(mContext)
//                        .load(AngelCarUtils.subUrlMessage(message.getMessageText()))
//                        .asBitmap()
//                        .transform(new RoundedCornersTransformation(mContext,20,0))
//                        .into(new GlideTargetBitmap(img));
//                viewHolder.mCell.addView(img);

            }
            /** Sender name, only for first message in cluster // User > 2 */
            if (!oneOnOne && (cluster.mClusterWithPrevious == null || cluster.mClusterWithPrevious == ClusterType.NEW_SENDER)) {
                MessageDao msg = mMessageDao.getListMessage().get(position);
                if (msg != null) {
                    viewHolder.mUserName.setText(msg.getDisplayName());
                } else {
                    MessageDao nameProvider = mMessageDao.getListMessage().get(position+1);
                    viewHolder.mUserName.setText(nameProvider != null ? nameProvider.getDisplayName() : "Unknown");
                }
                viewHolder.mUserName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mUserName.setVisibility(View.GONE);
            }

            // Avatars
            /**if (oneOnOne) { // TODO เช็ค user 1-1
                *//*Not in one-on-one conversations*//*
                viewHolder.mAvatar.setVisibility(View.GONE);
            } else */
            if (cluster.mClusterWithNext == null || cluster.mClusterWithNext != ClusterType.LESS_THAN_MINUTE) {
                /** Last message in cluster*/
                viewHolder.mAvatar.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(postCarDao.getFullPathShopLogo())//TODO- imageดึงมาจาก PostCar
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(viewHolder.mAvatar);
            } else {
                /** Invisible for clustered messages to preserve proper spacing*/
                viewHolder.mAvatar.setVisibility(View.INVISIBLE);
            }

        }
    }

    private int userTypeView(String messageBy, String daoMessageBy) {
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
        MessageDao messageDao =  mMessageDao.getListMessage().get(position);
        Cluster result = mClusterCache.get(mMessageDao.getListMessage().get(position).getMessageId());
        if (result == null) {
            result = new Cluster();
            mClusterCache.put(mMessageDao.getListMessage().get(position).getMessageId(), result);
        }

        int previousPosition = position - 1;
        MessageDao previousMessage = (previousPosition >= 0) ? mMessageDao.getListMessage().get(previousPosition) : null;
        if (previousMessage != null) {
            result.mDateBoundaryWithPrevious = isDateBoundary(previousMessage.getMessageStamp(), messageDao.getMessageStamp());
            result.mClusterWithPrevious = ClusterType.fromMessages(previousMessage, messageDao);

            Cluster previousCluster = mClusterCache.get(previousMessage.getMessageId());
            if (previousCluster == null) {
                previousCluster = new Cluster();
                mClusterCache.put(previousMessage.getMessageId(), previousCluster);
            } else {
                // does the previous need to change its clustering?
                if ((previousCluster.mClusterWithNext != result.mClusterWithPrevious) ||
                        (previousCluster.mDateBoundaryWithNext != result.mDateBoundaryWithPrevious)) {
//                    requestUpdate(previousMessage, previousPosition);
//                    notifyItemChanged(previousPosition,previousMessage);
                }
            }
            previousCluster.mClusterWithNext = result.mClusterWithPrevious;
            previousCluster.mDateBoundaryWithNext = result.mDateBoundaryWithPrevious;
        }

        int nextPosition = position + 1;
        MessageDao nextMessage = (nextPosition < getItemCount()-1) ? mMessageDao.getListMessage().get(nextPosition) : null;
        if (nextMessage != null) {
            result.mDateBoundaryWithNext = isDateBoundary(messageDao.getMessageStamp(),nextMessage.getMessageStamp());
            result.mClusterWithNext = ClusterType.fromMessages(messageDao, nextMessage);

            Cluster nextCluster = mClusterCache.get(nextMessage.getMessageId());
            if (nextCluster == null) {
                nextCluster = new Cluster();
                mClusterCache.put(nextMessage.getMessageId(), nextCluster);
            } else {
                // does the next need to change its clustering?
                if ((nextCluster.mClusterWithPrevious != result.mClusterWithNext) ||
                        (nextCluster.mDateBoundaryWithPrevious != result.mDateBoundaryWithNext)) {
//                    requestUpdate(nextMessage, nextPosition);
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

    private void requestUpdate(final MessageDao messageDao, final int lastPosition) {
        mUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(lastPosition,messageDao);
            }
        });
    }

    private static class Cluster {
        public boolean mDateBoundaryWithPrevious;
        public ClusterType mClusterWithPrevious;

        public boolean mDateBoundaryWithNext;
        public ClusterType mClusterWithNext;
    }

    static class ItemCarDetailViewHolder extends ViewHolder{
        ItemCarDetails itemCarDetails;
        public ItemCarDetailViewHolder(View itemView) {
            super(itemView);
            itemCarDetails = (ItemCarDetails) itemView;


        }
    }

    class CellViewHolder extends ViewHolder {
        public final static int RESOURCE_ID_ME = R.layout.angelcar_message_item_me;
        public final static int RESOURCE_ID_THEM = R.layout.angelcar_message_item_them;


        // View cache
        protected TextView mUserName;
        protected View mTimeGroup;
        protected TextView mTimeGroupDay;
        protected TextView mTimeGroupTime;
        protected Space mClusterSpaceGap;
        protected ImageView mAvatar;
        protected TextView mReceipt;
        protected FrameLayout mCell;

        public CellViewHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.sender);
            mTimeGroup = itemView.findViewById(R.id.time_group);
            mTimeGroupDay = (TextView) itemView.findViewById(R.id.time_group_day);
            mTimeGroupTime = (TextView) itemView.findViewById(R.id.time_group_time);
            mClusterSpaceGap = (Space) itemView.findViewById(R.id.cluster_space);
            mCell = (FrameLayout) itemView.findViewById(R.id.cell);
            mReceipt = (TextView) itemView.findViewById(R.id.receipt);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageDao message = mMessageDao.getListMessage().get(getAdapterPosition() - 1);
                    if (message != null) {
                        if (getAdapterPosition() - 1 > 1) {
                            if (message.getMessageText().contains("<img>") &&
                                    message.getMessageText().contains("</img>")) {
                                if (onItemChatClickListener != null){
                                    String url = message.getMessageText()
                                            .substring("<img>".length(),
                                                    message.getMessageText().lastIndexOf("</img>"));
                                    onItemChatClickListener.onClickImageChat(url,
                                            getAdapterPosition());
                                }
                            }
                        }
                    }
                }
            });

            if (mAvatar != null) {
                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickItemHeaderChatListener != null) {
                            onClickItemHeaderChatListener.onItemClickViewShop(postCarDao);
                        }
                    }
                });
            }
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
            if (!older.getMessageBy().equals(newer.getMessageBy())) return NEW_SENDER;

            // Time clustering for same user?
            Date oldReceivedAt = older.getMessageStamp();
            Date newReceivedAt = newer.getMessageStamp();
            if (oldReceivedAt == null || newReceivedAt == null) return LESS_THAN_MINUTE;
            long delta = Math.abs(newReceivedAt.getTime() - oldReceivedAt.getTime());
            if (delta <= MILLIS_MINUTE) return LESS_THAN_MINUTE;
            if (delta <= MILLIS_HOUR) return LESS_THAN_HOUR;
            return MORE_THAN_HOUR;
        }
    }

    boolean getParticipants(MessageCollectionDao dao) {
        for (MessageDao d : dao.getListMessage())
            if (d.getMessageBy().equals("officer"))
                return false;
        return true;
    }

    private class PictureReSize implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            ReduceSizeImage.Size newSize = ReduceSizeImage.solution(ReduceSizeImage.SIZE_SMALL, source.getWidth(), source.getHeight());
            Bitmap scaledBitmap;
            scaledBitmap = ScalingUtilities.createScaledBitmap(source, newSize.getWidth(), newSize.getHeight(), ScalingUtilities.ScalingLogic.CROP);
            source.recycle();
            return scaledBitmap;
        }


        @Override
        public String key() {
            return PictureReSize.class.getSimpleName()+".scaledBitmap";
        }
    }
}
