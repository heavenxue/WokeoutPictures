package com.lixue.aibei.wokeoutpictures;

import android.os.Message;

import com.lixue.aibei.wokeoutpictures.enums.CancelCause;
import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.ImageFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;

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


    private SketchPictures sketch;//sketch
    private String uri;//请求uri
    private UriSheme uriSheme;//uri协议格式

    public DefaultDownloadRequest(SketchPictures sketch, String uri, UriSheme uriScheme){

    }


    @Override
    public SketchPictures getSketch() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public UriSheme getUriScheme() {
        return null;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {

    }

    @Override
    public boolean isCacheInDisk() {
        return false;
    }

    @Override
    public void setIsCacheInDisk(boolean isCacheInDisk) {

    }

    @Override
    public void setDownloadListener(DownLoadListener downloadListener) {

    }

    @Override
    public void setRequestLevel(RequestLevel requestLevel) {

    }

    @Override
    public void setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {

    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {

    }

    @Override
    public void updateProgress(int totalLength, int completeLength) {

    }

    @Override
    public void toFailedStatus(FailCause failCause) {

    }

    @Override
    public void toCancleStatus(CancelCause cancelCause) {

    }

    @Override
    public void invokeInMainThread(Message message) {

    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public RequestStatus getRequestStatus() {
        return null;
    }

    @Override
    public ImageFrom getImageFrom() {
        return null;
    }

    @Override
    public FailCause getFailCause() {
        return null;
    }

    @Override
    public CancelCause getCancelCause() {
        return null;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void postRunDispatch() {

    }

    @Override
    public void postRunDownload() {

    }

    @Override
    public void postRunLoad() {

    }

    @Override
    public void run() {

    }
}
