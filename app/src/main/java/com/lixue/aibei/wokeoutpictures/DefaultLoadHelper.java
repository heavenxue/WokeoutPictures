package com.lixue.aibei.wokeoutpictures;

import android.util.Log;
import android.widget.ImageView;

import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.request.LoadRequest;
import com.lixue.aibei.wokeoutpictures.request.Request;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

/**
 * 默认加载协助器
 * Created by Administrator on 2015/11/6.
 */
public class DefaultLoadHelper implements LoadHelper {
    private static final String NAME = "DefaultLoadHelper";
    //基本属性
    protected SketchPictures sketchPictures;
    protected String name;
    protected String uri;
    protected RequestLevelFrom requestLevelFrom;
    protected RequestLevel requestLevel = RequestLevel.NET;//请求级别

    //下载属性
    protected boolean cacheInDisk;
    protected ProgressListener progressListener;//进度监听器

    //加载属性
    protected boolean decodeGifImage;
    protected boolean forceUseResize;
    protected boolean isLowQaulityImage;
    protected MaxSize maxSize;//最大尺寸
    protected ReSize reSize;//重设尺寸
    protected ImageProcessor processor;//图像处理器
    protected LoadListener loadListener;//图像加载监听器
    /**
     * 创建加载请求生成器
     * @param sketch Sketch
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     */
    public DefaultLoadHelper(SketchPictures sketch, String uri) {
        this.sketchPictures = sketch;
        this.uri = uri;
        if(sketch.getConfiguration().isPauseDownload()){
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }
    }

    @Override
    public LoadHelper Name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public LoadHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    @Override
    public LoadHelper disableDecodeGifImage() {
        this.decodeGifImage = false;
        return this;
    }

    @Override
    public LoadHelper setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public LoadHelper setMaxSize(int width, int height) {
        this.maxSize = new MaxSize(width,height);
        return this;
    }

    @Override
    public LoadHelper resize(int width, int height) {
        this.reSize = new ReSize(width,height);
        return this;
    }

    @Override
    public LoadHelper resize(int width, int height, ImageView.ScaleType scaleType) {
        this.reSize = new ReSize(width,height,scaleType);
        return this;
    }

    @Override
    public LoadHelper forceUseResize() {
        this.forceUseResize = true;
        return this;
    }

    @Override
    public LoadHelper lowQualityImage() {
        this.isLowQaulityImage = true;
        return this;
    }

    @Override
    public LoadHelper processor(ImageProcessor processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public LoadHelper listener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    @Override
    public LoadHelper progressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public LoadHelper options(LoadOptions options) {
        if (options == null){
            return  this;
        }
        if (maxSize == null){
            maxSize = options.getMaxSize();
        }
        if (reSize == null){
            reSize = options.getResize();
        }
        this.cacheInDisk = options.isCacheInDisk();
        this.forceUseResize = options.getIsForceUseResize();
        this.decodeGifImage = options.getIsDecodeGifImage();
        this.isLowQaulityImage = options.getIsLowQualityImage();
        if (processor == null){
            this.processor = options.getImageProcessor();
        }
        RequestLevel optionRequestLevel = options.getRequestLevel();
        if (optionRequestLevel != null && requestLevel != null){
            if (optionRequestLevel.getLevel() < requestLevel.getLevel() ){
                this.requestLevel = optionRequestLevel;
                this.requestLevelFrom = null;
            }
        }else if(optionRequestLevel != null){
            this.requestLevel = optionRequestLevel;
            this.requestLevelFrom = null;
        }

        return this;
    }

    @Override
    public LoadHelper options(Enum<?> optionsName) {
        return options((LoadOptions) SketchPictures.getOptions(optionsName));
    }

    @Override
    public LoadHelper requestLevel(RequestLevel requestLevel) {
        if(requestLevel != null){
            this.requestLevel = requestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    @Override
    public Request commit() {
        if(processor == null && reSize != null){
            processor = sketchPictures.getConfiguration().getDefaultCutImageProcessor();
        }
        if(maxSize == null){
            maxSize = sketchPictures.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(sketchPictures.getConfiguration().getContext());
        }
        if(name == null){
            name = uri;
        }
        if(!sketchPictures.getConfiguration().isDecodeGifImage()){
            decodeGifImage = false;
        }
        if(!sketchPictures.getConfiguration().isCacheInDisk()){
            cacheInDisk = false;
        }
        if(sketchPictures.getConfiguration().isLowQualityImage()){
            isLowQaulityImage = true;
        }
        if(loadListener != null){
            loadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if(loadListener != null){
                loadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriSheme uriScheme = UriSheme.valueOfUri(uri);
        if(uriScheme == null){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme", " - ", name));
            }
            if(loadListener != null){
                loadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        LoadRequest request = sketchPictures.getConfiguration().getRequestFactory().newLoadRequest(sketchPictures, uri, uriScheme);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setIsCacheInDisk(cacheInDisk);
        request.setProgressListener(progressListener);

        request.setResize(reSize);
        request.setMaxSize(maxSize);
        request.setIsForceUseReseze(forceUseResize);
        request.setIsLowQualityImage(isLowQaulityImage);
        request.setLoadListener(loadListener);
        request.setImageProcessor(processor);
        request.setIsDecodeGifImage(decodeGifImage);

        request.postRunDispatch();

        return request;
    }
}
