package com.jobeso.RNWhatsAppStickers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.orhanobut.hawk.Hawk;

import android.os.ParcelFileDescriptor;

public class StickerContentProvider extends ContentProvider {

    /**
     * Do not change the strings listed below, as these are used by WhatsApp. And changing these will break the interface between sticker app and WhatsApp.
     */
    public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
    public static final String STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name";
    public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
    public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
    public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
    public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
    public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
    public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
    public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
    public static final String LICENSE_AGREENMENT_WEBSITE = "sticker_pack_license_agreement_website";
    public static final String IMAGE_DATA_VERSION = "image_data_version"; // #373
    public static final String AVOID_CACHE = "whatsapp_will_not_cache_stickers"; // #373
    public static final String ANIMATED_STICKER_PACK = "animated_sticker_pack"; // #373

    public static final String STICKER_FILE_NAME_IN_QUERY = "sticker_file_name";
    public static final String STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji";
    public static final String CONTENT_FILE_NAME = "contents.json";
    private static final String TAG = StickerContentProvider.class.getSimpleName();

    // public static Uri AUTHORITY_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(RNWhatsAppStickersModule.getContentProviderAuthority()).appendPath(StickerContentProvider.METADATA).build();


    /**
     * Do not change the values in the UriMatcher because otherwise, WhatsApp will not be able to fetch the stickers from the ContentProvider.
     */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static final String METADATA = "metadata";
    private static final int METADATA_CODE = 1;

    private static final int METADATA_CODE_FOR_SINGLE_PACK = 2;

    static final String STICKERS = "stickers";
    private static final int STICKERS_CODE = 3;

    static final String STICKERS_ASSET = "stickers_asset";
    private static final int STICKERS_ASSET_CODE = 4;

    private static final int STICKER_PACK_TRAY_ICON_CODE = 5;

