package com.assemblermaticstudio.a5_11weathergetter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.core.app.ActivityCompat;

public class PermissionsManager {

    public static final int ACCESS_LOCATION_CODE = 1;


    public static void requestPermission(Activity activity, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.s_PERMISSION_title))
                    .setMessage(activity.getString(R.string.s_PERMISSION_msg))
                    .setPositiveButton(activity.getString(R.string.s_PERMISSION_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, ACCESS_LOCATION_CODE); // non blocking method
                        }
                    })
                    .setNegativeButton(activity.getString(R.string.s_PERMISSION_deny), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, ACCESS_LOCATION_CODE); // non blocking method
        }
    }

}

