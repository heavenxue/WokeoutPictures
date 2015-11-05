package com.lixue.aibei.wokeoutpictures;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.lixue.aibei.wokeoutpictures.request.Request;

/**
 * 图片接口
 * Created by Administrator on 2015/11/5.
 */
public interface SketchImageViewInterface {
    /**
     * 当显示图片的时候
     */
    void onDisplay();

    /**
     * 获取图片
     * @return
     */
    Drawable getDrawable();

    /**
     * 设置图片
     * @param drawable
     */
    void setImageDrawable(Drawable drawable);

    /**
     * 获取自己
     * @return
     */
    View getSelf();

    /**
     * 获取图像的缩放模式
     * @return
     */
    ImageView.ScaleType getScaleType();
    /**
     * 清除动画
     */
    void clearAnimation();

    /**
     * 设置动画
     * @param animation
     */
    void startAnimation(Animation animation);

    /**
     * 显示图片
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayIamge(String uri);

    /**
     * 显示drawable资源里的图片
     * @param drawableResId drawable id
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayResourceImage(int drawableResId);

    /**
     * 显示asset文件夹中的图片
     * @param imageFileName 文件名
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayAssetImage(String imageFileName);

    /**
     * 显示uri指定的图片
     * @param uri 图片uri
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayURIImage(Uri uri);

    /**
     * 获取显示参数
     * @return
     */
    DisplayOptions getDisplayOptions();

    /**
     * 设置显示参数
     * @param displayOptions
     */
    void setDisplayOptions(DisplayOptions displayOptions);

    /**
     * 设置显示参数的名称
     * @param displayOptions 显示参数的名称
     */
    void setDisplayOptions(Enum<?> displayOptions);

    /**
     * 获取显示监听器
     * @param isPauseDown
     * @return 显示监听器
     */
    DisplayListener getDisplayListener(boolean isPauseDown);

    /**
     * 设置显示监听器
     * @param displayListener
     */
    void setDisplayListener(DisplayListener displayListener);

    /**
     * 获取进度监听器
     * @return
     */
    ProgressListener getProgressListener();

    /**
     * 设置进度监听器
     * @param progressListener
     */
    void setProgressListener(ProgressListener progressListener);

    /**
     * 获取显示请求，你可以通过这个对象来查看状态或主动取消请求
     * @return
     */
    Request getDisplayRequest();

    /**
     * 设置显示请求，此方法由SkecthPictures来调用，你无需理会
     * @param request
     */
    void setDisplayRequest(Request request);

    /**
     * 获取显示参数集
     * @return
     */
    DisplayParams getDisplayParams();

    /**
     * 设置显示参数集
     * @param displayParams 显示参数集
     */
    void setDisplayParams(DisplayParams displayParams);

}
