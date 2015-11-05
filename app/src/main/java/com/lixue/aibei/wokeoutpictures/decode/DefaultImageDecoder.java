package com.lixue.aibei.wokeoutpictures.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

import com.lixue.aibei.wokeoutpictures.ImageFormat;
import com.lixue.aibei.wokeoutpictures.MaxSize;
import com.lixue.aibei.wokeoutpictures.RecycleGifDrawable;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.LoadRequest;
import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        }else if(loadRequest.getUriScheme() == UriSheme.FILE){
            return decodeFile(loadRequest);
        }else if (loadRequest.getUriScheme() == UriSheme.ASSET){
            return decodeAsset(loadRequest);
        }else if (loadRequest.getUriScheme() == UriSheme.CONTENT){
            return decodeContent(loadRequest);
        }else if (loadRequest.getUriScheme() == UriSheme.DRAWABLE){
            return decodeDrawable(loadRequest);
        }else{
            return null;
        }
    }

    @Override
    public String getIndentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIndentifier(StringBuilder stringBuilder) {
        return stringBuilder.append(NAME);
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

    private Object decodeFile(LoadRequest loadRequest){
        if(loadRequest.isLocalApkFile() && loadRequest.getCacheFile() != null){
            return decodeFromHelper(loadRequest, new CacheFileDecodeHelper(loadRequest.getCacheFile(), loadRequest));
        }else{
            return decodeFromHelper(loadRequest, new FileDecodeHelper(new File(loadRequest.getUri()), loadRequest));
        }
    }
    private Object decodeAsset(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest,new AssetsDecodeHelper(UriSheme.ASSET.crop(loadRequest.getUri()),loadRequest));
    }
    private Object decodeContent(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest,new ContentDecodeHelper(Uri.parse(loadRequest.getUri()),loadRequest));
    }
    private Object decodeDrawable(LoadRequest loadRequest){
        return decodeFromHelper(loadRequest,new DrawableDecodeHelper(Integer.valueOf(UriSheme.DRAWABLE.crop(loadRequest.getUri())), loadRequest));
    }

    public static Object decodeFromHelper(LoadRequest loadRequest,DecodeHelper decodeHelper){
        // just decode bounds
        /**
         * 实现步骤：
         * 第一步：BitmapFactory.Option 设置 inJustDecodeBounds为true
         * 第二步：BitmapFactory.decodeFile(path,option)方法
         * 解码图片路径为一个位图。如果指定的文件名是空的,或者不能解码到一个位图,函数将返回null[空值]。
         * 获取到outHeight(图片原始高度)和 outWidth(图片的原始宽度)
         * 第三步：计算缩放比例，也可以不计算，直接给它设定一个值。
         * options.inSampleSize = "你的缩放倍数";
         * 如果是2就是高度和宽度都是原始的一半。
         * 第四步：设置options.inJustDecodeBounds = false;
         * 重新读出图片
         * bitmap = BitmapFactory.decodeFile(path, options);
         * **/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeHelper.decode(options);
        options.inJustDecodeBounds = false;//不直接存入内存，先读
        //通过文件类型设置最好的图像配置
        loadRequest.setMimeType(options.outMimeType);
        ImageFormat imageFormat = ImageFormat.valueOf(loadRequest.getMimeType());
        if(imageFormat != null){
            options.inPreferredConfig = imageFormat.getConfig(loadRequest.isLowQualityImage());
        }
        //decode gif pictures
        if (imageFormat != null && loadRequest.isDecodeGifImage() && imageFormat == ImageFormat.GIF){
            try {
                return decodeHelper.getGifDrawable();
                //本地没有发现so库异常
                // which an implementation could not be found.
            }catch (UnsatisfiedLinkError e){
                Log.e(SketchPictures.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                loadRequest.getSketch().getConfiguration().setIsDecodeGifImage(false);
                e.printStackTrace();
                //初始化异常
            }catch (ExceptionInInitializerError e){
                Log.e(SketchPictures.TAG, "Didn't find “libpl_droidsonroids_gif.so” file, unable to process the GIF images, has automatically according to the common image decoding, and has set up a closed automatically decoding GIF image feature. If you need to decode the GIF figure please go to “https://github.com/xiaopansky/sketch” to download “libpl_droidsonroids_gif.so” file and put in your project");
                loadRequest.getSketch().getConfiguration().setIsDecodeGifImage(false);
                e.printStackTrace();
            }catch (Throwable e){
                Log.e(SketchPictures.TAG, "When decoding GIF figure some unknown exception, has shut down automatically GIF picture decoding function");
                loadRequest.getSketch().getConfiguration().setIsDecodeGifImage(false);
                e.printStackTrace();
            }
        }
        //decode normal image
        Bitmap bitmap = null;
        Point point = new Point(options.outWidth,options.outHeight);
        if (options.outHeight != -1 && options.outWidth != -1){
            MaxSize maxSize = loadRequest.getMaxSize();
            if (maxSize != null){
                //设置比例
                options.inSampleSize = loadRequest.getSketch().getConfiguration().getImageSizeCalculator().calculateInSampleSize(options.outWidth,options.outHeight,maxSize.getWidth(),maxSize.getWidth());
            }
            //decoding and exclude the width or height of 1 pixel image排除一像素的宽或高
            decodeHelper.decode(options);
            if(bitmap != null && (bitmap.getWidth() == 1 || bitmap.getHeight() == 1)){
                if(SketchPictures.isDebugMode()){
                    Log.w(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "bitmap width or height is 1px", " - ",
                            "ImageSize: ", point.x, "x", point.y, " - ", "BitmapSize: ", bitmap.getWidth(),
                            "x", bitmap.getHeight(), " - ", loadRequest.getName()));
                }
                bitmap.recycle();
                bitmap = null;
            }

        }else{
            if (SketchPictures.isDebugMode()){
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "image width or height is 1px", " - ", "ImageSize: ", point.x, "x", point.y, " - ", loadRequest.getName()));
            }
        }
        // Results the callback
        if(bitmap != null && !bitmap.isRecycled()){
            decodeHelper.onDecodeSuccess(bitmap, point, options.inSampleSize);
        }else{
            bitmap = null;
            decodeHelper.onDecodeFailed();
        }

        return bitmap;

    }

    public static class FileDecodeHelper implements DecodeHelper{
        private File file;
        private LoadRequest loadRequest;

        public FileDecodeHelper(File file,LoadRequest loadRequest){
            this.file = file;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            if (file.canRead()){
                return BitmapFactory.decodeFile(file.getPath(),options);
            }else{
                if(SketchPictures.isDebugMode()){
                    Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "can not read", " - ", file.getPath()));
                }
                return null;
            }
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - "+"decodeSuccess");
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
                StringBuilder log = new StringBuilder(NAME);
                log.append(" - ").append("decode failed");
                log.append(", ").append("filePath").append("=").append(file.getPath());
                if(file.exists()){
                    log.append(", ").append("fileLength").append("=").append(file.length());
                }
                Log.e(SketchPictures.TAG, log.toString());
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(new RandomAccessFile(file.getPath(), "r").getFD());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
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
    public static class AssetsDecodeHelper implements DecodeHelper{
        private static final String NAME = "AssetsDecodeHelper";
        private String assetsFilePath;
        private LoadRequest loadRequest;

        public AssetsDecodeHelper(String assetsFilePath,LoadRequest loadRequest){
            this.assetsFilePath = assetsFilePath;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            InputStream inputStream = null;
            try {
                inputStream = loadRequest.getSketch().getConfiguration().getContext().getAssets().open(assetsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeStream(inputStream,null,options);
            try {
                inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
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
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", assetsFilePath));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(loadRequest.getSketch().getConfiguration().getContext().getAssets(), assetsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public static class ContentDecodeHelper implements DecodeHelper{
        private static final String NAME = "ContentDecodeHelper";
        private Uri contentUri;
        private LoadRequest loadRequest;

        public ContentDecodeHelper(Uri contentUri,LoadRequest loadRequest){
            this.contentUri = contentUri;
            this.loadRequest = loadRequest;
        }

        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            InputStream inputStream = null;
            try {
                inputStream = loadRequest.getSketch().getConfiguration().getContext().getContentResolver().openInputStream(contentUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            if(inputStream != null){
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
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
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", contentUri.toString()));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(loadRequest.getSketch().getConfiguration().getContext().getContentResolver(), contentUri);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public static class DrawableDecodeHelper implements DecodeHelper{
        private static final String NAME = "DrawableDecodeHelper";
        private int drawableId;
        private LoadRequest loadRequest;

        public DrawableDecodeHelper(int drawableId, LoadRequest loadRequest) {
            this.drawableId = drawableId;
            this.loadRequest = loadRequest;
        }


        @Override
        public Bitmap decode(BitmapFactory.Options options) {
            return BitmapFactory.decodeResource(loadRequest.getSketch().getConfiguration().getContext().getResources(), drawableId, options);
        }

        @Override
        public void onDecodeSuccess(Bitmap bitmap, Point originalSize, int inSampleSize) {
            if(SketchPictures.isDebugMode()){
                StringBuilder stringBuilder = new StringBuilder(NAME)
                        .append(" - ").append("decodeSuccess");
                if(bitmap != null && loadRequest.getMaxSize() != null){
                    stringBuilder.append(" - ").append("originalSize").append("=").append(originalSize.x).append("x").append(originalSize.y);
                    stringBuilder.append(", ").append("targetSize").append("=").append(loadRequest.getMaxSize().getWidth()).append("x").append(loadRequest.getMaxSize().getHeight());
                    stringBuilder.append(",  ").append("inSampleSize").append("=").append(inSampleSize);
                    stringBuilder.append(",  ").append("finalSize").append("=").append(bitmap.getWidth()).append("x").append(bitmap.getHeight());
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
                Log.e(SketchPictures.TAG, SketchUtils.concat(NAME, " - ", "decode failed", " - ", String.valueOf(drawableId)));
            }
        }

        @Override
        public RecycleGifDrawable getGifDrawable() {
            try {
                return new RecycleGifDrawable(loadRequest.getSketch().getConfiguration().getContext().getResources(), drawableId);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
