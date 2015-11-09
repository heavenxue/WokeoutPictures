package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.DisplayRequest;

import java.util.HashMap;
import java.util.Map;

/** 图片加载器，可以从网络或者本地加载图片，并且支持自动清除缓存
 *
 * Created by Administrator on 2015/11/3.
 */
public class SketchPictures {
    public static  final String TAG = SketchPictures.class.getSimpleName();
    private static boolean debugMode;   //调试模式，在控制台输出日志
    public static SketchPictures instance;
    private Configuration configuration;
    public static Map<Enum<?>,RequestOptions> optionsMap;//请求选项集



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
     * 下载图片
     * @param uri 图片Uri 支持一下几种
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * </blockquote>
     * @param downLoadListener 下载监听器
     * @return  DownloadHelper 你可以继续设置一些参数，最后调用fire()方法开始下载
     */
    public DownloadHelper download(String uri,DownLoadListener downLoadListener){
        return configuration.getHelperFactory().getDownloadHelper(this,uri).listener(downLoadListener);
    }

    /**
     * 根据uri加载图片
     * @param uri 图片uri 支持一下几种
     *<blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param loadListener 加载器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
    public LoadHelper load(String uri,LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this, uri).listener(loadListener);
    }

    /**
     * 加载asset文件中的图片
     * @param fileName 文件名称
     * asset://image.png"; // from assets
     * @param loadListener 加载器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
    public LoadHelper loadFromAsset(String fileName,LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this, UriSheme.ASSET.createUri(fileName)).listener(loadListener);
    }

    /**
     * 加载资源中的图片
     * @param drawableID 资源id
     * @param loadListener 加载器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
    public LoadHelper loadFromResource(int drawableID,LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this,UriSheme.DRAWABLE.createUri(String.valueOf(drawableID))).listener(loadListener);
    }

    /**
     * 加载uri指向的图片
     * @param uri 图片rui
     * @param loadListener 加载器
     * @return LoadHelper 你可以继续设置一些参数，最后调用fire()方法开始加载
     */
    public LoadHelper loadFromURI(Uri uri,LoadListener loadListener){
        return configuration.getHelperFactory().getLoadHelper(this, uri.toString()).listener(loadListener);
    }

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
     * @param sketchImageViewInterface 显示图片的视图
     * @param uri
     * @param sketchImageViewInterface
     * @return  DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(String uri,SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, uri, sketchImageViewInterface);
    }

    /**显示asset文件夹中的图片
     * @param fileName 文件名
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper displayFromAsset(String fileName,SketchImageViewInterface sketchImageViewInterface){
    return configuration.getHelperFactory().getDisplayHelper(this, UriSheme.ASSET.createUri(fileName), sketchImageViewInterface);
    }

    /**
     * 显示资源中的图片
     * @param drawableID 图像资源ｉｄ
     * @param sketchImageViewInterface　图片接口
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper displayFromResource(int drawableID,SketchImageViewInterface sketchImageViewInterface){
     return configuration.getHelperFactory().getDisplayHelper(this,UriSheme.DRAWABLE.createUri(String.valueOf(drawableID)),sketchImageViewInterface);
    }

    /**
     * 显示指向uri的图片
     * @param uri
     * @param sketchImageViewInterface
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper displayFromURI(Uri uri,SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this,uri!=null?uri.toString():null,sketchImageViewInterface);
    }
    /**
     * 显示图片，主要用于配合SketchImageView兼容RecyclerView
     * @param displayParams 参数集
     * @param sketchImageViewInterface 显示图片的视图
     * @return DisplayHelper 你可以继续设置一些参数，最后调用fire()方法开始显示
     */
    public DisplayHelper display(DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface){
        return configuration.getHelperFactory().getDisplayHelper(this, displayParams, sketchImageViewInterface);
    }



    /**
     * 取消
     * @param sketchImageViewInterface ImageView
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancel(SketchImageViewInterface sketchImageViewInterface) {
        final DisplayRequest displayRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
        if (displayRequest != null && !displayRequest.isFinished()) {
            displayRequest.cancel();
            return true;
        }else{
            return false;
        }
    }
    /**
     * 获取选项
     * @param optionsName 选项名称
     * @return 选项
     */
     public static RequestOptions getOptions(Enum<?> optionsName){
         if(optionsMap == null){
             return null;
         }
         return optionsMap.get(optionsName);
     }
    /**
     * 放入选项
     * @param optionsName 选项名称
     * @param options 选项
     */
    public static void putOptions(Enum<?> optionsName, RequestOptions options){
        if(optionsMap == null){
            synchronized (SketchPictures.class){
                if(optionsMap == null){
                    optionsMap = new HashMap<Enum<?>, RequestOptions>();
                }
            }
        }
        optionsMap.put(optionsName, options);
    }
    /**
     * 获取OptionMap
     * @return OptionMap
     */
    public static Map<Enum<?>, RequestOptions> getOptionsMap() {
        return optionsMap;
    }

    /**
     * 是否开启调试模式
     * @return 否开启调试模式，开启调试模式后会在控制台输出LOG
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * 设置是否开启调试模式
     * @param debugMode 否开启调试模式，开启调试模式后会在控制台输出LOG
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

