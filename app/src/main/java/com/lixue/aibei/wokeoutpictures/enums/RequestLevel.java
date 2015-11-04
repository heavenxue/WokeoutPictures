package com.lixue.aibei.wokeoutpictures.enums;

/**
 * 请求Level，决定请求的处理深度
 * Created by Administrator on 2015/11/4.
 */
public enum RequestLevel {
    /**
     * 正常的情况，内存没有就从本地加载，本地还没有就从网络加载，适用于所有类型的图片
     */
    NET(2),

    /**
     * 只从内存或本地加载图片，如果本地还没有就结束处理，适用于网络图片
     */
    LOCAL(1),

    /**
     * 只从内存中加载图片，如果内存缓存中没有就结束处理，适用于所有类型的图片
     */
    MEMORY(0);

    private int level;

    RequestLevel(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
