package com.lixue.aibei.sample.fragment;

/**
 * Created by Administrator on 2015/11/30.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lixue.aibei.sample.MyFragment;

/**
 * 明星首页
 */
public class StarIndexFragment extends MyFragment {
    private ViewPager viewPager;
//    private GetStarTagStripListener getPagerSlidingTagStripListener;
//    private FragmentAdapter fragmentAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.fragment_star_index,null,false);
//        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        if(activity instanceof GetStarTagStripListener){
//            getPagerSlidingTagStripListener = (GetStarTagStripListener) activity;
//        }else{
//            getPagerSlidingTagStripListener = null;
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        if(getPagerSlidingTagStripListener != null){
//            getPagerSlidingTagStripListener = null;
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if(fragmentAdapter == null){
//            Fragment[] fragments = new Fragment[2];
//            fragments[0] = new HotStarFragment();
//            fragments[1] = new StarCatalogFragment();
//            fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragments);
        }
//        viewPager.setAdapter(fragmentAdapter);
//        getPagerSlidingTagStripListener.onGetStarTabStrip().setViewPager(viewPager);
    }

//    public interface GetStarTagStripListener{
//        public PagerSlidingTabStrip onGetStarTabStrip();
//    }
//}
