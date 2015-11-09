package com.lixue.aibei.wokeoutpictures.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.lixue.aibei.wokeoutpictures.ReSize;
import com.lixue.aibei.wokeoutpictures.ResizeCalculator;
import com.lixue.aibei.wokeoutpictures.SketchPictures;

/**
 * 默认图像处理器
 * Created by Administrator on 2015/11/9.
 */
public class DeFaultImageProcessor implements ImageProcessor {
    private static final String NAME = "DeFaultImageProcessor";

    @Override
    public Bitmap process(SketchPictures sketch, Bitmap bitmap, ReSize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()){
            return null;
        }
        if (resize == null ||(bitmap.getWidth() == resize.getWidth() && bitmap.getHeight() == resize.getHeight())){
            return bitmap;
        }

        ResizeCalculator.Result result = sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(), bitmap.getHeight(), resize.getWidth(), resize.getHeight(), resize.getScaleType(), forceUseResize);
        if(result == null){
            return bitmap;
        }

        Bitmap.Config newBitmapConfig = bitmap.getConfig();
        if(newBitmapConfig == null){
            newBitmapConfig = lowQualityImage ? Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888;
        }
        Bitmap newBitmap = Bitmap.createBitmap(result.imageWidth, result.imageHeight, newBitmapConfig);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, null);
        return newBitmap;
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
