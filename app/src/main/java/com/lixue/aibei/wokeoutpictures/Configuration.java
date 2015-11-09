package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.cache.DiskCache;
import com.lixue.aibei.wokeoutpictures.cache.LruDiskCache;
import com.lixue.aibei.wokeoutpictures.cache.LruMemoryCache;
import com.lixue.aibei.wokeoutpictures.cache.MemoryCache;
import com.lixue.aibei.wokeoutpictures.decode.DefaultImageDecoder;
import com.lixue.aibei.wokeoutpictures.decode.ImageDecoder;
import com.lixue.aibei.wokeoutpictures.download.HttpClientImageDownloader;
import com.lixue.aibei.wokeoutpictures.download.HttpUrlConnectionImageDownloader;
import com.lixue.aibei.wokeoutpictures.download.ImageDownloader;
import com.lixue.aibei.wokeoutpictures.process.DeFaultImageProcessor;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;

/**
 * 配置文件
 * Created by Administrator on 2015/11/3.
 */
public class Configuration {
    private static final String NAME = "Configuration";
    private Context context;
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;//内存缓存器
    private ImageDecoder imageDecoder;//图片解码器
    private ImageProcessor imageProcessor;//图片处理器
    private ImageDownloader imageDownloader;//图片下载器
    private HelperFactory helperFactory;    // 协助器工厂
    private ImageSizeCalculator imageSizeCalculator ;//图像尺寸计算器
    private RequestFactory requestFactory;  // 请求工厂
    private MemoryCache placeholderImageMemoryCache;//内存缓存器
    private ResizeCalculator resizeCalculator;

    private boolean pauseLoad;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean pauseDownload;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean isDecodeGifImage = true;//是否解码Gif图像
    private boolean isLowQualityImage;//是否是低质量的图片
    private boolean isCacheInDisk;//是否缓存到sd卡


    public Configuration(Context context){
        this.context = context.getApplicationContext();
        diskCache = new LruDiskCache(context);
        //将最大内存的八分之一作为内存缓存的最大存储空间
        memoryCache = new LruMemoryCache(context,(int) (Runtime.getRuntime().maxMemory()/8));
        imageDecoder = new DefaultImageDecoder();
        helperFactory = new DefaultHelperFactory();
        imageProcessor = new DeFaultImageProcessor();
        resizeCalculator = new DefaultResizeCalculator();
        if (Build.VERSION.SDK_INT >= 9){
            imageDownloader = new HttpUrlConnectionImageDownloader();
        }else{
            imageDownloader = new HttpClientImageDownloader();
        }
    }

    /**
     * 获取Resize计算器
     * @return ResizeCalculator
     */
    public ResizeCalculator getResizeCalculator() {
        return resizeCalculator;
    }

    /**
     * 设置Resize计算器
     * @param resizeCalculator
     * @return
     */
    public Configuration setResizeCalculator(ResizeCalculator resizeCalculator){
        if (resizeCalculator != null){
            this.resizeCalculator = resizeCalculator;
            if (SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(NAME).append(" :").append("set").append(" - ");
                stringBuilder.append("resizeCalculator").append(" (");
                resizeCalculator.appendIdentifier(stringBuilder);
                stringBuilder.append(")");
                Log.i(SketchPictures.TAG, stringBuilder.toString());
            }
        }
        return this;
    }

    /**
     *  获取默认的图片裁剪处理器
     * @return
     */
    public ImageProcessor getDefaultCutImageProcessor(){
        return imageProcessor;
    }

    /**
     * 获取是否暂停加载
     * @return
     */
    public boolean isPauseLoad(){
        return pauseLoad;
    }
    /**
     * 获取请求工厂
     * @return
     */
    public RequestFactory getRequestFactory(){
        return requestFactory;
    }

    /**
     * 获取是否缓存到sd卡
     * @return
     */
    public boolean isCacheInDisk(){
        return isCacheInDisk;
    }
    /**
     * 获取是否下载暂停
     * @return
     */
    public boolean isPauseDownload(){
        return pauseDownload;
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
     * 获取上下文
     * @return
     */
    public Context getContext() {
        return context;
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

    /**
     * 获取图像尺寸计算器
     * @return
     */
    public ImageSizeCalculator getImageSizeCalculator(){
        return  imageSizeCalculator;
    }

    /**
     * 获取内存缓存器
     * @return
     */
    public MemoryCache getPlaceholderImageMemoryCache(){
        return placeholderImageMemoryCache;
    }

    /**
     * 获取是否低质量图片
     * @return
     */
    public boolean isLowQualityImage(){
        return isLowQualityImage;
    }

    /**
     * 获取是否缓存到内存
     * @return
     */
    public boolean isCacheInMemory(){
        return isCacheInMemory();
    }

    /**
     * 获取协助工厂
     * @return
     */
    public HelperFactory getHelperFactory(){
        return helperFactory;
    };

    /**
     * 获取内存缓存器
     * @return
     */
    public MemoryCache getMemoryCache(){
        return memoryCache;
    }

    /**
     * 得到磁盘缓存器
     * @return
     */
    public DiskCache getDiskCache(){
        return diskCache;
    }

}
