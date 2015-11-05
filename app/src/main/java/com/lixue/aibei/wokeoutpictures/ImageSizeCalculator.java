package com.lixue.aibei.wokeoutpictures;

import android.content.Context;

/**
 * 图片最大尺寸和修正尺寸计算器
 * Created by Administrator on 2015/11/5.
 */
public interface ImageSizeCalculator {
    /**
     * 计算图像最大尺寸
     * @param sketchImageViewInterface 你需要根据ImageView的宽高来计算
     * @return
     */
    MaxSize caculateImageMaxSize(SketchImageViewInterface sketchImageViewInterface);

    /**
     * 计算图像的resize
     * @param sketchImageViewInterface 你需要根据ImageView的宽高来计算
     * @return
     */
    ReSize caculateImageReSize(SketchImageViewInterface sketchImageViewInterface);

    /**
     * 计算图像的fixedSize
     * @param sketchImageViewInterface 你需要根据ImageView的宽高来计算
     * @return
     */
    FixedSize caculateImageFixedSize(SketchImageViewInterface sketchImageViewInterface);

    /**
     * 获取图像的默认最大尺寸
     * @param context
     * @return
     */
    MaxSize getDefaultImageMaxSize(Context context);

    /**
     *  比较两个maxSize的大小，在使用options()方法批量设置属性的时候会使用此方法比较
     *  RequestOptions的maxSize和已有的maxSize，如果前者小于后者就会使用前者代替后者
     * @param maxSize1
     * @param maxSize2
     * @return 等于0：两者相等；小于0：maxSize1小于maxSize2；大于0：maxSize1大于maxSize2
     */
    int compareMaxSize(MaxSize maxSize1,MaxSize maxSize2);
    /**
     * 计算InSampleSize
     * @param outWidth 原始宽
     * @param outHeight 原始高
     * @param targetWidth 目标宽
     * @param targetHeight 目标高
     * @return 合适的InSampleSize
     */
    int calculateInSampleSize(int outWidth, int outHeight, int targetWidth, int targetHeight);

    /**
     * 获取标识符
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
