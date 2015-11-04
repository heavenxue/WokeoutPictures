package com.lixue.aibei.wokeoutpictures.enums;

/**
 * 取消原因
 * Created by Administrator on 2015/11/4.
 */
public enum CancelCause {
    /**
     * 正常取消
     */
    NORMAL,
    /**
     *本地磁盘不足
     */
    LEVEL_IS_LOCAL,
    /**
     *内存不足
     */
    LEVEL_IS_MEMORY,
    /**
     * 暂停下载
     */
    PAUSE_DOWNLOAD,
    /**
     * 暂停加载
     */
    PAUSE_LOAD,
}
