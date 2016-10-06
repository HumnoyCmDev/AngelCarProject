package com.dollarandtrump.angelcar.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.fragment.FeedPostFragment;
import com.dollarandtrump.angelcar.model.InfoCarModel;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**-Created by Kotlin-**/
public class FilterPriceDialog extends DialogFragment {
    private static final String TAG = FilterPriceDialog.class.getSimpleName();

    @Bind(R.id.edit_text_price_start) EditText edPriceStart;
    @Bind(R.id.edit_text_price_end) EditText edPriceEnd;

    @Bind(R.id.btnClose) Button buttonClose;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_price,container,false);
        initInstance(view,savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    private void initInstance(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this,view);

        Observable<Boolean> rxChangPriceStart = RxTextView.textChangeEvents(edPriceStart).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return edPriceStart.getText().length() > 0;
            }
        });
        Observable<Boolean> rxChangPriceEnd = RxTextView.textChangeEvents(edPriceEnd).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return edPriceStart.getText().length() > 0;
            }
        });

        Observable.combineLatest(rxChangPriceStart, rxChangPriceEnd, new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean, Boolean aBoolean2) {
                return aBoolean && aBoolean2;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                buttonClose.setEnabled(aBoolean);
            }
        });


    }

    @OnClick(R.id.btnClose)
    public void onClose(){
        String priceStart = edPriceStart.getText().toString();
        String priceEnd = edPriceEnd.getText().toString();
        Intent intent = getActivity().getIntent();
        intent.putExtra("pricestart", priceStart);
        intent.putExtra("priceend", priceEnd);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
