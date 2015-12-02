package com.lixue.aibei.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;

import com.lixue.aibei.sample.fragment.ContentFragment;
import com.lixue.aibei.sample.fragment.LeftFragment;

import java.util.List;


public class HomeActivity extends MyBaseActivity{
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawerLayout;

    private LeftFragment leftFragment;
    private ContentFragment contentFragment;

    private String mTitle;

    private static final String TAG = "HomeActivity";
    private static final String KEY_TITLLE = "key_title";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getSupportActionBar().setTitle("welcome");
        initToolBar();
        //初始化view
        initView();
        //恢复title
        restorTile(savedInstanceState);

        //加载fragement
        FragmentManager fragmentManager = getSupportFragmentManager();
        //查找当前显示的fragment
        contentFragment = (ContentFragment) fragmentManager.findFragmentByTag(mTitle);

        if (contentFragment == null){
            contentFragment = ContentFragment.newInstance(mTitle);
            fragmentManager.beginTransaction().add(R.id.content_container,contentFragment).commit();
        }
        leftFragment = (LeftFragment) fragmentManager.findFragmentByTag(mTitle);
        if (leftFragment == null){
            leftFragment = new LeftFragment();
            fragmentManager.beginTransaction().add(R.id.content_left,leftFragment).commit();
        }

        //隐藏别的fragment，如果存在的话
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null){
            for (Fragment fg : fragments){
                if (fg == leftFragment || fg == contentFragment){
                    fragmentManager.beginTransaction().hide(fg).commit();
                }
            }
        }

        //设置MenuItem的选择回调
        leftFragment.setOnMenuItemSelectedListener(new LeftFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {
                FragmentManager fm = getSupportFragmentManager();
                ContentFragment cf = (ContentFragment) fm.findFragmentByTag(title);
                if (cf == contentFragment){
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    return;
                }
                FragmentTransaction fst = fm.beginTransaction();
                fst.hide(contentFragment);

                if (cf == null){
                    cf = ContentFragment.newInstance(title);
                    fst.add(R.id.content_container,cf,title);
                }else{
                    fst.show(cf);
                }
                fst.commit();
                contentFragment = cf;
                mTitle = title;
                toolbar.setTitle(mTitle);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.image1);
        // Title
        toolbar.setTitle(getResources().getStringArray(R.array.array_left_menu)[0]);
        // Sub Title
//        toolbar.setSubtitle("Sub title");

        setSupportActionBar(toolbar);
        //Navigation Icon
        toolbar.setNavigationIcon(R.mipmap.ic_more);
    }

    private void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_home);
        drawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerToggle.syncState();
        mDrawerLayout.setDrawerListener(drawerToggle);
    }

    private void restorTile(Bundle savedInstanceState){
        if (savedInstanceState != null){
            mTitle = savedInstanceState.getString(KEY_TITLLE);
        }
        if (TextUtils.isEmpty(mTitle)){
            mTitle = getResources().getStringArray(R.array.array_left_menu)[0];
        }
        toolbar.setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLLE, mTitle);
    }


}
