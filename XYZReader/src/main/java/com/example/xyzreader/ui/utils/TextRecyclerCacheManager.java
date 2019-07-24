package com.example.xyzreader.ui.utils;

import android.support.v4.util.LruCache;

public class TextRecyclerCacheManager {

    private static final String LOG_TAG = TextRecyclerCacheManager.class.getSimpleName();
    private LruCache<Integer, String> mMemoryCache;
    private static TextRecyclerCacheManager instance;

    public static TextRecyclerCacheManager getInstance() {
        if(instance == null) {
            instance = new TextRecyclerCacheManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
        mMemoryCache = new LruCache<Integer, String>(4 * 1024 * 1024);
    }

    public void addStringToMemoryCache(Integer key, String string) {
        if(getStringFromMemCache(key) == null) {
            mMemoryCache.put(key, string);
        }
    }

    private String getStringFromMemCache(Integer key) {
        if(key == null) {
            // Returning null
        } return  mMemoryCache.get(key);
    }
    public void clear() {
        mMemoryCache.evictAll();
    }
}
