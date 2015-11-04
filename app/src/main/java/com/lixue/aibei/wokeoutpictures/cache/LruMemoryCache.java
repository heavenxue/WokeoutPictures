package com.lixue.aibei.wokeoutpictures.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.RecycleBitmapDrawable;
import com.lixue.aibei.wokeoutpictures.RecycleDrawableInterface;
import com.lixue.aibei.wokeoutpictures.util.LruCache;

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

    }

    @Override
    public Drawable get(String key) {
        return null;
    }

    @Override
    public Drawable remove(String key) {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getMaxSize() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder stringBuilder) {
        return null;
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
