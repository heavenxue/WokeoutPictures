package com.lixue.aibei.wokeoutpictures.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.RecycleBitmapDrawable;
import com.lixue.aibei.wokeoutpictures.RecycleDrawableInterface;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.util.LruCache;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

/**
 * 内存寄存器
 * Created by Administrator on 2015/11/4.
 */
public class LruMemoryCache implements MemoryCache {
    private static final String NAME = "MemoryCache";
    private Context context;
    //它的主要算法原理是把最近使用的对象用强引用存储在 LinkedHashMap 中，
    // 并且把最近最少使用的对象在缓存值达到预设定值之前从内存中移除。
    //从LinkedHashMap里移除出的缓存放到SoftReference里，这就是内存的二级缓存
    private LruCache<String,Drawable> drawableLruCache;//二级缓存

    public LruMemoryCache(Context context,int maxSize){
        this.context = context;
        drawableLruCache = new DrawableLruCache(maxSize);
    }

    @Override
    public void put(String key, Drawable value) {
        if(!(value instanceof RecycleDrawableInterface)){
            throw new IllegalArgumentException("drawable must be implemented RecycleDrawableInterface");
        }
        int cacheSize = 0;
        if(SketchPictures.isDebugMode()){
            cacheSize = drawableLruCache.size();
        }
        drawableLruCache.put(key, value);
        if(SketchPictures.isDebugMode()){
            Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "put", " - ", "beforeCacheSize=", Formatter.formatFileSize(context, cacheSize), " - ", ((RecycleDrawableInterface) value).getInfo(), " - ", "afterCacheSize=", Formatter.formatFileSize(context, drawableLruCache.size())));
        }
    }

    @Override
    public Drawable get(String key) {
        return drawableLruCache.get(key);
    }

    @Override
    public Drawable remove(String key) {
        Drawable drawable = drawableLruCache.remove(key);
        if(SketchPictures.isDebugMode()){
            Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "remove", " - ", "MemoryCacheSize: ", Formatter.formatFileSize(context, drawableLruCache.size())));
        }
        return drawable;
    }

    @Override
    public long getSize() {
        return drawableLruCache.size();
    }

    @Override
    public long getMaxSize() {
        return drawableLruCache.maxSize();
    }

    @Override
    public void clear() {
        if(SketchPictures.isDebugMode()){
            Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "clear", " - ", "before clean MemoryCacheSize: ", Formatter.formatFileSize(context, drawableLruCache.size())));
        }
        drawableLruCache.evictAll();
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder stringBuilder) {
        return stringBuilder.append(NAME)
                .append(" - ")
                .append("maxSize").append("=").append(Formatter.formatFileSize(context, getMaxSize()));
    }

    private class DrawableLruCache extends LruCache<String,Drawable>{

        public DrawableLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue) {
            ((RecycleBitmapDrawable)oldValue).setIsCached(NAME + ":entryRemoved",false);
            super.entryRemoved(evicted, key, oldValue, newValue);
        }

        @Override
        public int sizeOf(String key, Drawable value) {
            int bitmapSize = ((RecycleDrawableInterface) value).getByteCount();
            return bitmapSize == 0 ? 1 : bitmapSize;
        }

        @Override
        public Drawable put(String key, Drawable value) {
            ((RecycleDrawableInterface) value).setIsCached(NAME+":put", true);
            return super.put(key, value);
        }
    }
}
