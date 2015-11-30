package com.lixue.aibei.sample;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lixue.aibei.sample.Util.DeviceUtils;
import com.lixue.aibei.wokeoutpictures.SketchImageView;
import com.lixue.aibei.wokeoutpictures.SketchPictures;

import me.xiaopan.psts.PagerSlidingTabStrip;


public class MainActivity extends MyBaseActivity {
    private SketchImageView imageView;
    private View contentView;
    private PagerSlidingTabStrip starTabStrip;
    private PagerSlidingTabStrip appListTabStrip;
    private SlidingPaneLayout slidingPaneLayout;
    private View leftMenuView;
    private View searchButton;
    private View starButton;
    private View photoAlbumButton;
    private View appListButton;
    private View aboutButton;
    private View scrollingPauseLoadItem;
    private CheckBox scrollingPauseLoadCheckBox;
    private View mobileNetworkPauseDownloadItem;
    private CheckBox mobileNetworkPauseDownloadCheckBox;
    private View showImageDownloadProgressItem;
    private CheckBox showImageDownloadProgressCheckBox;
    private View showImageFromFlagItem;
    private CheckBox showImageFromFlagCheckBox;
    private View clickDisplayOnFailedItem;
    private CheckBox clickDisplayOnFailedCheckBox;
    private View clickDisplayOnPauseDownloadItem;
    private CheckBox clickDisplayOnPauseDownloadCheckBox;
    private View showPressedStatusItem;
    private CheckBox showPressedStatusCheckBox;
    private View cleanMemoryCacheItem;
    private TextView memoryCacheSizeTextView;
    private View cleanDiskCacheItem;
    private TextView diskCacheSizeTextView;
    private View cacheMemoryItem;
    private CheckBox cacheInMemoryCheckBox;
    private View cacheInDiskItem;
    private CheckBox cacheInDiskCheckBox;
    private View lowQualityImageItem;
    private CheckBox lowQualityImageCheckBox;

    private long lastClickBackTime;
    private Type type;
    private Settings settings;

    private WindowBackgroundManager windowBackgroundManager;
    private ActionBarDrawerToggle toggleDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onInitLayoutTopPadding();
        slidingPaneLayout.setShadowResourceLeft(R.drawable.shape_drawer_shadow_down_left);
        slidingPaneLayout.setSliderFadeColor(Color.parseColor("#00ffffff"));
        initSlidingPanLayoutSlideListener();

//        windowBackgroundManager = new WindowBackgroundManager(this);
//
//        StarHomeFragment starHomeFragment = new StarHomeFragment();
//        starHomeFragment.setArguments(getIntent().getExtras());
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.frame_onlyFragment_content, starHomeFragment)
//                .commit();
//    }

    }

    private void onInitLayoutTopPadding(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            int statusBarHeight = DeviceUtils.getStatusBarHeight(getResources());
            if(statusBarHeight > 0){
                contentView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
                leftMenuView.setPadding(contentView.getPaddingLeft(), statusBarHeight, contentView.getPaddingRight(), contentView.getPaddingBottom());
            }else{
                slidingPaneLayout.setFitsSystemWindows(true);
            }
        }
    }

    private void initSlidingPanLayoutSlideListener(){
        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                toggleDrawable.onDrawerSlide(panel, slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                toggleDrawable.onDrawerOpened(panel);
                refreshCacheSizeInfo(false, SketchPictures.getInstance(getBaseContext()).getConfiguration().getMemoryCache().getSize(), SketchPictures.getInstance(getBaseContext()).getConfiguration().getMemoryCache().getMaxSize());
                new AsyncTask<Integer, Integer, Long>() {
                    @Override
                    protected Long doInBackground(Integer... params) {
                        return SketchPictures.getInstance(getBaseContext()).getConfiguration().getDiskCache().getSize();
                    }

                    @Override
                    protected void onPostExecute(Long diskUsedSize) {
                        refreshCacheSizeInfo(true, diskUsedSize, SketchPictures.getInstance(getBaseContext()).getConfiguration().getDiskCache().getMaxSize());
                    }
                }.execute(0);
            }

            @Override
            public void onPanelClosed(View panel) {
                toggleDrawable.onDrawerClosed(panel);
            }
        });
    }

    private void refreshCacheSizeInfo(boolean disk, long useSize, long maxSize){
        String usedSizeFormat = Formatter.formatFileSize(getBaseContext(), useSize);//useSize字节
        String maxSizeFormat = Formatter.formatFileSize(getBaseContext(), maxSize);
        String cacheInfo = usedSizeFormat+"/"+maxSizeFormat;
        if(disk){
            diskCacheSizeTextView.setText(cacheInfo);
        }else{
            memoryCacheSizeTextView.setText(cacheInfo);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        // 同步状态，这一步很重要，要不然初始
        toggleDrawable.syncState();
        toggleDrawable.onDrawerSlide(null, 1.0f);
        toggleDrawable.onDrawerSlide(null, 0.5f);
        toggleDrawable.onDrawerSlide(null, 0.0f);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggleDrawable.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            if(slidingPaneLayout.isOpen()){
                slidingPaneLayout.closePane();
            }else{
                slidingPaneLayout.openPane();
            }
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if((currentTime - lastClickBackTime) > 2000){
            lastClickBackTime = currentTime;
            Toast.makeText(getBaseContext(), "再按一下返回键退出" + getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }

    private enum Type{
        STAR,
        SEARCH,
        LOCAL_PHOTO_ALBUM,
        ABOUT,
        APP_LIST,
    }
}
