package com.lixue.aibei.wokeoutpictures;

/**
 * 固定大小
 */
public class FixedSize implements ImageSize{
    private int width;
    private int height;

    public FixedSize(int width, int height) {
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
    public String getIndentifier(){
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
