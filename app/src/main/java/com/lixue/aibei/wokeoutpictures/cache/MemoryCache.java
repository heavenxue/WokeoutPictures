package com.lixue.aibei.wokeoutpictures.cache;

import android.graphics.drawable.Drawable;

/**�ڴ滺����
 * Created by Administrator on 2015/11/3.
 */
public interface MemoryCache {
    /**�Ž�ȥһ��ͼƬ
     * @param key ��
     * @param value ֵ
     */
    void put(String key,Drawable value);

    /**����ָ����key��ȡͼƬ
     * @param key
     * @return
     */
    Drawable get(String key);

    /**����ָ����keyɾ��ͼƬ
     * @param key
     * @return
     */
    Drawable remove(String key);

    /**��ȡ��������
     * @return ��������
     */
    long getSize();

    /**��ȡ�������
     * @return �������
     */
    long getMaxSize();

    /**
     * ����ڴ滺��
     */
    void clear();

    /**׷�ӱ�ʶ��
     * @param stringBuilder
     * @return
     */
    StringBuilder appendIdentifier(StringBuilder stringBuilder);

}
