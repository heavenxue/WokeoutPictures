package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.lixue.aibei.wokeoutpictures.display.ImageDisplayer;
import com.lixue.aibei.wokeoutpictures.display.TransitionImageDisplayer;
import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.request.DisplayRequest;
import com.lixue.aibei.wokeoutpictures.request.Request;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

/**
 * 默认显示协助器
 * Created by Administrator on 2015/11/6.
 */
public class DefaultDisplayHelper implements DisplayHelper {
    private static final String NAME = "DefaultDisplayHelper";

    // 基本属性
    protected SketchPictures sketch;
    protected String uri;
    protected String name;
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom;

    // 下载属性
    protected boolean cacheInDisk = true;
    protected ProgressListener progressListener;

    // 加载属性
    protected ReSize resize;
    protected boolean decodeGifImage = true;
    protected boolean forceUseResize;
    protected boolean lowQualityImage;
    protected MaxSize maxSize;
    protected ImageProcessor imageProcessor;

    // 显示属性
    protected String memoryCacheId;
    protected boolean cacheInMemory = true;
    protected FixedSize fixedSize;
    protected ImageDisplayer imageDisplayer;
    protected DisplayListener displayListener;
    protected ImageHolder loadingImageHolder;
    protected ImageHolder failureImageHolder;
    protected ImageHolder pauseDownloadImageHolder;
    protected SketchImageViewInterface sketchImageViewInterface;

    protected Context context;
    protected ImageView.ScaleType scaleType;

    /**
     * 创建显示请求生成器
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
     * @param sketchImageViewInterface 图片View
     */
    public DefaultDisplayHelper(SketchPictures sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        init(sketch, uri, sketchImageViewInterface);
    }
    /**
     * 创建显示请求生成器
     * @param sketch Sketch
     * @param displayParams 参数集
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param sketchImageViewInterface 图片View
     */
    public DefaultDisplayHelper(SketchPictures sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        init(sketch, displayParams, sketchImageViewInterface);
    }

    @Override
    public DisplayHelper init(SketchPictures sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        this.context = sketch.getConfiguration().getContext();
        this.sketch = sketch;
        this.uri = uri;
        this.sketchImageViewInterface = sketchImageViewInterface;

        if(sketch.getConfiguration().isPauseDownload()){
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }

        if(sketch.getConfiguration().isPauseLoad()){
            this.requestLevel = RequestLevel.MEMORY;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_LOAD;
        }

        if(sketchImageViewInterface != null){
            this.scaleType = sketchImageViewInterface.getScaleType();
            if (sketch.getConfiguration().getImageSizeCalculator() != null){
                this.fixedSize = sketch.getConfiguration().getImageSizeCalculator().caculateImageFixedSize(sketchImageViewInterface);
            }
            this.maxSize = sketch.getConfiguration().getImageSizeCalculator().caculateImageMaxSize(sketchImageViewInterface);

            this.sketchImageViewInterface.onDisplay();
            options(this.sketchImageViewInterface.getDisplayOptions());

            this.displayListener = this.sketchImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
            this.progressListener = this.sketchImageViewInterface.getProgressListener();
        }

        return this;
    }

    @Override
    public DisplayHelper init(SketchPictures sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        this.context = sketch.getConfiguration().getContext();

        this.sketch = sketch;
        this.uri = displayParams.uri;
        this.name = displayParams.name;
        this.requestLevel = displayParams.requestLevel;
        this.requestLevelFrom = displayParams.requestLevelFrom;

        this.cacheInDisk = displayParams.cacheInDisk;
        this.progressListener = displayParams.progressListener;

        this.resize = displayParams.resize;
        this.maxSize = displayParams.maxSize;
        this.forceUseResize = displayParams.forceUseResize;
        this.lowQualityImage = displayParams.lowQualityImage;
        this.requestLevel = displayParams.requestLevel;
        this.imageProcessor = displayParams.imageProcessor;
        this.decodeGifImage = displayParams.decodeGifImage;

        this.sketchImageViewInterface = sketchImageViewInterface;
        this.fixedSize = displayParams.fixedSize;
        this.memoryCacheId = displayParams.memoryCacheId;
        this.cacheInMemory = displayParams.cacheInMemory;
        this.imageDisplayer = displayParams.imageDisplayer;
        this.loadingImageHolder = displayParams.loadingImageHolder;
        this.failureImageHolder = displayParams.loadFailImageHolder;
        this.pauseDownloadImageHolder = displayParams.pauseDownloadImageHolder;
        this.displayListener = displayParams.displayListener;

        this.sketchImageViewInterface.onDisplay();

        this.displayListener = this.sketchImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
        this.progressListener = this.sketchImageViewInterface.getProgressListener();

        return this;
    }

