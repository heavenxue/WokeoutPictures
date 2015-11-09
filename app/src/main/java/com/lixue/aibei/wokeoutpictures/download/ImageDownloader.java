package com.lixue.aibei.wokeoutpictures.download;

import com.lixue.aibei.wokeoutpictures.DownLoadResult;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;

/**
 * 下载器
 * Created by Administrator on 2015/11/9.
 */
public interface ImageDownloader {
    int BUFFER_SIZE = 8*1024;   // 默认缓存池大小
    int DEFAULT_READ_TIMEOUT = 20*1000;   // 默认读取超时时间
    int DEFAULT_CONNECT_TIMEOUT = 20*1000;    // 默认连接超时时间
    int DEFAULT_MAX_RETRY_COUNT = 1;    // 默认最大重试次数
    int DEFAULT_PROGRESS_CALLBACK_NUMBER = 10;    // 默认进度回调次数

    /**
     * 下载
     * @param downloadRequest
     * @return
     */
    DownLoadResult download(DownloadRequest downloadRequest);

    /**
     * 设置最大重试次数
     * @param maxRetryCount 最大重试次数，默认为1
     */
    void setMaxRetryCount(int maxRetryCount);

    /**
     * 设置链接超时时间
     * @param connectTimeout 链接超时时间，单位毫秒，默认2000毫秒
     */
    void setConnectTimeout(int connectTimeout);

    /**
     * 设置进度回调次数
     * @param progressCallbackNumber 进度回调次数，默认为10，意思是整个下载过程中进度回调10次，例如第一次是10%，以此类推
     */
    void setProgressCallbackNumber(int progressCallbackNumber);
    /**
     * 获取标识符
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
