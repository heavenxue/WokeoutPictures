package com.lixue.aibei.wokeoutpictures.download;

import android.util.Log;

import com.lixue.aibei.wokeoutpictures.DownLoadResult;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;


/**
 * 使用HttpClient来访问网络的下载器
 * Created by Administrator on 2015/11/9.
 */
public class HttpClientImageDownloader implements ImageDownloader {
    private static final String NAME = "HttpClientImageDownloader";
    private static final int DEFAULT_WAIT_TIMEOUT = 60*1000;   // 默认从连接池中获取连接的最大等待时间
    private static final int DEFAULT_MAX_ROUTE_CONNECTIONS = 400;    // 默认每个路由的最大连接数
    private static final int DEFAULT_MAX_CONNECTIONS = 800;  // 默认最大连接数
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;  // 默认Socket缓存大小
    //用于 HTTP 请求的用户代理头的值,也就是表明是什么浏览器(chrome浏览器)
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.16 Safari/534.24";

    private DefaultHttpClient httpClient;
    private Map<String,ReentrantLock> urlLocks;
    private int maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    private int processCallbackNumber = DEFAULT_PROGRESS_CALLBACK_NUMBER;

    public HttpClientImageDownloader(){
        this.urlLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
        BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, DEFAULT_WAIT_TIMEOUT);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_ROUTE_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_CONNECT_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, DEFAULT_USER_AGENT);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),80));
        schemeRegistry.register(new Scheme("https",PlainSocketFactory.getSocketFactory(),443));
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams,schemeRegistry),httpParams);
        httpClient.addRequestInterceptor(new GzipProcessRequestInterceptor());
        httpClient.addResponseInterceptor(new GzipProcessResponseInterceptor());
    }
    /**
     * 获取一个URL锁，通过此锁可以防止重复下载
     * @param url 下载地址
     * @return URL锁
     */
    public synchronized ReentrantLock getUrlLock(String url){
        ReentrantLock urlLock = urlLocks.get(url);
        if(urlLock == null){
            urlLock = new ReentrantLock();
            urlLocks.put(url, urlLock);
        }
        return urlLock;
    }

    @Override
    public DownLoadResult download(DownloadRequest downloadRequest) {
        //根据下载地址枷锁，防止重复下载
        downloadRequest.setRequestStatus(RequestStatus.GET_DOWNLOAD_LOCK);
        ReentrantLock reentrantLock = urlLocks.get(downloadRequest.getUri());
        reentrantLock.lock();
        downloadRequest.setRequestStatus(RequestStatus.LOADING);
        DownLoadResult result = null;
        int number = 0;
        while (true){
            if (downloadRequest.isCanceled()){
                if (SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "get lock after", " - ", downloadRequest.getName()));
                }
                break;
            }
            //如果缓存文件已经存在，就直接返回缓存文件
            if (downloadRequest.isCacheInDisk()){
                File cacheFile = downloadRequest.getSketch().getConfiguration().getDiskCache().getCacheFile(downloadRequest.getUri());
                if (cacheFile != null && cacheFile.exists()){
                    result = DownLoadResult.createByFile(cacheFile,false);
                    break;
                }
            }
            try {
                result = realDownload(downloadRequest);
                break;
            }catch (Throwable e){
                boolean retry = (e instanceof SocketTimeoutException || e instanceof InterruptedException) && number <maxRetryCount;
                if (retry){
                    number ++;
                    if (SketchPictures.isDebugMode()) {
                        Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "download failed", " - ", "retry", " - ", downloadRequest.getName()));
                    }
                }else{
                    if (SketchPictures.isDebugMode()) {
                        Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "download failed", " - ", "end", " - ", downloadRequest.getName()));
                    }
                }
                e.printStackTrace();
                if (!retry){
                    break;
                }
            }
        }
        //释放锁
        reentrantLock.unlock();
        return result;
    }

    private DownLoadResult realDownload(DownloadRequest request) throws IOException {
        //请求是否取消
        if (request.isCanceled()){
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "get response after", " - ", request.getName()));
            }
            return null;
        }
        HttpResponse response = null;
        try {
            response = httpClient.execute(new HttpGet(request.getUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //检查状态码
        StatusLine statusLine = response.getStatusLine();
        if (statusLine == null){
            try {
                releaseConnectiion(response);
                if (SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "get status line failed", " - ", request.getName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        int responseCode = statusLine.getStatusCode();
        if (responseCode != 200){
            releaseConnectiion(response);
            if (SketchPictures.isDebugMode()) {
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "response code exception", " - ", "responseCode:", String.valueOf(responseCode), "; responseMessage:", response.getStatusLine().getReasonPhrase(), " - ", request.getName()));
            }
            return null;
        }
        // 检查内容长度
        int contentLength = 0;
        Header[] headers = response.getHeaders("Content-Length");
        if(headers != null && headers.length > 0){
            contentLength = Integer.valueOf(headers[0].getValue());
        }
        if (contentLength <= 0) {
            releaseConnectiion(response);
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "content length exception", " - ", "contentLength:" + contentLength, " - ", request.getName()));
            }
            return null;
        }

        return readData(request, response,contentLength);
    }
    private DownLoadResult readData(DownloadRequest request, HttpResponse httpResponse, int contentLength){
        // 生成缓存文件和临时缓存文件
        File tmpFile = null;//临时文件
        File cacheFile = null;//缓存文件
        if (request.isCacheInDisk()){
            cacheFile = request.getSketch().getConfiguration().getDiskCache().generateCacheFile(request.getUri());
            if (cacheFile != null && request.getSketch().getConfiguration().getDiskCache().applyForSpace(cacheFile.length())){
                tmpFile = new File(cacheFile.getPath() + ".temp");
                if (!SketchUtils.CreateFile(tmpFile)){
                    tmpFile = null;
                    cacheFile = null;
                }
            }
        }
        //获取输入流后判断是否取消
        InputStream inputStream = null;
        if (httpResponse != null){
            try {
                inputStream = httpResponse.getEntity().getContent();
            } catch (IOException e) {
                if (tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
                }
                e.printStackTrace();
            }
        }
        if (request.isCanceled()){
            HttpUrlConnectionImageDownloader.close(inputStream);
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "get input stream after", " - ", request.getName()));
            }
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
            return null;
        }

        // 当不需要将数据缓存到本地的时候就使用ByteArrayOutputStream来存储数据
        OutputStream outputStream = null;
        if (!request.isCacheInDisk()){
            if (tmpFile != null){
                try {
                    outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile,false),BUFFER_SIZE);
                } catch (FileNotFoundException e) {
                    HttpUrlConnectionImageDownloader.close(outputStream);
                    e.printStackTrace();
                }
            }else{
                outputStream = new ByteArrayOutputStream(contentLength);
            }
        }
        // 读取数据
        int completedLength = 0;
        boolean exception =false;
        try {
            completedLength = HttpUrlConnectionImageDownloader.readData(inputStream,outputStream,request,contentLength,processCallbackNumber);
        } catch (IOException e) {
            exception = true;
            e.printStackTrace();
        }finally {
            HttpUrlConnectionImageDownloader.close(inputStream);
            HttpUrlConnectionImageDownloader.close(outputStream);
            if (exception && tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
        }
        if (request.isCanceled()) {
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "read data after", " - ", request.getName()));
            }
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
            return null;
        }

        if (SketchPictures.isDebugMode()){
            Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "download success", " - ", "fileLength:", String.valueOf(completedLength), "/", String.valueOf(contentLength), " - ", request.getName()));
        }
        //转换结果
        if(tmpFile != null && tmpFile.exists()){
            if(!tmpFile.renameTo(cacheFile)){
                if(SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "rename failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
                }
                if (!tmpFile.delete() && SketchPictures.isDebugMode()) {
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
                }
                return null;
            }

            return DownLoadResult.createByFile(cacheFile, true);
        }else if(outputStream instanceof ByteArrayOutputStream){
            return DownLoadResult.createByArray(((ByteArrayOutputStream) outputStream).toByteArray(), true);
        }else{
            return null;
        }
    }

    @Override
    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        HttpParams httpParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,connectTimeout);
    }

    @Override
    public void setProgressCallbackNumber(int progressCallbackNumber) {
        this.processCallbackNumber = progressCallbackNumber;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME)
                .append(" - ")
                .append("maxRetryCount").append("=").append(maxRetryCount);
    }

    private static class GzipProcessRequestInterceptor implements HttpRequestInterceptor{
        /**
         * 头字段 - 接受的编码
         */
        public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

        /**
         * 编码 - gzip
         */
        public static final String ENCODING_GZIP = "gzip";
        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
            //如果请求头中没有HEADER_ACCEPT_ENCODING属性就添加进去
            if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
            }
        }
    }

    private static class GzipProcessResponseInterceptor implements HttpResponseInterceptor{

        @Override
        public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
            final HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity!=null){
                final Header encoding = httpEntity.getContentEncoding();
                if (encoding != null){
                    for (HeaderElement element : encoding.getElements()){
                        if (element.getName().equalsIgnoreCase(GzipProcessRequestInterceptor.ENCODING_GZIP)){
                            httpResponse.setEntity(new InflatingEntity(httpEntity));
                            break;
                        }
                    }
                }
            }
        }
    }
    private static class InflatingEntity extends HttpEntityWrapper{

        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
    public static void releaseConnectiion(HttpResponse response) throws IOException {
        if (response != null){
            HttpEntity entity = response.getEntity();
            if (entity == null){
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream == null){
                return;
            }
            inputStream.close();
        }
    }
}
