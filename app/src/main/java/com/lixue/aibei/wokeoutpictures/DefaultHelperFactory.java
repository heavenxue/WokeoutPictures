package com.lixue.aibei.wokeoutpictures;

/**
 * 默认协助器工厂
 * Created by Administrator on 2015/11/5.
 */
public class DefaultHelperFactory implements HelperFactory {
    @Override
    public DownloadHelper getDownloadHelper(SketchPictures sketchPictures, String uri) {
        return null;
    }

    @Override
    public LoadHelper getLoadHelper(SketchPictures sketchPictures, String uri) {
        return null;
    }

    @Override
    public DisplayHelper getDisplayHelper(SketchPictures sketchPictures, String uri, SketchImageViewInterface sketchImageViewInterface) {
        return null;
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
