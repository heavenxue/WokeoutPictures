package com.lixue.aibei.wokeoutpictures;

import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;

/**
 * Created by Administrator on 2015/11/4.
 */
public interface LoadListener {
    /**
     * 加载开始
     */
    void onStarted();

    /**
     *加载完成的时候
     * @param drawable RecyleBitmapDrawable:normal drawable;RecycleGifDrawable:gif image;null:exception
     * @param imageFrom 图像来源
     * @param mimetype 图像类型
     */
    void onCompleted(Drawable drawable,ImageFrom imageFrom,String mimetype);

    /**
     * 当加载失败的时候
     * @param failCause 失败原因
     */
    void onFailed(FailCause failCause);

    /**
     * 当加载取消的时候
     * @param cancelCause 取消原因
     */
    void onCancled(CancelCause cancelCause);
}
