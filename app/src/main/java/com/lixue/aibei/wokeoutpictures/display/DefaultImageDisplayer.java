package com.lixue.aibei.wokeoutpictures.display;

import android.graphics.drawable.Drawable;

import com.lixue.aibei.wokeoutpictures.SketchImageViewInterface;

/**
 * 默认的图片显示器，没有任何动画效果
 * Created by Administrator on 2015/11/10.
 */
public class DefaultImageDisplayer implements ImageDisplayer{
    private static final String NAME = "DefaultImageDisplayer";

    @Override
    public void display(SketchImageViewInterface sketchImageViewInterface, Drawable newDrawable) {
        if(newDrawable == null){
            return;
        }
        sketchImageViewInterface.clearAnimation();
        sketchImageViewInterface.setImageDrawable(newDrawable);
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
