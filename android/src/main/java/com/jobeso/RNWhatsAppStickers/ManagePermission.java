package com.jobeso.RNWhatsAppStickers;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class ManagePermission {
    private static final String TAG = "ReactNative";
    public void askPermissions(Activity activity){
        Log.d(TAG,"Asking , Checking Permissions");
        int grant = PackageManager.PERMISSION_GRANTED;
        boolean check = ActivityCompat.checkSelfPermission(activity, permissions()[0]) == grant &&
                ActivityCompat.checkSelfPermission(activity, permissions()[1]) == grant &&
                ActivityCompat.checkSelfPermission(activity, permissions()[2]) == grant;
        if (check) {
            Log.d(TAG,"yes permissions granted for read write ");
        } else {
            
            // ActivityCompat.requestPermissions(activity, permissions(),1);
            // ActivityCompat.requestPermissions(activity, permissions(),1);
            // Permission is not granted, request it.
            Log.d(TAG,"permission not granted... asking now!!!");
        }
    }

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    public static String[] storage_permissions = {
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            READ_MEDIA_AUDIO,
            READ_MEDIA_IMAGES,
            READ_MEDIA_VIDEO
    };
}
