package com.jobeso.RNWhatsAppStickers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

class StickerPack implements Parcelable {
          String    identifier;
          String    name;
          String    publisher;
          String    trayImageFile;
    final String    publisherEmail;
    final String    publisherWebsite;
    final String    privacyPolicyWebsite;
    final String    licenseAgreementWebsite;
          String    imageDataVersion;  // #373
    final boolean   avoidCache;   // #373
    final boolean   animatedStickerPack;  // #373

    String iosAppStoreLink;
    private List<Sticker> stickers;
    private long totalSize;
    String androidPlayStoreLink;
    private boolean isWhitelisted;

    StickerPack(String identifier, String name, String publisher, String trayImageFile, String publisherEmail, 
    String publisherWebsite, String privacyPolicyWebsite, String licenseAgreementWebsite, String imageDataVersion, boolean avoidCache, boolean animatedStickerPack) {
        this.identifier = identifier;
        this.name = name;
        this.publisher = publisher;
        this.trayImageFile = trayImageFile;
        this.publisherEmail = publisherEmail;
        this.publisherWebsite = publisherWebsite;
        this.privacyPolicyWebsite = privacyPolicyWebsite;
        this.licenseAgreementWebsite = licenseAgreementWebsite;
        this.imageDataVersion = imageDataVersion;
        this.avoidCache = avoidCache;
        this.animatedStickerPack = animatedStickerPack;
    }

    void setIsWhitelisted(boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    boolean getIsWhitelisted() {
        return isWhitelisted;
    }

    protected StickerPack(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        publisher = in.readString();
        trayImageFile = in.readString();
        publisherEmail = in.readString();
        publisherWebsite = in.readString();
        privacyPolicyWebsite = in.readString();
        licenseAgreementWebsite = in.readString();
        iosAppStoreLink = in.readString();
        stickers = in.createTypedArrayList(Sticker.CREATOR);
        totalSize = in.readLong();
        androidPlayStoreLink = in.readString();
        isWhitelisted = in.readByte() != 0;
        imageDataVersion = in.readString(); // #373
        avoidCache = in.readByte() != 0;    // #373
        animatedStickerPack = in.readByte() != 0;   // #373
    }

    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        @Override
        public StickerPack createFromParcel(Parcel in) {
            return new StickerPack(in);
        }

        @Override
        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };

    void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
        totalSize = 0;
        for (Sticker sticker : stickers) {
            totalSize += sticker.size;
        }
    }

    public void addSticker(Sticker sticker){
        // String index = String.valueOf(stickersAddedIndex);
        this.stickers.add(sticker);
        totalSize += sticker.size;
        increaseImageDataVersion();

    }
    

    public void addMultipleStickers(Sticker[] stickers){
        // String index = String.valueOf(stickersAddedIndex);
        // this.stickers.addAll(stickers);
        // for (Sticker sticker : stickers) {
            // totalSize += sticker.size;
        // }
        // increaseImageDataVersion();

    }

    public void removeSticker(Sticker sticker){
        // String index = String.valueOf(stickersAddedIndex);
        // this.stickers.remove(sticker);
        // totalSize -= sticker.size;
        // increaseImageDataVersion();

    }
    
    public void removeMultipleStickers(Sticker[] stickers){
        // String index = String.valueOf(stickersAddedIndex);
        // this.stickers.addAll(stickers);
        // for (Sticker sticker : stickers) {
            // totalSize += sticker.size;
        // }

        // increaseImageDataVersion();
    }

    public void changeTrayImageFile(String trayImageFile){
        // String index = String.valueOf(stickersAddedIndex);
        this.trayImageFile = trayImageFile;
    }
    public void setAndroidPlayStoreLink(String androidPlayStoreLink) {
        this.androidPlayStoreLink = androidPlayStoreLink;
    }

    public void setIosAppStoreLink(String iosAppStoreLink) {
        this.iosAppStoreLink = iosAppStoreLink;
    }

    public void setimageDataVersion(String imageDataVersion) {
        this.imageDataVersion = imageDataVersion;
    }

    public void increaseImageDataVersion() {
        int currentVersion = Integer.parseInt(this.imageDataVersion);
        int nextVersion = currentVersion + 1;
        this.imageDataVersion = String.valueOf(nextVersion);
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(publisher);
        dest.writeString(trayImageFile);
        dest.writeString(publisherEmail);
        dest.writeString(publisherWebsite);
        dest.writeString(privacyPolicyWebsite);
        dest.writeString(licenseAgreementWebsite);
        dest.writeString(iosAppStoreLink);
        dest.writeTypedList(stickers);
        dest.writeLong(totalSize);
        dest.writeString(androidPlayStoreLink);
        dest.writeByte((byte) (isWhitelisted ? 1 : 0));
        dest.writeString(imageDataVersion);     // #373
        dest.writeByte((byte) (avoidCache ? 1 : 0));        // #373
        dest.writeByte((byte) (animatedStickerPack ? 1 : 0));       // #373
    }

    public JSONObject getContent() {
        try {
            JSONObject contents = new JSONObject();
            contents.put("identifier", identifier);
            contents.put("name", name);
            contents.put("publisher", publisher);
            contents.put("isWhitelisted", isWhitelisted ? 1 : 0);
            contents.put("animatedStickerPack", animatedStickerPack ? 1 : 0); // #373 // ksr
            contents.put("imageDataVersion", imageDataVersion); // #373 // ksr
            contents.put("trayImageFile", trayImageFile); // #373 // ksr
            return contents;
         } catch (JSONException e) {
             e.printStackTrace();
             return null;
         }
    }
}