package com.lixue.aibei.wokeoutpictures;

import android.os.Message;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;

/**
 * 默认下载请求
 * Created by Administrator on 2015/11/10.
 */
public class DefaultDownloadRequest implements DownloadRequest,Runnable {
    private static final int WHAT_CALLBACK_COMPLETED = 302;
    private static final int WHAT_CALLBACK_FAILED = 303;
    private static final int WHAT_CALLBACK_CANCELED = 304;
    private static final int WHAT_CALLBACK_PROGRESS = 305;
    private static final String NAME = "DefaultDownloadRequest";

    //basic fields
    private SketchPictures sketch;//sketch
    private String uri;//请求uri
    private UriSheme uriSheme;//uri协议格式
    private String name;
    private RequestLevel requestLevel;
    private RequestLevelFrom requestLevelFrom;

    //download fields
    private DownLoadListener downLoadListener;
    private ProgressListener progressListener;
    private boolean cacheInDisk = true;

    //Runtime fields
    private File resultFile;
    private RunStatus runStatus = RunStatus.DISPATCH;
    private byte[] resultBytes;
    private FailCause failCause;
    private ImageFrom imageFrom;
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;


    public DefaultDownloadRequest(SketchPictures sketch, String uri, UriSheme uriScheme){
        this.sketch = sketch;
        this.uri = uri;
        this.uriSheme = uriScheme;
    }

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
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    @Override
    public void setIsCacheInDisk(boolean isCacheInDisk) {
        this.cacheInDisk = isCacheInDisk;
    }

    @Override
    public void setDownloadListener(DownLoadListener downloadListener) {
        this.downLoadListener = downloadListener;
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
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public void updateProgress(int totalLength, int completeLength) {
        if(progressListener != null){
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completeLength, this).sendToTarget();
        }
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
    }

    @Override
    public void toCancleStatus(CancelCause cancelCause) {
        this.requestStatus = RequestStatus.CANCELED;
        setRequestStatus(RequestStatus.CANCELED);
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED,this).sendToTarget();
    }

    @Override
    public void invokeInMainThread(Message message) {
        switch (message.what){
            case WHAT_CALLBACK_CANCELED:
               setRequestStatus(RequestStatus.CANCELED);
                if (downLoadListener != null){
                    downLoadListener.onCancle();
                }
                break;
            case WHAT_CALLBACK_COMPLETED:
                if (isCanceled()){
                    if (SketchPictures.isDebugMode()){
                        Log.w(SketchPictures.TAG, SketchUtils.concat(NAME,"-","handleCompletedOnMainThread","cancled","-",name));
                    }
                    return;
                }
                setRequestStatus(RequestStatus.COMPLETED);
                if (downLoadListener != null){
                    if (resultFile != null){
                        downLoadListener.onCompaleted(resultFile,imageFrom==ImageFrom.NETWORK);
                    }else if(resultBytes != null){
                        downLoadListener.onCompaleted(resultBytes);
                    }
                }
                break;
            case WHAT_CALLBACK_FAILED:
                if(isCanceled()){
                    if(SketchPictures.isDebugMode()){
                        Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", name));
                    }
                    return;
                }

                setRequestStatus(RequestStatus.FAILED);
                if(downLoadListener != null){
                    downLoadListener.onFailed(failCause);
                }
                break;
            case WHAT_CALLBACK_PROGRESS:
                if (isFinished()){
                    if (SketchPictures.isDebugMode()){
                        Log.w(SketchPictures.TAG,SketchUtils.concat(NAME,"-","handleProgressOnMainThread","finished","-",name));
                    }
                    return;
                }
                if (progressListener != null){
                    progressListener.updateProgress(message.arg1,message.arg2);
                }
                break;
            default:
                new IllegalArgumentException("unknown message what: "+message.what).printStackTrace();
                break;
        }
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
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return null;
    }

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
        if (isFinished()){
            return true;
        }else{
          return false;
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
    public void run() {
        switch (runStatus){
            case DISPATCH:
                executeDispatch();
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
     * 执行分发请求
     */
    private void executeDispatch(){
        setRequestStatus(RequestStatus.DISPATCHING);
        if (uriSheme == UriSheme.HTTPS || uriSheme == UriSheme.HTTP){
            if (isCanceled()){
                if (SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG,SketchUtils.concat(NAME,"-"));
                }
                return;
            }
            if (cacheInDisk){
                File cacheFile = sketch.getConfiguration().getDiskCache().getCacheFile(uri);
                if (cacheFile != null && cacheFile.exists()){
                    if(SketchPictures.isDebugMode()){
                        Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "diskCache", " - ", name));
                    }
                    resultFile = cacheFile;
                    imageFrom = ImageFrom.DISK_CACHE;
                }
                return;
            }else{
                if (requestLevel == RequestLevel.LOCAL){
                    if (requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD){
                        toCancleStatus(CancelCause.PAUSE_DOWNLOAD);
                        if (SketchPictures.isDebugMode()){
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
            }
            postRunDownload();
            if(SketchPictures.isDebugMode()){
                Log.d(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "download", " - ", name));
            }
        }else{
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "not support uri:", uri, " - ", name));
            }
            toFailedStatus(FailCause.URI_NO_SUPPORT);
        }
    }

    /**
     * 执行下载请求
     */
    private void executeDownload(){
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "startDownload", " - ", name));
            }
            return;
        }
        DownLoadResult downLoadResult = sketch.getConfiguration().getImageDownloader().download(this);
        if(isCanceled()){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "downloadAfter", " - ", name));
            }
            return;
        }
        if (downLoadResult != null && downLoadResult.getResult() != null){
            imageFrom = downLoadResult.isFromNetwork() ? ImageFrom.NETWORK : ImageFrom.DISK_CACHE;
            this.requestStatus = RequestStatus.COMPLETED;
            if (downLoadListener != null){
                if(downLoadResult.getResult().getClass().isAssignableFrom(File.class)){
                    this.resultFile = (File) downLoadResult.getResult();
                }else{
                    this.resultBytes = (byte[]) downLoadResult.getResult();
                }
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            }
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }
}
