package com.lixue.aibei.wokeoutpictures.display;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import com.lixue.aibei.wokeoutpictures.RecycleGifDrawable;
import com.lixue.aibei.wokeoutpictures.SketchImageViewInterface;

/**
 * 过渡效果的图片显示器
 * Created by Administrator on 2015/11/6.
 */
public class TransitionImageDisplayer implements ImageDisplayer {
    private static final String NAME = "TransitionImageDisplayer";
    private int duration;//持续时间

    public TransitionImageDisplayer(int duration){
        this.duration = duration;
    }

    public TransitionImageDisplayer(){
        this(400);
    }

    @Override
    public void display(SketchImageViewInterface sketchImageViewInterface, Drawable newDrawable) {
        if (newDrawable == null){
            return;
        }
        if (newDrawable instanceof RecycleGifDrawable){
            sketchImageViewInterface.clearAnimation();
            sketchImageViewInterface.setImageDrawable(newDrawable);
        }else{
            Drawable oldDrawable = sketchImageViewInterface.getDrawable();
            if (oldDrawable != null){
                new ColorDrawable(Color.TRANSPARENT);
            }
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{oldDrawable,newDrawable});
            sketchImageViewInterface.clearAnimation();
            transitionDrawable.setCrossFadeEnabled(true);
            transitionDrawable.startTransition(duration);
        }
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME)
                .append(" - ")
                .append("duration").append("=").append(duration);
    }
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
