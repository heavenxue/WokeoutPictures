package com.lixue.aibei.wokeoutpictures.enums;

/**
 * 失败原因
 * Created by Administrator on 2015/11/4.
 */
public enum FailCause {
    /**
     * URI为NULL或空
     */
    URI_NULL_OR_EMPTY,

    /**
     * ImageView为NULL
     */
    IMAGE_VIEW_NULL,

    /**
     * URI不支持
     */
    URI_NO_SUPPORT,

    /**
     * 下载失败
     */
    DOWNLOAD_FAIL,

    /**
     * 解码失败
     */
    DECODE_FAIL,
}
