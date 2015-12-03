package com.lixue.aibei.sample.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lixue.aibei.sample.R;

import java.util.List;

/**
 * Created by Administrator on 2015/12/3.
 */
public class RightAdapter extends ArrayAdapter<MyItem> {
    private LayoutInflater mInflater;

    private int mSelected;

    public RightAdapter(Context context, List<MyItem> resource) {
        super(context,-1, resource);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_left_menu, parent, false);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.id_item_icon);
        TextView title = (TextView) convertView.findViewById(R.id.id_item_title);
        title.setText(getItem(position).mText);
        title.setTextColor(convertView.getResources().getColor(R.color.window_background_start));
        iv.setVisibility(View.GONE);
        convertView.setBackgroundColor(Color.TRANSPARENT);

        if (position == mSelected) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.state_menu_item_selected));
        }
        return convertView;
    }

    public void setmSelected(int position){
        this.mSelected = position;
        notifyDataSetChanged();
    }
}
