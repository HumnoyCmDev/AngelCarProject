package com.dollarandtrump.angelcar.dialog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.manager.bus.MainThreadBus;
import com.dollarandtrump.angelcar.model.Gallery;
import com.dollarandtrump.angelcar.model.ImageModel;
import com.dollarandtrump.angelcar.rx_picker.RxImagePicker;
import com.dollarandtrump.angelcar.rx_picker.Sources;
import com.dollarandtrump.angelcar.view.RecyclerGridAutoFit;
import com.squareup.otto.Produce;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;


public class EvidenceDialog extends DialogFragment {

    @Bind(R.id.recycler_evidence) RecyclerGridAutoFit mListImage;

    private EvidenceAdapter adapter;
    private Gallery mGallery;

    private int index = 0;
    private int maxImage = 1;

    public static EvidenceDialog newInstance(int maxImage,Gallery imageEvidence) {
        Bundle args = new Bundle();
        EvidenceDialog fragment = new EvidenceDialog();
        args.putInt("max",maxImage);
        args.putParcelable("image_evidence", Parcels.wrap(imageEvidence));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        maxImage = getArguments().getInt("max");
        mGallery = Parcels.unwrap(getArguments().getParcelable("image_evidence"));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_evidence, container, false);
        initInstance(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    private void initInstance(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        adapter = new EvidenceAdapter();
        mListImage.setAdapter(adapter);
        adapter.setListPicture(mGallery);
        adapter.setOnItemClickListener(new EvidenceAdapter.OnItemClickListener() {
            @Override
            public void onItemClickSelect() {
                if (mGallery != null && mGallery.getListGallery() != null){
                    if (mGallery.getListGallery().size() > maxImage)
                        return;
                }
                rxImagePicker();
            }

            @Override
            public void onItemClickDelete(int position) {
                mGallery.getListGallery().remove(position);
                adapter.notifyDataSetChanged();
            }
        });


    }

    private void rxImagePicker() {
        RxImagePicker.with(getContext()).requestImage(Sources.GALLERY)
                .subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        mGallery.setListGallery(new ImageModel(uri,String.valueOf(index)));
                        index++;
                        adapter.setListPicture(mGallery);
                        adapter.notifyDataSetChanged();
                        MainThreadBus.getInstance().post(onProduceImageEvidence());
                    }
                });
    }

    @OnClick(R.id.button_close)
    public void onCloseDialog(){
        dismiss();
    }

    @Produce
    public Gallery onProduceImageEvidence(){
        return mGallery;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainThreadBus.getInstance().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /******************
     * Inner class zone*
     ******************/
    public static class EvidenceAdapter extends RecyclerView.Adapter<EvidenceAdapter.ViewHolder> {
        Gallery mGallery;
        OnItemClickListener onItemClickListener;
        Context mContext;

        public void setListPicture( Gallery gallery) {
            this.mGallery = gallery;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public EvidenceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_gallery, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(EvidenceAdapter.ViewHolder holder, int position) {
            if (getItemViewType(position) == 1) {
                Glide.with(mContext)
                        .load(mGallery.getListGallery().get(position - 1).getUri())
                        .crossFade()
                        .into(holder.imageGallery);
            }
        }

        @Override
        public int getItemCount() {
            if (mGallery == null) return 1;
            if (mGallery.getListGallery() == null) return 1;

            return mGallery.getListGallery().size() + 1;

        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.imageGallery)
            ImageView imageGallery;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                imageGallery.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {

                    if (getAdapterPosition() == 0)
                        onItemClickListener.onItemClickSelect();
                    else
                        onItemClickListener.onItemClickDelete(getAdapterPosition() - 1);
                }
            }
        }

        public interface OnItemClickListener {
            void onItemClickSelect();

            void onItemClickDelete(int position);
        }
    }

}