    @Override
    public void reset() {
        sketch = null;
        uri = null;
        name = null;
        requestLevel = RequestLevel.NET;
        requestLevelFrom = null;

        cacheInDisk = true;
        progressListener = null;

        resize = null;
        maxSize = null;
        forceUseResize = false;
        lowQualityImage = false;
        imageProcessor = null;
        decodeGifImage = true;

        memoryCacheId = null;
        fixedSize = null;
        cacheInMemory = true;
        sketchImageViewInterface = null;
        imageDisplayer = null;
        loadingImageHolder = null;
        failureImageHolder = null;
        pauseDownloadImageHolder = null;
        displayListener = null;
    }

    @Override
    public void saveDisplayParams() {
        if(sketchImageViewInterface != null){
            DisplayParams displayParams = sketchImageViewInterface.getDisplayParams();
            if(displayParams == null){
                displayParams = new DisplayParams();
            }

            displayParams.uri = uri;
            displayParams.name = name;
            displayParams.requestLevel = requestLevel;
            displayParams.requestLevelFrom = requestLevelFrom;

            displayParams.cacheInDisk = cacheInDisk;
            displayParams.progressListener = progressListener;

            displayParams.resize = resize;
            displayParams.maxSize = maxSize;
            displayParams.forceUseResize = forceUseResize;
            displayParams.lowQualityImage = lowQualityImage;
            displayParams.imageProcessor = imageProcessor;
            displayParams.decodeGifImage = decodeGifImage;

            displayParams.memoryCacheId = memoryCacheId;
            displayParams.fixedSize = fixedSize;
            displayParams.cacheInMemory = cacheInMemory;
            displayParams.imageDisplayer = imageDisplayer;
            displayParams.loadingImageHolder = loadingImageHolder;
            displayParams.loadFailImageHolder = failureImageHolder;
            displayParams.pauseDownloadImageHolder = pauseDownloadImageHolder;
            displayParams.displayListener = displayListener;

            sketchImageViewInterface.setDisplayParams(displayParams);
        }
    }

    @Override
    public DefaultDisplayHelper name(String name){
        this.name = name;
        return this;
    }

    @Override
    public DefaultDisplayHelper memoryCacheId(String memoryCacheId){
        this.memoryCacheId = memoryCacheId;
        return this;
    }

    @Override
    public DefaultDisplayHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    @Override
    public DefaultDisplayHelper disableDecodeGifImage() {
        this.decodeGifImage = false;
        return this;
    }

