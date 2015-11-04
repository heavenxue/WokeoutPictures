package com.lixue.aibei.wokeoutpictures;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Administrator on 2015/11/4.
 */
public class RecycleGifDrawable extends GifDrawable implements RecycleDrawableInterface{
    private static final String NAME = "RecycleGifDrawable";
    private int waitDisplayRefCount;//等待的缓存引用计数
    private int displayRefCount;//显示的缓存的引用计数
    private int cacheRefCount;//缓存的引用计数
    private String mimeType; //文件名类型
    private boolean isAllowRecycle; //是否允许缓存


    public RecycleGifDrawable(Resources res, int id) throws Resources.NotFoundException, IOException {
        super(res, id);
    }

    public RecycleGifDrawable(AssetManager assets, String assetName) throws IOException {
        super(assets, assetName);
    }

    public RecycleGifDrawable(String filePath) throws IOException {
        super(filePath);
    }

    public RecycleGifDrawable(File file) throws IOException {
        super(file);
    }

    public RecycleGifDrawable(InputStream stream) throws IOException {
        super(stream);
    }

    public RecycleGifDrawable(AssetFileDescriptor afd) throws IOException {
        super(afd);
    }

    public RecycleGifDrawable(FileDescriptor fd) throws IOException {
        super(fd);
    }

    public RecycleGifDrawable(byte[] bytes) throws IOException {
        super(bytes);
    }

    public RecycleGifDrawable(ByteBuffer buffer) throws IOException {
        super(buffer);
    }

    public RecycleGifDrawable(ContentResolver resolver, Uri uri) throws IOException {
        super(resolver, uri);
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
        synchronized (this){
            if (displayed){
                displayRefCount ++;
            }else{
                if (displayRefCount > 0){
                    displayRefCount --;
                }
            }
        }
        //清楚缓存
        tryRecycle((displayed ? "display" : "hide"), callingStation);
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
        synchronized (this) {
            if (cached) {
                cacheRefCount++;
            } else {
                if(cacheRefCount > 0){
                    cacheRefCount--;
                }
            }
        }
        tryRecycle((cached ? "putToCache" : "removedFromCache"), callingStation);
    }

    @Override
    public void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        synchronized (this) {
            if (waitDisplay) {
                waitDisplayRefCount++;
            } else {
                if(waitDisplayRefCount > 0){
                    waitDisplayRefCount--;
                }
            }
        }
        tryRecycle((waitDisplay ? "waitDisplay" : "displayed"), callingStation);

    }

    @Override
    public int getByteCount() {
        return (int) getAllocationByteCount();
    }

    @Override
    public boolean isRecyled() {
        return super.isRecycled();
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public void recyle() {
        super.recycle();
    }

    @Override
    public String getSize() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            return SketchUtils.concat(bitmap.getWidth(), "x", bitmap.getHeight());
        }else{
            return null;
        }
    }

    @Override
    public String getConfig() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null && bitmap.getConfig() != null){
            return bitmap.getConfig().name();
        }else{
            return null;
        }
    }

    @Override
    public String getInfo() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            return SketchUtils.concat("RecycleGifDrawable(mimeType=", mimeType, "; hashCode=", Integer.toHexString(bitmap.hashCode()), "; size=", bitmap.getWidth(), "x", bitmap.getHeight(), "; config=", bitmap.getConfig() != null ? bitmap.getConfig().name() : null, "; byteCount=", getByteCount(), ")");
        }else{
            return null;
        }
    }

    @Override
    public boolean canRecyle() {
        return isAllowRecycle && !isRecycled();
    }

    @Override
    public void setAllowRecycle(boolean allowRecycle) {
        this.isAllowRecycle = allowRecycle;
    }
    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitDisplayRefCount <= 0 && canRecyle()) {
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "recycled gif drawable", " - ", callingStation, ":", type, " - ", getInfo()));
            }
            recycle();
        }else{
            if(SketchPictures.isDebugMode()){
                Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "can't recycle gif drawable", " - ", callingStation, ":", type, " - ", getInfo(), " - ", "references(cacheRefCount=", cacheRefCount, "; displayRefCount=", displayRefCount, "; waitDisplayRefCount=", waitDisplayRefCount, "; canRecycle=", canRecyle(), ")"));
            }
        }
    }
}
