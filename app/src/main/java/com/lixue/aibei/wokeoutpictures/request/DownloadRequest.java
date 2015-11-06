package com.lixue.aibei.wokeoutpictures.request;

import android.os.Message;

import com.lixue.aibei.wokeoutpictures.DownLoadListener;
import com.lixue.aibei.wokeoutpictures.ProgressListener;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;

/**
 * 下载请求
 * Created by Administrator on 2015/11/4.
 */
public interface DownloadRequest extends Request,RequestRunManager {
    /**
     * 获取图片加载器
     * @return
     */
    SketchPictures getSketch();

    /**设置请求名称，用于在Log中区分请求
     * @param name 名字
     */
    void setName(String name);

    /**
     * 获取Uri协议类型
     * @return
     */
    UriSheme getUriScheme();

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     */
    void setProgressListener(ProgressListener progressListener);

    /**
     * 是否将图片缓存在本地
     * @return
     */
    boolean isCacheInDisk();

    /**设置是否将图片缓存在本地
     * @param isCacheInDisk
     */
    void setIsCacheInDisk(boolean isCacheInDisk);

    /**
     * 设置下载监听器
     * @param downloadListener
     */
    void setDownloadListener(DownLoadListener downloadListener);

    /**设置请求Level
     * @param requestLevel
     */
    void setRequestLevel(RequestLevel requestLevel);

    /**
     * 设置请求Level的来源
     * @param requestLevelFrom
     */
    void setRequestLevelFrom(RequestLevelFrom requestLevelFrom);

    /**设置请求的状态
     * @param requestStatus
     */
    void setRequestStatus(RequestStatus requestStatus);

    /**更新状态
     * @param totalLength 总长度
     * @param completeLength 已完成长度
     */
    void updateProgress(int totalLength,int completeLength);

    /**
     * 设置失败状态
     * @param failCause 由于失败原因
     */
    void toFailedStatus(FailCause failCause);

    /**
     * 设置取消状态
     * @param cancelCause 取消原因
     */
    void toCancleStatus(CancelCause cancelCause);

    /**在主线程中执行
     * @param message 消息
     */
    void invokeInMainThread(Message message);


}
