package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.enums.RequestLevel;
import com.lixue.aibei.wokeoutpictures.process.ImageProcessor;

/**
 * 加载选项
 * Created by Administrator on 2015/11/5.
 */
public class LoadOptions extends DownloadOptions {
    private ReSize reSize;
    private MaxSize maxSize;
    private boolean isDecodeGifImage = false;
    private boolean isForceUseResize;
    private boolean isLowQualityImage;
    private ImageProcessor imageProcessor;

    public LoadOptions(){

    }

    public LoadOptions(LoadOptions loadOptions){
        this.reSize = loadOptions.reSize;
        this.maxSize = loadOptions.maxSize;
        this.isForceUseResize = loadOptions.isForceUseResize;
        this.isDecodeGifImage = loadOptions.isDecodeGifImage;
        this.isLowQualityImage =  loadOptions.isLowQualityImage;
        this.imageProcessor = loadOptions.imageProcessor;
    }

    @Override
    public LoadOptions setCacheInDisk(boolean cacheInDisk) {
        super.setCacheInDisk(cacheInDisk);
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxSize 最大尺寸
     * @return
     */
    public LoadOptions setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }
    public LoadOptions setMaxSize(int width,int height){
        this.maxSize.set(width,height);
        return this;
    }

    /**
     * 获得最大尺寸
     * @return
     */
    public MaxSize getMaxSize() {
        return maxSize;
    }
    /**
     * 获取新尺寸
     * @return 新尺寸
     */
    public ReSize getResize() {
        return reSize;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，
     * 但尺寸不一定会等于resize，也有可能小于resize
     * @param reSize
     * @return
     */
    public LoadOptions setResize(ReSize reSize) {
        this.reSize = reSize;
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，
     * 但尺寸不一定会等于resize，也有可能小于resize
     * @param width
     * @param height
     * @return
     */
    public LoadOptions setResize(int width,int height){
        this.reSize = new ReSize(width,height);
        return this;
    }

    /**
     * 获取图片处理器
     * @return
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器
     * @param imageProcessor
     * @return
     */
    public LoadOptions setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
        return this;
    }

    /**
     * 设置是否解码Gif图片
     * @param isDecodeGifImage
     * @return
     */
    public LoadOptions setIsDecodeGifImage(boolean isDecodeGifImage) {
        this.isDecodeGifImage = isDecodeGifImage;
        return this;
    }

    /**
     * 获取是否解码GIG图片
     * @return
     */
    public boolean getIsDecodeGifImage(){
        return isDecodeGifImage;
    }

    /**获取是否返回低质量的图片
     * @return
     */
    public boolean getIsLowQualityImage() {
        return isLowQualityImage;
    }

    /**
     * 设置是否返回低质量的图片
     * @param isLowQualityImage
     */
    public LoadOptions setIsLowQualityImage(boolean isLowQualityImage) {
        this.isLowQualityImage = isLowQualityImage;
        return this;
    }

    @Override
    public LoadOptions setRequestLevel(RequestLevel requestLevel) {
        super.setRequestLevel(requestLevel);
        return this;
    }

    /**
     * 设置是否强制使经过resize返回的图片同resize的尺寸一致
     * @param isForceUseResize
     * @return
     */
    public LoadOptions setIsForceUseResize(boolean isForceUseResize) {
        this.isForceUseResize = isForceUseResize;
        return this;
    }

    /**
     * 获取设置是否强制使经过resize返回的图片同resize的尺寸一致
     * @return
     */
    public boolean getIsForceUseResize(){
        return isForceUseResize;
    }
}
