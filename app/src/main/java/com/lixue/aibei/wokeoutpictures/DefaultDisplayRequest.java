package com.lixue.aibei.wokeoutpictures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.lixue.aibei.wokeoutpictures.display.ImageDisplayer;
import com.lixue.aibei.wokeoutpictures.display.TransitionImageDisplayer;
import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.request.DisplayRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;

/**
 * 显示请求
 * Created by Administrator on 2015/11/10.
 */
public class DefaultDisplayRequest implements DisplayRequest,Runnable{
    private static final int WHAT_CALLBACK_COMPLETED = 102;
    private static final int WHAT_CALLBACK_FAILED = 103;
    private static final int WHAT_CALLBACK_CANCELED = 104;
    private static final int WHAT_CALLBACK_PROGRESS = 105;
    private static final int WHAT_CALLBACK_PAUSE_DOWNLOAD = 106;
    private static final String NAME = "DefaultDisplayRequest";
    //base fields
    private SketchPictures sketch;
    private String uri;//图片地址
    private UriSheme uriSheme;//uri协议格式
    private String name;//名称，用于在输出Log的时候区分不同的请求
    private RequestLevel requestLevel = RequestLevel.NET;//请求level
    private RequestLevelFrom requestLevelFrom;
    //download fields
    private boolean cacheInDisk = true;//是否开启磁盘缓存
    private ProgressListener progressListener;//下载进度器
    //load fields
    private ReSize reSize;//裁剪尺寸，ImageProcessor会根据此尺寸来裁剪图片
    private boolean decodeGifImage = true;//是否解码gif图片
    private boolean forceUseResize;//是否强制使用resize
    private boolean lowQualityImage;//是否返回低质量图片
    private MaxSize maxSize;//最大尺寸，用于读取图片时计算inSampleSize
    private ImageProcessor imageProcessor;//图片处理器
    //display fields
    private String memoryCacheId;//内存缓存id
    private boolean cacheInMemory;//是否开启内存缓存
    private FixedSize fixedSize;//固定尺寸
    private ImageHolder failureImageHolder;//失败时显示的图片
    private ImageHolder pauseDownloadImageHolder;//当暂停下载时显示的图片
    private ImageDisplayer imageDisplayer;//图片显示器
    private DisplayListener displayListener;//显示监听器
    //runtime fields
    private File cacheFile;//缓存文件
    private byte[] imageData;//如果不使用磁盘缓存的话，下载完成的图片数据就用字节数组保存
    private String mimeType;//文件类型
    private Context context;//上下文
    private Drawable resultDrawable;//最终的图片（下载下来的图片）
    private ImageFrom imageFrom;//图片来源
    private FailCause failCause;//失败原因
    private RunStatus runStatus = RunStatus.DISPATCH;//运行状态，用于在执行run方法时，知道在干什么
    private CancelCause cancelCause;//取消原因
    private RequestStatus requestStatus= RequestStatus.WAIT_DISPATCH;//请求状态，默认为等待分发
    private SketchImageViewInterfaceHolder sketchImageViewInterfaceHolder;//绑定imageView

    protected ImageView.ScaleType scaleType;

    public DefaultDisplayRequest(SketchPictures sketch, String uri, UriSheme uriScheme, String memoryCacheId, SketchImageViewInterface sketchImageViewInterface){
        this.context = sketch.getConfiguration().getContext();
        this.sketch = sketch;
        this.uri = uri;
        this.uriSheme = uriScheme;
        this.memoryCacheId = memoryCacheId;
        this.sketchImageViewInterfaceHolder = new SketchImageViewInterfaceHolder(sketchImageViewInterface,this);
        this.scaleType = sketchImageViewInterface.getScaleType();
    }

    //------------base methods-------------
    @Override
    public SketchPictures getSketch() {
        return sketch;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UriSheme getUriScheme() {
        return uriSheme;
    }

    @Override
    public void setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
    }

    @Override
    public void setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        this.requestLevelFrom = requestLevelFrom;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    //-------------download methods---------------------

    @Override
    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    @Override
    public void setIsCacheInDisk(boolean isCacheInDisk) {
        this.cacheInDisk = isCacheInDisk;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    //--------------load methods--------------------------------

    @Override
    public void setResize(ReSize resize) {
        this.reSize = resize;
    }

    @Override
    public ReSize getResize() {
        return reSize;
    }

    @Override
    public void setIsForceUseReseze(boolean isForceUseReseze) {
        this.forceUseResize = isForceUseReseze;
    }

    @Override
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    @Override
    public MaxSize getMaxSize() {
        return maxSize;
    }

    @Override
    public void setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    @Override
    public void setIsLowQualityImage(boolean isLowQualityImage) {
        this.lowQualityImage = isLowQualityImage;
    }

    @Override
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    //---------------display methods------------------

    @Override
    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    @Override
    public void setCacheInMemory(boolean cacheInMemory) {
        this.cacheInMemory = cacheInMemory;
    }

    @Override
    public void setImageFailedHolder(ImageHolder imageHolder) {
        this.failureImageHolder = imageHolder;
    }

    @Override
    public Drawable getFailureDrawable() {
        if(failureImageHolder == null){
            return null;
        }else if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
            return new FixedRecycleBitmapDrawable(failureImageHolder.getRecycleBitmapDrawable(context), fixedSize);
        }else{
            return failureImageHolder.getRecycleBitmapDrawable(context);
        }
    }