    @Override
    public DefaultDisplayHelper maxSize(MaxSize maxSize){
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public DefaultDisplayHelper maxSize(int width, int height){
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    @Override
    public DefaultDisplayHelper resize(int width, int height){
        this.resize = new ReSize(width, height);
        return this;
    }

    @Override
    public DefaultDisplayHelper resize(int width, int height, ImageView.ScaleType scaleType) {
        this.resize = new ReSize(width, height, scaleType);
        return this;
    }

    @Override
    public DefaultDisplayHelper resizeByFixedSize(){
        this.resize = sketch.getConfiguration().getImageSizeCalculator().caculateImageReSize(sketchImageViewInterface);
        return this;
    }

    @Override
    public DefaultDisplayHelper forceUseResize() {
        this.forceUseResize = true;
        return this;
    }

    @Override
    public DefaultDisplayHelper lowQualityImage() {
        this.lowQualityImage = true;
        return this;
    }

    @Override
    public DefaultDisplayHelper processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    @Override
    public DefaultDisplayHelper disableMemoryCache() {
        this.cacheInMemory = false;
        return this;
    }

    @Override
    public DefaultDisplayHelper listener(DisplayListener displayListener) {
        this.displayListener = displayListener;
        return this;
    }

    @Override
    public DefaultDisplayHelper displayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    @Override
    public DefaultDisplayHelper loadingImage(ImageHolder loadingImageHolder) {
        this.loadingImageHolder = loadingImageHolder;
        return this;
    }

    @Override
    public DefaultDisplayHelper loadingImage(int drawableResId) {
        loadingImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DefaultDisplayHelper failureImage(ImageHolder failureImageHolder) {
        this.failureImageHolder = failureImageHolder;
        return this;
    }

    @Override
    public DefaultDisplayHelper failureImage(int drawableResId) {
        failureImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DefaultDisplayHelper pauseDownloadImage(ImageHolder pauseDownloadImageHolder) {
        this.pauseDownloadImageHolder = pauseDownloadImageHolder;
        return this;
    }

    @Override
    public DefaultDisplayHelper pauseDownloadImage(int drawableResId) {
        pauseDownloadImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DefaultDisplayHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public DefaultDisplayHelper requestLevel(RequestLevel requestLevel){
        if(requestLevel != null){
            this.requestLevel = requestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    @Override
    public DefaultDisplayHelper options(DisplayOptions options){
        if(options == null){
            return this;
        }

        this.cacheInDisk = options.isCacheInDisk();
        this.cacheInMemory = options.isCacheInMemory();
        if(this.maxSize == null || (options.getMaxSize() != null && sketch.getConfiguration().getImageSizeCalculator().compareMaxSize(options.getMaxSize(), this.maxSize) < 0)){
            this.maxSize = options.getMaxSize();
        }
        if(this.resize == null){
            if(options.isResizeByFixedSize()){
                resizeByFixedSize();
            }else if(options.getResize() != null){
                this.resize = new ReSize(options.getResize());
            }
        }
        this.forceUseResize = options.getIsForceUseResize();
        this.lowQualityImage = options.getIsLowQualityImage();
        if(this.imageProcessor == null){
            this.imageProcessor = options.getImageProcessor();
        }
        if(this.imageDisplayer == null){
            this.imageDisplayer = options.getImageDisplayer();
        }
        this.decodeGifImage = options.getIsDecodeGifImage();
        if(this.loadingImageHolder == null){
            this.loadingImageHolder = options.getLoadingImageHolder();
        }
        if(this.failureImageHolder == null){
            this.failureImageHolder = options.getFailureImage();
        }
        if(this.pauseDownloadImageHolder == null){
            this.pauseDownloadImageHolder = options.getPauseDownloadImage();
        }
        RequestLevel optionRequestLevel = options.getRequestLevel();
        if(requestLevel != null && optionRequestLevel != null){
            if(optionRequestLevel.getLevel() < requestLevel.getLevel()){
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
    public DefaultDisplayHelper options(Enum<?> optionsName){
        return options((DisplayOptions) sketch.getOptions(optionsName));
    }

    /**
     * 处理一下参数
     */
    protected void handleParams(){
        if(resize != null && resize.getScaleType() == null && sketchImageViewInterface != null){
            resize.setScaleType(sketchImageViewInterface.getScaleType());
        }
        if(resize != null && imageProcessor == null){
            imageProcessor = sketch.getConfiguration().getDefaultCutImageProcessor();
        }
        if(maxSize == null){
            maxSize = sketch.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(sketch.getConfiguration().getContext());
        }
        if(name == null && memoryCacheId != null){
            name = memoryCacheId;
        }
        if(!sketch.getConfiguration().isDecodeGifImage()){
            decodeGifImage = false;
        }
        if(!sketch.getConfiguration().isCacheInDisk()){
            cacheInDisk = false;
        }
        if(!sketch.getConfiguration().isCacheInMemory()){
            cacheInMemory = false;
        }
        if(sketch.getConfiguration().isLowQualityImage()){
            lowQualityImage = true;
        }
        if(imageDisplayer instanceof TransitionImageDisplayer){
            if(fixedSize != null){
                if(loadingImageHolder != null && scaleType != ImageView.ScaleType.CENTER_CROP){
                    throw new IllegalArgumentException("When using TransitionImageDisplayer ImageView wide tall if is fixed and set the loadingImage, then ScaleType must be CENTER_CTOP");
                }
            }else{
                if(loadingImageHolder != null){
                    throw new IllegalArgumentException("When using TransitionImageDisplayer ImageView wide tall if is unknown may not be used then loadingImage");
                }
            }
        }
    }

    @Override
    public Request commit() {
        saveDisplayParams();

        handleParams();

        if(displayListener != null){
            displayListener.onStarted();
        }

        // 验证imageView参数
        if(sketchImageViewInterface == null){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "sketchImageViewInterface is null", " - ", (name != null ? name : uri)));
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.IMAGE_VIEW_NULL);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if(sketchImageViewInterface != null){
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = failureImageHolder != null ? failureImageHolder.getRecycleBitmapDrawable(context) : null;
            if(failureDrawable != null){
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriSheme uriScheme = UriSheme.valueOfUri(uri);
        if(uriScheme == null){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme: ", uri, " - ", (name != null ? name : uri)));
            }
            if(sketchImageViewInterface != null){
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = failureImageHolder != null ? failureImageHolder .getRecycleBitmapDrawable(context) : null;
            if(failureDrawable != null){
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 尝试从内存中寻找缓存图片
        String memoryCacheId = this.memoryCacheId !=null? this.memoryCacheId : generateMemoryCacheId(uri, maxSize, resize, forceUseResize, lowQualityImage, imageProcessor);
        if(name == null){
            name = memoryCacheId;
        }
        if(cacheInMemory){
            Drawable cacheDrawable = sketch.getConfiguration().getMemoryCache().get(memoryCacheId);
            if(cacheDrawable != null){
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if(!recycleDrawable.isRecyled()){
                    if(SketchPictures.isDebugMode()){
                        Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "from memory get bitmap", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                    if(sketchImageViewInterface != null){
                        sketchImageViewInterface.setDisplayRequest(null);
                    }
                    sketchImageViewInterface.setImageDrawable(cacheDrawable);
                    if(displayListener != null){
                        displayListener.onCompleted(ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    }
                    sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                    return null;
                }else{
                    sketch.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if(SketchPictures.isDebugMode()){
                        Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                }
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if(requestLevel == RequestLevel.MEMORY){
            Drawable loadingDrawable = loadingImageHolder != null ? loadingImageHolder.getRecycleBitmapDrawable(context) : null;
            sketchImageViewInterface.clearAnimation();
            sketchImageViewInterface.setImageDrawable(loadingDrawable);
            if(displayListener != null){
                displayListener.onCanceled(requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? CancelCause.PAUSE_LOAD :CancelCause.LEVEL_IS_MEMORY);
                if(SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", (requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? "pause load" : "requestLevel is memory"), " - ", name));
                }
            }
            if(sketchImageViewInterface != null){
                sketchImageViewInterface.setDisplayRequest(null);
            }
            return null;
        }

        // 试图取消已经存在的请求
        DisplayRequest potentialRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
        if(potentialRequest != null && !potentialRequest.isFinished()){
            if(memoryCacheId.equals(potentialRequest.getMemoryCacheId())){
                if(SketchPictures.isDebugMode()){
                    Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "don't need to cancel", "；", "ImageViewCode", "=", Integer.toHexString(sketchImageViewInterface.hashCode()), "；", potentialRequest.getName()));
                }
                sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                return potentialRequest;
            }else{
                potentialRequest.cancel();
            }
        }

        // 组织请求
        final DisplayRequest request = sketch.getConfiguration().getRequestFactory().newDisplayRequest(sketch, uri, uriScheme, memoryCacheId, sketchImageViewInterface);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setIsCacheInDisk(cacheInDisk);
        request.setProgressListener(progressListener);

        request.setResize(resize);
        request.setMaxSize(maxSize);
        request.setIsForceUseReseze(forceUseResize);
        request.setIsLowQualityImage(lowQualityImage);
        request.setImageProcessor(imageProcessor);
        request.setIsDecodeGifImage(decodeGifImage);

        request.setFixedSize(fixedSize);
        request.setImageDisplayer(imageDisplayer);
        request.setDisplayListener(displayListener);
        request.setCacheInMemory(cacheInMemory);
        request.setImageFailedHolder(failureImageHolder);
        request.setImagePauseDownloadHolder(pauseDownloadImageHolder);

        // 显示默认图片
        Drawable loadingBindDrawable;
        if(loadingImageHolder != null){
            RecycleBitmapDrawable loadingDrawable = loadingImageHolder.getRecycleBitmapDrawable(context);
            FixedSize tempFixedSize = null;
            if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
                tempFixedSize = fixedSize;
            }
            loadingBindDrawable = new BindFixedRecycleBitmapDrawable(loadingDrawable, tempFixedSize, request);
        }else{
            loadingBindDrawable = new BindFixedRecycleBitmapDrawable(null, request);
        }
        sketchImageViewInterface.setImageDrawable(loadingBindDrawable);

        if(sketchImageViewInterface != null){
            sketchImageViewInterface.setDisplayRequest(request);
        }

        // 分发请求
        request.postRunDispatch();
        sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }

    @Override
    public String generateMemoryCacheId(String uri, MaxSize maxSize, ReSize resize, boolean forceUseResize, boolean lowQualityImage, ImageProcessor imageProcessor){
        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        if(maxSize != null){
            builder.append("_");
            maxSize.appendIndentifier(builder);
        }
        if(resize != null){
            builder.append("_");
            resize.appendIndentifier(builder);
        }
        if(forceUseResize){
            builder.append("_");
            builder.append("forceUseResize");
        }
        if(lowQualityImage){
            builder.append("_");
            builder.append("lowQualityImage");
        }
        if(imageProcessor != null){
            builder.append("_");
            imageProcessor.appendIdentifier(builder);
        }
        return builder.toString();
    }
}
