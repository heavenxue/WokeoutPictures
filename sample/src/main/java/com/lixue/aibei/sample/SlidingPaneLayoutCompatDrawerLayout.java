package com.lixue.aibei.sample;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;

/**
 * Created by Administrator on 2015/12/3.
 */
public class SlidingPaneLayoutCompatDrawerLayout extends DrawerLayout{
    private SlidingPaneLayout mSliddingPaneLayout;

    public SlidingPaneLayoutCompatDrawerLayout(Context context, SlidingPaneLayout paneLayout) {
        super(context);
        mSliddingPaneLayout = paneLayout;
    }

    @Override
    public boolean isDrawerOpen(View drawer) {
        return mSliddingPaneLayout.isOpen();
    }

    @Override
    public boolean isDrawerVisible(View drawer) {
        return mSliddingPaneLayout.isOpen();
    }

    @Override
    public void closeDrawer(View drawerView) {
        mSliddingPaneLayout.closePane();
    }

    @Override
    public void openDrawer(View drawerView) {
        mSliddingPaneLayout.openPane();
    }

    @Override
    public Resources getResources() {
        if (mSliddingPaneLayout != null){
            return mSliddingPaneLayout.getResources();
        }else{
            return super.getResources();
        }
    }
}
