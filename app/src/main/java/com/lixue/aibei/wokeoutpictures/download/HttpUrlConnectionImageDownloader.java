package com.lixue.aibei.wokeoutpictures.download;

import android.os.Build;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.DownLoadResult;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.enums.RequestStatus;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用HttpURLConnection来访问网络的下载器
 * Created by Administrator on 2015/11/9.
 */
public class HttpUrlConnectionImageDownloader implements ImageDownloader {
    private static final String NAME = "HttpUrlConnectionImageDownloader";
    // ReentrantLock 类实现了 Lock ，它拥有与 synchronized 相同的并发性和内存语义，
    // 但是添加了类似锁投票、定时锁等候和可中断锁等候的一些特性
    private Map<String,ReentrantLock> urlLocks;//一个url对应一把锁
    private int maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int progressCallbackNumber = DEFAULT_PROGRESS_CALLBACK_NUMBER;

    public HttpUrlConnectionImageDownloader(){
        this.urlLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
    }

    @Override
    public DownLoadResult download(DownloadRequest downloadRequest) {
        // 根据下载地址加锁，防止重复下载
        downloadRequest.setRequestStatus(RequestStatus.GET_DOWNLOAD_LOCK);
        ReentrantLock urlLock = getUrlLock(downloadRequest.getUri());
        urlLock.lock();

        downloadRequest.setRequestStatus(RequestStatus.LOADING);
        DownLoadResult result = null;
        int number = 0;
        while (true){
            // 如果已经取消了就直接结束
            if (downloadRequest.isCanceled()){
                if (SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "get lock after", " - ", downloadRequest.getName()));
                }
                break;
            }
            // 如果缓存文件已经存在了就直接返回缓存文件
            if (downloadRequest.isCacheInDisk()){
                File cacheFile = downloadRequest.getSketch().getConfiguration().getDiskCache().getCacheFile(downloadRequest.getUri());
                if (cacheFile != null && cacheFile.exists()) {
                    result = DownLoadResult.createByFile(cacheFile,false);
                    break;
                }
            }
            try {
                result = realDownload(downloadRequest);
            }catch (Throwable e){
                boolean retry = (e instanceof SocketTimeoutException || e instanceof InterruptedIOException) && number < maxRetryCount;
                if(retry){
                    number++;
                    if (SketchPictures.isDebugMode()) {
                        Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "download failed", " - ", "retry", " - ", downloadRequest.getName()));
                    }
                }else{
                    if (SketchPictures.isDebugMode()) {
                        Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "download failed", " - ", "end", " - ", downloadRequest.getName()));
                    }
                }
                e.printStackTrace();
                if(!retry){
                    break;
                }
            }
        }
        urlLock.unlock();
        return result;
    }

    /**
     * 真正的下载（验证各种条件）
     * @param request
     * @return
     */
    private DownLoadResult realDownload(DownloadRequest request){
        //打开连接
        HttpURLConnection httpURLConnection = null;
        String url = request.getUri();
        try {
            httpURLConnection = openUrlConnection(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (request.isCanceled()){
            releaseConnection(httpURLConnection,request);
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "connect after", " - ", request.getName()));
            }
            return null;
        }
        //检查状态码
        int responseCode;
        try {
            responseCode = httpURLConnection.getResponseCode();
        }catch (IOException e){
            e.printStackTrace();
            releaseConnection(httpURLConnection, request);
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "get response code failed", " - ", request.getName(), " - ", "HttpResponseHeader:", getResponseHeadersString(httpURLConnection)));
            }
            return null;
        }
        //检查响应消息
        String responseMsg;
        try {
            responseMsg = httpURLConnection.getResponseMessage();
        } catch (IOException e) {
            releaseConnection(httpURLConnection, request);
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "get response message failed", " - ", request.getName(), " - ", "HttpResponseHeader:", getResponseHeadersString(httpURLConnection)));
            }
            return null;
        }
        if (responseCode != 200){
            releaseConnection(httpURLConnection, request);
            if (SketchPictures.isDebugMode()) {
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "response code exception", " - ", "responseCode:", String.valueOf(responseCode), "; responseMessage:", responseMsg, " - ", request.getName() + " - ", "HttpResponseHeader:", getResponseHeadersString(httpURLConnection)));
            }
            return null;
        }
        //检查内容长度
        int contentLength = httpURLConnection.getHeaderFieldInt("Content-Length",-1);
        if (contentLength <= 0){
            releaseConnection(httpURLConnection, request);
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "content length exception", " - ", "contentLength:" + contentLength, " - ", request.getName(), " - ", "HttpResponseHeader:", getResponseHeadersString(httpURLConnection)));
            }
            return null;
        }
        return readData(request,httpURLConnection,contentLength);
    }

    /**
     * 读取response中的数据
     * @param request
     * @param connection
     * @param length
     * @return
     */
    private DownLoadResult readData(DownloadRequest request,HttpURLConnection connection,int length){
        //生成缓存文件和临时缓存文件
        File tmpFile = null;
        File cacheFile = null;
        if (request.isCacheInDisk()){
            cacheFile = request.getSketch().getConfiguration().getDiskCache().generateCacheFile(request.getUri());
            if(cacheFile != null && request.getSketch().getConfiguration().getDiskCache().applyForSpace(length)){
                tmpFile = new File(cacheFile.getPath()+".temp");
                if(!SketchUtils.CreateFile(tmpFile)){
                    tmpFile = null;
                    cacheFile = null;
                }
            }
        }
        //获取输入流
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
        }
        //是否取消
        if(request.isCanceled()){
            close(inputStream);
            if (SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "get input stream after", " - ", request.getName()));
            }
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
            return null;
        }
        //当不需要将数据缓存到本地的时候就使用ByteArrayOutputStream来存储数据
        OutputStream outputStream = null;
        if (tmpFile != null){
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile,false),BUFFER_SIZE);
            } catch (FileNotFoundException e) {
                close(inputStream);
                e.printStackTrace();
            }
        }else{
            outputStream = new ByteArrayOutputStream();
        }
        //读取数据
        int completedLength = 0;
        boolean exception = false;
        try {
            completedLength = readData(inputStream, outputStream, request, length, progressCallbackNumber);
        } catch (IOException e) {
            exception = true;
            e.printStackTrace();
        }finally {
            close(outputStream);
            close(inputStream);
            if (exception && tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
        }
        if (request.isCanceled()) {
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "read data after", " - ", request.getName()));
            }
            if (tmpFile != null && tmpFile.exists() && !tmpFile.delete() && SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp download file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", request.getName()));
            }
            return null;
        }

        if (SketchPictures.isDebugMode()) {
            Log.i(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "download success", " - ", "fileLength:", String.valueOf(completedLength), "/", String.valueOf(length), " - ", request.getName()));
        }

        // 转换结果
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

    /**
     * 读取数据
     * @param inputStream 输入流
     * @param outputStream 输出流
     * @param downloadRequest 下载请求
     * @param contentLength 内容长度
     * @param progressCallbackAccuracy 进度回调
     * @return
     */
    public static int readData(InputStream inputStream, OutputStream outputStream, DownloadRequest downloadRequest, int contentLength, int progressCallbackAccuracy) throws IOException {
        int readNumber;
        int completedLength = 0;
        int averageLength = completedLength / progressCallbackAccuracy;//平均长度
        int callbackNumber = 0;
        byte[] cacheBytes = new byte[4*1024];
        while (!downloadRequest.isCanceled() && (readNumber = inputStream.read(cacheBytes)) != -1){
            outputStream.write(cacheBytes,0,readNumber);
            completedLength += readNumber;
            if (completedLength >= (callbackNumber +1) * averageLength || completedLength == contentLength){
                callbackNumber++;
                downloadRequest.updateProgress(contentLength,completedLength);
            }
        }
        outputStream.flush();
        return completedLength;
    }
    /**
     * 得到连接头消息
     * @param connection
     * @return
     */
    public static String getResponseHeadersString(HttpURLConnection connection){
        Map<String, List<String>> headers = connection.getHeaderFields();
        if(headers == null){
            return null;
        }StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for(Map.Entry<String, List<String>> entry : headers.entrySet()){
            if(stringBuilder.length() != 1){
                stringBuilder.append(", ");
            }

            stringBuilder.append("{");

            stringBuilder.append(entry.getKey());

            stringBuilder.append(":");

            List<String> values = entry.getValue();
            if(values.size() == 0){
                stringBuilder.append("");
            }else if(values.size() == 1){
                stringBuilder.append(values.get(0));
            }else{
                stringBuilder.append(values.toString());
            }

            stringBuilder.append("}");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * 释放连接
     * @param connection
     * @param request
     */
    public static void releaseConnection(HttpURLConnection connection,DownloadRequest request){
        if (connection == null){
            return;
        }
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            if (SketchPictures.isDebugMode()) {
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", e.getClass().getName(), " - ", "get input stream failed on release connection", " - ", e.getMessage(), " - ", request.getName()));
            }
            return;
        }
        close(inputStream);
    }

    /**
     * 关闭连接
     * @param closeable
     */
    public static void close(Closeable closeable){
        if (closeable == null){
            return;
        }
        if (closeable instanceof OutputStream){
            try {
                ((OutputStream)closeable).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public void setProgressCallbackNumber(int progressCallbackNumber) {
        this.progressCallbackNumber = progressCallbackNumber;
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME)
                .append(" - ")
                .append("maxRetryCount").append("=").append(maxRetryCount)
                .append(", ")
                .append("progressCallbackNumber").append("=").append(progressCallbackNumber)
                .append(", ")
                .append("connectTimeout").append("=").append(connectTimeout)
                .append(", ")
                .append("readTimeout").append("=").append(readTimeout);
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

    /**
     * 打开一个url连接
     * @param url
     * @return
     * @throws IOException
     */
    private HttpURLConnection openUrlConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        // HTTP connection reuse which was buggy pre-froyo <android2.2
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            connection.setRequestProperty("http.keepAlive", "false");
        }
        return connection;
    }
}
