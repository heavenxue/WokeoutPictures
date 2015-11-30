package com.lixue.aibei.sample;

import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 2015/11/30.
 */
public class MyFragment extends Fragment {

    @Override
    public void onPause() {
        super.onPause();
        if(getUserVisibleHint()){
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            onUserVisibleChanged(isVisibleToUser);
        }
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser){

    }

    public boolean isVisibleToUser(){
        return isResumed() && getUserVisibleHint();
    }
}
