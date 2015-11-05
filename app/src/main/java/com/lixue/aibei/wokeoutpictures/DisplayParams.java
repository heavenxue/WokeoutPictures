package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.display.ImageDisplayer;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;

/**
 * 显示参数
 */
public class DisplayParams {
    // 基本属性
    public String uri;
    public String name;
    public RequestLevel requestLevel = RequestLevel.NET;
    public RequestLevelFrom requestLevelFrom;

    // 下载属性
    public boolean cacheInDisk = true;
    public ProgressListener progressListener;

    // 加载属性
    public ReSize resize;
    public boolean forceUseResize;
    public boolean decodeGifImage = true;
    public boolean lowQualityImage;
    public MaxSize maxSize;
    public ImageProcessor imageProcessor;

    // 显示属性
    public String memoryCacheId;
    public boolean cacheInMemory = true;
    public FixedSize fixedSize;
    public ImageHolder loadingImageHolder;
    public ImageHolder loadFailImageHolder;
    public ImageHolder pauseDownloadImageHolder;
    public ImageDisplayer imageDisplayer;
    public DisplayListener displayListener;
}