package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.DisplayRequest;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.request.LoadRequest;

/**
 * request创建工厂
 * Created by Administrator on 2015/11/6.
 */
public interface RequestFactory {
    DisplayRequest newDisplayRequest(SketchPictures sketch, String uri, UriSheme uriScheme, String memoryCacheId, SketchImageViewInterface sketchImageViewInterface);
    LoadRequest newLoadRequest(SketchPictures sketch, String uri, UriSheme uriScheme);
    DownloadRequest newDownloadRequest(SketchPictures sketch, String uri, UriSheme uriScheme);

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
