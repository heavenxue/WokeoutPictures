package com.lixue.aibei.wokeoutpictures.request;

import com.lixue.aibei.wokeoutpictures.LoadListener;
import com.lixue.aibei.wokeoutpictures.MaxSize;
import com.lixue.aibei.wokeoutpictures.ReSize;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;

import java.io.File;

/**
 * 加载请求
 * Created by Administrator on 2015/11/4.
 */
public interface LoadRequest extends DownloadRequest {
    /**
     * 获取新的尺寸，ImageProcessor会根据此尺寸来裁剪图片
     * @return
     */
    ReSize getResize();

    /**
     * 设置新的尺寸，ImageProcessor会根据此尺寸来裁剪图片
     * @param resize
     */
    void setResize(ReSize resize);

    /**
     * 是否强制使用resize
     * @return true (最终返回的图片尺寸一定跟resize一样)
     */
    boolean isForceUseResize();

    /**
     * 设置是否强制使用resize
     * @param isForceUseReseze  true (最终返回的图片尺寸一定跟resize一样)
     */
    void setIsForceUseReseze(boolean isForceUseReseze);

    /**
     * 获取最大尺寸
     * @return
     */
    MaxSize getMaxSize();

    /**
     * 设置最大尺寸
     * @param maxSize
     */
    void setMaxSize(MaxSize maxSize);

    /**
     * 是否返回低质量的图像
     * @return
     */
    boolean isLowQualityImage();

    /**
     * 设置是否返回低质量的图像
     * @param isLowQualityImage
     */
    void setIsLowQualityImage(boolean isLowQualityImage);

    /**
     * 获得图像处理器
     * @return
     */
    ImageProcessor getImageProcessor();

    /**
     * 设置图像处理器
     * @param imageProcessor
     */
    void setImageProcessor(ImageProcessor imageProcessor);

    /**
     * 设置加载监听器
     * @param loadListener
     */
    void setLoadListener(LoadListener loadListener);

    /**
     * 是否解码git动画 是否解码Gif图片，如果为false，Gif图将使用BitmapFactory来解码
     * @return
     */
    boolean isDecodeGifImage();

    /**
     * 设置是否解码Gif图片，如果为false,Gif图将使用BitmapFactory来解码
     * @param isDecodeGifImage
     */
    void setIsDecodeGifImage(boolean isDecodeGifImage);

    /**
     * 获取缓存文件
     * @return
     */
    File getCacheFile();

    /**获取图片数据
     * @return
     */
    byte[] getImageData();

    /**
     * 是否是本地apk文件
     * @return
     */
    boolean isLocalApkFile();

    /**
     * 获取图片类型
     * @return
     */
    String getMimeType();

    /**
     * 设置图片类型
     */
    void setMimeType(String mimeType);

}
