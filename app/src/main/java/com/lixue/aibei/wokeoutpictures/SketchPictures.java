package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.util.Log;

/** ͼƬ�����������Դ�������߱��ؼ���ͼƬ������֧���Զ��������
 *
 * Created by Administrator on 2015/11/3.
 */
public class SketchPictures {
    public static  final String TAG = SketchPictures.class.getSimpleName();
    private static boolean debugMode;   //����ģʽ���ڿ���̨�����־
    public static SketchPictures instance;
    private Configuration configuration;



    private SketchPictures(Context context){
        Log.i(TAG,"SketchPictures:"+BuildConfig.BUILD_TYPE);
        this.configuration = new Configuration(context);
    }
    public static SketchPictures getInstance(Context context){
        if (instance == null){
            synchronized (SketchPictures.class){
                instance = new SketchPictures(context);
            }
        }
        return instance;
    }

    /**
     * �Ƿ�������ģʽ
     * @return �Ƿ�������ģʽ����������ģʽ����ڿ���̨���LOG
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * �����Ƿ�������ģʽ
     * @param debugMode �Ƿ�������ģʽ����������ģʽ����ڿ���̨���LOG
     */
    public static void setDebugMode(boolean debugMode) {
        SketchPictures.debugMode = debugMode;
    }

    /**
     * ��ȡ���ö���
     * @return
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * �������ö���
     * @param configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}

