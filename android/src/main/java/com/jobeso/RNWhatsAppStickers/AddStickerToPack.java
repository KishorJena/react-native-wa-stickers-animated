package com.jobeso.RNWhatsAppStickers;

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

// NOTE and these are from another libvips wrapper
// import com.criteo.vips.Vips;
// import com.criteo.vips.VipsContext;
// import com.criteo.vips.VipsException;
// import com.criteo.vips.VipsImage;
// import com.jobeso.RNWhatsAppStickers.VipsTestUtils;
// import com.criteo.vips.enums.VipsImageFormat;
// import java.awt.*;
import java.io.IOException;
import android.widget.Toast; 
// import com.arthenica.ffmpegkit;
// import com.arthenica.ffmpegkit.FFmpegKit;
// import com.arthenica.ffmpegkit.ReturnCode;
// import com.n4no.webpencoder.webp.graphics.WebpBitmapEncoder;
import com.jobeso.RNWhatsAppStickers.BuildConfig;

import java.nio.file.*;

public class AddStickerToPack {

    public static String path;

    AddStickerToPack(){
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/Sticker Pack/";
    }

    public void addToRecentPack(String path, String identifier) throws IOException {
        List<StickerPack> Allpacks = new ArrayList<StickerPack>();
        List<Sticker> mStickers = new ArrayList<>();
        List<String> mEmojis = new ArrayList<>();
        mEmojis.add("😀");
        mEmojis.add("😁");

        Log.d("ReactNative","recent pack");
        
        try {

            Allpacks.addAll(Hawk.get("sticker_packs", new ArrayList<StickerPack>()));
            // Log.d("ReactNative","size "+String.valueOF(stickerPacks.size()));
            StickerPack newPack = null;
            int index = -1;

            for (StickerPack aPack : Allpacks) {
                if(aPack.identifier.equals(identifier)){
                    newPack = aPack;
                    index = Allpacks.indexOf(aPack);
                    // Log.d("ReactNative"," 📩 idenitifer detceted ");                    
                }
            }
            if(index < 0){
                android.util.Log.d("ReactNative","");
                return;
            }else{
                mStickers.addAll(Hawk.get(identifier));
                new DownloadImage().execute(
                    path, 
                    identifier,
                    identifier
                );
                
                mStickers.add(new Sticker(
                    getLastBitFromUrl(path).replace(".png", ".webp"), mEmojis));
                Hawk.put(identifier,mStickers);

                newPack.setimageDataVersion(String.valueOf(Integer.parseInt(newPack.imageDataVersion) + 1)); 
                newPack.setStickers(mStickers);
                Allpacks.set(index, newPack);
                // Allpacks.add(newPack);     
                
                Hawk.put("sticker_packs", Allpacks);
                Log.d("ReactNative","sticker pack updated");
                // promise.resolve("sticker pack updated");
            }
            
        } catch (Exception e) {
        //     e.printStackTrace();
        //     promise.reject(ERROR_ADDING_STICKER_PACK, e);
        }

    }

    private static String getLastBitFromUrl(final String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        public String imageFileName;
        public String identifier;
        public String name;

        private Bitmap downloadImageBitmap(String sUrl, String sIdentifier, String sName) {
            imageFileName = getLastBitFromUrl(sUrl).replace(".png", ".webp");
            identifier = sIdentifier;
            // name = sName;
            Bitmap bitmap = null;
            
            try {
                InputStream inputStream = new URL(sUrl).openStream(); // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream); // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return downloadImageBitmap(params[0], params[1], params[2]);
        }

        protected void onPostExecute(Bitmap result) {
            SaveImage(result, imageFileName, identifier);
        }
    }

    public static void SaveImage(Bitmap finalBitmap, String fileName, String identifier) {

        String root = path + "/" + identifier;
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = fileName;
        File file = new File(myDir, fname);
        // android.util.Log.d("ReactNative","identifier:"+identifier+" | name:"+name+" | SaveImage : "+file.getAbsolutePath());
        
        if (file.exists()){
            // Log.d("ReactNative","root "+root);
            file.delete();
        }

        
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out);
            out.flush();
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
