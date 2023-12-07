package com.jobeso.RNWhatsAppStickers;

import android.os.Environment;
import android.os.Build;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.os.Bundle;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.*;
import android.webkit.MimeTypeMap;

import java.lang.reflect.Field;
import java.util.*;
import java.lang.Exception;
// import javax.naming.Context;
// import javax.security.auth.callback.Callback;

// import javax.naming.Context;

// import javax.naming.Context;
// import javax.security.auth.callback.Callback;

import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.os.AsyncTask;
import android.os.FileUtils;
import java.util.*;
// added for #373
// NOTE and these are from another libvips wrapper
// import com.criteo.vips.Vips;
// import com.criteo.vips.VipsContext;
// import com.criteo.vips.VipsException;
// import com.criteo.vips.VipsImage;
// import com.jobeso.RNWhatsAppStickers.VipsTestUtils;
// import com.criteo.vips.enums.VipsImageFormat;
// import java.awt.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.Method;

import android.widget.Toast; 
// import com.arthenica.ffmpegkit;
// import com.arthenica.ffmpegkit.FFmpegKit;
// import com.arthenica.ffmpegkit.ReturnCode;
// import com.n4no.webpencoder.webp.graphics.WebpBitmapEncoder;
import com.jobeso.RNWhatsAppStickers.BuildConfig;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat; 
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.annotation.NonNull;

import java.nio.file.*;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.facebook.react.bridge.ReactContext;
// import com.facebook.react.modules.core.ActivityEventListener;
// import com.facebook.react.modules.core.DeviceEventManagerModule;
import static android.graphics.Bitmap.CompressFormat.WEBP;
import static android.graphics.Bitmap.CompressFormat.WEBP_LOSSLESS;
import static android.graphics.Bitmap.CompressFormat.WEBP_LOSSY;

// WEBP coder
import com.n4no.webpencoder.webp.codec.WebpAnimatedDecoder;
import com.n4no.webpencoder.webp.codec.WebpBitmapEncoder;
import com.n4no.webpencoder.webp.webpData.ProcessedFrames;


public class RNWhatsAppStickersModule extends ReactContextBaseJavaModule implements PermissionListener {
  
    // 
    public static final int PERMISSION_REQUEST_CODE = 123;
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";
    public static String path;
    public static String STICKER_PACKS_DIR; 
    public static String STICKER_PACK_FOLDER_NAME = "stickers_asset"; 
    public static final int ADD_PACK = 200;
    public static final String ERROR_ADDING_STICKER_PACK = "Could not add this sticker pack. Please install the latest version of WhatsApp before adding sticker pack";
    
    private final ReactApplicationContext reactContext;
    public static ReactApplicationContext reactContext2;
    public static String TAG = "ReactNative";
    
    private Promise mSendPromise;
    private Callback mSuccessCallback;
    private Callback mCancelCallback;

    private ReactInstanceManager mReactInstanceManager;
    public final ActivityEventListener mActivityEventListener = new BaseActivityEventListener(){
 
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("ReactNative","Override - onActivityResult ");

            if (requestCode == 200) {
                if (resultCode == Activity.RESULT_CANCELED && data != null) {
                    final String validationError = data.getStringExtra("validation_error");
                    if (validationError != null) {
                        if (Boolean.parseBoolean("true")) {
                            // error should be shown to developer only, not users.
                            Log.d(TAG, ":?validationError" );
                        }
                        Log.d(TAG, "Validation failed:" + validationError);
                    }
                } 
            }
        }
        
        // public void onActivityResumed(Activity activity) {
        //     // requestPermissions(activity); 

        //     ManagePermission permission = new ManagePermission();
        //     permission.askPermissions(activity);
        // }
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            // 
            Log.d("ReactNative","onActivityResult requestCode:"+requestCode);
            Log.d("ReactNative","onActivityResult resultCode:"+resultCode);
            
            String STATUS_CODE = "status_code";
            String STATUS_KEY = "status_key";
            String STATUS_VALUE = "status_value";
            
            // WritableMap map = Arguments.createMap(); 
            WritableMap map = new Arguments().createMap(); 
            

