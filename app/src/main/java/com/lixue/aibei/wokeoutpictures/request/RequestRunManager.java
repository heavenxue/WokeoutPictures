package com.lixue.aibei.wokeoutpictures.request;

/**
 * 请求运行管理器
 * Created by Administrator on 2015/11/4.
 */
public interface RequestRunManager {
    /**
     *分发
     */
    void postRunDispatch();

    /**
     *下载
     */
    void postRunDownload();

    /**
     * 加载
     */
    void postRunLoad();

    enum RunStatus{
        /**
         * 分发
         */
        DISPATCH,

        /**
         * 加载
         */
        LOAD,

        /**
         * 下载
         */
        DOWNLOAD,
    }
}
