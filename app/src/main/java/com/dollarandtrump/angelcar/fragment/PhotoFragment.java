package com.dollarandtrump.angelcar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.PictureDao;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;


/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย nuuneoi Android Developer
 * ลงวันที่ 11/16/2014. เวลา 11:42
 ***************************************/
@Deprecated
public class PhotoFragment extends Fragment {

    ImageView photo;
    PictureDao gao;
    public PhotoFragment() {
        super();
    }

    public static PhotoFragment newInstance(PictureDao gao) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putParcelable("PictureDao", Parcels.wrap(gao));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        gao = Parcels.unwrap(getArguments().getParcelable("PictureDao"));


    }

    private void initInstances(View rootView, Bundle savedInstanceState) {
       photo = (ImageView) rootView.findViewById(R.id.detail_photo);
       if (savedInstanceState == null){
//           String urlImage = gao.getCarImagePath()
//                   .replace("chatcarimage","thumbnailcarimages");
           Picasso.with(getContext())
                   .load(gao.getCarImageThumbnailPath())
                   .placeholder(R.drawable.loading)
                   .into(photo);
       }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
