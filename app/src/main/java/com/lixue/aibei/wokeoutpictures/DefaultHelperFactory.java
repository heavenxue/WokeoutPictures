package com.lixue.aibei.wokeoutpictures;

/**
 * 默认协助器工厂
 * Created by Administrator on 2015/11/5.
 */
public class DefaultHelperFactory implements HelperFactory {
    private static final String NAME = "DefaultHelperFactory";
    private DisplayHelper displayHelper;


    @Override
    public DownloadHelper getDownloadHelper(SketchPictures sketchPictures, String uri) {
        return new DefaultDownloadHelper(sketchPictures, uri);
    }

    @Override
    public LoadHelper getLoadHelper(SketchPictures sketchPictures, String uri) {
        return new DefaultLoadHelper(sketchPictures,uri);
    }

    @Override
    public DisplayHelper getDisplayHelper(SketchPictures sketchPictures, String uri, SketchImageViewInterface sketchImageViewInterface) {
        if(this.displayHelper == null){
            return new DefaultDisplayHelper(sketchPictures, uri, sketchImageViewInterface);
        }else{
            DisplayHelper displayHelper = this.displayHelper;
            this.displayHelper = null;
            displayHelper.init(sketchPictures, uri, sketchImageViewInterface);
            return displayHelper;
        }
    }

    @Override
    public DisplayHelper getDisplayHelper(SketchPictures sketchPictures, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        return null;
    }

    @Override
    public void recycleDisplayHelper(DisplayHelper obsoletingDisplayHelper) {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return null;
    }
}
