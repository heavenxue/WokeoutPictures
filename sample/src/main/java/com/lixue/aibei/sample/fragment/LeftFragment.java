package com.lixue.aibei.sample.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lixue.aibei.sample.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class LeftFragment extends ListFragment implements AbsListView.OnItemClickListener {
    private static final int SIZE_MENU_ITEM = 3;

    private MenuItem[] mItems = new MenuItem[SIZE_MENU_ITEM];

    private LeftMenuAdapter mAdapter;

    private LayoutInflater mInflater;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = LayoutInflater.from(getActivity());

        MenuItem menuItem = null;
        for (int i = 0; i < SIZE_MENU_ITEM; i++) {
            menuItem = new MenuItem(getResources().getStringArray(R.array.array_left_menu)[i], false, R.mipmap.ic_music, R.mipmap.ic_music_light);
            mItems[i] = menuItem;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mAdapter = new LeftMenuAdapter(getActivity(), mItems));
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onListItemClick((ListView) parent, view, position, id);
        if (mMenuItemSelectedListener != null) {
            mMenuItemSelectedListener.menuItemSelected(((MenuItem) getListAdapter().getItem(position)).text);
        }

        mAdapter.setSelected(position);


    }

    //选择回调的接口
    public interface OnMenuItemSelectedListener {
        void menuItemSelected(String title);
    }
    private OnMenuItemSelectedListener mMenuItemSelectedListener;

    public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener menuItemSelectedListener) {
        this.mMenuItemSelectedListener = menuItemSelectedListener;
    }
}