    @Override
    public void setImagePauseDownloadHolder(ImageHolder imageHolder) {
        this.pauseDownloadImageHolder = imageHolder;
    }

    @Override
    public Drawable getPauseDownloadDrawable() {
        if(pauseDownloadImageHolder == null){
            return null;
        }else if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
            return new FixedRecycleBitmapDrawable(pauseDownloadImageHolder.getRecycleBitmapDrawable(context), fixedSize);
        }else{
            return pauseDownloadImageHolder.getRecycleBitmapDrawable(context);
        }
    }

    @Override
    public void setImageDisplayer(ImageDisplayer imageDisplayer) {
        this.imageDisplayer = imageDisplayer;
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    @Override
    public void setFixedSize(FixedSize fixedSize) {
        this.fixedSize = fixedSize;
    }
    //--------------runtime methods------------------------

    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean cancel() {
        if(isFinished()){
            return false;
        }
        toCancleStatus(cancelCause.NORMAL);
        return true;
    }

    @Override
    public File getCacheFile() {
        return cacheFile;
    }

    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    //-------------------------other methods------------------------

    @Override
    public boolean isCanceled() {
        boolean isCanceled = requestStatus == RequestStatus.CANCELED;
        if(!isCanceled){
            isCanceled = sketchImageViewInterfaceHolder != null && sketchImageViewInterfaceHolder.isCollected();
            if(isCanceled){
                toCancleStatus(CancelCause.NORMAL);
            }
        }
        return isCanceled;
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        setRequestStatus(RequestStatus.WAIT_DISPLAY);
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCancleStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        if(displayListener != null){
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
        }
    }

    @Override
    public void invokeInMainThread(Message msg) {
        switch (msg.what){
            case WHAT_CALLBACK_COMPLETED:
                handleCompletedOnMainThread();
                break;
            case WHAT_CALLBACK_PROGRESS :
                updateProgressOnMainThread(msg.arg1, msg.arg2);
                break;
            case WHAT_CALLBACK_FAILED:
                handleFailedOnMainThread();
                break;
            case WHAT_CALLBACK_CANCELED:
                handleCanceledOnMainThread();
                break;
            case WHAT_CALLBACK_PAUSE_DOWNLOAD:
                handlePauseDownloadOnMainThread();
                break;
            default:
                new IllegalArgumentException("unknown message what: "+msg.what).printStackTrace();
                break;
        }
    }

    @Override
    public void updateProgress(int totalLength, int completeLength) {
        if(progressListener != null){
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completeLength, this).sendToTarget();
        }
    }

    @Override
    public void postRunDispatch() {
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
        this.runStatus = RunStatus.DISPATCH;
        sketch.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void postRunDownload() {
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
        this.runStatus = RunStatus.DOWNLOAD;
        sketch.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void postRunLoad() {
        setRequestStatus(RequestStatus.WAIT_LOAD);
        this.runStatus = RunStatus.LOAD;
        sketch.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    @Override
    public void setLoadListener(LoadListener loadListener) {
    }

    @Override
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    @Override
    public void setIsDecodeGifImage(boolean isDecodeGifImage) {

    }

    public void setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
    }

    @Override
    public void setDownloadListener(DownLoadListener downloadListener) {

    }

    @Override
    public boolean isLocalApkFile() {
        return uriSheme == UriSheme.FILE && SketchUtils.checkSuffix(uri, ".apk");
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    @Override
    public void run() {
        switch(runStatus){
            case DISPATCH:
                executeDispatch();
                break;
            case LOAD:
                executeLoad();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalArgumentException("unknown runStatus: "+runStatus.name()).printStackTrace();
                break;
        }
    }

    /**
     * 执行分发
     */
    private void executeDispatch(){
        setRequestStatus(RequestStatus.DISPATCHING);
        if (uriSheme ==  UriSheme.HTTP || uriSheme == UriSheme.HTTPS){
            File diskCacheFile = cacheInDisk ? sketch.getConfiguration().getDiskCache().getCacheFile(uri):null;
            if (diskCacheFile != null && diskCacheFile.exists()){
                cacheFile = diskCacheFile;
                this.imageFrom = ImageFrom.DISK_CACHE;
                postRunLoad();
                if (SketchPictures.isDebugMode()){
                    Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "diskCache", " - ", name));
                }
            }else{
                if (requestLevel == RequestLevel.LOCAL){
                    if (requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD){
                        setRequestStatus(RequestStatus.WAIT_DISPLAY);
                        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PAUSE_DOWNLOAD,this).sendToTarget();
                        if (SketchPictures.isDebugMode()){
                            Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "pause download", " - ", name));
                        }
                    }else{
                        toCancleStatus(CancelCause.LEVEL_IS_LOCAL);
                        if (SketchPictures.isDebugMode()){
                            if(SketchPictures.isDebugMode()){
                                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "requestLevel is local", " - ", name));
                            }
                        }
                    }
                    return;
                }
                postRunDownload();
                if (SketchPictures.isDebugMode()){
                    Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "download", " - ", name));
                }
            }
        }else{
            this.imageFrom = ImageFrom.LOCAL;
            postRunLoad();
            if(SketchPictures.isDebugMode()){
                Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "local", " - ", name));
            }
        }
    }

    /**
     * 执行下载
     */
    private void executeDownload(){
        if (isCanceled()){
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "startDownload", " - ", name));
            }
            return;
        }
         DownLoadResult downLoadResult = sketch.getConfiguration().getImageDownloader().download(this);
        if (isCanceled()){
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "downloadAfter", " - ", name));
            }
            return;
        }
        if (downLoadResult != null && downLoadResult.getResult() != null){
            if (downLoadResult.getResult().getClass().isAssignableFrom(File.class)){
                this.cacheFile = (File)downLoadResult.getResult();
            }else{
                this.imageData = (byte[])downLoadResult.getResult();
            }
            this.imageFrom = downLoadResult.isFromNetwork() ? ImageFrom.NETWORK : ImageFrom.DISK_CACHE;
            postRunLoad();
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    /**
     * 执行加载
     */
    private void executeLoad(){
        if (isCanceled()){
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "startLoad", " - ", name));
            }
            return;
        }
        //检查是否已经有了
        if (cacheInMemory){
            Drawable cachedDrawable = sketch.getConfiguration().getMemoryCache().get(memoryCacheId);
            if (cachedDrawable != null){
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface)cachedDrawable;
                if (!recycleDrawable.isRecyled()){
                    if (SketchPictures.isDebugMode()){
                        Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "from memory get drawable", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                    this.resultDrawable = cachedDrawable;
                    imageFrom = ImageFrom.MEMORY_CACHE;
                    setRequestStatus(RequestStatus.WAIT_DISPLAY);
                    recycleDrawable.setIsWaitDisplay("executeLoad:fromMemory",true);
                    sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
                    return;
                }else{
                    sketch.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if(SketchPictures.isDebugMode()){
                        Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                }
            }
        }
        setRequestStatus(RequestStatus.LOADING);
        //如果是本地apk文件就尝试得到其缓存文件
        if(isLocalApkFile()){
            File apkFile = getApkCacheIconFile();
            if (apkFile != null){
                this.cacheFile = apkFile;
            }
        }
        //解码
        Object decodeResult = sketch.getConfiguration().getImageDecoder().decode(this);
        if (decodeResult == null){
            toFailedStatus(FailCause.DECODE_FAIL);
            return;
        }
        if (decodeResult instanceof Bitmap){
            Bitmap bitmap = (Bitmap) decodeResult;
            if(!bitmap.isRecycled()){
                if(SketchPictures.isDebugMode()){
                    Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
            }else{
                if(SketchPictures.isDebugMode()){
                    Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "decode failed bitmap recycled", " - ", "decode after", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
            }
            if(isCanceled()) {
                if (SketchPictures.isDebugMode()) {
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "decode after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
                bitmap.recycle();
                return;
            }

            //处理
            if(!bitmap.isRecycled()){
                ImageProcessor imageProcessor = getImageProcessor();
                if(imageProcessor != null){
                    Bitmap newBitmap = imageProcessor.process(sketch, bitmap, reSize, forceUseResize, lowQualityImage);
                    if(newBitmap != null && newBitmap != bitmap && SketchPictures.isDebugMode()){
                        Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "process after", " - ", "newBitmap", " - ", RecycleBitmapDrawable.getInfo(newBitmap, mimeType), " - ", "recycled old bitmap", " - ", name));
                    }
                    if(newBitmap == null || newBitmap != bitmap){
                        bitmap.recycle();
                    }
                    bitmap = newBitmap;
                }
            }

            if(isCanceled()){
                if(SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "process after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
                if(bitmap != null){
                    bitmap.recycle();
                }
                return;
            }

            if(bitmap != null && !bitmap.isRecycled()){
                RecycleBitmapDrawable bitmapDrawable = new RecycleBitmapDrawable(bitmap);
                if(cacheInMemory && memoryCacheId != null){
                    sketch.getConfiguration().getMemoryCache().put(memoryCacheId, bitmapDrawable);
                }
                bitmapDrawable.setMimeType(mimeType);
                this.resultDrawable = bitmapDrawable;
                setRequestStatus(RequestStatus.WAIT_DISPLAY);
                bitmapDrawable.setIsWaitDisplay("executeLoad:new", true);
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }else{
                toFailedStatus(FailCause.DECODE_FAIL);
            }

        }else if(decodeResult instanceof RecycleGifDrawable){
            RecycleGifDrawable gifDrawable = (RecycleGifDrawable) decodeResult;
            gifDrawable.setMimeType(mimeType);

            if(SketchPictures.isDebugMode()){
                Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new gif drawable", " - ", gifDrawable.getInfo(), " - ", name));
            }

            if(!gifDrawable.isRecycled()){
                if(cacheInMemory && memoryCacheId != null){
                    sketch.getConfiguration().getMemoryCache().put(memoryCacheId, gifDrawable);
                }
                this.resultDrawable = gifDrawable;
                setRequestStatus(RequestStatus.WAIT_DISPLAY);
                gifDrawable.setIsWaitDisplay("executeLoad:new", true);
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }else{
                toFailedStatus(FailCause.DECODE_FAIL);
            }

        }else{
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }

    private File getApkCacheIconFile(){
        File apkIconCacheFile = sketch.getConfiguration().getDiskCache().getCacheFile(uri);
        if(apkIconCacheFile != null){
            return apkIconCacheFile;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(context, uri, lowQualityImage, NAME);
        if(iconBitmap != null && !iconBitmap.isRecycled()){
            apkIconCacheFile = sketch.getConfiguration().getDiskCache().saveBitmap(iconBitmap, uri);
            if(apkIconCacheFile != null){
                return apkIconCacheFile;
            }
        }

        return null;
    }

    private void handleCompletedOnMainThread() {
        if(isCanceled()){
            if(resultDrawable != null && resultDrawable instanceof RecycleDrawableInterface){
                ((RecycleDrawableInterface) resultDrawable).setIsWaitDisplay("completedCallback:cancel", false);
            }
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);

        // Set FixedSize
        Drawable finalDrawable;
        if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && resultDrawable instanceof RecycleBitmapDrawable && fixedSize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
            finalDrawable = new FixedRecycleBitmapDrawable((RecycleBitmapDrawable) resultDrawable, fixedSize);
        }else{
            finalDrawable = resultDrawable;
        }

        if(imageDisplayer == null){
            imageDisplayer = sketch.getConfiguration().getDefaultImageDisplayer();
        }
        imageDisplayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), finalDrawable);
        ((RecycleDrawableInterface) resultDrawable).setIsWaitDisplay("completedCallback", false);
        setRequestStatus(RequestStatus.COMPLETED);
        if(displayListener != null){
            displayListener.onCompleted(imageFrom, mimeType);
        }
    }

    private void handleFailedOnMainThread() {
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);
        if(imageDisplayer == null){
            imageDisplayer = sketch.getConfiguration().getDefaultImageDisplayer();
        }
        imageDisplayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), getFailureDrawable());
        setRequestStatus(RequestStatus.FAILED);
        if(displayListener != null){
            displayListener.onFailed(failCause);
        }
    }

    private void handleCanceledOnMainThread() {
        if(displayListener != null){
            displayListener.onCanceled(cancelCause);
        }
    }

    private void handlePauseDownloadOnMainThread() {
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "handlePauseDownloadOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        if(pauseDownloadImageHolder != null){
            setRequestStatus(RequestStatus.DISPLAYING);
            if(imageDisplayer == null){
                imageDisplayer = sketch.getConfiguration().getDefaultImageDisplayer();
            }
            imageDisplayer.display(sketchImageViewInterfaceHolder.getSketchImageViewInterface(), getPauseDownloadDrawable());
        }

        cancelCause = CancelCause.PAUSE_DOWNLOAD;
        setRequestStatus(RequestStatus.CANCELED);
        if(displayListener != null){
            displayListener.onCanceled(cancelCause);
        }
    }

    private void updateProgressOnMainThread(int totalLength, int completedLength) {
        if(isFinished()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "updateProgressOnMainThread", " - ", "finished", " - ", name));
            }
            return;
        }

        if(progressListener != null){
            progressListener.updateProgress(totalLength, completedLength);
        }
    }
}
