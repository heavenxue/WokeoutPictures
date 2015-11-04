package com.lixue.aibei.wokeoutpictures.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.ImageFormat;
import com.lixue.aibei.wokeoutpictures.RecycleGifDrawable;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.LoadRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 默认的图片解码器
 * Created by Administrator on 2015/11/4.
 */
public class DefaultImageDecoder implements ImageDecoder{
    private static final String NAME = "DefaultImageDecoder";

    @Override
    public Object decode(LoadRequest loadRequest) {
        if (loadRequest.getUriScheme() == UriSheme.HTTP || loadRequest.getUriScheme() == UriSheme.HTTPS){
            return decodeHttpOrHttps(loadRequest);
        }
        return null;
    }

    @Override
    public String getIndentifier() {
        return appendIndentifier(new StringBuilder()).toString();
    }

    @Override
    public String appendIndentifier(StringBuilder stringBuilder) {
        return null;
    }

    private Object decodeHttpOrHttps(LoadRequest loadRequest){
        File cacheFile = loadRequest.getCacheFile();
        if (cacheFile != null && cacheFile.exists()){
            return decodeFromHelper(loadRequest,new CacheFileDecodeHelper(cacheFile,loadRequest));
        }
        byte[] imageData = loadRequest.getImageData();
        if (imageData != null && imageData.length > 0){
            return decodeFromHelper(loadRequest,new ByteArrayDecodeHelper(imageData,loadRequest));
        }
        return null;
    }

    public static Object decodeFromHelper(LoadRequest loadRequest,DecodeHelper decodeHelper){
        // just decode bounds
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeHelper.decode(options);
        options.inJustDecodeBounds = false;//不直接存入内存，先读
        //通过文件类型设置最好的图像配置
        loadRequest.setMimeType(options.outMimeType);
        ImageFormat imageFormat = ImageFormat.valueOf(loadRequest.getMimeType());
        return null;
    }

    public static class CacheFileDecodeHelper implements DecodeHelper{
        private File cacheFile;
        private LoadRequest loadRequest;

        public CacheFileDecodeHelper(File cacheFile,LoadRequest loadRequest){
            this.cacheFile = cacheFile;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            if (!cacheFile.canRead()){
                if (SketchPictures.isDebugMode()){
                    Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "can not read", " - ", cacheFile.getPath()));
                }
                return null;
            }
            return BitmapFactory.decodeFile(cacheFile.getPath(),options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if (!cacheFile.setLastModified(System.currentTimeMillis())){
                if (SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG,SketchUtils.concat("-","update lasted modified time failed","-",cacheFile.getPath()));
                }
            }
            if(SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME).append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(SketchPictures.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if (SketchPictures.isDebugMode()){
                StringBuilder logContent = new StringBuilder(NAME);
                logContent.append(" - ").append("decode failed");
                logContent.append(", ").append("filePath").append("=").append(cacheFile.getPath());
                if(cacheFile.exists()){
                    logContent.append(",  ").append("fileLength").append("=").append(cacheFile.length());
                }
                logContent.append(",  ").append("imageUri").append("=").append(loadRequest.getUri());
                Log.e(SketchPictures.TAG, logContent.toString());
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(new RandomAccessFile(cacheFile.getPath(), "r").getFD());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public static class ByteArrayDecodeHelper implements DecodeHelper{
        private static final String NAME = "ByteArrayDecodeHelper";
        private byte[] imageData;
        private LoadRequest loadRequest;

        public ByteArrayDecodeHelper(byte[] imgData,LoadRequest loadRequest){
            this.imageData = imgData;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            if (imageData != null && imageData.length > 0){
                BitmapFactory.decodeByteArray(imageData,0,imageData.length,options);
            }
            return null;
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME).append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(", ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(", ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
                }else{
                    stringBuilder.append(" - ").append("unchanged");
                }
                stringBuilder.append(" - ").append(loadRequest.getName());
                Log.d(SketchPictures.TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onDecodeFailed() {
            if(SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", loadRequest.getName()));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(imageData);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
