package com.jobeso.RNWhatsAppStickers;

import com.facebook.react.bridge.ReactApplicationContext;
import com.orhanobut.hawk.Hawk;
import android.util.Log;
import java.util.*;
import java.util.stream.*;
import org.apache.commons.io.*;
// import java.nio.file.*;
import java.io.*;

public class HawkUtil {
    public static String TAG = "ReactNative";

    public static void GetPack(String key){
        String str =  Hawk.get(key);
        Log.d(TAG,"got "+str);
    }
    public static void DeletePack(String identifier){
        List<StickerPack> stickerPacks = Hawk.get("sticker_packs", new ArrayList<StickerPack>());
        Log.d(TAG, "original size "+stickerPacks.size());

        List<StickerPack> filterPacks = stickerPacks.stream()
            .filter(pack -> !pack.identifier.equals(identifier))
            .collect(Collectors.toList());

        Log.d(TAG, "filter size "+filterPacks.size());
        // Update the sticker packs in Hawk
        Hawk.put("sticker_packs", filterPacks);

    }
    public static void DeleteAllPacks(){
        List<StickerPack> stickerPacks = Hawk.get("sticker_packs", new ArrayList<StickerPack>());
        Log.d(TAG, "original size "+stickerPacks.size());
        
        Hawk.delete("sticker_packs");
        
        List<StickerPack> stickerPacks2 = Hawk.get("sticker_packs", new ArrayList<StickerPack>());
        Log.d(TAG, "filter size "+stickerPacks2.size());


    }



    public void DeletePack_pack(){
        
        // Log.d(TAG,"given identifier... "+identifier);
        // // identifier is null then handle

        // // 1 ==> Delete from hawk
        // List<StickerPack> stickerPacks = Hawk.get("sticker_packs", new ArrayList<StickerPack>());
        // List<StickerPack> restorePacks = new ArrayList();
        
        // for(StickerPack stickerPack : stickerPacks){
        //     Log.d(TAG,"check stickerPack.identifier "+stickerPack.identifier);
        //     if(!identifier.equals(stickerPack.identifier)){
        //         restorePacks.add(stickerPack);
        //     }
        // }
        // Hawk.put("sticker_packs",restorePacks);
        // // Delete from storage
        
        // String stickerPackDir = getStickersDir() + "/"+ identifier;
        // boolean flag = deleteDirectory(new File(stickerPackDir));
        // Log.d(TAG,"Delete identifier "+(flag == true ? "true":"false"));
        

        // // notify CP
        // final String PROVIDER_NAME = getContentProviderAuthority(reactContext);
        // final String URL = "content://" + PROVIDER_NAME + "/stickers_asset/" + identifier+"/4";
        // final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);
        // reactContext.getContentResolver().notifyChange(STICKER_CONTENT_PROVIDER_URI, null);

        // // Hawk.delete(identifier);
        // // String key = Hawk.contains(identifier)?"true":"false";
        // // Log.d(TAG,"Hawk.contains(key) "+key);

        // // Delete from Content Provider
        // promise.resolve("Resolved Deetiong");
    }
}
