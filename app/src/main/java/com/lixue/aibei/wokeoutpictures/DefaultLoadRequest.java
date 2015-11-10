package com.lixue.aibei.wokeoutpictures;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;
import com.lixue.aibei.wokeoutpictures.request.LoadRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;

/**
 * 默认加载请求
 * Created by Administrator on 2015/11/10.
 */
public class DefaultLoadRequest implements LoadRequest,Runnable{
    private static final int WHAT_CALLBACK_COMPLETED = 202;
    private static final int WHAT_CALLBACK_FAILED = 203;
    private static final int WHAT_CALLBACK_CANCELED = 204;
    private static final int WHAT_CALLBACK_PROGRESS = 205;
    private static final String NAME = "DefaultLoadRequest";

    // Base fields
    private SketchPictures sketch;  // Sketch
    private String uri;// 图片地址
    private String name;// 名称，用于在输出LOG的时候区分不同的请求
    private UriSheme uriScheme;// Uri协议格式
    private RequestLevel requestLevel = RequestLevel.NET;  // 请求Level
    private RequestLevelFrom requestLevelFrom; // 请求Level的来源

    // Download fields
    private boolean cacheInDisk = true;// 是否开启磁盘缓存
    private ProgressListener progressListener;  // 下载进度监听器

    // Load fields
    private ReSize resize;// 裁剪尺寸，ImageProcessor会根据此尺寸来裁剪图片
    private boolean decodeGifImage = true;  // 是否解码GIF图片
    private boolean forceUseResize; // 是否强制使用resize
    private boolean lowQualityImage;   // 是否返回低质量的图片
    private MaxSize maxSize;// 最大尺寸，用于读取图片时计算inSampleSize
    private LoadListener loadListener;// 监听器
    private ImageProcessor imageProcessor;// 图片处理器

    // Runtime fields
    private File cacheFile;// 缓存文件
    private byte[] imageData;
    private Drawable resultBitmap;
    private String mimeType;
    private ImageFrom imageFrom;    // 图片来源
    private FailCause failCause;    // 失败原因
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态

