package com.lixue.aibei.wokeoutpictures.process;

import android.graphics.Bitmap;

import com.lixue.aibei.wokeoutpictures.ReSize;
import com.lixue.aibei.wokeoutpictures.SketchPictures;

/**
 * 图片处理器，你可以实现此接口，将你的图片处理成你想要的效果
 */
public interface ImageProcessor {
    /**
     * 处理
     * @param sketch Sketch
     * @param bitmap 要被处理的图片
     * @param resize 新的尺寸
     * @param forceUseResize 是否强制使用resize
     * @param lowQualityImage 需要一个低质量的新图片
     * @return 新的图片
     */
    Bitmap process(SketchPictures sketch, Bitmap bitmap, ReSize resize, boolean forceUseResize, boolean lowQualityImage);

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
