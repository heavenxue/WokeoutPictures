package com.lixue.aibei.wokeoutpictures;

/**
 * 更新进度监听器
 * Created by Administrator on 2015/11/4.
 */
public interface ProgressListener {
    /**更新进度
     * @param totalLength 总长度
     * @param completedLength 已完成进度
     */
    void updateProgress(int totalLength,int completedLength);
}
