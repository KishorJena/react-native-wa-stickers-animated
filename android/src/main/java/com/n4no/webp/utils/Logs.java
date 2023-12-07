package com.n4no.webpencoder.webp.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Logs {
    private static final String TAG = "WebpDemo";
    private static boolean DEBUG = true;
    private static ArrayList<String> blacklist =  new ArrayList<>();

    private static String className(Object o) {
        return o.getClass().getSimpleName();
    }
    public static void disable(Object o) {
        blacklist.add(o.getClass().getSimpleName());
    }
    public static void enable(Object o) {
        blacklist.remove(o.getClass().getSimpleName());
    }

    public static void disableAll() {
        DEBUG = true;
    }
    public static void enableAll() {
        DEBUG = false;
    }
    //
    public static void i(Object o, String s) {
        if (DEBUG && !blacklist.contains(o.getClass().getSimpleName()))
            Log.i(TAG, className(o) + ": " + s);
    }

    public static void e(Object o, String s) {
        if (DEBUG && !blacklist.contains(o.getClass().getSimpleName()))
            Log.e(TAG, className(o) + ": " + s);
    }

    public static void v(Object o, String s) {
        if (DEBUG && !blacklist.contains(o.getClass().getSimpleName()))
            Log.v(TAG, className(o) + ": " + s);
    }

    public static void w(Object o, String s) {
        if (DEBUG && !blacklist.contains(o.getClass().getSimpleName()))
            Log.w(TAG, className(o) + ": " + s);
    }

    public static void d(Object o, String s) {
        if (DEBUG && !blacklist.contains(o.getClass().getSimpleName()))
            Log.d(TAG, className(o) + ": " + s);
    }


}

