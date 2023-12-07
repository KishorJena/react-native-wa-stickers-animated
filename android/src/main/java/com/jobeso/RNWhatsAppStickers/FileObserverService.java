package com.jobeso.RNWhatsAppStickers;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import java.io.File;

public class FileObserverService extends Service {

    public static final String TAG = "ReactNative";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service Started");
        File sdCard = Environment.getExternalStorageDirectory();
        AppClass.fileObserver = new RecursiveFileObserver(sdCard.getAbsolutePath());
        AppClass.fileObserver.startWatching();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Service Destroyed");
    }
}