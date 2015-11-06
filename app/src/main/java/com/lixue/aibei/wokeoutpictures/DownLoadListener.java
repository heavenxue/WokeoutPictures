package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.enums.FailCause;

import java.io.File;

/**
 * 下载监听器
 * Created by Administrator on 2015/11/6.
 */
public interface DownLoadListener {
    /**
     * 下载开始
     */
    void onStart();

    /**
     * 下载完成
     * @param cacheFile 下载完成后获取到的文件
     * @param isFromNet true:是来自网络;faulse:来自本地的缓存文件
     */
    void onCompaleted(File cacheFile,boolean isFromNet);

    /**
     * 下载完成 当没有选择本地缓存的时候调用此方法
     * @param data
     */
    void onCompaleted(byte[] data);

    /**
     * 下载失败
     * @param failCause 失败原因
     */
    void onFailed(FailCause failCause);

    /**
     * 下载已取消
     */
    void onCancle();
}
