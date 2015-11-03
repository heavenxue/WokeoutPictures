package com.lixue.aibei.wokeoutpictures.cache;

import android.graphics.Bitmap;

import java.io.File;

/**
 * ���̻�����
 * Created by Administrator on 2015/11/3.
 */
public interface DiskCache {

    /**��ȡ�����ļ�
     * @param uri ͼƬuri
     * @return null:û��
     */
    File getCacheFile(String uri);

    /**
     * ���ɻ����ļ���ֻnewһ��File�������ʼ���û���Ŀ¼
     * @param uri ͼƬuri
     * @return ������ļ�
     */
    File generateCacheFile(String uri);

    /**����ռ䣬�����ڳ��㹻�Ŀռ䣬ɾ����ԭ��������޸�ʱ������ÿһ�η��ʻ����ļ��������������޸�ʱ�䣩��ɾ���ļ���ֱ���ڳ��㹻�Ŀռ�
     * @param length ���������
     * @return true������ռ�ɹ���false������ռ�ʧ��
     */
    boolean applyForSpace(long length);

    /**���û����Ŀ¼
     * @param cacheDir ����Ŀ¼
     */
    void setCacheDir(File cacheDir);

    /**
     * ��ȡ�����Ŀ¼
     * @return �����Ŀ¼
     */
    File getCacheDir();

    /**���ñ����ռ��С�����豸ʣ��洢�ռ�С�ڱ����ռ�ʱ��Ҫ����ɵĻ����ļ��򷵻�����ʧ��
     * @param reserveSize �����ռ��С��Ĭ��Ϊ100M
     */
    void setReserveSize(int reserveSize);

    /**��ȡ�����ռ��С
     * @return  �����ռ��С��Ĭ��Ϊ100M
     */
    long getReserveSize();

    /**�����������
     * @param maxSize ���������Ĭ��Ϊ100M
     */
    void setMaxSize(int maxSize);

    /**��ȡ�������
     * @return ���������Ĭ��Ϊ100M
     */
    int getMaxSize();

    /**��uri����ת��ת��Ϊ�����ļ�����
     * @param uri
     * @return
     */
    String uriToFileName(String uri);

    /**��ȡ��������
     * @return ��������
     */
    long getSize();

    /**
     * �������
     */
    void clear();

    /**����bitmap
     * @param bitmap
     * @param uri ͼƬuri
     * @return �����ļ�
     */
    File saveBitmap(Bitmap bitmap,String uri);

    /**��ȡ��ʶ��
     * @return ��ʶ��
     */
    String getIdentifier();

    /**׷�ӱ�ʶ��
     * @param stringBuilder
     * @return
     */
    StringBuilder appendIdentifier(StringBuilder stringBuilder);

}
