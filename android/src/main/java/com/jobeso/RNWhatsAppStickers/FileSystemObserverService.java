package com.jobeso.RNWhatsAppStickers;

import android.os.FileObserver;
import android.os.*;

import java.lang.NullPointerException;
import android.os.Environment;
import android.os.Build;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.*;

import android.content.*;

import androidx.core.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.*;
import android.webkit.MimeTypeMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException; // added for #373
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.os.AsyncTask;
import android.os.FileUtils;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import android.util.Base64;
import android.util.Log;

import android.app.*;
import android.content.*;
import android.os.*;


public class FileSystemObserverService extends Service {
     
    private static Context context;

    public static void setContext(Context cntxt) {
        context = cntxt;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
   
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FileSystemObserverService", "onStartCommand");
        observe();
        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }
   
    public File getInternalStoragePath() {
        File parent = Environment.getExternalStorageDirectory().getParentFile();
        File external = Environment.getExternalStorageDirectory();
        // File external = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/WhatsApp Stickers/";
        File[] files = parent.listFiles();
        File internal = null;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("ReactNative","File: " + files[i].getAbsolutePath());
                if (files[i].getName().toLowerCase().startsWith("sdcard") && !files[i].equals(external)) {
                    internal = files[i];
                    Log.d("ReactNative","ANYTHING FOUND ??????????");
                }
            }
        }
   
        return internal;
    }
    public File getExtenerStoragePath() {
   
        return Environment.getExternalStorageDirectory();
    }
   
    public void observe() {
        Thread t = new Thread(new Runnable() {
   
            @Override
            public void run() {
   
   
                //File[]   listOfFiles = new File(path).listFiles();
                // String test = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/WhatsApp Stickers/";
                
                // File str = new File(test);
                File str = getInternalStoragePath();
                if (str != null) {
                    String internalPath = str.getAbsolutePath();
   
                    new Obsever(internalPath).startWatching();
                }
                // str = getExtenerStoragePath();
                // if (str != null) {
   
                //     String externalPath = str.getAbsolutePath();
                //     new Obsever(externalPath).startWatching();
                // }
   
   
   
            }
        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
   
   
    }
   
    class Obsever extends FileObserver {
   
        List < SingleFileObserver > mObservers;
        String mPath;
        int mMask;
        public Obsever(String path) {
            // TODO Auto-generated constructor stub
            this(path, ALL_EVENTS);
        }
        public Obsever(String path, int mask) {
            super(path, mask);
            mPath = path;
            mMask = mask;
            // TODO Auto-generated constructor stub
   
        }
        @Override
        public void startWatching() {
            // TODO Auto-generated method stub
            if (mObservers != null)
                return;
            mObservers = new ArrayList < SingleFileObserver > ();
            Stack < String > stack = new Stack < String > ();
            stack.push(mPath);
            while (!stack.empty()) {
                String parent = stack.pop();
                mObservers.add(new SingleFileObserver(parent, mMask));
                File path = new File(parent);
                File[] files = path.listFiles();
                if (files == null) continue;
                for (int i = 0; i < files.length; ++i) {
                    if (files[i].isDirectory() && !files[i].getName().equals(".") && !files[i].getName().equals("..")) {
                        stack.push(files[i].getPath());
                    }
                }
            }
            for (int i = 0; i < mObservers.size(); i++) {
                mObservers.get(i).startWatching();
            }
        }
        @Override
        public void stopWatching() {
            // TODO Auto-generated method stub
            if (mObservers == null)
                return;
            for (int i = 0; i < mObservers.size(); ++i) {
                mObservers.get(i).stopWatching();
            }
            mObservers.clear();
            mObservers = null;
        }
        @Override
        public void onEvent(int event, final String path) {
            String s = getContext().getFilesDir().getAbsolutePath();
            android.util.Log.d("ReactNative","onEvent: "+s);
            
            if (event == FileObserver.OPEN) {
                //do whatever you want
                Log.d("ReactNative", "OPEN " + path + " : "); 
            } else if (event == FileObserver.CREATE) {
                //do whatever you want
                Log.d("ReactNative", "CREATE " + path+ " : "); 
                // new AddStickerToPack().addToRecentPack(path);
                // Log.d("ReactNative", "CREATE FILE ABS: " );
            } else if (event == FileObserver.DELETE_SELF || event == FileObserver.DELETE) {
                //do whatever you want
                Log.d("ReactNative", "DELETE " + path);
            } else if (event == FileObserver.MOVE_SELF || event == FileObserver.MOVED_FROM || event == FileObserver.MOVED_TO) {
                //do whatever you want
                Log.d("ReactNative", "MOVE " + path);
            }
        }
   
        private class SingleFileObserver extends FileObserver {
            private String mPath;
            public SingleFileObserver(String path, int mask) {
                super(path, mask);
                // TODO Auto-generated constructor stub
                mPath = path;
            }
   
            @Override
            public void onEvent(int event, String path) {
                // TODO Auto-generated method stub
                String newPath = mPath + "/" + path;
                Obsever.this.onEvent(event, newPath);
            }
   
        }
   
    }
}
