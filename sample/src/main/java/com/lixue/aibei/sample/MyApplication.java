package com.lixue.aibei.sample;

import android.app.Application;
import android.text.format.Formatter;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.SketchPictures;

/**
 * Created by Administrator on 2015/11/30.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SketchManager sketchManager = new SketchManager(getBaseContext());
        sketchManager.initConfig();
        sketchManager.initDisplayOptions();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w("Application", "Memory is very low, has automatic releasing Sketch in memory cache(" + Formatter.formatFileSize(getBaseContext(), SketchPictures.getInstance(getBaseContext()).getConfiguration().getMemoryCache().getSize()) + ")");
        SketchPictures.getInstance(getBaseContext()).getConfiguration().getMemoryCache().clear();
    }
}
