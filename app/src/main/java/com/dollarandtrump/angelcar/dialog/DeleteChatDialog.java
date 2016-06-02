package com.dollarandtrump.angelcar.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;

import com.dollarandtrump.angelcar.dao.Results;
import com.dollarandtrump.angelcar.manager.http.HttpManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by humnoyDeveloper on 25/3/59. 10:07
 */
public class DeleteChatDialog extends DialogFragment {
    private static final String ARG_MESSAGE_FROM_USER = "ARG_MESSAGE_FROM_USER";
    String messageFromUser;

    public static DeleteChatDialog newInstance(String messageFromUser) {
        Bundle args = new Bundle();
        DeleteChatDialog fragment = new DeleteChatDialog();
        args.putString(ARG_MESSAGE_FROM_USER,messageFromUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageFromUser = getArguments().getString(ARG_MESSAGE_FROM_USER);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("คุณต้องการลบข้อความ")
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Call<Results> call = HttpManager.getInstance().getService().deleteChatList(messageFromUser);
                        call.enqueue(new Callback<Results>() {
                            @Override
                            public void onResponse(Call<Results> call, Response<Results> response) {
                                if(response.isSuccessful()){
                                    Log.i("DialogFragment", "onResponse: "+response.body().getSuccess());
                                }else {
                                    try {
                                        Log.i("DialogFragment", "onResponse: "+response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Results> call, Throwable t) {
                                Log.e("DialogFragment", "onFailure: ", t);
                            }
                        });

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }
}
