package com.lixue.aibei.wokeoutpictures;

import android.widget.ImageView;

/**
 * 重塑大小
 * Created by Administrator on 2015/11/4.
 */
public class ReSize implements ImageSize {
    private int width;
    private int height;
    //均衡的缩放图像（保持图像原始比例），使图片的两个坐标（宽、高）都大于等于 相应的视图坐标（负的内边距）。图像则位于视图的中央
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;

    public ReSize(ReSize reSize){
        this.height = reSize.height;
        this.width = reSize.width;
        this.scaleType = reSize.scaleType;
    }

    public ReSize(int width,int height){
        this.width = width;
        this.height = height;
    }

    public ReSize(int width,int height,ImageView.ScaleType scaleType){
        this(width,height);
        this.scaleType = scaleType;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void set(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String getIndentifier() {
        return appendIndentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIndentifier(StringBuilder builder) {
        builder.append("Resize(");
        builder.append(width);
        builder.append("x");
        builder.append(height);
        if(scaleType != null){
            builder.append(":");
            builder.append(scaleType.name());
        }
        builder.append(")");
        return builder;
    }
}
