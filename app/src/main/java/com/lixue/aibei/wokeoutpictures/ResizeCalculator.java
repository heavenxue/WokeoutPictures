package com.lixue.aibei.wokeoutpictures;

import android.graphics.Rect;
import android.widget.ImageView;

/**
 * 重置大小计算器接口
 * Created by Administrator on 2015/11/9.
 */
public interface ResizeCalculator {
    Result calculator(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight, ImageView.ScaleType scaleType, boolean forceUseResize);

    String getIdentifier();
    StringBuilder appendIdentifier(StringBuilder builder);

    class Result{
        public int imageWidth;//图像宽度
        public int imageHeight;//图像高度
        public Rect srcRect;//源区域
        public Rect destRect;//目标区域
    }
}
