package com.lixue.aibei.wokeoutpictures.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/3.
 */
public class SketchUtils {
    /**
     * ��ѹAPK��ͼ��
     * @return
     */
    public static Bitmap decodeIconFromApk(Context context,String apkFilePath,boolean lowQualityImage,String logName){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, packageManager.GET_ACTIVITIES);
        if (packageInfo == null){
            return null;
        }
        packageInfo.applicationInfo.sourceDir = apkFilePath;
        packageInfo.applicationInfo.publicSourceDir = apkFilePath;
        Drawable drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        if (drawable != null && drawable == packageManager.getDefaultActivityIcon()){
            return null;
        }
        return DrawableToBitmap(drawable,lowQualityImage);
    }

    /**��drawableת��Ϊbitmap����
     * @param drawable
     * @param lowQuality
     * @return
     */
    public static Bitmap DrawableToBitmap(Drawable drawable,boolean lowQuality){
        if (drawable == null){
            return null;
        }else if (drawable instanceof BitmapDrawable){
            return null;
        }else {
            if(drawable.getIntrinsicHeight() == 0 || drawable.getIntrinsicWidth() == 0){
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), lowQuality ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888);

            if (bitmap == null) return null;
            // ������Ӧ bitmap �Ļ���
            Canvas canvas = new Canvas(bitmap);
            // �� drawable ���ݻ���������
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /** �����ļ�
     * @param file
     * @return true or false
     */
    public static boolean CreateFile(File file){
        if (file.exists()) return true;
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdir()) return false;
        try {
            if (!file.createNewFile()){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * �����ļ����ȣ��˷����Ĺؼ������ڣ���Ҳ�ܻ�ȡĿ¼�ĳ���
     *
     * @return length
     */
    public static long countFileLength(File file){
        if (!file.exists()) return 0;
        if (file.isFile()) return file.length();
        File[] childFiles = file.listFiles();
        if (childFiles == null || childFiles.length <= 0) return 0;
        List<File> fileList = new LinkedList<File>();
        Collections.addAll(fileList,childFiles);
        long mLength = 0;
        for (File cdfile : fileList){
            if (cdfile.isFile()){
                mLength += cdfile.length();
            }else{
                File[] CdFiles = cdfile.listFiles();
                if (CdFiles != null && CdFiles.length > 0){
                    Collections.addAll(fileList,CdFiles);
                }
            }

        }
        return mLength;
    }

    /**
     * ɾ���������ļ��������ǰ�ļ���Ŀ¼���ɾ������������е��ļ���Ŀ¼
     * @param file
     * @return
     */
    public static boolean deleteFile(File file){
        if (!file.exists()) return true;
        if (file.isFile()) return file.delete();
        File[] childFiles = file.listFiles();
        boolean deleteSuccess = true;
        for (File cdFile : childFiles){
            if (!deleteFile(cdFile)){
                deleteSuccess = false;
            }
        }
        if (deleteSuccess){
            deleteSuccess = file.delete();
        }
        return deleteSuccess;
    }

    /**
     * ���ָ�����ļ����Ƿ��д˺�׺��
     * @param name
     * @param suffix
     * @return
     */
    public static boolean checkSuffix(String name,String suffix){
        if (name == null) return false;
        int index = name.indexOf(".");
        String tmpSuffix;
        if (index > -1){
            tmpSuffix = name.substring(index);
        }else{
            return false;
        }

        return suffix.equalsIgnoreCase(tmpSuffix);
    }

    /**�ַ�������
     * @param strings
     * @return
     */
    public static String concat(Object... strings){
        if (strings == null || strings.length <= 0) return null;
        StringBuilder sb = new StringBuilder();
        for(Object str : strings){
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * ��ͼ����ԭͼ���Сת��ΪĿ��ͼ���С��
     * @param sourceWidth
     * @param sourceHeight
     * @param targetWidth
     * @param targetHeight
     * @param rect
     */
    public static void mapping(int sourceWidth,int sourceHeight,int targetWidth,int targetHeight,Rect rect){
        float widthScale = (float)targetWidth / sourceWidth;
        float heightScale = (float)targetHeight / sourceHeight;
        float finalScale = widthScale - heightScale > 0 ? heightScale : widthScale;
        int srcWidth = (int)(sourceWidth * finalScale);
        int srcHeight = (int)(sourceHeight * finalScale);
        int left = (sourceWidth - srcWidth) / 2;
        int top = (sourceHeight - srcHeight)/2;
        rect.set(left,top,left + srcWidth,top + srcHeight);
    }

}
