package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;

/**
 * 显示监听器，值的注意的是DisplayListener中所有的方法都会在主线中执行，所以实现着不必考虑异步线程中刷新UI的问题
 */
public interface DisplayListener {
    /**
     * 已开始
     */
    void onStarted();

    /**
     * 已完成
     * @param imageFrom 图片来源
     * @param mimeType 图片类型
     */
    void onCompleted(ImageFrom imageFrom, String mimeType);

    /**
     * 已失败
     * @param failCause 失败原因
     */
    void onFailed(FailCause failCause);

    /**
     * 已取消
     * @param cancelCause 原因
     */
    void onCanceled(CancelCause cancelCause);
}