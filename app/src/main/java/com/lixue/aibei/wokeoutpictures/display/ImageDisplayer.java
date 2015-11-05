package com.lixue.aibei.wokeoutpictures.display;

import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.SketchImageViewInterface;

/**
 * 图片显示器
 */
public interface ImageDisplayer {
    int DEFAULT_ANIMATION_DURATION = 400;
    /**
     * 显示
     * @param sketchImageViewInterface ImageView
     * @param newDrawable 图片
     */
    void display(SketchImageViewInterface sketchImageViewInterface, Drawable newDrawable);

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