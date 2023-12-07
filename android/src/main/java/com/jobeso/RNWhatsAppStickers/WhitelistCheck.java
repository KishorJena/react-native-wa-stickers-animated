/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.jobeso.RNWhatsAppStickers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

class WhitelistCheck {
    private static final String AUTHORITY_QUERY_PARAM = "authority";
    private static final String IDENTIFIER_QUERY_PARAM = "identifier";
    // private static final String STICKER_APP_AUTHORITY = BuildConfig.CONTENT_PROVIDER_AUTHORITY;
    // FIXME
    private static String STICKER_APP_AUTHORITY = "com.kot1.stickercontentprovider";
    // private static final String STICKER_APP_AUTHORITY = "com.jobeso.RNWhatsAppStickers.stickercontentprovider";
    static final String CONSUMER_WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    static final String SMB_WHATSAPP_PACKAGE_NAME = "com.whatsapp.w4b";
    private static final String CONTENT_PROVIDER = ".provider.sticker_whitelist_check";
    private static final String QUERY_PATH = "is_whitelisted";
    private static final String QUERY_RESULT_COLUMN_NAME = "result";

    private static final String TAG = "ReactNative";


    static boolean isWhitelisted(@NonNull Context context, @NonNull String identifier) {
        STICKER_APP_AUTHORITY= context.getPackageName() + ".stickercontentprovider";
        try {
            if (!isWhatsAppConsumerAppInstalled(context.getPackageManager()) && !isWhatsAppSmbAppInstalled(context.getPackageManager())) {
                return false;
            }
            boolean consumerResult = isStickerPackWhitelistedInWhatsAppConsumer(context, identifier);
            boolean smbResult = isStickerPackWhitelistedInWhatsAppSmb(context, identifier);
            String a = consumerResult == true ? "True":"False";
            String b = smbResult == true ? "True":"False";
            Log.d(TAG,identifier+" isAdded consumer:"+a+" |  smb:"+b);
            return consumerResult && smbResult;
            // return consumerResult ;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isWhitelistedFromProvider(@NonNull Context context, @NonNull String identifier, String whatsappPackageName) {
        final PackageManager packageManager = context.getPackageManager();
        if (isPackageInstalled(whatsappPackageName, packageManager)) {
            final String whatsappProviderAuthority = whatsappPackageName + CONTENT_PROVIDER;
            final ProviderInfo providerInfo = packageManager.resolveContentProvider(whatsappProviderAuthority, PackageManager.GET_META_DATA);
            // provider is not there. The WhatsApp app may be an old version.
            if (providerInfo == null) {
                return false;
            }
            final Uri queryUri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(whatsappProviderAuthority).appendPath(QUERY_PATH).appendQueryParameter(AUTHORITY_QUERY_PARAM, STICKER_APP_AUTHORITY).appendQueryParameter(IDENTIFIER_QUERY_PARAM, identifier).build();
            try (final Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    final int whiteListResult = (int)cursor.getInt(cursor.getColumnIndexOrThrow(QUERY_RESULT_COLUMN_NAME));
                    Log.d("ReactNative","whiteListResult for "+identifier+" is "+whiteListResult);
                    return whiteListResult == 1;
                }
            }
        } else {
            //if app is not installed, then don't need to take into its whitelist info into account.
            return true;
        }
        return false;
    }

    static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            //noinspection SimplifiableIfStatement
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static boolean isWhatsAppConsumerAppInstalled(PackageManager packageManager) {
        return WhitelistCheck.isPackageInstalled(CONSUMER_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    static boolean isWhatsAppSmbAppInstalled(PackageManager packageManager) {
        return WhitelistCheck.isPackageInstalled(SMB_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    static boolean isStickerPackWhitelistedInWhatsAppConsumer(@NonNull Context context, @NonNull String identifier) {
        return isWhitelistedFromProvider(context, identifier, CONSUMER_WHATSAPP_PACKAGE_NAME);
    }

    static boolean isStickerPackWhitelistedInWhatsAppSmb(@NonNull Context context, @NonNull String identifier) {
        return isWhitelistedFromProvider(context, identifier, SMB_WHATSAPP_PACKAGE_NAME);
    }
}