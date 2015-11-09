package com.lixue.aibei.wokeoutpictures;

import java.io.File;

/**
 * 下载结果
 * Created by Administrator on 2015/11/9.
 */
public class DownLoadResult {
    private Object result;//结果
    private boolean fromNetwork; //是否来自网络
    public DownLoadResult(){}

    public Object getResult(){
        return result;
    }

    public void setResult(Object result){
        this.result = result;
    }
    public boolean isFromNetwork(){
        return fromNetwork;
    }
    public void setFromNetwork(boolean fromNetwork){
        this.fromNetwork = fromNetwork;
    }

    /**
     * 通过文件创建下载结果
     * @param resultFile
     * @param fromNetwork
     * @return
     */
    public static DownLoadResult createByFile(File resultFile,boolean fromNetwork){
        DownLoadResult downLoadResult = new DownLoadResult();
        downLoadResult.setResult(resultFile);
        downLoadResult.setFromNetwork(fromNetwork);
        return downLoadResult;
    }
    public static DownLoadResult createByArray(byte[] resultData,boolean fromNetwork){
        DownLoadResult result = new DownLoadResult();
        result.setFromNetwork(fromNetwork);
        result.setResult(resultData);
        return result;
    }

}
