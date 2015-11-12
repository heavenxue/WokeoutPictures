package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.cache.DiskCache;
import com.lixue.aibei.wokeoutpictures.cache.LruDiskCache;
import com.lixue.aibei.wokeoutpictures.cache.LruMemoryCache;
import com.lixue.aibei.wokeoutpictures.cache.MemoryCache;
import com.lixue.aibei.wokeoutpictures.decode.DefaultImageDecoder;
import com.lixue.aibei.wokeoutpictures.decode.ImageDecoder;
import com.lixue.aibei.wokeoutpictures.display.DefaultImageDisplayer;
import com.lixue.aibei.wokeoutpictures.display.ImageDisplayer;
import com.lixue.aibei.wokeoutpictures.download.HttpClientImageDownloader;
import com.lixue.aibei.wokeoutpictures.download.HttpUrlConnectionImageDownloader;
import com.lixue.aibei.wokeoutpictures.download.ImageDownloader;
import com.lixue.aibei.wokeoutpictures.execute.DefaultRequestExecutor;
import com.lixue.aibei.wokeoutpictures.execute.RequestExecutor;
import com.lixue.aibei.wokeoutpictures.process.DeFaultImageProcessor;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.util.MobileNetworkPauseDownloadManager;


/**
 * 配置文件
 * Created by Administrator on 2015/11/3.
 */
public class Configuration {
    private static final String NAME = "Configuration";
    private Context context;
    private Handler handler;

    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;//内存缓存器
    private MemoryCache placeholderImageMemoryCache;    // 占位图内存缓存器
    private ImageDecoder imageDecoder;//图片解码器
    private ImageProcessor imageProcessor;//图片处理器
    private ImageDownloader imageDownloader;//图片下载器
    private ImageSizeCalculator imageSizeCalculator ;//图像尺寸计算器
    private ImageDisplayer defaultImageDisplayer;//默认图片显示器


    private RequestFactory requestFactory;  // 请求工厂
    private HelperFactory helperFactory;    // 协助器工厂
    private ResizeCalculator resizeCalculator;
    private RequestExecutor requestExecutor;//请求执行器
    private MobileNetworkPauseDownloadManager mobileNetworkPauseDownloadManager;



    private boolean pauseLoad;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean pauseDownload;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean isDecodeGifImage = true;//是否解码Gif图像
    private boolean isLowQualityImage;//是否是低质量的图片
    private boolean isCacheInDisk;//是否缓存到sd卡
    private boolean iscacheInMemory;//是否缓存内存