    private List<StickerPack> stickerPackList;


    
    @Override
    public boolean onCreate() {
        Hawk.init(getContext()).build();
        
        final String authority = RNWhatsAppStickersModule.getContentProviderAuthority(getContext());
        if (!authority.startsWith(Objects.requireNonNull(getContext()).getPackageName())) {
            throw new IllegalStateException("your authority (" + authority + ") for the content provider should start with your package name: " + getContext().getPackageName());
        }
        // Log.d("ReactNative2","onCreate() authority "+authority);
        //the call to get the metadata for the sticker packs.
        MATCHER.addURI(authority, METADATA, METADATA_CODE);

        //the call to get the metadata for single sticker pack. * represent the identifier
        MATCHER.addURI(authority, METADATA + "/*", METADATA_CODE_FOR_SINGLE_PACK);

        //gets the list of stickers for a sticker pack, * respresent the identifier.
        MATCHER.addURI(authority, STICKERS + "/*", STICKERS_CODE);
 
        for (StickerPack stickerPack : getStickerPackList()) {
            Log.d("ReactNative2","StickerPack add-URI: "+authority+"/"+String.valueOf(STICKERS_ASSET) + "/" + stickerPack.identifier + "/" + stickerPack.trayImageFile+"/"+STICKER_PACK_TRAY_ICON_CODE);
            MATCHER.addURI(authority, STICKERS_ASSET + "/" + stickerPack.identifier + "/" + stickerPack.trayImageFile, STICKER_PACK_TRAY_ICON_CODE);
            
            for (Sticker sticker : stickerPack.getStickers()) {
                Log.d("ReactNative2",authority + "/"+STICKERS_ASSET + "/" + stickerPack.identifier + "/" + sticker.imageFileName+" | "+String.valueOf(STICKERS_ASSET_CODE));
                
                MATCHER.addURI(authority, STICKERS_ASSET + "/" + stickerPack.identifier + "/" + sticker.imageFileName, STICKERS_ASSET_CODE);
            }
        }

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final int code = MATCHER.match(uri);

        if (code == METADATA_CODE) {
            // Log.d("ReactNative2","METADATA_CODE "+String.valueOf(code)+" | get getPackForAllStickerPacks()");
            return getPackForAllStickerPacks(uri);
        } else if (code == METADATA_CODE_FOR_SINGLE_PACK) {
            // Log.d("ReactNative2","METADATA_CODE_FOR_SINGLE_PACK "+String.valueOf(code)+" | get getPackForSingleStickerPack()");	
            return getCursorForSingleStickerPack(uri);
        } else if (code == STICKERS_CODE) {
            // Log.d("ReactNative2","STICKERS_CODE "+String.valueOf(code)+" | get getStickersForStickerPack()");
            return getStickersForAStickerPack(uri);
        } else {
            // Log.d("ReactNative2","UNKNOWN_CODE "+String.valueOf(code));
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) {
        final int matchCode = MATCHER.match(uri);
        // Log.d("ReactNative2","openAssetFile() matchCode "+String.valueOf(matchCode));
        // if (matchCode == STICKERS_ASSET_CODE || matchCode == STICKER_PACK_TRAY_ICON_CODE) {
        return getImageAsset(uri);
        // return null;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int matchCode = MATCHER.match(uri);
        // android.util.Log.d("ReactNative2","getType() matchCode "+String.valueOf(matchCode));
        
        switch (matchCode) {
            case METADATA_CODE:
                return "vnd.android.cursor.dir/vnd." + RNWhatsAppStickersModule.getContentProviderAuthority(getContext()) + "." + METADATA;
            case METADATA_CODE_FOR_SINGLE_PACK:
                return "vnd.android.cursor.item/vnd." + RNWhatsAppStickersModule.getContentProviderAuthority(getContext()) + "." + METADATA;
            case STICKERS_CODE:
                return "vnd.android.cursor.dir/vnd." + RNWhatsAppStickersModule.getContentProviderAuthority(getContext()) + "." + STICKERS;
            case STICKERS_ASSET_CODE:
                return "image/webp";
            case STICKER_PACK_TRAY_ICON_CODE:
                return "image/png";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private synchronized void readContentFile(@NonNull Context context) {
        // try (InputStream contentsInputStream = context.getAssets().open(CONTENT_FILE_NAME)) {
        //     stickerPackList = ContentFileParser.parseStickerPacks(contentsInputStream);
        // } catch (IOException | IllegalStateException e) {
        //     throw new RuntimeException(CONTENT_FILE_NAME + " file has some issues: " + e.getMessage(), e);
        // }
        // Log.d("ReactNative2","readContentFile() ");
        
        if (Hawk.get("sticker_packs", new ArrayList<StickerPack>()) != null) {
            stickerPackList.addAll(Hawk.get("sticker_packs", new ArrayList<StickerPack>()));
        }
    }


    // executes on fetches via cursor 
    public List<StickerPack> getStickerPackList() {
        // if (stickerPackList == null) {
        //     readContentFile(Objects.requireNonNull(getContext()));
        // }
        // return stickerPackList;

        /*
         * if (stickerPackList == null) {
         * readContentFile(Objects.requireNonNull(getContext())); }
         */
        // Log.d("ReactNative2","getStickerPackList() ");
        
        return (List) Hawk.get("sticker_packs", new ArrayList<StickerPack>());
    }

    // executes while fetching via cursor
    private Cursor getPackForAllStickerPacks(@NonNull Uri uri) {
        // Log.d("ReactNative2","getPackForAllStickerPacks() ");
        return getStickerPackInfo(uri, getStickerPackList());
    }

    private Cursor getCursorForSingleStickerPack(@NonNull Uri uri) {
        // Log.d("ReactNative2","getCursorForSingleStickerPack() ");
        final String identifier = uri.getLastPathSegment();
        for (StickerPack stickerPack : getStickerPackList()) {
            if (identifier.equals(stickerPack.identifier)) {
                return getStickerPackInfo(uri, Collections.singletonList(stickerPack));
            }
        }

        return getStickerPackInfo(uri, new ArrayList<StickerPack>());
    }

    @NonNull
    private Cursor getStickerPackInfo(@NonNull Uri uri, @NonNull List<StickerPack> stickerPackList) {
        // Log.d("ReactNative2","getStickerPackInfo() ");
        // Log.d("ReactNative2","getStickerPackInfo() | stickerPack.imageDataVersion here: "+String.valueOf(stickerPack.imageDataVersion));
        MatrixCursor cursor = new MatrixCursor(
                new String[]{
                        STICKER_PACK_IDENTIFIER_IN_QUERY,
                        STICKER_PACK_NAME_IN_QUERY,
                        STICKER_PACK_PUBLISHER_IN_QUERY,
                        STICKER_PACK_ICON_IN_QUERY,
                        ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                        IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                        PUBLISHER_EMAIL,
                        PUBLISHER_WEBSITE,
                        PRIVACY_POLICY_WEBSITE,
                        LICENSE_AGREENMENT_WEBSITE,
                        IMAGE_DATA_VERSION, // #3373
                        AVOID_CACHE,    // #3373
                        ANIMATED_STICKER_PACK,  // #3373
                });
        for (StickerPack stickerPack : stickerPackList) {
            
            MatrixCursor.RowBuilder builder = cursor.newRow();
            builder.add(stickerPack.identifier);
            builder.add(stickerPack.name);
            builder.add(stickerPack.publisher);
            builder.add(stickerPack.trayImageFile);
            builder.add(stickerPack.androidPlayStoreLink);
            builder.add(stickerPack.iosAppStoreLink);
            builder.add(stickerPack.publisherEmail);
            builder.add(stickerPack.publisherWebsite);
            builder.add(stickerPack.privacyPolicyWebsite);
            builder.add(stickerPack.licenseAgreementWebsite);
            builder.add(stickerPack.imageDataVersion);  // #373
            builder.add(stickerPack.avoidCache ? 1 : 0);    // #373
            builder.add(stickerPack.animatedStickerPack ? 1 : 0);   // #373
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @NonNull
    private Cursor getStickersForAStickerPack(@NonNull Uri uri) {
        // Log.d("ReactNative2","getStickersForAStickerPack() ");
        final String identifier = uri.getLastPathSegment();
        MatrixCursor cursor = new MatrixCursor(new String[]{STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY});
        for (StickerPack stickerPack : getStickerPackList()) {
            if (identifier.equals(stickerPack.identifier)) {
                for (Sticker sticker : stickerPack.getStickers()) {
                    cursor.addRow(new Object[]{sticker.imageFileName, TextUtils.join(",", sticker.emojis)});
                }
            }
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    private AssetFileDescriptor getImageAsset(Uri uri) throws IllegalArgumentException {
        // Log.d("ReactNative2","getImageAsset() "+uri.toString());
        
        AssetManager am = Objects.requireNonNull(getContext()).getAssets();
        final List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() != 3) {
            throw new IllegalArgumentException("path segments should be 3, uri is: " + uri);
        }
        String fileName = pathSegments.get(pathSegments.size() - 1);
        final String identifier = pathSegments.get(pathSegments.size() - 2);
        if (TextUtils.isEmpty(identifier)) {
            throw new IllegalArgumentException("identifier is empty, uri: " + uri);
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("file name is empty, uri: " + uri);
        }
        // Log.d("ReactNative2","making sure the file that is trying to be fetched is in the list of stickers.");
        
        //making sure the file that is trying to be fetched is in the list of stickers.
        for (StickerPack stickerPack : getStickerPackList()) {
            // android.util.Log.d("ReactNative2","stickerPack.identifier "+stickerPack.identifier);
            
            if (identifier.equals(stickerPack.identifier)) {
                if (fileName.equals(stickerPack.trayImageFile)) {
                    return fetchFile(uri, am, fileName, identifier, true);
                } else {
                    for (Sticker sticker : stickerPack.getStickers()) {
                        if (fileName.equals(sticker.imageFileName)) {
                            return fetchFile(uri, am, fileName, identifier, false);
                        }
                    }
                }
            }
        }
        return null;
    }

    private AssetFileDescriptor fetchFile(@NonNull Uri uri, @NonNull AssetManager am, @NonNull String fileName, @NonNull String identifier, Boolean isTrayFile) {
        // Log.d("ReactNative2","fetchFile() "+fileName);
        try {
            File file;
            file = new File(getContext().getFilesDir() + "/" + "stickers_asset" + "/" + identifier + "/", fileName);
            if (!file.exists()) {
                // Log.d("ReactNative2", "StickerPack dir not found");
                // Log.d("fetFile", "StickerPack dir not found");
            }
            return new AssetFileDescriptor(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY), 0L, -1L);
        } catch (IOException e) {
            Log.e(Objects.requireNonNull(getContext()).getPackageName(),
                    "IOException when getting asset file, uri:" + uri, e);
            Log.d("ReactNative2","IOException when getting asset file, uri:" + uri);
            
            try {
                return am.openFd("1" + "/" + "namaskar.webp");
            } catch (IOException err) {
                Log.e(Objects.requireNonNull(getContext()).getPackageName(), "IOException when getting asset file, uri:" + uri, err);
                return null;
            }
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, String[] selectionArgs) {
        // throw new UnsupportedOperationException("Not supported");
        // android.util.Log.d("ReactNative2","delete() "+uri.toString());
        
        return 0;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Log.d("ReactNative2","insert "+uri.toString());
        
        // getContext().getContentResolver().notifyChange(uri, null);
        return uri;
        // throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // throw new UnsupportedOperationException("Not supported");
        final String authority = RNWhatsAppStickersModule.getContentProviderAuthority(getContext());

        // Log.d("ReactNative2","update "+uri.toString());

        // Log.d("ReactNative2","values "+values.toString());
        
        String imageFileName = values.getAsString(STICKER_FILE_NAME_IN_QUERY);
        String emoji = values.getAsString(STICKER_FILE_EMOJI_IN_QUERY);
        String identifier = uri.getLastPathSegment();

        // Log.d("ReactNative2","values imageFileName "+imageFileName+" emoji "+emoji+" identifier "+identifier);
        // StringBuilder str = new StringBuilder();
        // str.append("Sticker pack count: ");
        // str.append(getStickerPackList().size());
        // for (StickerPack whatsappStickerPack : getStickerPackList()) {
        //     StringBuilder stringBuilder = new StringBuilder();
        //     stringBuilder.append("Stickerpack iddd: ");
        //     stringBuilder.append(whatsappStickerPack.identifier);
        //     UriMatcher strArr = MATCHER;
        //     String str2 = authority;
        //     StringBuilder stringBuilder2 = new StringBuilder();
        //     stringBuilder2.append("stickers_asset/");
        //     stringBuilder2.append(whatsappStickerPack.identifier);
        //     stringBuilder2.append("/");
        //     stringBuilder2.append(whatsappStickerPack.trayImageFile);
        //     strArr.addURI(str2, stringBuilder2.toString(), 5);
        //     for (Sticker whatsappSticker : whatsappStickerPack.getStickers()) {
        //         UriMatcher uriMatcher = MATCHER;
        //         String str3 = authority;
        //         StringBuilder stringBuilder3 = new StringBuilder();
        //         stringBuilder3.append("stickers_asset/");
        //         stringBuilder3.append(whatsappStickerPack.identifier);
        //         stringBuilder3.append("/");
        //         stringBuilder3.append(whatsappSticker.imageFileName);
        //         MATCHER.addURI(authority, STICKERS_ASSET + "/" + stickerPack.identifier + "/" + sticker.imageFileName, STICKERS_ASSET_CODE);

        //         uriMatcher.addURI(str3, stringBuilder3.toString(), 4);
        //     }
        // }
        MATCHER.addURI(authority, STICKERS_ASSET + "/" + identifier + "/" + imageFileName, STICKERS_ASSET_CODE);
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;

    }

    // @Override
    // public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
    /*StringBuilder str = new StringBuilder();
    str.append("Sticker pack count: ");
    str.append(getStickerPackList().size());
    for (StickerPack whatsappStickerPack : getStickerPackList()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Stickerpack iddd: ");
        stringBuilder.append(whatsappStickerPack.identifier);
        UriMatcher strArr = MATCHER;
        String str2 = this.authority;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("stickers_asset/");
        stringBuilder2.append(whatsappStickerPack.identifier);
        stringBuilder2.append("/");
        stringBuilder2.append(whatsappStickerPack.trayImageFile);
        strArr.addURI(str2, stringBuilder2.toString(), 5);
        for (Sticker whatsappSticker : whatsappStickerPack.getStickers()) {
            UriMatcher uriMatcher = MATCHER;
            String str3 = this.authority;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("stickers_asset/");
            stringBuilder3.append(whatsappStickerPack.identifier);
            stringBuilder3.append("/");
            stringBuilder3.append(whatsappSticker.imageFileName);
            uriMatcher.addURI(str3, stringBuilder3.toString(), 4);
        // }
    // }
    getContext().getContentResolver().notifyChange(uri, null);*/
    // return 0;
    // }
}