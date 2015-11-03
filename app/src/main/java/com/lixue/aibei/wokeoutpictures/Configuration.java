package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.cache.DiskCache;
import com.lixue.aibei.wokeoutpictures.cache.MemoryCache;

/**
 * Created by Administrator on 2015/11/3.
 */
public class Configuration {
    private static final String NAME = "Configuration";
    private Context context;
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;//图片缓存器

    private boolean pauseLoad;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean pauseDownload;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求

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
}
