package com.lixue.aibei.sample;

import android.content.Context;
import android.widget.ImageView;

import com.lixue.aibei.wokeoutpictures.Configuration;
import com.lixue.aibei.wokeoutpictures.DisplayOptions;
import com.lixue.aibei.wokeoutpictures.ImageHolder;
import com.lixue.aibei.wokeoutpictures.LoadOptions;
import com.lixue.aibei.wokeoutpictures.ReSize;
import com.lixue.aibei.wokeoutpictures.SketchPictures;
import com.lixue.aibei.wokeoutpictures.display.TransitionImageDisplayer;
import com.lixue.aibei.wokeoutpictures.process.CircleImageProcessor;
import com.lixue.aibei.wokeoutpictures.process.GaussianBlurImageProcessor;
import com.lixue.aibei.wokeoutpictures.process.RoundedCornerImageProcessor;

/**
 * Created by Administrator on 2015/11/11.
 */
public class SketchManager {
    private Context context;

    public SketchManager(Context context) {
        this.context = context;
    }

    public void initConfig(){
        SketchPictures.setDebugMode(BuildConfig.DEBUG);
        Settings settings = Settings.with(context);
        Configuration sketchConfiguration = SketchPictures.getInstance(context).getConfiguration();
        sketchConfiguration.setMobileNetworkPauseDownload(settings.isMobileNetworkPauseDownload());
        sketchConfiguration.setLowQualityImage(settings.isLowQualityImage());
        sketchConfiguration.setLowQualityImage(settings.isLowQualityImage());
        sketchConfiguration.setIsCacheInDisk(true);
        sketchConfiguration.setCacheInMemory(settings.isCacheInMemory());
    }

    public void initDisplayOptions(){
        TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();
        SketchPictures.putOptions(OptionsType.NORMAL_RECT, new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setFailureImage(R.drawable.image_failure)
                        .setIsDecodeGifImage(false)
                        .setImageDisplayer(transitionImageDisplayer)
        );
        RoundedCornerImageProcessor roundedCornerImageProcessor = new RoundedCornerImageProcessor(20);
        ReSize appIconSize = new ReSize(30, 30, ImageView.ScaleType.CENTER_CROP);
        SketchPictures.putOptions(OptionsType.APP_ICON, new DisplayOptions()
                        .setLoadingImage(new ImageHolder(R.drawable.image_loading).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setFailureImage(new ImageHolder(R.drawable.image_failure).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setPauseDownloadImage(new ImageHolder(R.drawable.image_loading).setImageProcessor(roundedCornerImageProcessor).setResize(appIconSize).setForceUseResize(true))
                        .setIsDecodeGifImage(false)
                        .setResizeByFixedSize(true)
                        .setIsForceUseResize(true)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(roundedCornerImageProcessor)
        );

        SketchPictures.putOptions(OptionsType.DETAIL, new DisplayOptions()
                        .setImageDisplayer(transitionImageDisplayer)
        );

        SketchPictures.putOptions(OptionsType.NORMAL_CIRCULAR, new DisplayOptions()
                        .setLoadingImage(new ImageHolder(R.drawable.image_loading).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setFailureImage(new ImageHolder(R.drawable.image_failure).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setPauseDownloadImage(new ImageHolder(R.drawable.image_loading).setImageProcessor(CircleImageProcessor.getInstance()))
                        .setIsDecodeGifImage(false)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageProcessor(CircleImageProcessor.getInstance())
        );

        SketchPictures.putOptions(OptionsType.WINDOW_BACKGROUND, new LoadOptions()
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
                        .setIsDecodeGifImage(false)
        );
    }
}
