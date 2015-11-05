package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;

/**
 * 下载选项
 * Created by Administrator on 2015/11/5.
 */
public class DownloadOptions implements RequestOptions  {
    private boolean cacheInDisk = true;//是否开启磁盘缓存
    private RequestLevel requestLevel;//请求级别

    public DownloadOptions(){

    }
    public DownloadOptions(DownloadOptions downloadOptions){
        this.cacheInDisk = downloadOptions.cacheInDisk;
        this.requestLevel = downloadOptions.requestLevel;
    }
    /**
     * 设置是否将图片缓存在本地
     * @param cacheInDisk 是否将图片缓存在本地（默认是）
     * @return DownloadOptions
     */
    public DownloadOptions setCacheInDisk(boolean cacheInDisk) {
        this.cacheInDisk = cacheInDisk;
        return this;
    }

    /**
     * 是否将图片缓存在本地
     * @return 是否将图片缓存在本地（默认是）
     */
    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    /**
     * 获取请求Level
     * @return 请求Level
     */
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     */
    public DownloadOptions setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }
}
