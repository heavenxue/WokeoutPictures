package com.lixue.aibei.wokeoutpictures.cache;

import android.graphics.drawable.Drawable;

/**内存缓存器
 * Created by Administrator on 2015/11/3.
 */
public interface MemoryCache {
    /**放进去一张图片
     * @param key 键
     * @param value 值
     */
    void put(String key,Drawable value);

    /**根据指定的key获取图片
     * @param key
     * @return
     */
    Drawable get(String key);

    /**根据指定的key删除图片
     * @param key
     * @return
     */
    Drawable remove(String key);

    /**获取已用容量
     * @return 已用容量
     */
    long getSize();

    /**获取最大容量
     * @return 最大容量
     */
    long getMaxSize();

    /**
     * 清除内存缓存
     */
    void clear();

    /**追加标识符
     * @param stringBuilder
     * @return
     */
    StringBuilder appendIdentifier(StringBuilder stringBuilder);

}
