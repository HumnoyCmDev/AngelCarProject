package com.dollarandtrump.angelcar.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by humnoyDeveloper on 6/4/59. 11:50
 */
public class Permission {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Context mContext;
    private Activity act;
    public Permission(Activity act) {
        this.mContext = Contextor.getInstance().getContext();
        this.act = act;
    }

    public boolean isAsKForPermission(final String permission, String message){
        if (ContextCompat.checkSelfPermission(mContext,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    permission)) {
                //กรณีไม่ให้สิทธิ์// แสดงรายการคำขอ ผลหากไม่ให้สิท ขอ Permission ผ่าน Dialog
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(act,
                                new String[]{permission},
                                REQUEST_CODE_ASK_PERMISSIONS);
                    }
                });
                return false;
            } else {
                // ขอสิทธิ์เข้าถึง
                ActivityCompat.requestPermissions(act,
                        new String[]{permission},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }

        }else {
            return true;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(act)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
