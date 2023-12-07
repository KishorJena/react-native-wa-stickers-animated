package com.jobeso.RNWhatsAppStickers;

import android.app.*;
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
public class AppClass extends Application {
    public static RecursiveFileObserver fileObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, FileObserverService.class);
        startService(intent);
    }
}