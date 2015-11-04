package com.lixue.aibei.wokeoutpictures;

/**
 * Created by Administrator on 2015/11/4.
 */
public interface ImageSize {
    int getWidth();
    int getHeight();
    void set(int width,int height);
    String getIndentifier();
    StringBuilder appendIndentifier(StringBuilder stringBuilder);
}
