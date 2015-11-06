package com.lixue.aibei.wokeoutpictures;


import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.request.Request;

/**
 * 下载协助器
 */
public interface DownloadHelper {
    /**
     * 设置名称，用于在log总区分请求
     * @param name 名称
     * @return DownloadHelper
     */
    DownloadHelper name(String name);

    /**
     * 设置监听器
     * @return DownloadHelper
     */
    DownloadHelper listener(DownLoadListener downloadListener);

    /**
     * 关闭磁盘缓存
     * @return DownloadHelper
     */
    DownloadHelper disableDiskCache();

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return DownloadHelper
     */
    DownloadHelper progressListener(ProgressListener progressListener);

    /**
     * 设置下载参数
     * @param options 下载参数
     * @return DownloadHelper
     */
    DownloadHelper options(DownloadOptions options);

    /**
     * 设置下载参数，你只需要提前将DownloadOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return DownloadHelper
     */
    DownloadHelper options(Enum<?> optionsName);

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     * @return DisplayHelper
     */
    DownloadHelper requestLevel(RequestLevel requestLevel);

    /**
     * 提交请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    Request commit();
}