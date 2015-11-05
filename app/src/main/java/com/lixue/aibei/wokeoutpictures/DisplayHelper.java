package com.lixue.aibei.wokeoutpictures;

import android.widget.ImageView;

import com.lixue.aibei.wokeoutpictures.display.ImageDisplayer;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.request.Request;

/**
 * 显示协助器
 * Created by Administrator on 2015/11/5.
 */
public interface DisplayHelper {

    /**
     * 初始化
     */
    DisplayHelper init(SketchPictures sketch, String uri, SketchImageViewInterface sketchImageViewInterface);

    /**
     * 初始化
     */
    DisplayHelper init(SketchPictures sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface);

    /**
     * 恢复默认值
     */
    void reset();

    /**
     * 填充SketchImageView的显示参数
     */
    void saveDisplayParams();

    /**
     * 设置名称，用于在log总区分请求
     * @param name 名称
     * @return DisplayHelper
     */
    DisplayHelper name(String name);

    /**
     * 设置内存缓存ID（大多数情况下你不需要手动设置缓存ID，除非你想使用通过putBitmap()放到缓存中的图片）
     * @param memoryCacheId 内存缓存ID
     * @return DisplayHelper
     */
    DisplayHelper memoryCacheId(String memoryCacheId);

    /**
     * 关闭硬盘缓存
     * @return DisplayHelper
     */
    DisplayHelper disableDiskCache();

    /**
     * 禁止解码Gif图片
     * @return LoadHelper
     */
    DisplayHelper disableDecodeGifImage();

    /**
     * 设置最大尺寸，在解码时会使用此Size来计算inSimpleSize
     * @param maxSize 最大尺寸
     * @return DisplayHelper
     */
    DisplayHelper maxSize(MaxSize maxSize);

    /**
     * 设置最大尺寸，在解码时会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return DisplayHelper
     */
    DisplayHelper maxSize(int width, int height);

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     * @param width 宽
     * @param height 高
     * @return DisplayHelper
     */
    DisplayHelper resize(int width, int height);

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     * @param width 宽
     * @param height 高
     * @param scaleType
     * @return LoadHelper
     */
    DisplayHelper resize(int width, int height, ImageView.ScaleType scaleType);

    /**
     * 使用ImageView的LayoutSize作为resize
     */
    DisplayHelper resizeByFixedSize();

    /**
     * 强制使经过resize返回的图片同resize的尺寸一致
     * @return DisplayHelper
     */
    DisplayHelper forceUseResize();

    /**
     * 返回低质量的图片
     * @return LoadHelper
     */
    DisplayHelper lowQualityImage();

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor Bitmap处理器
     * @return DisplayHelper
     */
    DisplayHelper processor(ImageProcessor processor);

    /**
     * 关闭内存缓存
     * @return DisplayHelper
     */
    DisplayHelper disableMemoryCache();

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    DisplayHelper listener(DisplayListener displayListener);

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     * @param displayer 图片显示器
     */
    DisplayHelper displayer(ImageDisplayer displayer);

    /**
     * 设置正在加载时显示的图片
     * @param loadingImageHolder 正在加载时显示的图片
     */
    DisplayHelper loadingImage(ImageHolder loadingImageHolder);

    /**
     * 设置正在加载时显示的图片
     * @param drawableResId 资源图片ID
     */
    DisplayHelper loadingImage(int drawableResId);

    /**
     * 设置失败时显示的图片
     * @param failureImageHolder 失败时显示的图片
     */
    DisplayHelper failureImage(ImageHolder failureImageHolder);

    /**
     * 设置失败时显示的图片
     * @param drawableResId 资源图片ID
     */
    DisplayHelper failureImage(int drawableResId);

    /**
     * 设置暂停下载时显示的图片
     * @param pauseDownloadImageHolder 暂停下载时显示的图片
     */
    DisplayHelper pauseDownloadImage(ImageHolder pauseDownloadImageHolder);

    /**
     * 设置暂停下载时显示的图片
     * @param drawableResId 资源图片ID
     */
    DisplayHelper pauseDownloadImage(int drawableResId);

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return DisplayHelper
     */
    DisplayHelper progressListener(ProgressListener progressListener);

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     * @return DisplayHelper
     */
    DisplayHelper requestLevel(RequestLevel requestLevel);

    /**
     * 设置显示参数
     * @param options 显示参数
     * @return DisplayHelper
     */
    DisplayHelper options(DisplayOptions options);

    /**
     * 设置显示参数，你只需要提前将DisplayOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return DisplayHelper
     */
    DisplayHelper options(Enum<?> optionsName);

    /**
     * 提交请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    Request commit();

    /**
     * 生成内存缓存ID
     */
    String generateMemoryCacheId(String uri, MaxSize maxSize, ReSize resize, boolean forceUseResize, boolean lowQualityImage, ImageProcessor imageProcessor);
}
