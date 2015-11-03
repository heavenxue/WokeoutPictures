package com.lixue.aibei.wokeoutpictures.cache;

import android.graphics.Bitmap;

import java.io.File;

/**
 * 磁盘缓存器
 * Created by Administrator on 2015/11/3.
 */
public interface DiskCache {

    /**获取缓存文件
     * @param uri 图片uri
     * @return null:没有
     */
    File getCacheFile(String uri);

    /**
     * 生成缓存文件，只new一个File并负责初始化好缓存目录
     * @param uri 图片uri
     * @return 缓存的文件
     */
    File generateCacheFile(String uri);

    /**申请空间，尝试腾出足够的空间，删除的原则是最后修改时间排序（每一次访问缓存文件都会更新其最后修改时间）来删除文件，直到腾出足够的空间
     * @param length 申请的容量
     * @return true：申请空间成功；false：申请空间失败
     */
    boolean applyForSpace(long length);

    /**设置缓存的目录
     * @param cacheDir 缓存目录
     */
    void setCacheDir(File cacheDir);

    /**
     * 获取缓存的目录
     * @return 缓存的目录
     */
    File getCacheDir();

    /**设置保留空间大小，当设备剩余存储空间小于保留空间时就要清理旧的缓存文件或返回申请失败
     * @param reserveSize 保留空间大小，默认为100M
     */
    void setReserveSize(int reserveSize);

    /**获取保留空间大小
     * @return  保留空间大小，默认为100M
     */
    long getReserveSize();

    /**设置最大容量
     * @param maxSize 最大容量，默认为100M
     */
    void setMaxSize(int maxSize);

    /**获取最大容量
     * @return 最大容量，默认为100M
     */
    int getMaxSize();

    /**将uri进行转码转换为缓存文件名字
     * @param uri
     * @return
     */
    String uriToFileName(String uri);

    /**获取已用容量
     * @return 已用容量
     */
    long getSize();

    /**
     * 清除缓存
     */
    void clear();

    /**保存bitmap
     * @param bitmap
     * @param uri 图片uri
     * @return 缓存文件
     */
    File saveBitmap(Bitmap bitmap,String uri);

    /**获取标识符
     * @return 标识符
     */
    String getIdentifier();

    /**追加标识符
     * @param stringBuilder
     * @return
     */
    StringBuilder appendIdentifier(StringBuilder stringBuilder);

}
