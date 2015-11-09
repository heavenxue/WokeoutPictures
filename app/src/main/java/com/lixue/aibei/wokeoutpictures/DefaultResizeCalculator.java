package com.lixue.aibei.wokeoutpictures;

import android.graphics.Rect;
import android.widget.ImageView;

/**
 * 默认重置大小计算器
 * Created by Administrator on 2015/11/9.
 */
public class DefaultResizeCalculator implements ResizeCalculator {
    private static final String NAME = "DefaultResizeCalculator";

    @Override
    public Result calculator(int originalImageWidth, int originalImageHeight, int targetImageWidth, int targetImageHeight, ImageView.ScaleType scaleType, boolean forceUseResize) {
        if (originalImageWidth == targetImageWidth && originalImageHeight == targetImageHeight){
            Result result = new Result();
            result.imageHeight = originalImageHeight;
            result.imageWidth = originalImageWidth;
            result.destRect = new Rect(0,0,originalImageWidth,originalImageHeight);
            result.srcRect = result.destRect;
            return result;
        }
        if (scaleType == null){
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }
        int newImageWidth;
        int newImageHeight;
        if (forceUseResize){
            newImageWidth = targetImageWidth;
            newImageHeight = targetImageHeight;
        }else{
            int[] finalSize = scaleTargetSize(originalImageWidth,originalImageHeight,targetImageWidth,targetImageHeight);
            newImageWidth = finalSize[0];
            newImageHeight = finalSize[1];
        }
        Rect srcRect;
        Rect destRect = new Rect(0,0,newImageWidth,newImageHeight);
        if(scaleType == ImageView.ScaleType.CENTER || scaleType == ImageView.ScaleType.CENTER_CROP || scaleType == ImageView.ScaleType.CENTER_INSIDE){
            srcRect = srcMappingCenterRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_START){
            srcRect = srcMappingStartRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_CENTER){
            srcRect = srcMappingCenterRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_END){
            srcRect = srcMappingEndRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else if(scaleType == ImageView.ScaleType.FIT_XY){
            srcRect = new Rect(0, 0, originalImageWidth, originalImageHeight);
        }else if(scaleType == ImageView.ScaleType.MATRIX){
            srcRect = srcMatrixRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }else{
            srcRect = srcMappingCenterRect(originalImageWidth, originalImageHeight, newImageWidth, newImageHeight);
        }
        Result result = new Result();
        result.imageWidth = newImageWidth;
        result.imageHeight = newImageHeight;
        result.srcRect = srcRect;
        result.destRect = destRect;
        return result;
    }

    /**scaleType.CENTER:按图片的原来size居中显示，当图片长/宽超过View的长/宽，则截取图片的居中部分显示
     * scaleType.CENTER.CROP:按比例扩大图片的size居中显示，使得图片长(宽)等于或大于View的长(宽)
     * scaleType.CENTER_INSIDE:将图片的内容完整居中显示，通过按比例缩小或原来的size使得图片长/宽等于或小于View的长/宽
     * sCaleType.FIT_CENTER:把图片按比例扩大/缩小到View的宽度，居中显示
     * @param originalWidth
     * @param originalHeight
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Rect srcMappingCenterRect(int originalWidth,int originalHeight,int targetWidth,int targetHeight){
        float widthScale = (float)originalWidth/targetWidth;
        float heightScale = (float)originalHeight/targetHeight;
        float finalScale = widthScale < heightScale ? widthScale : heightScale;
        int srcWidth = (int)(targetWidth * finalScale);
        int srcHeight = (int)(targetHeight * finalScale);
        int srcLeft = (originalWidth - srcWidth) / 2;//中心点位置
        int srcTop = (originalHeight - srcHeight) / 2;//中心点位置
        return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
    }

    /**scaleType.FIT_START:FIT_START在图片缩放效果上与FIT_CENTER一样，只是显示的位置不同，FIT_START是置于顶部
     * @param originalWidth
     * @param originalHeight
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Rect srcMappingStartRect(int originalWidth,int originalHeight,int targetWidth,int targetHeight){
        float widthScale = (float)originalWidth/targetWidth;
        float heightScale = (float)originalHeight/targetHeight;
        float finalScale = widthScale > heightScale ? heightScale : widthScale;
        int srcWidth = (int)(targetWidth * finalScale);
        int srcHeight = (int)(targetHeight * finalScale);
        int srcLeft = 0;
        int srcTop = 0;
        return new Rect(srcLeft,srcTop,srcLeft+srcWidth,srcTop +srcHeight);
    }

    /**
     * scaleType.FIT_END:FIT_END在图片缩放效果上与FIT_CENTER一样，只是显示的位置不同，FIT_END是置于底部
     * @param originalWidth
     * @param originalHeight
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Rect srcMappingEndRect(int originalWidth,int originalHeight,int targetWidth,int targetHeight){
        float widthScale = (float)originalWidth / targetWidth;
        float heightScale = (float)originalHeight/targetHeight;
        float finalScale = widthScale > heightScale ? heightScale : widthScale;
        int srcWidth = (int)(originalWidth * finalScale);
        int srcHeight = (int)(originalHeight * finalScale);
        int srcLeft = 0;
        int srcTop;
        if(originalWidth > originalHeight){
            srcLeft= originalWidth-srcWidth;
            srcTop= originalHeight-srcHeight;
        }else{
            srcLeft= originalHeight-srcWidth;
            srcTop= originalHeight-srcHeight;
        }
        return new Rect(srcLeft,srcTop,srcLeft+srcWidth,srcTop+srcHeight);
    }

    /**scaleType.MATRIX:用matrix来绘制
     * @param originalWidth
     * @param originalHeight
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Rect srcMatrixRect(int originalWidth,int originalHeight,int targetWidth,int targetHeight){
        if(originalWidth > targetWidth && originalHeight > targetHeight){
            return new Rect(0, 0, targetWidth, targetHeight);
        }else{
            float scale = targetWidth-originalWidth > targetHeight-originalHeight ? (float) targetWidth/originalWidth:(float) targetHeight/originalHeight;
            int srcWidth = (int)(targetWidth / scale);
            int srcHeight = (int)(targetHeight / scale);
            int srcLeft = 0;
            int srcTop = 0;
            return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
        }
    }
    /**
     * 缩放图像的最终尺寸
     * @param originalImageWidth 原宽度
     * @param originalImageHeight 原高度
     * @param targetImageWidth 最终宽度
     * @param targetImageHeight 最终高度
     * @return 尺寸数组
     */
    public static int[] scaleTargetSize(int originalImageWidth,int originalImageHeight,int targetImageWidth,int targetImageHeight){
        if (targetImageWidth > originalImageWidth || targetImageHeight > originalImageHeight){
            float scale = targetImageHeight - originalImageHeight < targetImageWidth - originalImageWidth ? (float)targetImageWidth/originalImageWidth :(float)targetImageHeight/originalImageHeight;
            targetImageWidth /= scale;
            targetImageHeight /= scale;
        }
        return new int[]{targetImageWidth,targetImageHeight};
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }
}
