package com.lixue.aibei.sample;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2015/11/30.
 */
public abstract class MyBaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            onPreSetSupportActionBar();
            setSupportActionBar(toolbar);
            toolbar.setLogo(R.mipmap.image1);
            toolbar.setNavigationIcon(R.mipmap.ic_more);
            onPostSetSupportActionBar();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        setTransparentStatusBar();
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        setTransparentStatusBar();
        super.setContentView(view, params);
    }

    @Override
    public void setContentView(View view) {
        setTransparentStatusBar();
        super.setContentView(view);
    }

    protected void onPreSetSupportActionBar(){

    }

    protected void onPostSetSupportActionBar(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if(!isDisableSetFitsSystemWindows()){
            setFitsSystemWindows();
        }
    }

    /**
     * 让状态栏完全透明
     */
    private void setTransparentStatusBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private void setFitsSystemWindows(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            ViewGroup contentViewGroup = (ViewGroup) findViewById(android.R.id.content);
            if(contentViewGroup != null && contentViewGroup.getChildCount() > 0){
                contentViewGroup.getChildAt(0).setFitsSystemWindows(true);
            }
        }
    }

    protected boolean isDisableSetFitsSystemWindows(){
        return false;
    }
}
