package com.lixue.aibei.wokeoutpictures;

import com.lixue.aibei.wokeoutpictures.enums.UriSheme;
import com.lixue.aibei.wokeoutpictures.request.DisplayRequest;
import com.lixue.aibei.wokeoutpictures.request.DownloadRequest;
import com.lixue.aibei.wokeoutpictures.request.LoadRequest;

/**
 * 默认请求工厂
 * Created by Administrator on 2015/11/10.
 */
public class DeafaultRequestFactory  implements  RequestFactory{
    private static final String NAME = "DeafaultRequestFactory";
    @Override
    public DisplayRequest newDisplayRequest(SketchPictures sketch, String uri, UriSheme uriScheme, String memoryCacheId, SketchImageViewInterface sketchImageViewInterface) {
        return new DefaultDisplayRequest(sketch,uri,uriScheme,memoryCacheId,sketchImageViewInterface);
    }

    @Override
    public LoadRequest newLoadRequest(SketchPictures sketch, String uri, UriSheme uriScheme) {
        return new DefaultLoadRequest(sketch, uri, uriScheme);
    }

    @Override
    public DownloadRequest newDownloadRequest(SketchPictures sketch, String uri, UriSheme uriScheme) {
        return new DefaultDownloadRequest(sketch, uri, uriScheme);
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
