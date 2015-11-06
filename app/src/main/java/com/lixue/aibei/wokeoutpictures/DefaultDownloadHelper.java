package com.lixue.aibei.wokeoutpictures;



import android.util.Log;

import com.lixue.aibei.wokeoutpictures.enums.FailCause;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.enums.RequestLevelFrom;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.request.Request;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;


/**
 * 默认下载协助器
 * Created by Administrator on 2015/11/6.
 */
public class DefaultDownloadHelper implements DownloadHelper {
    private static final String NAME = "DefaultDownloadHelper";
    //基本属性
    protected SketchPictures sketchPictures;//图像类
    protected String uri;//文件uri
    protected String name;//名称
    //请求级别，默认为网络（先从内存请求，无再SD卡请求，无再网络请求）
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom; //请求来源
    //下载属性
    protected boolean cacheInDisk = true;//是否缓存到sd卡
    protected ProgressListener progressListener;//进度监听器
    protected DownLoadListener downLoadListener;//下载监听器

    //创建下载请求生成器
    public DefaultDownloadHelper(SketchPictures sketchPictures ,String uri){
        this.sketchPictures = sketchPictures;
        this.uri = uri;
        if (sketchPictures.getConfiguration().isPauseDownload()){
            requestLevel = RequestLevel.LOCAL;
            requestLevelFrom = null;
        }
    }

    @Override
    public DownloadHelper name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public DownloadHelper listener(DownLoadListener downloadListener) {
        this.downLoadListener = downloadListener;
        return this;
    }

    @Override
    public DownloadHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    @Override
    public DownloadHelper progressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public DownloadHelper options(DownloadOptions options) {
        if (options == null){
            return this;
        }
        this.cacheInDisk = options.isCacheInDisk();
        RequestLevel optionsRequestLevel = options.getRequestLevel();
        if (requestLevel != null && optionsRequestLevel != null){
            if (requestLevel.getLevel() > optionsRequestLevel.getLevel()){
                this.requestLevel = optionsRequestLevel;
                this.requestLevelFrom = null;
            }
        }else if (optionsRequestLevel != null){
            this.requestLevel = optionsRequestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    @Override
    public DownloadHelper options(Enum<?> optionsName) {
        return options((DownloadOptions)sketchPictures.getOptions(optionsName));
    }

    @Override
    public DownloadHelper requestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    @Override
    public Request commit() {
        //检测是否向SD卡进行缓存
        if (!sketchPictures.getConfiguration().isCacheInDisk()){
            cacheInDisk = false;
        }
        //检测下载监听器是否存在
        if (downLoadListener != null){
            downLoadListener.onStart();
        }
        //检测Uri是否符合
        if (uri == null && "".equals(uri)){
            if (SketchPictures.isDebugMode()){
                Log.e(NAME, SketchUtils.concat(NAME,"-","uri is null or empty"));
            }
            if (downLoadListener != null){
                downLoadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }
        //过滤掉不支持的Uri类型
        UriSheme uriScheme = UriSheme.valueOf(uri);
        if (uriScheme == null){
            if (SketchPictures.isDebugMode()){
                Log.e(NAME,SketchUtils.concat(NAME,"-","don't support the uri"));
            }
            if (downLoadListener != null){
                downLoadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        if (!(uriScheme == UriSheme.HTTPS || uriScheme == UriSheme.HTTP)){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "only support http or https", " - ", name));
            }
            if(downLoadListener != null){
                downLoadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }
        //检测名称是否正确
        if(name == null){
            name = uri;
        }
        //创建请求
        DownloadRequest downloadRequest = sketchPictures.getConfiguration().getRequestFactory().newDownloadRequest(sketchPictures,uri,UriSheme.valueOfUri(uri));
        downloadRequest.setDownloadListener(downLoadListener);
        downloadRequest.setName(name);
        downloadRequest.setIsCacheInDisk(cacheInDisk);
        downloadRequest.setProgressListener(progressListener);
        downloadRequest.setRequestLevel(requestLevel);
        downloadRequest.setRequestLevelFrom(requestLevelFrom);
        downloadRequest.postRunDispatch();

        return downloadRequest;
    }
}
