package com.lixue.aibei.wokeoutpictures;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

/**
 * 可被回收的图片
 * Created by Administrator on 2015/11/4.
 */
public class RecycleBitmapDrawable extends BitmapDrawable implements RecycleDrawableInterface {

    private static final String NAME = "RecycleBitmapDrawable";
    /**
     * 缓存引用计数
     */
    private int cacheRefCount;
    /**
     * 显示的缓存引用计数
     */
    private int displayRefCount;
    /**
     * 等待显示的缓存引用计数
     */
    private int waitdisplayRefCount;
    /**
     * 媒体文件类型
     */
    private String mimeType;
    /**
     * 是否允许回收
     */
    private boolean isAllowRecycle = true;

    public RecycleBitmapDrawable(Bitmap bitmap){
        super(bitmap);
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
        //如果此图片都没有引用，那么就回收掉
        tryRecycle(displayed ? "show" : "hide", callingStation);
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
       synchronized (this){
           if (cached){
               cacheRefCount ++;
           }else{
               if (cacheRefCount > 0){
                   cacheRefCount --;
               }
           }
       }
        tryRecycle(cached ? "putToCache" : "removedFromCache", callingStation);

    }

    @Override
    public void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        synchronized (this){
            if (waitDisplay){
                waitdisplayRefCount ++;
            }else{
                if (waitdisplayRefCount > 0){
                    waitdisplayRefCount -- ;
                }
            }
        }
        tryRecycle(waitDisplay ? "waitDisplay" : "displayed", callingStation);
    }

    @Override
    public int getByteCount() {
        return getByteCount(getBitmap());
    }

    @Override
    public boolean isRecyled() {
        Bitmap bitmap = getBitmap();
        //判断是否有被标记回收
        return bitmap == null || bitmap.isRecycled();
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
        Bitmap bitmap = getBitmap();
        if (bitmap != null){
            bitmap.recycle();
        }
    }

    @Override
    public String getSize() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null){
            return SketchUtils.concat(bitmap.getWidth(),"*",bitmap.getHeight());
        }
        return null;
    }

    @Override
    public String getConfig() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null){
            return bitmap.getConfig().name();
        }
        return null;
    }

    @Override
    public String getInfo() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null){
            return SketchUtils.concat("RecycleBitmapDrawable(mimeType=", mimeType, "; hashCode=", Integer.toHexString(bitmap.hashCode()), "; size=", bitmap.getWidth(), "x", bitmap.getHeight(), "; config=", bitmap.getConfig() != null ? bitmap.getConfig().name() : null, "; byteCount=", getByteCount(), ")");
        }
        return null;
    }

    @Override
    public boolean canRecyle() {
        return isAllowRecycle && getBitmap() != null && !getBitmap().isRecycled();
    }

    @Override
    public void setAllowRecycle(boolean allowRecycle) {
        this.isAllowRecycle = allowRecycle;
    }

    /**尝试回收
     * @param type 文件类型
     * @param callingStation 呼叫位置
     */
    private synchronized void tryRecycle(String type, String callingStation) {
        if (cacheRefCount <= 0 && displayRefCount <= 0 && waitdisplayRefCount <= 0 && canRecyle()) {
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "recycled bitmap", " - ", callingStation, ":", type, " - ", getInfo()));
            }
            getBitmap().recycle();
        }else{
            if(SketchPictures.isDebugMode()){
                Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "can't recycled bitmap", " - ", callingStation, ":", type, " - ", getInfo(), " - ", "references(cacheRefCount=", cacheRefCount, "; displayRefCount=", displayRefCount, "; waitDisplayRefCount=", waitdisplayRefCount, "; canRecycle=", canRecyle(), ")"));
            }
        }
    }

    /**
     * 得到bitmap图像的字节大小
     * @param bitmap
     * @return
     */
    private int getByteCount(Bitmap bitmap){
        int bitmapBytes = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            bitmapBytes = bitmap.getAllocationByteCount();
        }else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1){
            bitmapBytes = bitmap.getByteCount();
        }else{
            bitmapBytes = bitmap.getRowBytes() * bitmap.getHeight();
        }
        return bitmapBytes;
    }
}
