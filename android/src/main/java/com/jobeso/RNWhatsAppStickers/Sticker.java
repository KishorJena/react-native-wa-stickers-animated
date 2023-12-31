package com.jobeso.RNWhatsAppStickers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.util.Log;

class Sticker implements Parcelable {
    String TAG = "ReactNative";
    String imageFileName;
    List<String> emojis;
    long size;

    Sticker(String imageFileName, List<String> emojis) {
        this.imageFileName = imageFileName;
        Log.d(TAG,"Sticker-String : imageFileName "+imageFileName);
        this.emojis = emojis;
    }

    protected Sticker(Parcel in) {
        imageFileName = in.readString();
        emojis = in.createStringArrayList();
        size = in.readLong();
        Log.d(TAG,"Sticker-Parcel : imageFileName "+imageFileName);
    }

    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageFileName);
        dest.writeStringList(emojis);
        dest.writeLong(size);
    }

    public String getContent() {
        return imageFileName;
    }
}