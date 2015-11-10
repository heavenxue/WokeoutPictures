package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.request.DisplayRequest;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * ImageView持有器，以弱引用的方式持有关联的ImageView
 * Created by Administrator on 2015/11/10.
 */
public class SketchImageViewInterfaceHolder {
    private DisplayRequest displayRequest;
    private Reference<SketchImageViewInterface> sketchImageViewInterfaceReference;

    public SketchImageViewInterfaceHolder(SketchImageViewInterface sketchImageViewInterface,DisplayRequest displayRequest){
        this.displayRequest = displayRequest;
        this.sketchImageViewInterfaceReference = new WeakReference<SketchImageViewInterface>(sketchImageViewInterface);
    }
    public SketchImageViewInterface getSketchImageViewInterface(){
        final SketchImageViewInterface sketchImageViewInterface = sketchImageViewInterfaceReference.get();
        if (displayRequest != null){
            DisplayRequest holderDisplayRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
            if (displayRequest != null && holderDisplayRequest == displayRequest){
                return sketchImageViewInterface;
            }else{
                return null;
            }
        }else{
            return sketchImageViewInterface;
        }
    }

    /**
     * 判断sketchImageViewInterface是否存在
     * @return
     */
    public boolean isCollected(){
        return getSketchImageViewInterface()==null;
    }
}
