// package com.jobeso.RNWhatsAppStickers;

// import android.content.Context;
// import android.graphics.Bitmap;
// import android.graphics.BitmapFactory;
// import android.text.TextUtils;
// import android.util.Log;
// import android.util.Patterns;
// import android.webkit.URLUtil;

// import androidx.annotation.NonNull;

// import com.facebook.animated.webp.WebPImage;
// import com.facebook.imagepipeline.common.ImageDecodeOptions;

// import java.io.IOException;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.util.List;

// class StickerValidator {
//     private static final int STATIC_STICKER_FILE_LIMIT_KB = 100;
//     private static final int ANIMATED_STICKER_FILE_LIMIT_KB = 500;
//     static final int EMOJI_MAX_LIMIT = 3;
//     private static final int EMOJI_MIN_LIMIT = 1;
//     private static final int IMAGE_HEIGHT = 512;
//     private static final int IMAGE_WIDTH = 512;
//     private static final int STICKER_SIZE_MIN = 3;
//     private static final int STICKER_SIZE_MAX = 30;
//     private static final int CHAR_COUNT_MAX = 128;
//     private static final long KB_IN_BYTES = 1024;
//     private static final int TRAY_IMAGE_FILE_SIZE_MAX_KB = 50;
//     private static final int TRAY_IMAGE_DIMENSION_MIN = 24;
//     private static final int TRAY_IMAGE_DIMENSION_MAX = 512;
//     private static final int ANIMATED_STICKER_FRAME_DURATION_MIN = 8;
//     private static final int ANIMATED_STICKER_TOTAL_DURATION_MAX = 10 * 1000; //ms
//     private static final String PLAY_STORE_DOMAIN = "play.google.com";
//     private static final String APPLE_STORE_DOMAIN = "itunes.apple.com";


//     private static void validateStickerFile(@NonNull Context context, @NonNull String identifier, @NonNull final String fileName, final boolean animatedStickerPack) throws IllegalStateException {

//     }
  
//     private static WritableMap validateStickerFile(String filePath) {
//         WritableMap validationResults = Arguments.createMap();

//         try {
//             final File stickerFile = new File(filePath);
//             if (!stickerFile.exists()) {
//                 validationResults.putBoolean("fileExists", false);
//                 return validationResults;
//             }

//             final byte[] stickerInBytes = Files.readAllBytes(stickerFile.toPath());

//             if (!animatedStickerPack && stickerInBytes.length > STATIC_STICKER_FILE_LIMIT_KB * KB_IN_BYTES) {
//                 validationResults.putBoolean("staticSizeValid", false);
//             } else {
//                 validationResults.putBoolean("staticSizeValid", true);
//             }
//             if (animatedStickerPack && stickerInBytes.length > ANIMATED_STICKER_FILE_LIMIT_KB * KB_IN_BYTES) {
//                 validationResults.putBoolean("animatedSizeValid", false);
//             } else {
//                 validationResults.putBoolean("animatedSizeValid", true);
//             }
//             try {
//                 final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes, ImageDecodeOptions.defaults());
//                 if (webPImage.getHeight() != IMAGE_HEIGHT) {
//                     validationResults.putBoolean("heightValid", false);
//                 } else {
//                     validationResults.putBoolean("heightValid", true);
//                 }
//                 if (webPImage.getWidth() != IMAGE_WIDTH) {
//                     validationResults.putBoolean("widthValid", false);
//                 } else {
//                     validationResults.putBoolean("widthValid", true);
//                 }
//                 if (animatedStickerPack) {
//                     if (webPImage.getFrameCount() <= 1) {
//                         validationResults.putBoolean("animatedValid", false);
//                     } else {
//                         validationResults.putBoolean("animatedValid", true);
//                     }
//                     checkFrameDurationsForAnimatedSticker(webPImage.getFrameDurations(), identifier, filePath);
//                     if (webPImage.getDuration() > ANIMATED_STICKER_TOTAL_DURATION_MAX) {
//                         validationResults.putBoolean("animatedDurationValid", false);
//                     } else {
//                         validationResults.putBoolean("animatedDurationValid", true);
//                     }
//                 } else if (webPImage.getFrameCount() > 1) {
//                     validationResults.putBoolean("staticAnimatedValid", false);
//                 } else {
//                     validationResults.putBoolean("staticAnimatedValid", true);
//                 }
//             } catch (IllegalArgumentException e) {
//                 validationResults.putBoolean("webpParseError", false);
//             }
//         } catch (IOException e) {
//             validationResults.putBoolean("fileOpenError", false);
//         }

//         return validationResults;
//     }



// }