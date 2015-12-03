package com.lixue.aibei.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lixue.aibei.sample.fragment.ContentFragment;


public class SlidingPaneleLayoutActivity extends MyBaseActivity {
    private SlidingPaneLayout mSlidingPaneLayout;
    private ListView mSliderList;
    private ActionBarDrawerToggle mActionBarToggle;
    private CharSequence mDrawerTitle;
    private String mTitle;
    private String[] mPlanetTitles;

    private ArrayAdapter adapter;
    private ContentFragment contentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_panele_layout);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mSlidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_layout);
        mSliderList = (ListView) findViewById(R.id.sliding_list);

        //设置阴影
        mSlidingPaneLayout.setShadowResourceLeft(R.drawable.shape_drawer_shadow_down_left);
        mSliderList.setAdapter(adapter = new ArrayAdapter<String>(this, R.layout.list_item_title, mPlanetTitles));
        mSliderList.setOnItemClickListener(new DrawerItemClickListener());
        mActionBarToggle = new ActionBarDrawerToggle(this,new SlidingPaneLayoutCompatDrawerLayout(getBaseContext(), mSlidingPaneLayout),R.string.drawer_open,R.string.drawer_close);

        //加载fragement
        FragmentManager fragmentManager = getSupportFragmentManager();
        //查找当前显示的fragment
        contentFragment = (ContentFragment) fragmentManager.findFragmentByTag(mTitle);

        if (contentFragment == null){
            contentFragment = ContentFragment.newInstance(mTitle);
            fragmentManager.beginTransaction().add(R.id.content_frame,contentFragment).commit();
        }

        mSlidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                mActionBarToggle.onDrawerSlide(panel, slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                mActionBarToggle.onDrawerOpened(panel);
            }

            @Override
            public void onPanelClosed(View panel) {
                mActionBarToggle.onDrawerClosed(panel);
            }
        });

    }

    /* The click listner for ListView in the navigation drawer */
   private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          selectItem(position);
        }
      }
    private void selectItem(int position){
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment cf = (ContentFragment) fm.findFragmentByTag((String)adapter.getItem(position));
        if (cf == contentFragment){
            mSlidingPaneLayout.closePane();
            return;
        }
        FragmentTransaction fst = fm.beginTransaction();
        fst.hide(contentFragment);

        if (cf == null){
            cf = ContentFragment.newInstance((String)adapter.getItem(position));
            fst.add(R.id.content_frame,cf,(String)adapter.getItem(position));
        }else{
            fst.show(cf);
        }
        fst.commit();
        contentFragment = cf;
        mTitle = (String)adapter.getItem(position);
        mSlidingPaneLayout.closePane();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarToggle.syncState();
    }
}
