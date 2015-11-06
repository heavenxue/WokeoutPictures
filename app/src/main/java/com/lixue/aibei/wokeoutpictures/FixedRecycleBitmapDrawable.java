package com.lixue.aibei.wokeoutpictures;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.util.SketchUtils;

/**
 * 固定被回收的图片
 * Created by Administrator on 2015/11/6.
 */
public class FixedRecycleBitmapDrawable extends Drawable implements RecycleDrawableInterface{
    private static final int DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG;//抗锯齿标志位
    private int bitmapWidth;
    private int bitmapHeight;
    private Rect srcRect;
    private Rect dicRect;
    private Paint paint;
    private FixedSize fixedSize;
    private Bitmap bitmap;
    private RecycleBitmapDrawable recycleBitmapDrawable;

    public FixedRecycleBitmapDrawable(RecycleBitmapDrawable recycleBitmapDrawable,FixedSize fixedSize){
        this.recycleBitmapDrawable = recycleBitmapDrawable;
        bitmap = recycleBitmapDrawable != null ? recycleBitmapDrawable.getBitmap():null;
        if (bitmap != null){
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            paint = new Paint(DEFAULT_PAINT_FLAGS);
            dicRect = new Rect(0,0,bitmapWidth,bitmapHeight);
            if (fixedSize == null){
                srcRect = new Rect(0,0,bitmapWidth,bitmapHeight);
                //必须使用Drawable.setBounds设置Drawable的长宽。
                setBounds(0,0,bitmapWidth,bitmapHeight);
            }else{
                int fixedWidth = fixedSize.getWidth();
                int fixedHeight = fixedSize.getHeight();
                if(bitmapWidth == 0 || bitmapHeight == 0){
                    this.srcRect = new Rect(0, 0, 0, 0);
                }else if((float)bitmapWidth/(float)bitmapHeight == (float)fixedWidth/(float)fixedHeight){
                    this.srcRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
                }else{
                    this.srcRect = new Rect();
                    SketchUtils.mapping(bitmapWidth, bitmapHeight, fixedWidth, fixedHeight, srcRect);
                }
                //必须使用Drawable.setBounds设置Drawable的长宽。
                setBounds(0, 0, fixedSize.getWidth(), fixedSize.getHeight());
            }
        }

    }
    @Override
    public void draw(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled() && srcRect != null && dicRect != null && paint != null) {
            canvas.drawBitmap(bitmap, srcRect, dicRect, paint);
        }
    }

    @Override
    public void setAlpha(int i) {
        if (paint != null){
            final int oldAlpha = paint.getAlpha();
            if (i != oldAlpha){
                paint.setAlpha(i);
                invalidateSelf();
            }
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != null){
            paint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return fixedSize != null ? fixedSize.getWidth() : bitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return fixedSize != null ? fixedSize.getHeight() : bitmapHeight;
    }

    @Override
    public void setDither(boolean dither) {
        if(paint != null){
            paint.setDither(dither);
            invalidateSelf();
        }
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if(paint != null){
            paint.setFilterBitmap(filter);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        return (bitmap == null || paint == null || bitmap.hasAlpha() || paint.getAlpha() < 255) ?
                PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if(dicRect != null){
            dicRect.set(0, 0, bounds.width(), bounds.height());
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public FixedSize getFixedSize() {
        return fixedSize;
    }

    @Override
    public void setIsDisplayed(String callingStation, boolean displayed) {
        if(recycleBitmapDrawable != null){
            recycleBitmapDrawable.setIsDisplayed(callingStation, displayed);
        }
    }

    @Override
    public void setIsCached(String callingStation, boolean cached) {
        if(recycleBitmapDrawable != null){
            recycleBitmapDrawable.setIsCached(callingStation, cached);
        }
    }

    @Override
    public void setIsWaitDisplay(String callingStation, boolean waitDisplay) {
        if(recycleBitmapDrawable != null){
            recycleBitmapDrawable.setIsWaitDisplay(callingStation, waitDisplay);
        }
    }

    @Override
    public int getByteCount() {
        return recycleBitmapDrawable!=null?recycleBitmapDrawable.getByteCount():0;
    }

    @Override
    public boolean isRecyled() {
        return recycleBitmapDrawable==null || recycleBitmapDrawable.isRecyled();
    }

    @Override
    public String getMimeType() {
        return recycleBitmapDrawable!=null?recycleBitmapDrawable.getMimeType():null;
    }

    @Override
    public void setMimeType(String mimeType) {
        if(recycleBitmapDrawable != null){
            recycleBitmapDrawable.setMimeType(mimeType);
        }
    }

    @Override
    public void recyle() {
        if (bitmap != null || !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    @Override
    public String getSize() {
        return recycleBitmapDrawable!=null?recycleBitmapDrawable.getSize():null;
    }

    @Override
    public String getConfig() {
        return recycleBitmapDrawable!=null?recycleBitmapDrawable.getConfig():null;
    }

    @Override
    public String getInfo() {
        return recycleBitmapDrawable!=null?recycleBitmapDrawable.getInfo():null;
    }

    @Override
    public boolean canRecyle() {
        return recycleBitmapDrawable!=null&&recycleBitmapDrawable.canRecyle();
    }

    @Override
    public void setAllowRecycle(boolean allowRecycle) {
        if(recycleBitmapDrawable != null){
            recycleBitmapDrawable.setAllowRecycle(allowRecycle);
        }
    }
}
