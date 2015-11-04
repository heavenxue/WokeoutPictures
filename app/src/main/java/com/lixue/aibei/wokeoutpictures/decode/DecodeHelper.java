package com.lixue.aibei.wokeoutpictures.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.lixue.aibei.wokeoutpictures.RecycleGifDrawable;

/**
 * 解码帮助抽象类
 * Created by Administrator on 2015/11/4.
 */
public interface DecodeHelper{
    /**
     * 解码
     * @param options 解码选项
     * @return
     */
    Bitmap decode(BitmapFactory.Options options);

    /**
     * 解码成功
     * @param bitmap
     * @param originalSize
     * @param inSampleSize
     */
    void onDecodeSuccess(Bitmap bitmap,Point originalSize,int inSampleSize);

    /**
     * 解码失败
     */
    void onDecodeFailed();

    /** 获取gif图像
     * @return
     */
    RecycleGifDrawable getGifDrawable();
}