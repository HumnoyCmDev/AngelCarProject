package com.dollarandtrump.angelcar.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.activity.SearchActivity;
import com.dollarandtrump.angelcar.dao.CarBrandDao;
import com.dollarandtrump.angelcar.dao.CarSubDao;
import com.dollarandtrump.angelcar.dialog.FilterBrandDialog;
import com.dollarandtrump.angelcar.dialog.FilterPriceDialog;
import com.dollarandtrump.angelcar.dialog.FilterSubDetailDialog;
import com.dollarandtrump.angelcar.dialog.FilterSubDialog;
import com.dollarandtrump.angelcar.dialog.YearDialog;
import com.dollarandtrump.angelcar.model.InfoCarModel;

import org.parceler.Parcels;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressWarnings("unused")
public class SearchFragment extends Fragment {
    private final int REQUEST_CODE_BRAND = 1;
    private final int REQUEST_CODE_SUB = 2;
    private final int REQUEST_CODE_SUB_DETAIL = 3;
    private final int REQUEST_CODE_YEAR = 4;
    private final int REQUEST_CODE_PRICE = 5;

    private final String ARG_BRAND = "brand";
    private final String ARG_SUB = "sub";
    private final String ARG_SUB_DETAIL = "subDetail";

    @Bind(R.id.text_brand) TextView tvBrand;
    @Bind(R.id.text_sub) TextView tvSub;
    @Bind(R.id.text_sub_detail) TextView tvSubDetail;
    @Bind(R.id.text_year) TextView tvYear;
    @Bind(R.id.text_price) TextView tvPrice;
    @Bind(R.id.group_radio_gear) RadioGroup radioGearAll;

    private InfoCarModel mInfoCarModel;
    private DecimalFormat formatter;
    public SearchFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        mInfoCarModel = new InfoCarModel();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);
        setHasOptionsMenu(true);

        formatter = new DecimalFormat("#,###");

    }

    @OnClick({R.id.group_filter_brand,R.id.group_filter_sub,R.id.group_filter_sub_detail,
            R.id.group_filter_year,R.id.group_filter_price,R.id.group_filter_gear})
    public void onClickFilter(View view){
        switch (view.getId()){
            case R.id.group_filter_brand:
                showDialog(new FilterBrandDialog(), REQUEST_CODE_BRAND);
                break;
            case R.id.group_filter_sub:
                if (isNullFilter(mInfoCarModel,0)) {
                    showDialog(FilterSubDialog.newInstance(mInfoCarModel), REQUEST_CODE_SUB);
                }
                break;
            case R.id.group_filter_sub_detail:
                if (isNullFilter(mInfoCarModel,1)) {
                    showDialog(FilterSubDetailDialog
                            .newInstance(mInfoCarModel), REQUEST_CODE_SUB_DETAIL);
                }
                break;
            case R.id.group_filter_year:
                showDialog(new YearDialog(),REQUEST_CODE_YEAR);
                break;
            case R.id.group_filter_price:
                showDialog(new FilterPriceDialog(),REQUEST_CODE_PRICE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_CODE_BRAND:
                    CarBrandDao model = Parcels.unwrap(data.getParcelableExtra(ARG_BRAND));
                    mInfoCarModel.setResIdLogo(data.getIntExtra("logo",0));
                    mInfoCarModel.setBrandDao(model);
                    tvBrand.setText(model.getBrandName());
                    break;
                case REQUEST_CODE_SUB:
                    CarSubDao modelSub = Parcels.unwrap(data.getParcelableExtra(ARG_SUB));
                    mInfoCarModel.setSubDao(modelSub);
                    tvSub.setText(modelSub.getSubName());
                    break;
                case REQUEST_CODE_SUB_DETAIL:
                    CarSubDao modelSubDetail = Parcels.unwrap(data.getParcelableExtra(ARG_SUB_DETAIL));
                    mInfoCarModel.setSubDetailDao(modelSubDetail);
                    tvSubDetail.setText(modelSubDetail.getSubName());
                    break;
                case REQUEST_CODE_YEAR:
                    int resultYear = data.getIntExtra("TAG_YEAR",2016);
                    mInfoCarModel.setYear(resultYear);
                    tvYear.setText(String.valueOf(resultYear));
                    break;
                case REQUEST_CODE_PRICE:
                    String priceStart = data.getStringExtra("pricestart");
                    String priceEnd = data.getStringExtra("priceend");

                    double dPriceStart = Double.parseDouble(priceStart);
                    double dPriceEnd = Double.parseDouble(priceEnd);

                    double priceMax = Math.max(dPriceStart, dPriceEnd);
                    double priceMin = Math.min(dPriceStart, dPriceEnd);
                    mInfoCarModel.setPriceStart(priceMin+"");
                    mInfoCarModel.setPriceEnd(priceMax+"");

                    tvPrice.setText(formatter.format(priceMin)+" ถึง "+formatter.format(priceMax)+" บาท");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.radioGearAll,R.id.radioGearAt,R.id.radioGearMt})
    public void onSelectorGear(View view){
        boolean checked = ((RadioButton) view).isChecked();
        if (view.getId() == R.id.radioGearMt){
            mInfoCarModel.setGear(checked ? "1" : "NULL");
        }else if (view.getId() == R.id.radioGearAt){
            mInfoCarModel.setGear(checked ? "2" : "NULL");
        }else {// all
            mInfoCarModel.setGear("NULL");
        }
    }

    @OnClick(R.id.button_search)
    public void onSearch(){
            SearchActivity searchActivity = (SearchActivity) getActivity();
            searchActivity.addFragmentResult(mInfoCarModel);
            clearText(tvBrand,tvSub,tvSubDetail,tvPrice,tvYear);
    }


    private void showDialog(DialogFragment dialogFragment, int requestCode){
        @SuppressLint("CommitTransaction")
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(dialogFragment.getClass().getSimpleName());
        if (fragment != null) ft.remove(fragment);
        ft.addToBackStack(null);
        dialogFragment.setTargetFragment(this,requestCode);
        dialogFragment.show(getFragmentManager(),dialogFragment.getClass().getSimpleName());
    }

    private boolean isNullFilter(InfoCarModel infoCarModel, int mode){
        if (mode == 0){
            if (infoCarModel.getBrandDao() != null && !infoCarModel.getBrandDao().getBrandName().isEmpty())
                return true;
        }else {
            if (infoCarModel.getBrandDao() != null && infoCarModel.getSubDao() != null &&
                    !infoCarModel.getBrandDao().getBrandName().isEmpty() &&
                    !infoCarModel.getSubDao().getSubName().isEmpty())
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear){
            clearText(tvBrand,tvSub,tvSubDetail,tvPrice,tvYear);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearText(TextView... textViews){
        if (mInfoCarModel != null) {
            mInfoCarModel = new InfoCarModel();
        }
        radioGearAll.check(R.id.radioGearAll);
        for (TextView view : textViews){
            view.setText(R.string.filter_all);
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
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
