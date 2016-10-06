package com.dollarandtrump.angelcar.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dollarandtrump.angelcar.R;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.manager.Registration;
import com.dollarandtrump.angelcar.manager.http.HttpManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


@SuppressWarnings("unused")
public class SingInFragment extends Fragment {

    @Bind(R.id.edit_text_shop_number) EditText mShopNumber;
    @Bind(R.id.edit_text_password) EditText mPassword;
    @Bind(R.id.button_signin) Button buttonSignIn;

    private String token;

    public SingInFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SingInFragment newInstance() {
        SingInFragment fragment = new SingInFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this,rootView);

        token = FirebaseInstanceId.getInstance().getToken();//TODO Token Registration.getInstance().getToken();
        if (token == null){
            // alert Token null
        }


        Observable<Boolean> observableChangePass = RxTextView.textChangeEvents(mPassword).map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        });
        Observable<Boolean> observableChangeShopNumber = RxTextView.textChangeEvents(mShopNumber)
                .map(new Func1<TextViewTextChangeEvent, Boolean>() {
            @Override
            public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                return textViewTextChangeEvent.text().length() > 0;
            }
        });

        Observable.combineLatest(observableChangePass, observableChangeShopNumber, new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean, Boolean aBoolean2) {
                return aBoolean && aBoolean2;
            }
        }).doOnNext(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                buttonSignIn.setEnabled(aBoolean);
            }
        }).subscribe();


        mShopNumber.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 11; // size of pattern 000-000-000
            private static final int TOTAL_DIGITS = 9; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 4; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrecntString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // chech that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }
                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });

    }

    @OnClick(R.id.button_signin)
    public void onSignIn(){
        if (token != null) {
            //558-926-864
            String user = Registration.getInstance().getUserId();
            String message = user + "||" + mShopNumber.getText().toString() + "||" + mPassword.getText().toString() +
                    "||" + token;
            HttpManager.getInstance().getService().login(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e("SignIn", "call: ", throwable);
                        }
                    })
                    .doOnNext(new Action1<ResponseDao>() {
                        @Override
                        public void call(ResponseDao responseDao) {
                            String user = responseDao.getResult();
                            if (!user.equals("")) {
                                String shop = responseDao.getShopId();
                                Registration.getInstance().setUserOld();
                                Registration.getInstance().setUserShop(user, shop);
                                Registration.getInstance().firstApp(false);
                                Registration.getInstance().setIsSignIn(true);

                                //restart application
                                Intent i = getActivity().getPackageManager()
                                        .getLaunchIntentForPackage( getActivity().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            }else {
                                Toast.makeText(getContext(),"เลขร้านค้าหรือรหัสผิด กรุณาลองใหม่อีกครั้ง",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).subscribe();
        }
    }

    @OnClick(R.id.why_must_login)
    public void onButtonMustLogin(){
        new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle("ทำไม?ต้องลงชื่อเข้าใช้")
                .setMessage("ผู้ประกอบการเต้นท์รถ สามารถลงทะเบียนเพื่อใช้ในการล๊อคอินได้หลายเครื่อง")//(ติดต่อพนักงาน 02-571-3000)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