    public DefaultLoadRequest(SketchPictures sketch, String uri, UriSheme uriScheme) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriScheme = uriScheme;
    }
    /****************************************** Base methods ******************************************/
    @Override
    public SketchPictures getSketch() {
        return sketch;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UriSheme getUriScheme() {
        return uriScheme;
    }

    @Override
    public void setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
    }

    @Override
    public void setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        this.requestLevelFrom = requestLevelFrom;
    }


    /****************************************** Download methods ******************************************/
    @Override
    public void setIsCacheInDisk(boolean isCacheInDisk) {
        this.cacheInDisk = cacheInDisk;
    }

    @Override
    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /****************************************** Load methods ******************************************/
    @Override
    public ReSize getResize() {
        return resize;
    }

    @Override
    public void setResize(ReSize resize) {
        this.resize = resize;
    }

    @Override
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    @Override
    public void setIsForceUseReseze(boolean isForceUseReseze) {
        this.forceUseResize = forceUseResize;
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
        this.lowQualityImage = lowQualityImage;
    }

    @Override
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    @Override
    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    @Override
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    @Override
    public void setIsDecodeGifImage(boolean isDecodeGifImage) {
        this.decodeGifImage = isDecodeGifImage;
    }

    /****************************************** Runtime methods ******************************************/
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

    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public void setDownloadListener(DownLoadListener downloadListener) {

    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    /****************************************** Other methods ******************************************/
    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean isCanceled() {
        return requestStatus == RequestStatus.CANCELED;
    }

    @Override
    public boolean cancel() {
        if(isFinished()){
            return false;
        }
        toCancleStatus(CancelCause.NORMAL);
        return true;
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
    public void updateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, this).sendToTarget();
        }
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCancleStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
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
            default:
                new IllegalArgumentException("unknown message what: "+msg.what).printStackTrace();
                break;
        }
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
    private void executeDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);
        if(uriScheme == UriSheme.HTTP || uriScheme == UriSheme.HTTPS){
            File diskCacheFile = cacheInDisk ? sketch.getConfiguration().getDiskCache().getCacheFile(uri):null;
            if(diskCacheFile != null && diskCacheFile.exists()){
                this.cacheFile = diskCacheFile;
                this.imageFrom = ImageFrom.DISK_CACHE;
                postRunLoad();
                if(SketchPictures.isDebugMode()){
                    Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "diskCache", " - ", name));
                }
            }else{
                if(requestLevel == RequestLevel.LOCAL){
                    if(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD){
                        toCancleStatus(CancelCause.PAUSE_DOWNLOAD);
                        if(SketchPictures.isDebugMode()){
                            Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "pause download", " - ", name));
                        }
                    }else{
                        toCancleStatus(CancelCause.LEVEL_IS_LOCAL);
                        if(SketchPictures.isDebugMode()){
                            Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "requestLevel is local", " - ", name));
                        }
                    }
                    return;
                }

                postRunDownload();
                if(SketchPictures.isDebugMode()){
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
    private void executeDownload() {
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "startDownload", " - ", name));
            }
            return;
        }

        DownLoadResult downloadResult = sketch.getConfiguration().getImageDownloader().download(this);

        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "downloadAfter", " - ", name));
            }
            return;
        }

        if(downloadResult != null  && downloadResult.getResult() != null){
            if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                this.cacheFile = (File) downloadResult.getResult();
            }else{
                this.imageData = (byte[]) downloadResult.getResult();
            }
            this.imageFrom = downloadResult.isFromNetwork()?ImageFrom.NETWORK:ImageFrom.DISK_CACHE;
            postRunLoad();
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    /**
     * 执行加载
     */
    private void executeLoad(){
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "startLoad", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.LOADING);

        // 如果是本地APK文件就尝试得到其缓存文件
        if(isLocalApkFile()){
            File apkIconCacheFile = getApkCacheIconFile();
            if(apkIconCacheFile != null){
                this.cacheFile = apkIconCacheFile;
            }
        }

        // 解码
        Object decodeResult = sketch.getConfiguration().getImageDecoder().decode(this);
        if(decodeResult == null){
            toFailedStatus(FailCause.DECODE_FAIL);
        }

        if(decodeResult instanceof Bitmap){
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

            if(isCanceled()){
                if(SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "canceled", " - ", "decode after", " - ", "recycle bitmap", " - ", RecycleBitmapDrawable.getInfo(bitmap, mimeType), " - ", name));
                }
                bitmap.recycle();
                return;
            }

            //处理
            if(!bitmap.isRecycled()){
                ImageProcessor imageProcessor = getImageProcessor();
                if(imageProcessor != null){
                    Bitmap newBitmap = imageProcessor.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);
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
                RecycleBitmapDrawable recycleBitmapDrawable = new RecycleBitmapDrawable(bitmap);
                recycleBitmapDrawable.setMimeType(mimeType);
                this.resultBitmap = recycleBitmapDrawable;
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }else{
                toFailedStatus(FailCause.DECODE_FAIL);
            }
        }else if(decodeResult instanceof RecycleGifDrawable){
            RecycleGifDrawable recycleGifDrawable = (RecycleGifDrawable) decodeResult;
            recycleGifDrawable.setMimeType(mimeType);

            if(SketchPictures.isDebugMode()){
                Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeLoad", " - ", "new gif drawable", " - ", recycleGifDrawable.getInfo(), " - ", name));
            }

            this.resultBitmap = recycleGifDrawable;
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
        }else{
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }

    @Override
    public boolean isLocalApkFile(){
        return uriScheme == UriSheme.FILE && SketchUtils.checkSuffix(uri, ".apk");
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 获取APK图片的缓存文件
     * @return APK图片的缓存文件
     */
    private File getApkCacheIconFile(){
        File apkIconCacheFile = sketch.getConfiguration().getDiskCache().getCacheFile(uri);
        if(apkIconCacheFile != null){
            return apkIconCacheFile;
        }

        Bitmap iconBitmap = SketchUtils.decodeIconFromApk(sketch.getConfiguration().getContext(), uri, lowQualityImage, NAME);
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
            if(resultBitmap != null){
                ((RecycleDrawableInterface)resultBitmap).recyle();
            }
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.COMPLETED);
        if(loadListener != null){
            loadListener.onCompleted(resultBitmap, imageFrom, mimeType);
        }
    }

    private void handleFailedOnMainThread() {
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.FAILED);
        if(loadListener != null){
            loadListener.onFailed(failCause);
        }
    }

    private void handleCanceledOnMainThread() {
        if(loadListener != null){
            loadListener.onCancled(cancelCause);
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

