package com.lixue.aibei.wokeoutpictures.request;

import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;

/**
 * 请求
 */
public interface Request{
    /**
     * 获取图片Uri
     * @return 图片Uri
     */
    String getUri();

    /**
     * 获取名称，常用来在log中区分请求
     * @return 请求名称
     */
    String getName();

    /**
     * 获取请求的状态
     * @return 请求的状态
     */
    RequestStatus getRequestStatus();

    /**
     * 获取结果图片来源
     * @return 结果图片来源
     */
    ImageFrom getImageFrom();

    /**
     * 获取失败原因
     * @return 失败原因
     */
    FailCause getFailCause();

    /**
     * 获取取消原因
     * @return 取消原因
     */
    CancelCause getCancelCause();

    /**
     * 是否已经结束
     * @return true：已经结束了；false：还在处理中
     */
    boolean isFinished();

    /**
     * 是否已经取消
     * @return true：请求已经取消了；false：请求尚未取消
     */
    boolean isCanceled();

    /**
     * 取消请求
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    boolean cancel();
}
