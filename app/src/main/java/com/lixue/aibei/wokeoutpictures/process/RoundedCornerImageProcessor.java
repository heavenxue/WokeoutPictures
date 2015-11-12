package com.lixue.aibei.wokeoutpictures.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.lixue.aibei.wokeoutpictures.ReSize;
import com.lixue.aibei.wokeoutpictures.ResizeCalculator;
import com.lixue.aibei.wokeoutpictures.SketchPictures;

/**
 * 圆角图片处理器
 * Created by Administrator on 2015/11/12.
 */
public class RoundedCornerImageProcessor  implements  ImageProcessor{
    private static final String NAME = "RoundedCornerImageProcessor";
    private int roundPixels;

    public RoundedCornerImageProcessor(int roundPixels){
        this.roundPixels = roundPixels;
    }
    //创建一个默认为18的圆角图片处理器
    public RoundedCornerImageProcessor(){
        this(18);
    }

    @Override
    public Bitmap process(SketchPictures sketch, Bitmap bitmap, ReSize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()){
            return null;
        }
        ResizeCalculator.Result result = sketch.getConfiguration().getResizeCalculator().calculator(bitmap.getWidth(),bitmap.getHeight(),resize!=null?resize.getWidth():bitmap.getWidth(),resize!=null?resize.getHeight():bitmap.getHeight(), resize != null ? resize.getScaleType() : null, forceUseResize);
        if(result == null){
            return bitmap;
        }

        Bitmap output = Bitmap.createBitmap(result.imageWidth, result.imageHeight, lowQualityImage?Bitmap.Config.ARGB_4444:Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);

        // 绘制圆角的罩子
        canvas.drawRoundRect(new RectF(0, 0, result.imageWidth, result.imageHeight), roundPixels, roundPixels, paint);

        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, paint);
        return output;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME).append(" - ").append("roundPixels").append("=").append(roundPixels);
    }
    public int getRoundPixels() {
        return roundPixels;
    }

    public void setRoundPixels(int roundPixels) {
        this.roundPixels = roundPixels;
    }
}
