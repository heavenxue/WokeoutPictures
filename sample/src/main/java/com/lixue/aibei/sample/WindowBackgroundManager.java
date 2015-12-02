package com.lixue.aibei.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.Window;

import com.lixue.aibei.wokeoutpictures.RecycleBitmapDrawable;
import com.lixue.aibei.wokeoutpictures.RecycleDrawableInterface;
import com.lixue.aibei.wokeoutpictures.SketchPictures;

/**
 * Created by Administrator on 2015/11/30.
 */
public class WindowBackgroundManager {
    private Activity activity;
    private Drawable oneDrawable;
    private Drawable twoDrawable;
    private String currentBackgroundUri;

    public WindowBackgroundManager(Activity activity) {
        this.activity = activity;
        // 要先将Window的格式设为透明的，如果不这么做的话第一次改变Window的背景的话屏幕会快速的闪一下（黑色的）
        this.activity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    public void setBackground(String currentBackgroundUri, Drawable newDrawable) {
        this.currentBackgroundUri = currentBackgroundUri;
        Drawable oldOneDrawable = oneDrawable;
        Window window = activity.getWindow();
        Drawable oneDrawable = twoDrawable!=null?twoDrawable:activity.getResources().getDrawable(R.drawable.shape_window_background);
        Drawable[] drawables = new Drawable[]{oneDrawable, newDrawable};
        TransitionDrawable transitionDrawable = new TransitionDrawable(drawables);
        transitionDrawable.setCrossFadeEnabled(true);
        window.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(800);
        this.oneDrawable = oneDrawable;
        this.twoDrawable = newDrawable;
        recycleDrawable(oldOneDrawable);
    }

    public String getCurrentBackgroundUri() {
        return currentBackgroundUri;
    }

    public void destroy() {
        recycleDrawable(oneDrawable);
        recycleDrawable(twoDrawable);
    }

    private void recycleDrawable(Drawable drawable){
        if(drawable == null){
            return;
        }

        if(drawable instanceof RecycleDrawableInterface){
            Log.d(SketchPictures.TAG, "old window bitmap recycled - " + ((RecycleDrawableInterface) drawable).getInfo());
            ((RecycleDrawableInterface) drawable).recyle();
        }else if(drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()){
                Log.d(SketchPictures.TAG, "old window bitmap recycled - " + RecycleBitmapDrawable.getInfo(bitmap, null));
                bitmap.recycle();
            }
        }
    }

    public interface OnSetWindowBackgroundListener {
        void onSetWindowBackground(String uri, Drawable drawable);
        String getCurrentBackgroundUri();
    }

    public static class WindowBackgroundLoader {
        private Context context;
        private String windowBackgroundImageUri;
        private OnSetWindowBackgroundListener onSetWindowBackgroundListener;
        private boolean userVisible;

        public WindowBackgroundLoader(Context context, OnSetWindowBackgroundListener onSetWindowBackgroundListener) {
            this.context = context;
            this.onSetWindowBackgroundListener = onSetWindowBackgroundListener;
        }

        public void detach(){
            onSetWindowBackgroundListener = null;
        }
    }
}