    public Configuration(Context context){
        this.context = context.getApplicationContext();
        diskCache = new LruDiskCache(context);
        //将最大内存的八分之一作为内存缓存的最大存储空间
        memoryCache = new LruMemoryCache(context,(int) (Runtime.getRuntime().maxMemory()/8));
        imageDecoder = new DefaultImageDecoder();
        helperFactory = new DefaultHelperFactory();
        requestFactory = new DeafaultRequestFactory();
        imageProcessor = new DeFaultImageProcessor();
        resizeCalculator = new DefaultResizeCalculator();
        this.imageSizeCalculator = new DefaultImageSizeCalculator();
        if (Build.VERSION.SDK_INT >= 9){
            imageDownloader = new HttpUrlConnectionImageDownloader();
        }else{
            imageDownloader = new HttpClientImageDownloader();
        }
        this.requestExecutor = new DefaultRequestExecutor.Builder().build();
        this.defaultImageDisplayer = new DefaultImageDisplayer();
        this.placeholderImageMemoryCache = new LruMemoryCache(context,(int)(Runtime.getRuntime().maxMemory()/16));

        this.handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.obj instanceof DownloadRequest){
                    ((DownloadRequest) msg.obj).invokeInMainThread(msg);
                    return true;
                }else{
                    return false;
                }
            }
        });
        if(SketchPictures.isDebugMode()){
            Log.i(SketchPictures.TAG, getInfo());
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
     * 默认的默认的图片裁剪处理器
     * @param defaultCutImageProcessor 默认的图片裁剪处理器
     */
    public Configuration setDefaultCutImageProcessor(ImageProcessor defaultCutImageProcessor) {
        if(defaultCutImageProcessor != null){
            this.imageProcessor = defaultCutImageProcessor;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("defaultCutImageProcessor").append(" (");
                defaultCutImageProcessor.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取是否暂停加载
     * @return
     */
    public boolean isPauseLoad(){
        return pauseLoad;
    }

    /**
     * 设置是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     * @param pauseLoad 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public Configuration setPauseLoad(boolean pauseLoad) {
        if(this.pauseLoad != pauseLoad){
            this.pauseLoad = pauseLoad;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("pauseLoad").append(" (");
                builder.append(pauseLoad);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取请求工厂
     * @return
     */
    public RequestFactory getRequestFactory(){
        return requestFactory;
    }
    /**
     * 设置请求工厂
     * @param requestFactory 请求工厂
     */
    public Configuration setRequestFactory(RequestFactory requestFactory) {
        if(requestFactory != null){
            this.requestFactory = requestFactory;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("requestFactory").append(" (");
                requestFactory.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取是否缓存到sd卡
     * @return
     */
    public boolean isCacheInDisk(){
        return isCacheInDisk;
    }

    public Configuration setIsCacheInDisk(boolean cacheInDisk){
        this.isCacheInDisk = cacheInDisk;
        return this;
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
     * 设置是否开启移动网络下暂停下载的功能
     * @param mobileNetworkPauseDownload 是否开启移动网络下暂停下载的功能
     */
    public Configuration setMobileNetworkPauseDownload(boolean mobileNetworkPauseDownload){
        if(mobileNetworkPauseDownload){
            if(mobileNetworkPauseDownloadManager == null){
                mobileNetworkPauseDownloadManager = new MobileNetworkPauseDownloadManager(context);
            }
            mobileNetworkPauseDownloadManager.setPauseDownload(true);
        }else{
            if(mobileNetworkPauseDownloadManager != null){
                mobileNetworkPauseDownloadManager.setPauseDownload(false);
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
     * 获取是否低质量图片
     * @return
     */
    public boolean isLowQualityImage(){
        return isLowQualityImage;
    }

    /**
     * 设置是否返回低质量的图片
     * @param lowQualityImage true:是
     */
    public Configuration setLowQualityImage(boolean lowQualityImage) {
        if(this.isLowQualityImage != lowQualityImage){
            this.isLowQualityImage = lowQualityImage;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("lowQualityImage").append(" (");
                builder.append(lowQualityImage);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }


    /**
     * 获取是否缓存到内存
     * @return
     */
    public boolean isCacheInMemory(){
        return iscacheInMemory;
    }
    /**
     * 设置是否将图片缓存在内存中
     * @param cacheInMemory 是否将图片缓存在内存中（默认是）
     */
    public Configuration setCacheInMemory(boolean cacheInMemory) {
        if(this.iscacheInMemory != cacheInMemory) {
            this.iscacheInMemory = cacheInMemory;
            if (SketchPictures.isDebugMode()) {
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("cacheInMemory").append(" (");
                builder.append(cacheInMemory);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取协助工厂
     * @return
     */
    public HelperFactory getHelperFactory(){
        return helperFactory;
    };

    /**
     * 设置协助器工厂
     * @param helperFactory 协助器工厂
     */
    public Configuration setHelperFactory(HelperFactory helperFactory) {
        if(helperFactory != null){
            this.helperFactory = helperFactory;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("helperFactory").append(" (");
                helperFactory.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取内存缓存器
     * @return
     */
    public MemoryCache getMemoryCache(){
        return memoryCache;
    }

    /**
     * 设置内存缓存器
     * @param cache
     * @return
     */
    public Configuration setMemoryCache(MemoryCache cache){
        if (cache != null){
            MemoryCache oldMemoryCache = this.memoryCache;
            this.memoryCache = cache;
            if(oldMemoryCache != null){
                oldMemoryCache.clear();
            }
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("memoryCache").append(" (");
                memoryCache.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取占位图内存缓存器
     * @return 占位图内存缓存器
     */
    public MemoryCache getPlaceholderImageMemoryCache() {
        return placeholderImageMemoryCache;
    }

    /**
     * 设置占位图内存缓存器
     * @param placeholderImageMemoryCache 占位图内存缓存器
     */
    public Configuration setPlaceholderImageMemoryCache(MemoryCache placeholderImageMemoryCache) {
        if(placeholderImageMemoryCache != null){
            MemoryCache oldMemoryCache = this.placeholderImageMemoryCache;
            this.placeholderImageMemoryCache = placeholderImageMemoryCache;
            if(oldMemoryCache != null){
                oldMemoryCache.clear();
            }
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("placeholderImageMemoryCache").append(" (");
                placeholderImageMemoryCache.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 得到磁盘缓存器
     * @return
     */
    public DiskCache getDiskCache(){
        return diskCache;
    }
    public Configuration setDiskCache(DiskCache diskCache){
        if (diskCache != null){
            this.diskCache = diskCache;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("diskCache").append(" (");
                diskCache.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取请求执行器
     * @return
     */
    public RequestExecutor getRequestExecutor(){
        return requestExecutor;
    }

    /**
     * 设置请求执行器
     * @param requestExecutor
     * @return
     */
    public Configuration setRequestExecutor(RequestExecutor requestExecutor){
        if(requestExecutor != null){
            this.requestExecutor = requestExecutor;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("requestExecutor").append(" (");
                requestExecutor.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    public Handler getHandler(){
        return handler;
    }

    /**
     * 获取图片下载器
     * @return
     */
    public ImageDownloader getImageDownloader(){
        return imageDownloader;
    }

    /**
     * 设置图片下载器
     * @param downloader
     * @return
     */
    public Configuration setImageDownloader(ImageDownloader downloader){
        if (downloader != null){
            this.imageDownloader = downloader;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("imageDownloader").append(" (");
                imageDownloader.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取图片解码器
     * @return
     */
    public ImageDecoder getImageDecoder(){
        return  imageDecoder;
    }

    /**
     * 设置图片解码器
     * @param decoder
     * @return
     */
    public Configuration setImageDecoder(ImageDecoder decoder){
        if (decoder != null){
            this.imageDecoder = decoder;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("imageDecoder").append(" (");
                imageDecoder.appendIndentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     * @return
     */
    public ImageSizeCalculator getImageSizecalculator(){
        return this.imageSizeCalculator;
    }

    /**
     * 设置图片尺寸计算器
     * @param imageSizecaculator
     * @return
     */
    public Configuration setImageSizecaculator(ImageSizeCalculator imageSizecaculator){
        if (imageSizecaculator != null){
            this.imageSizeCalculator = imageSizeCalculator;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("imageSizeCalculator").append(" (");
                imageSizeCalculator.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }


    /**
     * 获取默认的图片显示器
     * @return
     */
    public ImageDisplayer getDefaultImageDisplayer(){
        return defaultImageDisplayer;
    }

    /**
     * 设置图片默认显示器
     * @param imageDisplayer
     * @return
     */
    public Configuration setDefaultImageDisplayer(ImageDisplayer imageDisplayer){
        if(defaultImageDisplayer != null){
            this.defaultImageDisplayer = defaultImageDisplayer;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("defaultImageDisplayer").append(" (");
                defaultImageDisplayer.appendIdentifier(builder);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }

    public String getInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append(NAME).append(": ");

        if(diskCache != null){
            builder.append("diskCache");
            builder.append(" (");
            diskCache.appendIdentifier(builder);
            builder.append(")");
        }

        if(memoryCache != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("memoryCache");
            builder.append(" (");
            memoryCache.appendIdentifier(builder);
            builder.append(")");
        }

        if(placeholderImageMemoryCache != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("placeholderImageMemoryCache");
            builder.append(" (");
            placeholderImageMemoryCache.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageDecoder != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("imageDecoder");
            builder.append(" (");
            imageDecoder.appendIndentifier(builder);
            builder.append(")");
        }

        if(helperFactory != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("helperFactory");
            builder.append(" (");
            helperFactory.appendIdentifier(builder);
            builder.append(")");
        }

        if(defaultImageDisplayer != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("defaultImageDisplayer");
            builder.append(" (");
            defaultImageDisplayer.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageProcessor != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("defaultCutImageProcessor");
            builder.append(" (");
            imageProcessor.appendIdentifier(builder);
            builder.append(")");
        }

        if(requestFactory != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("requestFactory");
            builder.append(" (");
            requestFactory.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageDownloader != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("imageDownloader");
            builder.append(" (");
            imageDownloader.appendIdentifier(builder);
            builder.append(")");
        }

        if(requestExecutor != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("requestExecutor");
            builder.append(" (");
            requestExecutor.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageSizeCalculator != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("imageSizeCalculator");
            builder.append(" (");
            imageSizeCalculator.appendIdentifier(builder);
            builder.append(")");
        }

        if(resizeCalculator != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("resizeCalculator");
            builder.append(" (");
            resizeCalculator.appendIdentifier(builder);
            builder.append(")");
        }

        if(builder.length() > 0) builder.append("; ");
        builder.append("pauseLoad");
        builder.append(" (");
        builder.append(pauseLoad);
        builder.append(")");

        builder.append("; ");
        builder.append("pauseDownload");
        builder.append(" (");
        builder.append(pauseDownload);
        builder.append(")");

        builder.append("; ");
        builder.append("decodeGifImage");
        builder.append(" (");
        builder.append(isDecodeGifImage);
        builder.append(")");

        builder.append("; ");
        builder.append("lowQualityImage");
        builder.append(" (");
        builder.append(isLowQualityImage);
        builder.append(")");

        builder.append("; ");
        builder.append("cacheInMemory");
        builder.append(" (");
        builder.append(iscacheInMemory);
        builder.append(")");

        builder.append("; ");
        builder.append("cacheInDisk");
        builder.append(" (");
        builder.append(isCacheInDisk);
        builder.append(")");

        return builder.toString();
    }
}
