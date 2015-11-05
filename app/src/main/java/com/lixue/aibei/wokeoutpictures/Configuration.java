package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.cache.DiskCache;
import com.lixue.aibei.wokeoutpictures.cache.MemoryCache;

/**
 * 配置文件
 * Created by Administrator on 2015/11/3.
 */
public class Configuration {
    private static final String NAME = "Configuration";
    private Context context;
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;//内存缓存器

    private boolean pauseLoad;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean pauseDownload;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean isDecodeGifImage = true;//是否解码Gif图像
    private ImageSizeCalculator imageSizeCalculator ;
    private MemoryCache placeholderImageMemoryCache;
    private boolean isLowQualityImage;


    public Configuration(Context context){
        this.context = context;
    }

    /**
     * 设置暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     * @param pauseDownload 暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     */
    public Configuration setPauseDownload(boolean pauseDownload) {
        if(this.pauseDownload != pauseDownload){
            this.pauseDownload = pauseDownload;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("pauseDownload").append(" (");
                builder.append(pauseDownload);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 是否解码GIF图
     * @return true：解码；false：不解码
     */
    public boolean isDecodeGifImage() {
        return isDecodeGifImage;
    }

    /**
     * 设置是否解码GIF图
     * @param decodeGifImage true：解码；false：不解码
     */
    public Configuration setIsDecodeGifImage(boolean decodeGifImage) {
        if(this.isDecodeGifImage != decodeGifImage){
            this.isDecodeGifImage = decodeGifImage;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("decodeGifImage").append(" (");
                builder.append(decodeGifImage);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }
    public ImageSizeCalculator getImageSizeCalculator(){
        return  imageSizeCalculator;
    }

    public MemoryCache getPlaceholderImageMemoryCache(){
        return placeholderImageMemoryCache;
    }
    public boolean isLowQualityImage(){
        return isLowQualityImage;
    }

}
