package com.lixue.aibei.wokeoutpictures;

/**
 * Created by Administrator on 2015/11/4.
 */
public class MaxSize implements ImageSize {
    private int width;
    private int height;

    public MaxSize(int width,int height){
        this.width = width;
        this.height = height;
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
        builder.append("MaxSize(");
        builder.append(width);
        builder.append("x");
        builder.append(height);
        builder.append(")");
        return builder;
    }
}
