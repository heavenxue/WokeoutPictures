package com.lixue.aibei.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 后测fragment
 * Created by Administrator on 2015/12/3.
 */
public class RightFragment extends ListFragment {
    private List<MyItem> dataStr;
    private RightAdapter rightAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataStr = new ArrayList<>();
        for (int i =0 ;i <10;i++){
            MyItem myItem = new MyItem(i + "");
            dataStr.add(myItem);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(rightAdapter = new RightAdapter(getActivity(),dataStr));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mMenuItemSelectedListener != null) {
            mMenuItemSelectedListener.menuItemSelected(((MyItem) getListAdapter().getItem(position)).mText);
        }
        rightAdapter.setmSelected(position);
    }

    public interface MyItemSelectedListener{
        void menuItemSelected(String s);
    }

    private MyItemSelectedListener mMenuItemSelectedListener;

    public void setmMenuItemSelectedListener(MyItemSelectedListener mitl){
        mMenuItemSelectedListener = mitl;
    }

}
