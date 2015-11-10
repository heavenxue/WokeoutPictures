package com.lixue.aibei.wokeoutpictures;

/**
 * 图片回收器接口
 * Created by Administrator on 2015/11/4.
 */
public interface RecycleDrawableInterface {
    /**
     * 设置缓存图片是否显示
     * @param callingStation
     * @param displayed
     */
    void setIsDisplayed(String callingStation,boolean displayed);

    /**
     * 设置缓存图片是否缓存
     * @param callingStation
     * @param cached
     */
    void setIsCached(String callingStation,boolean cached);

    /**设置缓存图片是否等待显示
     * @param callingStation
     * @param waitDisplay
     */
    void setIsWaitDisplay(String callingStation,boolean waitDisplay);

    /**
     * 得到缓存图片字节大小
     * @return
     */
    int getByteCount();

    /** 是否缓存
     * @return
     */
    boolean isRecyled();

    /**
     * 得到资源媒体类型
     * @return
     */
    String getMimeType();

    /**设置缓存图片的媒体类型
     * @param mimeType
     */
    void setMimeType(String mimeType);

    /**
     * 回收
     */
    void recyle();

    /**
     * 得到缓存图片的大小
     * @return
     */
    String getSize();

    /**
     * 获得配置
     * @return
     */
    String getConfig();

    /**
     * 获取信息
     * @return
     */
     String getInfo();

    /**
     * 是否可以被回收
     * @return
     */
    boolean canRecyle();

    /**设置是否可以被回收
     * @param allowRecycle
     */
    void setAllowRecycle(boolean allowRecycle);
}
