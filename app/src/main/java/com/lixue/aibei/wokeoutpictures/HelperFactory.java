package com.lixue.aibei.wokeoutpictures;

/**
 * Created by Administrator on 2015/11/5.
 */
public interface HelperFactory {
    /**
     * 获取下载协助器
     * @param sketchPictures
     * @param uri
     * @return
     */
    DownloadHelper getDownloadHelper(SketchPictures sketchPictures,String uri);

    /**
     * 获取加载协助器
     * @param sketchPictures
     * @param uri
     * @return
     */
    LoadHelper getLoadHelper(SketchPictures sketchPictures,String uri);

    /**
     * 获取显示协助器
     * @param sketchPictures
     * @param uri
     * @param sketchImageViewInterface
     * @return
     */
    DisplayHelper getDisplayHelper(SketchPictures sketchPictures,String uri,SketchImageViewInterface sketchImageViewInterface);

    /**获取显示协助器
     * @param sketchPictures
     * @param displayParams
     * @param sketchImageViewInterface
     * @return
     */
    DisplayHelper getDisplayHelper(SketchPictures sketchPictures,DisplayParams displayParams,SketchImageViewInterface sketchImageViewInterface);

    /**
     * 回收显示协助器
     * @param obsoletingDisplayHelper
     */
    void recycleDisplayHelper(DisplayHelper obsoletingDisplayHelper);
    /**
     * 获取标识符
     * @return 标识符
     */
    String getIdentifier();

    /**
     * 追加标识符
     */
    StringBuilder appendIdentifier(StringBuilder builder);
}
