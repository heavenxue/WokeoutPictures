package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.util.Log;

/** 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 *
 * Created by Administrator on 2015/11/3.
 */
public class SketchPictures {
    public static  final String TAG = SketchPictures.class.getSimpleName();
    private static boolean debugMode;   //调试模式，在控制台输出日志
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
     * 是否开启调试模式
     * @return 是否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * 设置是否开启调试模式
     * @param debugMode 是否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public static void setDebugMode(boolean debugMode) {
        SketchPictures.debugMode = debugMode;
    }

    /**
     * 获取配置对象
     * @return
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 设置配置对象
     * @param configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}

