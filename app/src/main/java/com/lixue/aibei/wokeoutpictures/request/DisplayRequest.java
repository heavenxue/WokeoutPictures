package com.lixue.aibei.wokeoutpictures.request;

import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.DisplayListener;
import com.lixue.aibei.wokeoutpictures.FixedSize;
import com.lixue.aibei.wokeoutpictures.ImageHolder;
import com.lixue.aibei.wokeoutpictures.display.ImageDisplayer;

/**
 * 显示请求
 * Created by Administrator on 2015/11/6.
 */
public interface DisplayRequest extends LoadRequest{
    /**
     * 获取内存缓存id
     * @return
     */
    String getMemoryCacheId();

    /**
     * 设置是否缓存在内存中
     * @param cacheInMemory 是否将图片缓存在内存中，默认是
     */
    void setCacheInMemory(boolean cacheInMemory);

    /**
     * 设置图片显示器（用于图片加载完成后显示图片）
     * @param imageDisplayer
     */
    void setImageDisplayer(ImageDisplayer imageDisplayer);

    /**
     * 设置图片失败持有器
     * @param imageHolder 图片失败持有器
     */
    void setImageFailedHolder(ImageHolder imageHolder);

    /**
     * 设置暂停下载图片持有器
     * @param imageHolder 图片暂停下载持有器
     */
    void setImagePauseDownloadHolder(ImageHolder imageHolder);

    /**
     * 获取失败时显示的图片
     * @return
     */
    Drawable getFailureDrawable();

    /**
     * 设置暂停下载时显示的图片
     * @return
     */
    Drawable getPauseDownloadDrawable();

    /**
     * 设置显示监听器
     * @param displayListener
     */
    void setDisplayListener(DisplayListener displayListener);

    /**
     * 设置固定尺寸，用于显示图片时使用
     * @param fixedSize 固定尺寸
     */
    void setFixedSize(FixedSize fixedSize);
}
