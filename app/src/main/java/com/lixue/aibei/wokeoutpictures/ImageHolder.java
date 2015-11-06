package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.cache.MemoryCache;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

public class ImageHolder {
    private int resId;
    private ReSize resize;
    private String memoryCacheId;
    private boolean lowQualityImage;
    private boolean forceUseResize;
    private ImageProcessor imageProcessor;
    private RecycleBitmapDrawable drawable;

    public ImageHolder(int resId) {
        this.resId = resId;
    }

    public boolean isForceUseResize() {
        return forceUseResize;
    }

    public ImageHolder setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
        return this;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public ImageHolder setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        return this;
    }

    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    public ImageHolder setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    public int getResId() {
        return resId;
    }

    public ReSize getResize() {
        return resize;
    }

    public ImageHolder setResize(ReSize resize) {
        this.resize = resize;
        return this;
    }

    /**
     * 获取可回收的图像
     * @param context
     * @return
     */
    public RecycleBitmapDrawable getRecycleBitmapDrawable(Context context){
        if(drawable != null && !drawable.isRecyled()){
            return drawable;
        }

        // 从内存缓存中取
        if(memoryCacheId == null){
            memoryCacheId = generateMemoryCacheId(resId, resize, forceUseResize, lowQualityImage, imageProcessor);
        }
        MemoryCache lruMemoryCache = SketchPictures.getInstance(context).getConfiguration().getPlaceholderImageMemoryCache();
        RecycleBitmapDrawable newDrawable = (RecycleBitmapDrawable) lruMemoryCache.get(memoryCacheId);
        if(newDrawable != null && !newDrawable.isRecyled()){
            this.drawable = newDrawable;
            return drawable;
        }

        if(newDrawable != null){
            lruMemoryCache.remove(memoryCacheId);
        }

        // 创建新的图片
        Bitmap bitmap;
        boolean tempLowQualityImage = this.lowQualityImage;
        if(SketchPictures.getInstance(context).getConfiguration().isLowQualityImage()){
            tempLowQualityImage = true;
        }
        boolean canRecycle = false;

        Drawable resDrawable = context.getResources().getDrawable(resId);
        if(resDrawable != null && resDrawable instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable) resDrawable).getBitmap();
        }else{
            bitmap = SketchUtils.DrawableToBitmap(resDrawable, tempLowQualityImage);
            canRecycle = true;
        }

        if(bitmap != null && !bitmap.isRecycled() && imageProcessor != null){
            Bitmap newBitmap = imageProcessor.process(SketchPictures.getInstance(context), bitmap, resize, forceUseResize, tempLowQualityImage);
            if(newBitmap != bitmap){
                if(canRecycle){
                    bitmap.recycle();
                }
                bitmap = newBitmap;
                canRecycle = true;
            }
        }

        if(bitmap != null && !bitmap.isRecycled()){
            newDrawable = new RecycleBitmapDrawable(bitmap);
            newDrawable.setAllowRecycle(canRecycle);
            if(canRecycle){
                lruMemoryCache.put(memoryCacheId, newDrawable);
            }
            drawable = newDrawable;
        }

        return drawable;
    }

    protected String generateMemoryCacheId(int resId, ReSize resize, boolean forceUseResize, boolean lowQualityImage, ImageProcessor imageProcessor){
        StringBuilder builder = new StringBuilder();
        builder.append(resId);
        if(resize != null){
            builder.append("_");
            resize.appendIndentifier(builder);
        }
        if(forceUseResize){
            builder.append("_");
            builder.append("forceUseResize");
        }
        if(lowQualityImage){
            builder.append("_");
            builder.append("lowQualityImage");
        }
        if(imageProcessor != null){
            builder.append("_");
            imageProcessor.appendIdentifier(builder);
        }
        return builder.toString();
    }
}