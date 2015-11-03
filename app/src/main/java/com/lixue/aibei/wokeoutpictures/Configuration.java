package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.cache.DiskCache;
import com.lixue.aibei.wokeoutpictures.cache.MemoryCache;

/**
 * Created by Administrator on 2015/11/3.
 */
public class Configuration {
    private static final String NAME = "Configuration";
    private Context context;
    private DiskCache diskCache;    // ���̻�����
    private MemoryCache memoryCache;//ͼƬ������

    private boolean pauseLoad;   // ��ͣ������ͼƬ��������ֻ���ڴ滺������ѰͼƬ��ֻӰ��display����
    private boolean pauseDownload;   // ��ͣ������ͼƬ�������󽫲��ٴ�����������ͼƬ��ֻӰ��display����

    public Configuration(Context context){
        this.context = context;
    }

    /**
     * ������ͣ����ͼƬ�������󽫲��ٴ���������ͼƬ��ֻӰ��display����
     * @param pauseDownload ��ͣ����ͼƬ�������󽫲��ٴ���������ͼƬ��ֻӰ��display����
     */
    public Configuration setPauseDownload(boolean pauseDownload) {
        if(this.pauseDownload != pauseDownload){
            this.pauseDownload = pauseDownload;
            if(SketchPictures.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("pauseDownload").append(" (");
                builder.append(pauseDownload);
                builder.append(")");
                Log.i(SketchPictures.TAG, builder.toString());
            }
        }
        return this;
    }
}
