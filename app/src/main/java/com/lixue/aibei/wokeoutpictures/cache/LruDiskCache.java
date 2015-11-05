package com.lixue.aibei.wokeoutpictures.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;

/** 默认实现的磁盘缓存器
 * Created by Administrator on 2015/11/3.
 */
public class LruDiskCache implements DiskCache {
    private static final String NAME = "LruDiskCache";
    private static final String DEFAULT_DIRECTORY_NAME = "SketchPictures";
    private static final int DEFAULT_RESERVE_SIZE = 100 * 1024 * 1024 ;
    private static final int DEFAULT_MAX_SIZE = 100 * 1024 * 1024 ;

    private File cacheDir;//缓存目录
    private Context context;//上下文
    private FileLastModifiedComparator fileLastModifiedComparator;
    private int reserveSize = DEFAULT_RESERVE_SIZE;//保留空间
    private int maxSize = DEFAULT_MAX_SIZE;//最大空间

    public LruDiskCache(Context context,File cacheDir){
        this.context = context;
        this.cacheDir = cacheDir;
        this.fileLastModifiedComparator = new FileLastModifiedComparator();
    }

    public LruDiskCache(Context context){
        this.context = context;
    }


    @Override
    public File getCacheFile(String uri) {
        return null;
    }

    @Override
    public File generateCacheFile(String uri) {
        String fileName = uriToFileName(uri);
        if(fileName == null){
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, "encode uri failed", " - ", uri));
            }
            return null;
        }
        File finalCacheDir = getCacheDir();
        if(finalCacheDir == null){
            return null;
        }
        return new File(finalCacheDir, fileName);
    }

    @Override
    public boolean applyForSpace(long length) {
        File finalCacheDir = getCacheDir();
        if (finalCacheDir == null){
            return false;
        }
        //获取SD卡磁盘整个空间大小
        long totalAvailableSize = Math.abs(getAvailableSize(finalCacheDir.getPath()));
        long usedSize = 0;
        //如果剩余空间够用
        if(totalAvailableSize - reserveSize > length){
            if (maxSize > 0 ){
                usedSize = Math.abs(SketchUtils.countFileLength(finalCacheDir));
                if (length + usedSize < maxSize) return true;
            }else return true;
        }
        //获取所有缓存文件
        File[] files = null;
        if (finalCacheDir.exists()){
            files = finalCacheDir.listFiles();
        }
        if (files != null && files.length > 0){
            //把所有文件按照最后修改日期排序
            Arrays.sort(files,fileLastModifiedComparator);
            //然后按照顺序来删除文件直到腾出足够的空间或文件删完为止
            for (File fl : files){
                if(SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "deleted cache file", " - ", fl.getPath()));
                }
                long currentFileLength = fl.length();
                if (fl.delete()){
                    totalAvailableSize += currentFileLength;
                    if (totalAvailableSize - reserveSize > length){
                        if (maxSize > 0){
                            usedSize -= currentFileLength;
                            if (usedSize + length < maxSize){
                                return  true;
                            }
                        }else return true;
                    }
                }
            }
        }

        // 返回申请空间失败
        if(SketchPictures.isDebugMode()){
            Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "apply for space failed", " - ", "remaining space��", Formatter.formatFileSize(context, totalAvailableSize), "; reserve size��", Formatter.formatFileSize(context, reserveSize), " - ", finalCacheDir.getPath()));
        }
        return false;
    }

    @Override
    public void setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    @Override
    public synchronized File getCacheDir() {
        // 首先尝试使用cacheDir参数指定的位置
        if (cacheDir != null){
            if (cacheDir.exists() || cacheDir.mkdirs()){
                return cacheDir;
            }else if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME,"-","create cache dir failed","-",cacheDir.getPath()));
            }
        }
        File superDir;
        //首先尝试使用cacheDir参数指定的位置
        //android版本号大于等于android2.2版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
            //通过Context.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
            //通过Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
            //这样当该应用被卸载后，这些数据还保留在SDCard中，留下了垃圾数据
            superDir = context.getExternalCacheDir();
            if(superDir != null){
                cacheDir = new File(superDir, DEFAULT_DIRECTORY_NAME);
                if(cacheDir.exists() || cacheDir.mkdirs()) {
                    return cacheDir;
                }
            }
        }
        // 然后尝试使用SD卡的默认缓存文件夹,getCacheDir()方法用于获取/data/data/<application package>/cache目录
        //getFilesDir()方法用于获取/data/data/<application package>/files目录
        //卸载后数据也会被清除
        superDir = context.getCacheDir();
        if (superDir != null){
            cacheDir = new File(superDir,DEFAULT_DIRECTORY_NAME);
            if (cacheDir.exists() || cacheDir.mkdirs()){
                return  cacheDir;
            }
        }
        if(SketchPictures.isDebugMode()){
            Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, "get cache dir failed"));
        }
        cacheDir = null;
        return null;
    }

    @Override
    public void setReserveSize(int reserveSize) {
        if (reserveSize > DEFAULT_RESERVE_SIZE){
            this.reserveSize = DEFAULT_RESERVE_SIZE;
        }
    }

    @Override
    public long getReserveSize() {
        return reserveSize;
    }

    @Override
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public String uriToFileName(String uri) {
        if (SketchUtils.checkSuffix(uri,".apk")){
            uri = uri + ".png";
        }
        try {
            return URLEncoder.encode(uri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getSize() {
        File finalCacheSize = getCacheDir();
        if (finalCacheSize != null && finalCacheSize.exists()) return SketchUtils.countFileLength(finalCacheSize);
        else
        return 0;
    }

    @Override
    public synchronized void clear() {
        File superDir;
        File finalCacheDir;

        superDir = cacheDir;
        finalCacheDir = superDir;
        if(finalCacheDir != null && finalCacheDir.exists()){
            SketchUtils.deleteFile(superDir);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
            superDir = context.getExternalCacheDir();
            finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
            if(finalCacheDir != null && finalCacheDir.exists()){
                SketchUtils.deleteFile(superDir);
            }
        }

        superDir = context.getCacheDir();
        finalCacheDir = superDir!=null?new File(superDir, DEFAULT_DIRECTORY_NAME):null;
        if(finalCacheDir != null && finalCacheDir.exists()){
            SketchUtils.deleteFile(superDir);
        }
    }

    @Override
    public synchronized File saveBitmap(Bitmap bitmap, String uri) {
        if (bitmap == null || bitmap.isRecycled()){
            return null;
        }
        File cachefile = generateCacheFile(uri);//根据uri生成缓存文件
        if (cachefile == null) return null;
        //申请空间
        //手机系统android版本号大于adnroid4.4版本
        int applySize;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            applySize = bitmap.getAllocationByteCount();
        }else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1){
            //版本号大于android3.1
            applySize = bitmap.getByteCount();
        }else{
            applySize = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (!applyForSpace(applySize)) return null;
        File tmpFile = new File(cachefile.getPath()+".temp");
        //创建文件失败
        if (!SketchUtils.CreateFile(tmpFile)){
            if (SketchPictures.isDebugMode()) {
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, "create file failed", " - ", tmpFile.getPath()));
            }
            return null;
        }
        //写出，将bitmap输出流写入到tmpfile文件中
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tmpFile,false);//将tmpFile生成文件输出流
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (tmpFile.exists()){
                if(!SketchUtils.deleteFile(tmpFile) && SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "delete temp cache file failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", uri));
                }
            }
            return null;
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!tmpFile.renameTo(cachefile)){
            if(SketchPictures.isDebugMode()){
                Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "rename failed", " - ", "tempFilePath:", tmpFile.getPath(), " - ", uri));
            }
            tmpFile.delete();
            return null;
        }

        return cachefile;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder stringBuilder) {
        stringBuilder.append(NAME).append(" - ");
        File cacheDir = getCacheDir();
        if(cacheDir != null){
            stringBuilder.append("cacheDir").append("=").append(cacheDir.getPath()) .append(", ");
        }
        stringBuilder.append("maxSize").append("=").append(Formatter.formatFileSize(context, maxSize))
                .append(", ")
                .append("reserveSize").append("=").append(Formatter.formatFileSize(context, reserveSize));
        return stringBuilder;
    }
    /**
     * 文件最后修改日期比较器
     */
    public static class FileLastModifiedComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            long lhsTime = lhs.lastModified();
            long rhsTime = rhs.lastModified();
            if(lhsTime == rhsTime){
                return 0;
            }else if(lhsTime > rhsTime){
                return 1;
            }else{
                return -1;
            }
        }
    }
    /**
     *获取SD卡可用容量
     * @param path ·文件路径
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private long getAvailableSize(String path){
        StatFs statFs = new StatFs(path);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            return (long)statFs.getAvailableBlocks() * statFs.getBlockSize();
        }else{
            return statFs.getAvailableBytes();
        }
    }
}