            String status_message = null;
            String status_type = null;
            if (intent != null) {
                Log.d("ReactNative","onActivityResult A)intent != null:  "+resultCode);
                
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Log.d("ReactNative","onActivityResult B)extras != null "+resultCode);
                    for (String key : extras.keySet()) {
                        Object value = extras.get(key);
                        if (value != null) {
                            Log.d("ReactNative","onActivityResult C)value != null "+resultCode);
                            // Do something with the key and value
                            Log.d("ReactNative","Key: " + key + ", Value: " + value.toString());
                            status_message = value.toString();
                            status_type = key;
                        }
                    }
                }
            }



            if (requestCode == ADD_PACK && mSendPromise != null) {
              
                if (resultCode == Activity.RESULT_CANCELED) {
                    // user_cancelled, validation_error, already_added, user_touched_outside
                    Log.d("ReactNative","| Cancelled "+String.valueOf(Activity.RESULT_CANCELED));
                    map.putInt(STATUS_CODE, resultCode);
                    if(intent==null){
                        status_type = "user_touched_outside";
                        status_message = "User touched outside the dialog";
                    }
                    map.putString(STATUS_KEY, status_type);
                    map.putString(STATUS_VALUE, status_message);
                    // mSendPromise.reject(map);
                    mSendPromise.resolve(map); //FIXME
                    // mCancelCallback.invoke(map);
                } else if (resultCode == Activity.RESULT_OK) {
                    // add_successful
                    Log.d("ReactNative","| Result is OK "+String.valueOf(Activity.RESULT_OK));
                    map.putInt(STATUS_CODE, resultCode);
                    map.putString(STATUS_KEY,status_type);
                    map.putString(STATUS_VALUE,status_message);
                    mSendPromise.resolve(map);
                    // mSuccessCallback.invoke(map);
                }else{
                    Log.d("ReactNative","| unknonw eror "+String.valueOf(resultCode));
                    map.putInt(STATUS_CODE, resultCode);
                    map.putString(STATUS_KEY,"unknown");
                    map.putString(STATUS_VALUE,"Issue is unknown");
                    // mSendPromise.reject(error);
                    mSendPromise.resolve(map); // FIXME
                    // mCancelCallback.invoke(map);
                }
            }
   
        }
         
        public void onActivityResumed(Activity activity) {
          Log.d(TAG, "onActivityResumed");
          // Request permissions
        }
      
        
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
          Log.d(TAG, "onActivityCreated");
        }
      
        
        public void onActivityPaused(Activity activity) {
          Log.d(TAG, "onActivityPaused");
        }
      
        
        public void onActivityStopped(Activity activity) {
          Log.d(TAG, "onActivityStopped");
        }
              
    };

    public RNWhatsAppStickersModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext2 = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
        Hawk.init(reactContext).build();
        path = reactContext.getFilesDir() + "/" + "stickers_asset";
        STICKER_PACKS_DIR = reactContext.getFilesDir() + "/" + STICKER_PACK_FOLDER_NAME;
        
        // new FileSystemObserverService().setContext(reactContext);
        // Intent intent = new Intent(reactContext, FileSystemObserverService.class);
        // intent.putExtra(INTENT_EXTRA_FILEPATH, path);
        // reactContext.startService(intent);

        // Intent intent = new Intent(this, FileObserverService.class);
        // startService(intent);
        // path2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // System.load("libs/JVips");

                
        Activity currentActivity = reactContext.getCurrentActivity();
        // Activity currentActivity = reactContext.getApplicationContext();
        // new ManagePermission().askPermissions(currentActivity);
       
        // // Check if the permission is already granted
        // if (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        //     Log.d("ReactNative","checkSelfPermission | NO  | rewwuesting... ");
            // ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        // }else{
        //     Log.d("ReactNative","checkSelfPermission | YES | ... ");
        // }
          // Register activity listener




    }
    // private void requestPermissions(Activity currentActivity) {
    //     // Call your permissions method
    //     String[] perms = permissions();

    //     // Request permissions
    //     ActivityCompat.requestPermissions(currentActivity, perms, 1); 
    // }
    @Override
    public String getName() {
        return "RNWhatsAppStickers";
    }

    public static String getContentProviderAuthority(Context context) {
        return context.getPackageName() + ".stickercontentprovider";
    }
    public static String getFileProviderAuthority(Context context) {
        return context.getPackageName() + ".fileprovider";
    }
   
    public WritableArray getFiles(String pack) {
        String directoryPath = reactContext.getApplicationContext().getFilesDir().getPath();
        directoryPath += "/stickers_asset/" + pack;
        
        File directory = new File(directoryPath);
        WritableArray filesArray = Arguments.createArray();
        
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    filesArray.pushString(file.getAbsolutePath());
                }
            }
            return filesArray;
        } else {
            return null;
        }
    }
    @ReactMethod
    public void GetStickerPacks(Promise promise){

        final String PROVIDER_NAME = getContentProviderAuthority(reactContext);
        final String URL = "content://" + PROVIDER_NAME + "/metadata" ;
        final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);


        Cursor cursor = reactContext.getContentResolver().query(STICKER_CONTENT_PROVIDER_URI, null, null,null,null );
        
        if(cursor.getCount()==0){
            promise.reject("no pack list found ni contentProvider");
        }

        WritableArray array = Arguments.createArray();

        while (cursor.moveToNext()) {
            String identifier = cursor.getString(cursor.getColumnIndex("sticker_pack_identifier"));
            String publisher = cursor.getString(cursor.getColumnIndex("sticker_pack_publisher"));
            String version = cursor.getString(cursor.getColumnIndex("image_data_version"));
            String animated_sticker_pack = cursor.getString(cursor.getColumnIndex("animated_sticker_pack"));
            String sticker_pack_icon = cursor.getString(cursor.getColumnIndex("sticker_pack_icon"));
            String cache = cursor.getString(cursor.getColumnIndex("whatsapp_will_not_cache_stickers"));
            
            // metadata
            WritableMap map = Arguments.createMap();
            map.putString("identifier",identifier);
            map.putString("publisher",publisher);
            map.putString("version",version);
            map.putString("animated_sticker_pack",animated_sticker_pack);
            map.putString("sticker_pack_icon",sticker_pack_icon);
            
            // filepaths
            WritableArray arrayFiles = getFiles(identifier);
            map.putArray("stickers",arrayFiles);
            
            array.pushMap(map);

            // Log.d("ReactNative","identifier : "+identifier);
        }
        

        promise.resolve(array);
        
    }
    @ReactMethod
    public void GetStickerPacks_hawk(Promise promise) {
        String ASSET_PATH = reactContext.getApplicationContext().getFilesDir().getPath();
        ASSET_PATH += "/stickers_asset" ;
        List<StickerPack> stickerPacks = Hawk.get("sticker_packs", new ArrayList<StickerPack>());

        // WritableArray array = Arguments.createArray();
        WritableArray array = new WritableNativeArray();

        for (StickerPack stickerPack : stickerPacks) {

            final String identifier = stickerPack.identifier;
            final String publisher = stickerPack.publisher;
            final String version = stickerPack.imageDataVersion;
            final boolean avoid_cache = stickerPack.avoidCache;
            final boolean animated_sticker_pack = stickerPack.animatedStickerPack;    
            Log.d(TAG,"GetStickerPacks_hawk- "+identifier);

            // metadata
            WritableMap map = Arguments.createMap();
            map.putString("identifier", identifier);
            map.putString("publisher", publisher);
            map.putString("image_data_version", version);
            map.putBoolean("avoid_cache", avoid_cache);
            map.putBoolean("animated_sticker_pack", animated_sticker_pack);

            // + icon
            final String parentPath = ASSET_PATH+"/"+identifier;
            final String tray_image_file = parentPath+"/"+stickerPack.trayImageFile;
            map.putString("tray_image_file", tray_image_file);
            // map.putString("parentPath", parentPath);
            // map.putString("ASSET_PATH", ASSET_PATH);
            // map.putString("identifier", identifier);
            // Log.d(TAG,"tray_image_file "+ASSET_PATH);

            // + isAdded?
            final boolean isWhitelisted = WhitelistCheck.isWhitelisted(reactContext,identifier);  
            map.putBoolean("isAdded", isWhitelisted);
            
            
            WritableArray arrayFiles = new WritableNativeArray();
            // WritableArray arrayFiles = Arguments.createArray();
            for (Sticker sticker : stickerPack.getStickers()) {
                final String fileName = sticker.getContent();
                final String sticker_path = parentPath+"/"+fileName;
                arrayFiles.pushString(parentPath+"/"+fileName);
                Log.d(TAG,"+sticker_path "+sticker.getContent());

            }

            map.putArray("stickers", arrayFiles);
            array.pushMap(map);
        }

        promise.resolve(array);
    }

    public static byte[] getByteArray(File file) throws IOException{

        // Creating an object of FileInputStream to
        // read from a file
        FileInputStream fl = new FileInputStream(file);

        // Now creating byte array of same length as file
        byte[] arr = new byte[(int)file.length()];

        // Reading file content to byte array
        // using standard read() method
        fl.read(arr);

        // lastly closing an instance of file input stream
        // to avoid memory leakage
        fl.close();

        // Returning above byte array
        return arr;
    }
    

    public boolean isDirHaveImages(String hiddenDirectoryPath) {

		File listFile[] = new File(hiddenDirectoryPath).listFiles();

		boolean dirHaveImages = false;

		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].getName().endsWith(".png")
						|| listFile[i].getName().endsWith(".jpg")
						|| listFile[i].getName().endsWith(".jpeg")
						|| listFile[i].getName().endsWith(".gif")) {

					// Break even if folder have a single image file
					dirHaveImages = true;
					break;

				}
			}
		}
		return dirHaveImages;

	}

    private ArrayList<CustomFile> filterFiles(String path,Context context) {

		ArrayList<CustomFile> listOfHiddenFiles = new ArrayList<CustomFile>();
		String hiddenFilePath;

		// Scan all no Media files
		String nonMediaCondition = MediaStore.Files.FileColumns.MEDIA_TYPE
				+ "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

		// Files with name contain .nomedia
		String where = nonMediaCondition + " AND "
				+ MediaStore.Files.FileColumns.TITLE + " LIKE ?";

		String[] params = new String[] { "%" + path + "%" };

		// make query for non media files with file title contain ".nomedia" as
		// text on External Media URI
		Cursor cursor = reactContext.getContentResolver().query(
				MediaStore.Files.getContentUri("external"),
				new String[] { MediaStore.Files.FileColumns.DATA }, where,
				params, null);

		// No Hidden file found
		if (cursor.getCount() == 0) {
            Log.d("ReactNative","CUrson is empty");
            
			listOfHiddenFiles.add(new CustomFile("No Hidden File Found",
					"Nothing to show Here", "Nothing to show Here", false));

			// show Nothing Found
			return listOfHiddenFiles;
		}

		// Add Hidden file name, path and directory in file object
		// while (cursor.moveToNext()) {
		// 	hiddenFilePath = cursor.getString(cursor
		// 			.getColumnIndex(MediaStore.Files.FileColumns.DATA));
		// 	if (hiddenFilePath != null) {

		// 		listOfHiddenFiles
		// 				.add(new CustomFile(
        //                         , 
        //                         ,
		// 						,
		// 						isDirHaveImages()
        //                         )
        //                     );
		// 	}
		// }

		cursor.close();

		return listOfHiddenFiles;

	}
    
    @ReactMethod
    private void NoMedia(String path, Promise promise){
        filterFiles(path, reactContext);
        promise.resolve("Media>?");
    }



    @ReactMethod
    public void DeleteAllPacks_Hawk(String mode, Promise promise){
        Hawk.deleteAll();
        promise.resolve("All deleted");
    }

    @ReactMethod
    public void getStickersPaths(String identifier, Promise promise){
		List<Sticker> stk = Hawk.get(identifier);
		int size = stk.size();
        WritableArray stickers = Arguments.createArray();

		for(Sticker s :stk){
			Log.d("ReactNative","stk to content-"+s.imageFileName);
            stickers.pushString(s.imageFileName);
			// Log.d("ReactNative","stk to content-"+String.valueOf(s.size));
			// Log.d("ReactNative","stk to content-"+String.valueOf(s.emojis));
		}
        
		promise.resolve(stickers);
	}

	
    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {

        Log.d("ReactNative","onRequestPermissionsResult .. ");

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing the ContentProvider
                return true;
            } else {
                return false;
                // Permission denied, handle this case (show a message, etc.)
            }
        }
        return false;
    }

    

    public String convertContentUriToFileUri(Uri contentUri) {
        String fileUriString = null;

        try {
            ContentResolver contentResolver = reactContext.getContentResolver();
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(contentUri, "r");
            
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                fileUriString = "file://" + fileDescriptor.toString();
                
                parcelFileDescriptor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileUriString;
    }
    public File getCopyOf(String path) {
        File src = new File(path);
        
        File directory = new File("/storage/emulated/0/Pictures/stk/");
        File dst = new File(directory, UUID.randomUUID().toString()+".webp");
        
       
            try (InputStream in = new FileInputStream(src); 
                 OutputStream out = new FileOutputStream(dst)){
                android.os.FileUtils.copy(in, out);
            }catch(Exception e){
                Log.d("ReactNative","Error copying file: "+e.getMessage());
                // promise.reject("Error copying file: " + e.getMessage());
            }
            // file1.copyTo(file2, true);
        return dst;

       
    }

    @ReactMethod
    public void insert(String identifier, Promise promise){
         
        
        List<StickerPack> stickerPacks = new ArrayList<StickerPack>();
        stickerPacks.addAll(Hawk.get("sticker_packs", new ArrayList<StickerPack>()));
        
        for(StickerPack pack : stickerPacks){
            if(pack.identifier.equals(identifier)){

                StickerPack mStickerPack = new StickerPack(
                    pack.identifier, 
                    pack.name,
                    pack.publisher,
                    pack.trayImageFile,
                    pack.publisherEmail, 
                    pack.publisherWebsite,
                    pack.privacyPolicyWebsite, 
                    pack.licenseAgreementWebsite,
                    pack.imageDataVersion,// #373
                    pack.avoidCache,// #373
                    pack.animatedStickerPack); // #373
                
                List<Sticker> mStickers = pack.getStickers();
                mStickers.remove(mStickers.size()-1);
                mStickerPack.setIsWhitelisted(false);
                mStickerPack.setIosAppStoreLink(pack.iosAppStoreLink);
                mStickerPack.setAndroidPlayStoreLink(pack.androidPlayStoreLink);
                mStickerPack.setStickers(mStickers);

                List<StickerPack> temp = (List)Hawk.get("sticker_packs");
                Log.d("ReactNative","temp "+String.valueOf(temp.size()));
                temp.remove(temp.size()-1);
                Log.d("ReactNative","remove "+String.valueOf(temp.size()));
                temp.add(mStickerPack);
                Log.d("ReactNative","added "+String.valueOf(temp.size()));
                Hawk.put("sticker_packs", temp);
                    
            }
        }

       
        promise.resolve("resolve de;eted ");

    }
    
    @ReactMethod
    public void GetStickers_Hawk(String identifier,Promise promise){
        android.util.Log.d("ReactNative","GetStickers_Hawk ");
        List<Sticker> stickers = Hawk.get(identifier);
        for(Sticker sticker : stickers){
            android.util.Log.d("ReactNative","Hawk.get() sticker: "+sticker.imageFileName);
            
        }
        promise.resolve(stickers.size());
    }
    @ReactMethod
    public void GetStickers(String identifier,Promise promise){
        // final String PROVIDER_NAME = "com.kot1.stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        final String PACKAGE =getContentProviderAuthority(reactContext);
        final String PROVIDER_NAME = PACKAGE+".stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        final String URL = "content://" + PROVIDER_NAME + "/stickers/" + identifier ;
        final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);

        Cursor cursor = reactContext.getContentResolver().query(
            STICKER_CONTENT_PROVIDER_URI, 
            null, 
            null,
            null,
            null 
        );

        int count = cursor.getCount();
        if(count<1)
            promise.reject("Error: no stickers found");
        
        WritableArray stickers = Arguments.createArray();

        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);
            int index = cursor.getColumnIndex("sticker_file_name");
            String sticker_file_name = cursor.getString(index);
            // Cursor cursor2 = cursor.getString(index);
            stickers.pushString(sticker_file_name);
            // Log.d("ReactNative","stickers count  : "+sticker_file_name);
            // String[] cols = cursor2.getColumnNames();
            // for (String string : cols) {
            //     Log.d("ReactNative","stickers columns  : "+string);
            // }
            // for(int j=0;j<cursor2.getCount();j++){
            //     cursor2.moveToPosition(j);
            //     int index2 = cursor2.getColumnIndex("sticker_file_name");
            //     String sticker_file_name = cursor2.getString(index2);
            //     Log.d("ReactNative","sticker_file_name : "+sticker_file_name);
            // }
        }
        // String[] names = cursor.getColumnNames();
        // for (String string : names) {
        //     Log.d("ReactNative","string : "+string);
        // }

        
        
        // reactContext.getContentResolver().notifyChange(STICKER_CONTENT_PROVIDER_URI, null);
        
        promise.resolve(stickers);
    }
    
    public String GetVersion(String identifier){
        final String PROVIDER_NAME = "com.kot1.stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        final String URL = "content://" + PROVIDER_NAME + "/metadata/" + identifier ;
        final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);

        Cursor cursor = reactContext.getContentResolver().query(
            STICKER_CONTENT_PROVIDER_URI, 
            null, 
            null,
            null,
            null 
        );
        
        int count = cursor.getCount();
        String[] names = cursor.getColumnNames();
        
        for (String string : names) {
            // Log.d("ReactNative","string "+string);
        }

        if(count==0){
            return "0";
        }

        String version = "-1";
        
        for(int i=0;i<count;i++){
            cursor.moveToPosition(i);
            
            String v = cursor.getString(cursor.getColumnIndex("sticker_pack_identifier"));
            if(identifier.equals(v)){
                version = cursor.getString(cursor.getColumnIndex("image_data_version"));
                // Log.d("ReactNative",identifier+" is equals "+v+"| version is "+version);	

                break;
            }
            // for(int j=0;j<names.length;j++){
                // Log.d("ReactNative",names[j] + " : "+cursor.getString(j));
                // int ind0 = cursor.getColumnIndex("identifier");
                // String s  = cursor.getString(ind0);
                
                // int ind = cursor.getColumnIndex("image_data_version");
                // version = cursor.getString(ind);
                // Log.d("ReactNative","idenitifer: "+s+" ver:"+version);
                
                // return version;
            // }
        }
        
        return version;

    }

    @ReactMethod
    public void GetPackVersion_Hawk(String identifier,Promise promise){
        List<StickerPack> stickerpacks = Hawk.get("sticker_packs");
        for(StickerPack stickerpack : stickerpacks){
            if(stickerpack.identifier.equals(identifier)){
                promise.resolve(stickerpack.imageDataVersion);
                return;
            }
        }
    }
    
    @ReactMethod
    public void GetPackVersion(String identifier,Promise promise){

        String v = GetVersion(identifier);
        
        // if(v==0){
        //     promise.resolve(v);
        // }

        promise.resolve(v);
        

    }
    @ReactMethod
    public void GetPackInfo(String identifier,Promise promise){
        final String PROVIDER_NAME = "com.kot1.stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        final String URL = "content://" + PROVIDER_NAME + "/metadata/" + identifier ;
        final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);

        Cursor cursor = reactContext.getContentResolver().query(
            STICKER_CONTENT_PROVIDER_URI, 
            null, 
            null,
            null,
            null 
        );
        
        int count = cursor.getCount();
        String[] names = cursor.getColumnNames();
        
        if(count==0){
            promise.reject("error","no pack info found");
        }

        for(int i=0;i<count;i++){
            cursor.moveToPosition(i);
            for(int j=0;j<names.length;j++){
                Log.d("ReactNative",names[j] + " : "+cursor.getString(j));
                int ind = cursor.getColumnIndex("image_data_version");
                String ver = cursor.getString(ind);
                promise.resolve(ver);

            }
        }
        promise.resolve("count " + count);
        
    }
    @ReactMethod
    public void PacksCountCursor(Promise promise){
        // final String PROVIDER_NAME = "com.kot1.stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        final String PROVIDER_NAME = getContentProviderAuthority(reactContext)+".stickercontentprovider";
        final String URL = "content://"+ PROVIDER_NAME + "/metadata" ;
        final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);

        Cursor cursor = reactContext.getContentResolver().query(
            STICKER_CONTENT_PROVIDER_URI, 
            null, 
            null,
            null,
            null 
        );
        
        int count = cursor.getCount();
        String[] names = cursor.getColumnNames();
        
        for(String s:names){

            Log.d("ReactNative"," columns "+s);
        }
        promise.resolve(count);
        
    }
    @ReactMethod
    public void ResizeStaticImages(String path, Promise promise){
        int quality = 70;
        try{
            String inFile = Uri.parse(path).toString();
            String outFile = createOutputFile();
            Bitmap bitmap = BitmapFactory.decodeFile(inFile);
            

            Log.d(TAG,"making512 ");
            // Bitmap bitmap_512 = scalePreserveRatio(bitmap,512,512);
            Bitmap bitmap_512 = scalePreserveRatioM(bitmap,512,512);
            if(bitmap_512 == null){
                Log.d(TAG,"null  ");
                
            }else{
                Log.d(TAG,"no null  ");

            }
            Bitmap.CompressFormat format;
            if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
                if(quality==100){
                    format = WEBP_LOSSLESS;
                }else{
                    format = WEBP_LOSSY;
                }
            }else{
                format = WEBP;
            }

            try(FileOutputStream fos = new FileOutputStream(outFile) ){
                bitmap_512.compress(format, quality, fos);
            }catch(IOException e){
                Log.d(TAG, "IOException : file exits?"+(new File(outFile).exists()));
                promise.reject("ResizeStaticImages : Could not compress  ");
            }

            promise.resolve(outFile);

        }catch(Exception e){
            promise.resolve("❌Error in Native :"+e.getMessage());
        }

    }
    
    @ReactMethod
    public void ResizeAnimatedImages(String path, Promise promise){
        try{
            File inFile = new File(Uri.parse(path).toString());
            WebpAnimatedDecoder decoder = new WebpAnimatedDecoder(inFile);
            List<ProcessedFrames> processedFrames = decoder.decode();


            String outFile = createOutputFile();
            WebpBitmapEncoder encoder = new WebpBitmapEncoder(new File(outFile));
            encoder.setLoops(0);
            
            for(int i = 0; i < processedFrames.size(); i++){
                ProcessedFrames processedFrame = processedFrames.get(i);
                Bitmap bitmap = processedFrame.bitmap;
                // Bitmap bitmap_512 = scalePreserveRatio(bitmap,512,512);
                // Bitmap bitmap_512 = scalePreserveRatio(bitmap,512,512);
                Bitmap bitmap_512 = scalePreserveRatioM(bitmap,512,512);
                // bitmap recycle;
                Log.d(TAG, "delay "+processedFrame.delay);
                encoder.setDuration(processedFrame.delay < 80 ? 80:processedFrame.delay); //TODO add real delay
                encoder.writeFrame(bitmap_512,70);
            }
            decoder.close();
            encoder.close();
            promise.resolve(outFile);
        }catch(Exception e){
            promise.resolve("❌Error in Native :"+e.getMessage());
        }

    }
    public static Bitmap scalePreserveRatio2(Bitmap imageToScale, int destinationWidth, int destinationHeight) {
        if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            int width = imageToScale.getWidth();
            int height = imageToScale.getHeight();
    
            float widthRatio = (float) destinationWidth / (float) width;
            float heightRatio = (float) destinationHeight / (float) height;
    
            float scaleFactor = Math.min(widthRatio, heightRatio);
    
            int finalWidth = (int) (width * scaleFactor);
            int finalHeight = (int) (height * scaleFactor);
    
            // Scale the image using the calculated dimensions
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, true);
    
            return imageToScale;
        } else {
            return imageToScale;
        }
    }
    private String createOutputFile() {

        String newFileName = getUniqueName();
        
        File sd = Environment.getExternalStorageDirectory();
        File down = new File(sd,Environment.DIRECTORY_DOWNLOADS+"/output");
        File file = new File(down,newFileName);

        // Check if the file exists
        if (!file.exists()) {
            try {
                // Create the file and its parent directories if they don't exist
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                // Create the actual file
                file.createNewFile();

                Log.d(TAG,"File created: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Error creating the file: " + e.getMessage());
            }
        } else {
            Log.d(TAG,"File already exists: " + file.getAbsolutePath());
        }
        return file.getAbsolutePath();
    }
    private String getUniqueName() {
        //
        return "out_"+ UUID.randomUUID().toString() + ".webp";
    }
    @ReactMethod
    public void GetPackList_Hawk(Promise promise){
        List<StickerPack> stickerpacks = Hawk.get("sticker_packs");
        WritableArray array = Arguments.createArray();
        if(stickerpacks.size()==0){
            promise.reject("no pack list in Hawk");
        }else{
            for(StickerPack stickerpack : stickerpacks){
                array.pushString(stickerpack.identifier);
            }
            promise.resolve(array);
        }
    }

    @ReactMethod
    public void NewTest(String stickerPackIdentifier,  Promise promise){
        String filesDir = reactContext.getApplicationContext().getFilesDir().getPath();
        String imagePath = filesDir + "/stickers_asset";
        
        Log.d(TAG,"...filesDir "+filesDir);
        //
        final String STICKERS_ASSET = "stickers_asset";
        final int STICKER_PACK_TRAY_ICON_CODE = 5;

        // Uri trayIconUri = Uri.withAppendedPath(
        // Uri.parse("content://"+getContentProviderAuthority(reactContext2)+"/stickers_asset/"+stickerPackIdentifier+"sticker_pack_tray_icon/5"), // Replace with your authority
        // "sticker_pack_tray_icon/" + stickerPackIdentifier);
        final String PROVIDER_NAME = getContentProviderAuthority(reactContext);
        final String URL = "content://" + PROVIDER_NAME + "/stickers_asset/tray_Cuppy.webp/5" ;
        final Uri trayIconUri = Uri.parse(URL);

        // Query the content provider for the tray icon
        Cursor cursor = reactContext.getContentResolver().query(trayIconUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // The column containing the tray icon data
            int iconDataColumnIndex = cursor.getColumnIndexOrThrow("sticker_pack_icon"); // Replace with the actual column name

            // Retrieve the tray icon data
            // byte[] trayIconData = cursor.getBlob(iconDataColumnIndex);

            // Now you can use trayIconData to display the tray icon in your UI
            Log.d(TAG,"tag "+iconDataColumnIndex);
        }

        if (cursor != null) {
            cursor.close();
        }
        promise.resolve("..");
    }
    @ReactMethod
    public void ListFiles(Promise promise) {
        // String directoryPath = "/data/user/0/com.stickerapp/files/stickers_asset/";
        String directoryPath = reactContext.getApplicationContext().getFilesDir().getPath();
        directoryPath += "/stickers_asset/";
         
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            WritableArray resultArray = Arguments.createArray();
            
            for (File folder : directory.listFiles()) {
                if (folder.isDirectory()) {
                    WritableMap folderMap = Arguments.createMap();
                    folderMap.putString("packName", folder.getName());

                    WritableArray filesArray = Arguments.createArray();
                    for (File file : folder.listFiles()) {
                        if (file.isFile()) {
                            filesArray.pushString(file.getAbsolutePath());
                        }
                    }

                    folderMap.putArray("stickers", filesArray);
                    resultArray.pushMap(folderMap);
                }
            }

            promise.resolve(resultArray);
        } else {
            promise.reject("DIRECTORY_NOT_FOUND", "Directory not found");
        }
    }

    
    // public void checkPermission(Promise promise){
    //     if (ContextCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    // }
    @ReactMethod
    public void GetMetaData(Promise promise){
        // final String PROVIDER_NAME = "com.kot1.stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        // final String PROVIDER_NAME = "com.stickerapp.stickercontentprovider";
        // final String PROVIDER_NAME = reactContext2.getPackageName()+".stickercontentprovider";
        final String PROVIDER_NAME = getContentProviderAuthority(reactContext2);
        final String URL = "content://" + PROVIDER_NAME + "/metadata" ;
        final Uri STICKER_CONTENT_PROVIDER_URI = Uri.parse(URL);

        // Log.d(TAG," _"+BuildConfig.APPLICATION_ID + ".stickercontentprovider");
        Log.d(TAG," __"+reactContext.getPackageName() + ".stickercontentprovider");
        Cursor cursor = reactContext.getContentResolver().query(
            STICKER_CONTENT_PROVIDER_URI, 
            null, 
            null,
            null,
            null 
        );
        
        int count = cursor.getCount();
        
        if(count==0){
            promise.reject("no pack list found ni contentProvider");
        }

        WritableArray array = Arguments.createArray();

        for (String columnName : cursor.getColumnNames()) {
            // Print each column name
            Log.d(TAG,"Column name: "+ columnName);  
        }

        while (cursor.moveToNext()) {
            String identifier = cursor.getString(cursor.getColumnIndex("sticker_pack_identifier"));
            String version = cursor.getString(cursor.getColumnIndex("image_data_version"));
            String animated_sticker_pack = cursor.getString(cursor.getColumnIndex("animated_sticker_pack"));
            String sticker_pack_icon = cursor.getString(cursor.getColumnIndex("sticker_pack_icon"));
            
            WritableMap map = Arguments.createMap();
            map.putString("identifier",identifier);
            map.putString("version",version);
            map.putString("animated_sticker_pack",animated_sticker_pack);
            map.putString("sticker_pack_icon",sticker_pack_icon);
            
            array.pushMap(map);

            // Log.d("ReactNative","identifier : "+identifier);
        }
        

        promise.resolve(array);
        
    }
    @ReactMethod
    public void PacksCountHawk(Promise promise){
        List<StickerPack> sp = Hawk.get("sticker_packs");
        if(sp==null)
            promise.resolve(0);
        else
            promise.resolve(sp.size());
    }
    
    private StickerPack extractPack(List<StickerPack> stickerpacks, String identifier){

        for (StickerPack stickerPack : stickerpacks) {
            if(stickerPack.identifier.equals(identifier)){
                Log.d("ReactNative","sticker pack found ");
                return stickerPack;
            }
        }
        return null;
    }

    

    @ReactMethod
    public void GetWhatsAppStickersFiles(boolean limit, Promise promise){
        
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Stickers/");
        folder.mkdirs();
        File[] allFiles = folder.listFiles();

        List<File> filesList = new ArrayList<File>();
        filesList.addAll(Arrays.asList(allFiles));
        // File[] allFiles = folder.listFiles(new FilenameFilter() {
            // public boolean accept(File dir, String name) {
                // return name.endsWith(".webp");
            // }
        // });
        // Arrays.sort(allFiles, new Comparator<File>() {
            // @Override
            // public int compare(File f1, File f2) {
                // return Long.compare(f1.lastModified(), f2.lastModified());
                // For descending
                // return -Long.compare(f1.lastModified(), f2.lastModified());
            // }
        // });

        // Arrays.sort(allFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        Arrays.sort(allFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        if(limit){
            allFiles = Arrays.copyOfRange(allFiles, 0, 30);
        }
        
        
        WritableArray array = new WritableNativeArray();
        
        for(File file : allFiles){
            String uri = file.getAbsolutePath();
            boolean isAnimated = isWebpAnimated(uri);
            WritableMap map = Arguments.createMap();
            map.putString("uri",uri);
            map.putBoolean("isAnimated",isAnimated);
            array.pushMap(map);
            // android.util.Log.d("ReactNative","looking "+file.getAbsolutePath());
        }

        // WritableArray array = Arguments.createArray();
        promise.resolve(array);
        
    }
    
    @ReactMethod
    public void looking(Promise promise){

        // android.util.Log.d("ReactNative","looking "+Environment.getDataDirectory());
        // android.util.Log.d("ReactNative","looking "+Environment.getExternalStorageDirectory());
        // File[] files = new File("/storage/emulated/0/WhatsApp/Media/WhatsApp Stickers").listFiles();


        
        // File[] files = new File(reactContext.getFilesDir()+"/stickers_asset").listFiles();

        // for (File file : files) {
        //     if(file.isDirectory()){
        //         // file.delete();
        //         android.util.Log.d("ReactNative"," FOLDER: "+file.getAbsolutePath());
        //     }else{
        //         android.util.Log.d("ReactNative"," FILE: "+file.getAbsolutePath());
                
        //     }
        // }
        
        promise.resolve("array");
    }	
    

    
    public static Bitmap scalePreserveRatio(Bitmap imageToScale, int destinationWidth,
        int destinationHeight) {
            // NOTE 
        if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            int width = imageToScale.getWidth();
            int height = imageToScale.getHeight();

            //Calculate the max changing amount and decide which dimension to use
            float widthRatio = (float) destinationWidth / (float) width;
            float heightRatio = (float) destinationHeight / (float) height;

            //Use the ratio that will fit the image into the desired sizes
            int finalWidth = (int)Math.floor(width * widthRatio);
            int finalHeight = (int)Math.floor(height * widthRatio);
            if (finalWidth > destinationWidth || finalHeight > destinationHeight) {
                finalWidth = (int)Math.floor(width * heightRatio);
                finalHeight = (int)Math.floor(height * heightRatio);
            }

            //Scale given bitmap to fit into the desired area
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, true);

            //Created a bitmap with desired sizes
            Bitmap scaledImage = Bitmap.createBitmap(destinationWidth, destinationHeight, Bitmap.Config.ARGB_8888);
            
            Canvas canvas = new Canvas(scaledImage);

            //Draw background color
            Paint paint = new Paint();
            paint.setColor(Color.BLACK); // if transparent then ecnoder must read ALPH
            // paint.setColor(Color.TRANSPARENT); // if transparent then ecnoder must read ALPH
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            //Calculate the ratios and decide which part will have empty areas (width or height)
            float ratioBitmap = (float)finalWidth / (float)finalHeight;
            float destinationRatio = (float) destinationWidth / (float) destinationHeight;
            float left = ratioBitmap >= destinationRatio ? 0 : (float)(destinationWidth - finalWidth) / 2;
            float top = ratioBitmap < destinationRatio ? 0: (float)(destinationHeight - finalHeight) / 2;
            canvas.drawBitmap(imageToScale, left, top, null);

            return scaledImage;
        } else {
            return imageToScale;
        }
    }

    public static Bitmap scalePreserveRatioM(Bitmap imageToScale, int destinationWidth, int destinationHeight) {
        if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            int width = imageToScale.getWidth();
            int height = imageToScale.getHeight();

            // Calculate the max changing amount and decide which dimension to use
            float widthRatio = (float) destinationWidth / (float) width;
            float heightRatio = (float) destinationHeight / (float) height;

            // Use the ratio that will fit the image into the desired sizes
            float minRatio = Math.min(widthRatio, heightRatio);
            int finalWidth = (int) Math.floor(width * minRatio);
            int finalHeight = (int) Math.floor(height * minRatio);

            // Scale given bitmap to fit into the desired area
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, true);

            // Create a bitmap with desired sizes
            Bitmap scaledImage = Bitmap.createBitmap(destinationWidth, destinationHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(scaledImage);

            // Draw background color
            Paint paint = new Paint();
            paint.setColor(Color.TRANSPARENT); // if transparent then encoder must read ALPHA
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            // Calculate the ratios and decide which part will have empty areas (width or height)
            float ratioBitmap = (float) finalWidth / (float) finalHeight;
            float destinationRatio = (float) destinationWidth / (float) destinationHeight;
            float left = ratioBitmap >= destinationRatio ? 0 : (float) (destinationWidth - finalWidth) / 2;
            float top = ratioBitmap < destinationRatio ? 0 : (float) (destinationHeight - finalHeight) / 2;
            canvas.drawBitmap(imageToScale, left, top, null);

            return scaledImage;
        } else {
            return imageToScale;
        }
    }
    @ReactMethod
    public void getImageSize(String path, Promise promise){
        Uri uri = Uri.parse(path);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = reactContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);  input.close();
            Log.d("ReactNative","width : "+String.valueOf(options.outWidth));
            Log.d("ReactNative","width : "+String.valueOf(options.outHeight));
            long len = new File(path).length();
            Log.d("ReactNative","len : "+String.valueOf(len));
            // return new int[]{options.outWidth, options.outHeight};
        }catch (Exception e){}
        // return new int[]{0,0};
    }
    
    @ReactMethod
    public void addNewSticker(String path, String identifier, Promise promise) throws IOException {
        final String PROVIDER_NAME = "com.kot1.stickercontentprovider";//"org.geometerplus.android.fbreader.interfaces.SendDataProvider";
        final String URL = "content://" + PROVIDER_NAME + "/metadata"+"/"+identifier;
        final Uri CONTENT_URI = Uri.parse(URL);
        
        File file = getCopyOf(path);
        // File file = new File(path);

        List<String> mEmojis = new ArrayList<>();
        mEmojis.add("😀");
        mEmojis.add("😁");

        List<StickerPack> stickerPacks = new ArrayList<StickerPack>();
        stickerPacks.addAll(Hawk.get("sticker_packs", new ArrayList<StickerPack>()));
        
        // List<Sticker> stk = Hawk.get(identifier);
        boolean flag = false;
        for (StickerPack stickerPack : stickerPacks) {
            
            if(stickerPack.identifier.equals(identifier)){
                // hold object
                Log.d("ReactNative","sticker pack found ");
                
                StickerPack tStickerPack = stickerPack;
                int nextVesrion = Integer.parseInt(tStickerPack.imageDataVersion) + 1;
                tStickerPack.setimageDataVersion(String.valueOf(nextVesrion));
                
                new DownloadImage().execute(
                    getLastBitFromUrl(
                        file.getAbsolutePath()).replace(".png", ".webp"
                    ), 
                    identifier,
                    identifier
                    );
                
                List<Sticker> mStickers = tStickerPack.getStickers();
                mStickers.add(new Sticker(getLastBitFromUrl(file.getAbsolutePath()).replace(".png", ".webp"), mEmojis));
                // tStickerPack.setStickers(mStickers);
                // tStickerPack.get(tStickerPack.size() - 1).setStickers(Hawk.get(identifier, new ArrayList<Sticker>()));
                tStickerPack.setStickers(mStickers);
                
                

                // remove object from list
                // stickerPacks.remove(stickerPack);
                
                // put all stickerpack back
                Hawk.put("sticker_packs", stickerPacks);

                // add stickers files
                Hawk.put(identifier, mStickers);

                // reactContext.getContentResolver().notifyChange(Uri.parse(URL+"/"+file.getAbsolutePath()), null);

                break;
            }
        }

        // if(flag){
            // Hawk.get(identifier);
        // }
        promise.resolve("resolved ");
        // return;
        // init a sticker object

        // save to Cache Dir
        // new DownloadImage().execute(
        //     getLastBitFromUrl(
        //         file.getAbsolutePath()).replace(".png", ".webp"
        //     ), 
        //     identifier,
        //     identifier
        //     );

        // mStickers.add(new Sticker(getLastBitFromUrl(filePath).replace(".png", ".webp"), mEmojis));

        // Sticker sticker = new Sticker(identifier, file.getAbsolutePath(), emojis);
        // Hawk.put(stickerPack.getString("identifier"), mStickers);

        // promise.resolve("resolved:addnewsticker"); 
    }

    private void deleteRecursive(File dir) {

        if (dir.isDirectory()) 
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
              new File(dir, children[i]).delete();
            }
        }
    
    }
    public void deletePackFromDir(String identifier){
        
        File[] files = new File(reactContext.getFilesDir()+"/stickers_asset").listFiles();
        
        for (File file : files) {
            if(file.exists() && file.isDirectory()){
                file.delete();
                android.util.Log.d("ReactNative","Deleted FOLDER: "+file.getAbsolutePath());
            }else{
                android.util.Log.d("ReactNative"," FILE: "+file.getAbsolutePath());
                
            }
        }
        
    }
    public String getAppFilesDir() {
        return reactContext.getApplicationContext().getFilesDir().getPath();
    }

    public String getStickersDir() {
        return getAppFilesDir() + "/stickers_asset/"; 
    }

    public boolean deleteDirectory(File directory) {
            if (!directory.exists()) {
                return true;
            }

            if (!directory.isDirectory()) {
                return false;
            }

            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "file to delte "+file.getAbsolutePath());
                    if (file.isDirectory()) {
                        // Recursively delete subdirectories
                        deleteDirectory(file);
                    } else {
                        // Delete files
                        if (!file.delete()) {
                            return false;
                        }
                    }
                }
            }

            // Delete the empty directory itself
            return directory.delete();
    }
    
    // @ReactMethod
    // public void log(Promise promise){
    //     promise.resolve(Const.getClassName(this));
    // }
    @ReactMethod
    public void CleanHawk(Promise promise){
        try{
            // boolean result = reactContext.deleteFile(str);
            HawkUtil.DeleteAllPacks();
            promise.resolve("");
        }catch (Exception e){
            promise.reject(e.toString());
        }
        
    }
    @ReactMethod
    public void CleanFile(Promise promise){
        try{
            // boolean result = reactContext.deleteFile(str);
            FileUtil.DeleteAllPacks(STICKER_PACKS_DIR);
            promise.resolve("");
        }catch (IOException  e){
            promise.reject(e.toString());
        }

        // //
        // String stickerAssetDir = getAppFilesDir() + "/"+"stickers_asset";
        // boolean flag = deleteDirectory(new File(stickerAssetDir));
        // promise.resolve("cleared "+stickerAssetDir);

    }

    @ReactMethod
    public void DeletePack_Hawk(String identifier, Promise promise) {
        // TODO
        try{
            HawkUtil.DeletePack(identifier);
            promise.resolve("resolved");
        }catch(Exception e){
            promise.reject("DeletePack in hawk "+e.toString());
        }

    }
    @ReactMethod
    public void DeletePack_File(String identifier, Promise promise) {
        // TODO
        try{
            FileUtil.DeletePack(STICKER_PACKS_DIR, identifier);
            promise.resolve("resolved");
        }catch(IOException e){
            promise.reject("DeletePack in files "+e.toString());
        }

    }
    
    @ReactMethod
    public void delOne(String removable, String identifier, Promise promise) throws IOException {
        List<StickerPack> Allpacks = new ArrayList<StickerPack>();
        List<Sticker> mStickers = new ArrayList<>();
        List<String> mEmojis = new ArrayList<>();
        mEmojis.add("😀");
        mEmojis.add("😁");

        // Log.d("ReactNative","A");
        
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
            if(index < 0)
                promise.reject("sticker pack not found");
                // return;
            else{
                mStickers.addAll(Hawk.get(identifier));
                
                for (Sticker sticker : mStickers) {
                    if(sticker.getContent().equals(removable)){
                        Log.d("ReactNative"," 📩 sticker detceted ");
                        mStickers.remove(sticker);
                        break;
                    }
                }
                // for(int i = 0; i < mStickers.size(); i++){
                    // Log.d("ReactNative"," : "+mStickers.get(i).getContent()+" | "+removable);
                    // if(mStickers.get(i).getContent().equals(removable)){
                        // mStickers.remove(i);
                        // Log.d("ReactNative"," 📩 sticker removed "+mStickers.get(i).getContent()+" | "+removable);
                        // break;
                    // }
                // }
                // mStickers.remove(removable);

                Hawk.put(identifier,mStickers);

                newPack.setimageDataVersion(String.valueOf(Integer.parseInt(newPack.imageDataVersion) + 1)); 
                newPack.setStickers(mStickers);
                Allpacks.set(index, newPack);
                // Allpacks.add(newPack);     
                
                Hawk.put("sticker_packs", Allpacks);
                promise.resolve("sticker pack updated");
            }
            
            // promise.resolve("resolved early "+newPack.identifier+" "+newPack.trayImageFile+" "+newPack.imageDataVersion +" "+newPack.animatedStickerPack);
            // // : create JSONArray from JSONObject stickerPack
            


            // mStickers.add(new Sticker(path.replace(".png", ".webp"),mEmojis));


            promise.resolve("resolved later ");
            // mStickers.addAll(Hawk.get(identifier),new ArrayList<>());
            // // : Then donwload icon from JSON ojbect and stickers from JSONArray
                        

            // for (int j = 0; j < stickers.length(); j++) {
            //     JSONObject jsonStickersChildNode = stickers.getJSONObject(j);
            //     new DownloadImage().execute(
            //             jsonStickersChildNode.getString("image_file"), 
            //             stickerPack.getString("identifier"),
            //             stickerPack.getString("name")
            //             );
            //     mStickers.add(new Sticker(
            //     getLastBitFromUrl(jsonStickersChildNode.getString("image_file")).replace(".png", ".webp"), mEmojis));
            // }
            
            

            // Hawk.put(stickerPack.getString("identifier"), mStickers);

            // stickerPacks.get(stickerPacks.size() - 1).setAndroidPlayStoreLink("https://play.google.com/store/apps/details?id=com.dawwati");
            // stickerPacks.get(stickerPacks.size() - 1).setStickers(Hawk.get(stickerPack.getString("identifier"), new ArrayList<Sticker>()));
            // Hawk.put("sticker_packs", stickerPacks);
            // Hawk.put("dawwati_ping", "PONG");

            // // container for storing size as int and pack as string 
            // // WritableMap info = Arguments.createMap();

            // WritableArray array = Arguments.createArray();

            // array.pushString(String.valueOf(stickerPacks.size()));
            // array.pushString(stickerPacks.get(stickerPacks.size()-1).getContent().toString());
            // promise.resolve(array);
            // promise.resolve(stickerPacks.size() + stickerPacks.get(0).getContent().toString());
        } catch (Exception e) {
        //     e.printStackTrace();
        //     promise.reject(ERROR_ADDING_STICKER_PACK, e);
        }

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
            
            // promise.resolve("resolved early "+newPack.identifier+" "+newPack.trayImageFile+" "+newPack.imageDataVersion +" "+newPack.animatedStickerPack);
            // // : create JSONArray from JSONObject stickerPack
            


            // mStickers.add(new Sticker(path.replace(".png", ".webp"),mEmojis));


            // promise.resolve("resolved later ");
            // mStickers.addAll(Hawk.get(identifier),new ArrayList<>());
            // // : Then donwload icon from JSON ojbect and stickers from JSONArray
                        

            // for (int j = 0; j < stickers.length(); j++) {
            //     JSONObject jsonStickersChildNode = stickers.getJSONObject(j);
            //     new DownloadImage().execute(
            //             jsonStickersChildNode.getString("image_file"), 
            //             stickerPack.getString("identifier"),
            //             stickerPack.getString("name")
            //             );
            //     mStickers.add(new Sticker(
            //     getLastBitFromUrl(jsonStickersChildNode.getString("image_file")).replace(".png", ".webp"), mEmojis));
            // }
            
            

            // Hawk.put(stickerPack.getString("identifier"), mStickers);

            // stickerPacks.get(stickerPacks.size() - 1).setAndroidPlayStoreLink("https://play.google.com/store/apps/details?id=com.dawwati");
            // stickerPacks.get(stickerPacks.size() - 1).setStickers(Hawk.get(stickerPack.getString("identifier"), new ArrayList<Sticker>()));
            // Hawk.put("sticker_packs", stickerPacks);
            // Hawk.put("dawwati_ping", "PONG");

            // // container for storing size as int and pack as string 
            // // WritableMap info = Arguments.createMap();

            // WritableArray array = Arguments.createArray();

            // array.pushString(String.valueOf(stickerPacks.size()));
            // array.pushString(stickerPacks.get(stickerPacks.size()-1).getContent().toString());
            // promise.resolve(array);
            // promise.resolve(stickerPacks.size() + stickerPacks.get(0).getContent().toString());
        } catch (Exception e) {
        //     e.printStackTrace();
        //     promise.reject(ERROR_ADDING_STICKER_PACK, e);
        }

    }
    public String getRandomFileName() {
        return UUID.randomUUID().toString();
    }
    

    private String saveGIF(File src)
    {
        try
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "shared_gif_shai" + System.currentTimeMillis() + ".webp");

            long startTime = System.currentTimeMillis();

            Log.d(TAG, "on do in background, url open connection");

            InputStream is = new FileInputStream(src);
            Log.d(TAG, "on do in background, url get input stream");
            BufferedInputStream bis = new BufferedInputStream(is);
            Log.d(TAG, "on do in background, create buffered input stream");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.d(TAG, "on do in background, create buffered array output stream");

            byte[] img = new byte[1024];

            int current = 0;

            Log.d(TAG, "on do in background, write byte to baos");
            while ((current = bis.read()) != -1) {
                baos.write(current);
            }


            Log.d(TAG, "on do in background, done write");

            Log.d(TAG, "on do in background, create fos");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());

            Log.d(TAG, "on do in background, write to fos");
            fos.flush();

            fos.close();
            is.close();
            Log.d(TAG, "on do in background, done write to fos");
            return file.getAbsolutePath();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "on do in background, error "+e.getMessage());
            return null;
        }
    }
    
    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src); 
             OutputStream out = new FileOutputStream(dst);) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
        }catch(Exception e){
            Log.d("ReactNative","error copying buf[]  "+e.toString());
        }
      }

      public String CopyToCache(String source, String folder) {

        // File target = new File(folder + "/"+ getRandomFileName() + ".webp");
        String  random = getRandomFileName();
        try{
            Log.d("ReactNative","Random "+random);
            File outputDir = reactContext.getCacheDir(); // context being the Activity pointer
            File outputFile = File.createTempFile(getRandomFileName(), ".webp", outputDir);
            android.util.Log.d("ReactNative","outputFile "+outputFile.getAbsolutePath());
            File sourceFile = new File(source);
            android.util.Log.d("ReactNative","sourceFile "+sourceFile.getAbsolutePath());
            
            copy(sourceFile, outputFile);
            return sourceFile.getAbsolutePath();
        }catch(Exception e){
          Log.d("ReactNative","ioexceptin in copyToCache "+e.toString());
          return null;
        }
        
      }

    @ReactMethod
    public void addImage(String source, String identifier, int isAnimated, String folder, Promise promise) throws IOException {
    
    }
    // TODO Rename and separet methods as :
    //      addStickerFromLocalStorage() to add sticker from local storage
    //      addStickerFromWhatsAPPFolder() for stickers from WhatsApp folder
    // verify if images meet requirements and add to sticker pack
    // OR Resize and then verify if images are random
    // Dont resize if images are from wa folder or readymade packs from cloud 

    /**
     * @param source
     * @param identifier
     * @param isAnimated
     * @param folder
     * @param promise
     * @throws IOException
     */
    @ReactMethod
    public void addOne(String source, String identifier, int isAnimated, String folder, Promise promise) throws IOException {
        List<StickerPack> Allpacks = new ArrayList<StickerPack>();
        List<Sticker> mStickers = new ArrayList<>();
        List<String> mEmojis = new ArrayList<>();
        mEmojis.add("😀");
        mEmojis.add("😁");


        Log.d("ReactNative","addOne() adding one sticker to  "+identifier);
        
        // String path = CopyToCache(source,folder);
        // String path = saveGIF(new File(source));
        if (!new File(source).exists()){
            promise.reject(ERROR_ADDING_STICKER_PACK, "File not found");
            return;
        }

        String path = "file://"+getCopyOf(source).getAbsolutePath();
        
        if(new File(path) == null){
            promise.reject(ERROR_ADDING_STICKER_PACK, new Exception("Error copying file to cache"));
            return;
        }else{
            Log.d("ReactNative","addOne() File found.. adding one sticker to  "+identifier+" path "+path);
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
                    promise.reject("sticker pack not found");
                    return;
                }else{

                    mStickers.addAll(Hawk.get(identifier));
                    
                    if(isAnimated == 1){
                        Log.d("ReactNative","Download: isAnimated "+isAnimated);
                        new DownloadImageAnimated().execute(
                            path, 
                            identifier,
                            identifier
                            );
                    }else{  
                        Log.d("ReactNative","Download: isAnimated "+isAnimated);

                        new DownloadImage().execute(
                            path, 
                            identifier,
                            identifier
                            );
                    }

                    mStickers.add(new Sticker(
                        getLastBitFromUrl(path).replace(".png", ".webp"), 
                        mEmojis));

                    Hawk.put(identifier,mStickers);
                        
                    newPack.setimageDataVersion(String.valueOf(Integer.parseInt(newPack.imageDataVersion) + 1)); 
                    newPack.setStickers(mStickers);
                    Allpacks.set(index, newPack);
                    // Allpacks.add(newPack);     
                    Hawk.put("sticker_packs", Allpacks);
                    promise.resolve("sticker pack updated");
                } // end else; should run if index not 0 
                        
                promise.resolve("resolved later ");
            // end try     
            } catch (Exception e) {
                promise.reject(ERROR_ADDING_STICKER_PACK, e.toString());
            }
        
        } // end of else ; run if file exists

    }

    @ReactMethod
    public void isAnimatedSticker(String path, Promise promise) {
        boolean bool = isWebpAnimated(path);
        String str = String.valueOf(bool);
        // Log.d("ReactNative","isAnimatedSticker ");
        
        promise.resolve(String.valueOf(bool));
    }

    @ReactMethod
    public void prepare(String stickerPackParam, Promise promise) {
        List<StickerPack> stickerPacks = new ArrayList<StickerPack>();
        List<Sticker> mStickers = new ArrayList<>();
        List<String> mEmojis = new ArrayList<>();
        mEmojis.add("😀");
        mEmojis.add("😁");



        try {
            //: create JSOONObject from json string (decorated in RN)
            JSONObject stickerPack = new JSONObject(stickerPackParam);
            // Log.d("ReactNative","toString "+stickerPack.toString());

            //: get list of stickerpack objects and add to List<StickerPack> stickerPacks
            stickerPacks.addAll(Hawk.get("sticker_packs", new ArrayList<StickerPack>()));
            
            //: create new STickerPAck class passing JSONObject stickerPack 
            //: then add one more StickerPack to List<StickerPack> stickerPacks
            Log.d("ReactNative","A stickerPack.getString(name) "+stickerPack.getString("name"));
            Log.d("ReactNative","A stickerPack.getString(identifier) "+stickerPack.getString("identifier"));

            stickerPacks.add(new StickerPack(
                    stickerPack.getString("identifier"), 
                    stickerPack.getString("name"),
                    stickerPack.getString("publisher"),
                    getLastBitFromUrl(stickerPack.getString("tray_image_file")).replace(" ", "_").replace(".png",".webp"),
                    stickerPack.getString("publisher_email"), 
                    stickerPack.getString("publisher_website"),
                    stickerPack.getString("privacy_policy_website"), 
                    stickerPack.getString("license_agreement_website"),
                    stickerPack.getString("image_data_version"),// #373
                    stickerPack.getBoolean("avoid_cache"),// #373
                    stickerPack.getBoolean("animated_sticker_pack")) // #373
                );
            
            // : create JSONArray from JSONObject stickerPack
            JSONArray stickers = stickerPack.getJSONArray("stickers");
            // : Then donwload icon from JSON ojbect and stickers from JSONArray
            
            if(stickerPack.getBoolean("animated_sticker_pack")){
              new DownloadImageAnimated().execute(
                    stickerPack.getString("tray_image_file"), 
                    stickerPack.getString("identifier"),
                    stickerPack.getString("name")
                );
              
              // iterate through each image files from "stickers" key in input json
              for (int j = 0; j < stickers.length(); j++) {
                  JSONObject jsonStickersChildNode = stickers.getJSONObject(j);
                  new DownloadImageAnimated().execute(jsonStickersChildNode.getString("image_file"), stickerPack.getString("identifier"),
                          stickerPack.getString("name"));
                  mStickers.add(new Sticker(
                          getLastBitFromUrl(jsonStickersChildNode.getString("image_file")).replace(".png", ".webp"), mEmojis));
              }
            }else{
              new DownloadImage().execute(
                stickerPack.getString("tray_image_file"), 
                stickerPack.getString("identifier"),
                stickerPack.getString("name")
                );
              for (int j = 0; j < stickers.length(); j++) {
                  JSONObject jsonStickersChildNode = stickers.getJSONObject(j);
                  new DownloadImage().execute(
                          jsonStickersChildNode.getString("image_file"), 
                          stickerPack.getString("identifier"),
                          stickerPack.getString("name")
                          );
                          

                  mStickers.add(new Sticker(
                    getLastBitFromUrl(jsonStickersChildNode.getString("image_file")).replace(".png", ".webp"), mEmojis));
              }
            }
            

            Hawk.put(stickerPack.getString("identifier"), mStickers);

            stickerPacks.get(stickerPacks.size() - 1).setAndroidPlayStoreLink("https://play.google.com/store/apps/details?id=com.dawwati");
            stickerPacks.get(stickerPacks.size() - 1).setStickers(Hawk.get(stickerPack.getString("identifier"), new ArrayList<Sticker>()));
            Hawk.put("sticker_packs", stickerPacks);
            Hawk.put("dawwati_ping", "PONG");

            // container for storing size as int and pack as string 
            // WritableMap info = Arguments.createMap();

            // WritableArray array = Arguments.createArray();
            // array.pushString(String.valueOf(stickerPacks.size()));
            // array.pushString(stickerPacks.get(stickerPacks.size()-1).getContent().toString());
            WritableMap response = Arguments.createMap();
            response.putString("a",stickerPacks.get(stickerPacks.size()-1).getContent().toString());
            response.putInt("size",stickerPacks.size());
            promise.resolve(response);
            // promise.resolve(stickerPacks.size() + stickerPacks.get(0).getContent().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("ReactNative","key "+e.getMessage());

            promise.reject(ERROR_ADDING_STICKER_PACK, e);
        }
    }



    public void getMethods(String s, Class c){
        try {
            // create class object
            // Class c = WritableMap.class;
  
            // get list of methods
            Method[] methods = c.getMethods();
  
            // get the name of every method present in the list
            for (Method method : methods) {
  
                String MethodName = method.getName();
                Log.d(TAG,s+" Name of the method: " + MethodName);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean isAnimated(String src) {
        File file = new File(src);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try{
                ImageDecoder.Source source = ImageDecoder.createSource(file);
                Drawable drawable = ImageDecoder.decodeDrawable(source);
                boolean result = drawable instanceof AnimatedImageDrawable;    
                return result;
            }catch(Exception e){
                // TODO
            }
        } else {
            try(FileInputStream input = new FileInputStream(file)){
                Movie movie = Movie.decodeStream(input);
                return movie != null;
            }catch(Exception e) {
                // TODO
            }
        }

        return false;

    }

    public static boolean isWebpAnimated(String src) {
        boolean riff = false;
        boolean webp = false;
        boolean vp8x = false;
        boolean anim = false;

        try (InputStream in = new FileInputStream(new File(src))) {

            byte[] buf = new byte[4];
            int i = in.read(buf); // 4
            
            if(buf[0] == 0x52 && buf[1] == 0x49 && buf[2]==0x46 && buf[3] == buf[2] )
                riff = true;

            in.skip(4); // ???? (8+)
            i = in.read(buf); // (12+)
            if(buf[0] == 0x57 && buf[1] == 0x45 && buf[2]==0x42 && buf[3] == 0x50 )
                    webp = true   ;             

            i = in.read(buf); // (16+)
            if(buf[0] == 0x41 && buf[1] == 0x4e && buf[2]==0x49 && buf[3] == 0x4d );
                vp8x = true;
                
            in.skip(14); // then next 4 should contain ANIM - 41 4e 49 4d
            i = in.read(buf);
            if(buf[0] == 0x41 && buf[1] == 0x4e && buf[2]==0x49 && buf[3] == 0x4d )
                anim = true;

        } catch (Exception e) {
            Log.d("ReactNative","Error:At isWebpAnimated() " + e.getMessage());
        }
        return riff && webp && anim;

    }
    
    static boolean isWebpAnimated2(String src) {
        
        boolean result = false;
        
        try (InputStream in = new FileInputStream(new File(src))) {
            in.skip(12);
            byte[] buf = new byte[4];
            int i = in.read(buf);
            
            android.util.Log.d("ReactNative","i "+new String(buf,0,i));
            
            if ("VP8X".equals(new String(buf, 0, i))) {
                in.skip(12);
                result = (in.read(buf) == 4 && (buf[3] & 0x00000002) != 0);
            }


        } catch (Exception e) {
            // TODO
        // } finally {
        //     try {
        //         in.close();
        //     } catch (Exception e) {
        //     }
        }
        return result;
    }

    @ReactMethod
    public void getImages(int limit, boolean animated, Promise promise){
        // isAnimated
        final String[] columns = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                // MediaStore.Images.Media.DISPLAY_NAME,
                // MediaStore.Images.Media.SIZE,
                // MediaStore.Images.Media.WIDTH,
                // MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_ADDED
        };
        // final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID+" DESC LIMIT "+String.valueOf(limit);
        
        // String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
        // String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif");
        // String[] selectionArgsPdf = new String[]{ mimeType };
        
        //Stores all the images from the gallery in Cursor
        Cursor cursor = reactContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        // cursor.moveToFirst();
        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String[] arrPath = new String[count];
        // WritableMap map = Arguments.createMap();
        WritableArray array = Arguments.createArray();

       
        // getMethods("WritableArray ", WritableArray.class);
        // promise.resolve(array);
        boolean bool = false;
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i] = cursor.getString(dataColumnIndex);


            String[] fileArr = arrPath[i].split("\\.");
            String fileExtension = fileArr[fileArr.length - 1].toLowerCase();

            switch(fileExtension){
                case "gif":
                    bool = true;
                    break;
                case "webp":
                    bool = isWebpAnimated(arrPath[i]);
                    break;
                default:
                    bool = false;
            }
            WritableMap map = new WritableNativeMap();

            map.putInt("id", i);
            map.putString("path", arrPath[i]);
            map.putBoolean("animated", bool);
            array.pushMap(map);
        }  
 
        promise.resolve(array);

    }

    @ReactMethod
    public void isAdded(String identifier, Promise promise){
        boolean f = WhitelistCheck.isWhitelisted(reactContext,identifier);        
        promise.resolve(String.valueOf(f));
    }

    @ReactMethod
    public void send(String identifier, String stickerPackName, Promise promise) {
        // Log.d("ReactNative","getContentProviderAuthority(reactContext) : "+getContentProviderAuthority(reactContext));
        Log.d("ReactNative","identifier in INtent "+identifier);
        Log.d("ReactNative","identifier in stickerPackName "+stickerPackName);
        
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra(EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, getContentProviderAuthority(reactContext));
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPackName);

        mSendPromise = promise;
        

        try {
            Activity activity = getCurrentActivity();
            ResolveInfo should = activity.getPackageManager().resolveActivity(intent, 0);
            if (should != null) {
                activity.startActivityForResult(intent, ADD_PACK);
                // promise.resolve("Started intent");
            } else {
                // promise.resolve("OK, but not opened");
                mSendPromise.resolve("OK, but not opened");
            }
        } catch (ActivityNotFoundException e) {
            // promise.reject("activity not found: ",e);
            mSendPromise.reject("activity not found: ",e);
        } catch (Exception e) {
            mSendPromise.reject(" Activity ffound but : ",e.getMessage());
            mSendPromise = null;
        }
    }

    @ReactMethod
    public void getDownloadedStickers(Promise promise) {
        try {
            JSONArray identifiers = new JSONArray();
            File[] files = new File(path).listFiles();

            for (File aFile : files) {
                if (aFile.isDirectory()) {
                    identifiers.put(aFile.getName());
                }
            }
            promise.resolve(identifiers.toString());
        } catch (Exception e) {
            promise.reject(ERROR_ADDING_STICKER_PACK, e);
        }
    }

    private static String getLastBitFromUrl(final String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    public static void SaveTryImage(Bitmap finalBitmap, String name, String identifier) {

        String root = path + "/" + identifier;
        File myDir = new File(root + "/" + "try");
        myDir.mkdirs();
        String fname = name.replace(".png", "").replace(" ", "_") + ".png";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 40, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
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
        Log.d("ReactNative","SaveImage : Static "+file.getAbsolutePath());

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
    // ANIMATED 
    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

      // append = false
      try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
          int read;
          byte[] bytes = new byte[8192];
          while ((read = inputStream.read(bytes)) != -1) {
              outputStream.write(bytes, 0, read);
          }
      }

    }

    public static void SaveImageAnimated(String sUrl, String name, String identifier) {

      String root = path + "/" + identifier;
      File myDir = new File(root);
      myDir.mkdirs();
    //   String fname = name; // WA folder recived sticker name cases issue.
      String fname = name;
      File file = new File(myDir, fname);

      if (file.exists())
          file.delete();
      
      
      Log.d("ReactNative",sUrl);
      try{
        InputStream inputStream = new URL(sUrl).openStream();
        copyInputStreamToFile(inputStream,file);
      }catch(Exception e){
        Log.d("ReactNative",e.toString());
      }
      Log.d("ReactNative","SaveImage : Animated "+file.getAbsolutePath());

     
    }

    private class DownloadImageAnimated extends AsyncTask<String, Void, String>{
      private String TAG = "DownloadImage";
      public String imageFileName;
      public String identifier;
      public String name;
      public boolean animated;

      private String downloadImageBitmap(String sUrl, String sIdentifier, String sName) {
          imageFileName = getLastBitFromUrl(sUrl).replace(".png", ".webp");
          identifier = sIdentifier;
          name = sName;
          // Bitmap bitmap = null;
          // try {
              // InputStream inputStream = new URL(sUrl).openStream(); // Download Image from URL
              // bitmap = BitmapFactory.decodeStream(inputStream); // Decode Bitmap
              // inputStream.close();
          // } catch (Exception e) {
          //     Log.d(TAG, "Exception 1, Something went wrong!");
          //     e.printStackTrace();
          // }
          return sUrl;
      }

      @Override
      protected String doInBackground(String... params) {
          return downloadImageBitmap(params[0], params[1], params[2]);
      }

      protected void onPostExecute(String result) {
          SaveImageAnimated(result, imageFileName, identifier);
      }
    }
    //! TESTING... 

    @ReactMethod
    public void addSticker(String url, String identifier, Promise promise){
        String tg = "ReactNative";
        String name = identifier;
        // try{
        //     Log.d("ReactNative", "cntx dir "+reactContext.getFilesDir().toString());
        // }catch(Exception e){
        //     Log.d("ReactNative", "exception: cntx dir:  "+e.toString());
        // }

        // File imagePath;
        // File[] files;
        // try{
        //     imagePath = new File(reactContext.getFilesDir(), "sticker_asset");
            
        //     files = imagePath.listFiles();

        //     for(int i=0;i<files.length;i++){
        //         if (files[i].isFile()) {
        //             Log.d("ReactNative", "File " + files[i].getName());
        //           } else if (files[i].isDirectory()) {
        //             Log.d("ReactNative", "Directory " + files[i].getName());
        //           }
        //     }
            // Log.d("ReactNative", "imagepath getParent : "+imagePath.getParent());
            // Log.d("ReactNative", "imagepath getAbsolutePath : "+imagePath.getAbsolutePath());
            // Log.d("ReactNative", "imagepath getName : "+imagePath.getName());
        // }catch(Exception e){
        //     Log.d("ReactNative", "Exception @main "+e.toString());
        // }
        // File newFile = new File(imagePath, "default_image.jpg");
        // Uri contentUri = FileProvider.getUriForFile(getContext(),getContentProviderAuthority(reactContext) , newFile);
        
        new DownloadImageA().execute(url,identifier,name);
        Log.d(tg,"Called the addSticker "+name+" "+identifier+" "+url);

        String root = path + "/" + identifier;
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = name;
        File file = new File(myDir, url);

        try{
            File newFile = file;
            Uri contentUri = FileProvider.getUriForFile(reactContext,
                getFileProviderAuthority(reactContext), newFile);
            
            // reactContext2.getPackageName()
             
            reactContext.grantUriPermission("com.whatsapp.sticker", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            Intent intent = new Intent(Intent.ACTION_SEND);
            // intent.setType("image/webp");
            intent.setData(contentUri);
            // Intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Activity activity = getCurrentActivity();
            activity.setResult(Activity.RESULT_OK, intent);
            Log.d("ReactNative","-------====================-----");
            Log.d("ReactNative","getEncodedPath() " +contentUri.getEncodedPath());
            Log.d("ReactNative","toString() " +contentUri.toString());
            Log.d("ReactNative","isAbsolute() " +contentUri.isAbsolute());
            Log.d("ReactNative","isRelative() " +contentUri.isRelative());
            Log.d("ReactNative","-------==========X==========-----");
        }catch(Exception e){
            Log.d("ReactNative","Exception in FP "+e.getClass());
            Log.d("ReactNative","Exception in FP "+e.getMessage());
            e.printStackTrace();
        }

        // Intent intent = new Intent();
        // intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        // intent.putExtra(EXTRA_STICKER_PACK_ID, identifier);
        // intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, getContentProviderAuthority(reactContext2));
        // intent.putExtra(EXTRA_STICKER_PACK_NAME, identifier);

        // try {
        //     Activity activity = getCurrentActivity();
        //     ResolveInfo should = activity.getPackageManager().resolveActivity(intent, 0);
        //     if (should != null) {
        //         activity.startActivityForResult(intent, ADD_PACK);
        //         Log.d("ReactNative","OK");
        //     } else {
        //         Log.d("ReactNative","OK, but not opened");
        //     }
        // } catch (ActivityNotFoundException e) {
        //     Log.d("ReactNative","ERROR_ADDING_STICKER_PACK");
        // } catch (Exception e) {
        //     Log.d("ReactNative","ERROR_ADDING_STICKER_PACK 2 ");
        // }
    }

    private class DownloadImageA extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        public String imageFileName;
        public String identifier;
        public String name;

        private Bitmap downloadImageBitmap(String sUrl, String sIdentifier, String sName) {
            imageFileName = getLastBitFromUrl(sUrl).replace(".png", ".webp");
            identifier = sIdentifier;
            name = sName;
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
}