package com.lixue.aibei.wokeoutpictures.decode;

import com.lixue.aibei.wokeoutpictures.request.LoadRequest;

/**
 * Created by Administrator on 2015/11/4.
 */
public interface ImageDecoder {
    /**解码
     * @param loadRequest    加载请求
     * @return null：解码失败；Bitmap：一般的图片；RecycleGifDrawable：GIF图片
     */
    Object decode(LoadRequest loadRequest);

    /**获取标识符
     * @return
     */
    String getIndentifier();

    /**
     * 追加标识符
     * @param stringBuilder
     * @return
     */
    String appendIndentifier(StringBuilder stringBuilder);
}
