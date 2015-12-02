package com.lixue.aibei.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/12/2.
 */
public class ContentFragment extends Fragment {
    private final static String PARAMS = "CONTENT_PARAMS" ;

    public static ContentFragment newInstance(String title){
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAMS,title);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String title = this.getArguments().getString(PARAMS);
        TextView tv = new TextView(getActivity());
        if (!"".equals(title)){
            tv.setText(title);
            tv.setTextSize(36);
            tv.setGravity(Gravity.CENTER);
        }
        return tv;
    }
}
