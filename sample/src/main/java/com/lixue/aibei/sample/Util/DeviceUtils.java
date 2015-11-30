package com.lixue.aibei.sample.Util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * Created by Administrator on 2015/11/30.
 */
public class DeviceUtils {
    private static final String ATTR_NAME_STATUS_BAR_HEIGHT = "status_bar_height";
    private static final String ATTR_NAME_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String ATTR_NAME_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";

    /**
     * 获取状态栏高度
     * @param resources
     * @return
     */
    public static int getStatusBarHeight(Resources resources){
        return getInternalDimensionSize(resources, ATTR_NAME_STATUS_BAR_HEIGHT);
    }

    /**
     * 获取导航栏的高度
     * @param resources
     * @return
     */
    public static int getNavigationBarHeight(Resources resources){
        if(resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            return getInternalDimensionSize(resources, ATTR_NAME_NAVIGATION_BAR_HEIGHT);
        }else if(resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return getInternalDimensionSize(resources, ATTR_NAME_NAVIGATION_BAR_HEIGHT_LANDSCAPE);
        }else{
            return getInternalDimensionSize(resources, ATTR_NAME_NAVIGATION_BAR_HEIGHT);
        }
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");

        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * dp转换为px
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, int dpValue){
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5);
    }
}
