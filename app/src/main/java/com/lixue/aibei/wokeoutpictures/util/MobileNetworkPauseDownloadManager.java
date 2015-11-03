package com.lixue.aibei.wokeoutpictures.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lixue.aibei.wokeoutpictures.SketchPictures;

/**
 * 移动网络下暂停下载新图片管理器
 * Created by Administrator on 2015/11/3.
 */
public class MobileNetworkPauseDownloadManager {
    private Context context;
    private BroadcastReceiver receiver;

    public MobileNetworkPauseDownloadManager(Context context){
        this.context = context;
    }

    public void setPauseDownload(boolean pauseDownloadImage){
        if (pauseDownloadImage){
            updateStatus(context);
            if (receiver == null){
                receiver = new NetworkChangedBroadcastReceiver();
            }
            context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }else{
            SketchPictures.getInstance(context).getConfiguration().setPauseDownload(false);
            if (receiver != null){
                context.unregisterReceiver(receiver);
            }
        }
    }

    private void updateStatus(Context context){
        NetworkInfo networkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isPause = networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        SketchPictures.getInstance(context).getConfiguration().setPauseDownload(isPause);

    }

    private class NetworkChangedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                updateStatus(context);
            }
        }
    }
}
