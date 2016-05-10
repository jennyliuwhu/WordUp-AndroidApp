package com.example.jialingliu.wordup.entities;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.model.Category;
import com.example.jialingliu.wordup.model.Item;

import java.util.List;

/**
 * Created by jialingliu on 4/14/16.
 */
public class MenuAdapter extends BaseAdapter {

    public interface MenuListener {

        void onActiveViewChanged(View v);
    }

    private Context context;

    private List<Object> items;

    private MenuListener mListener;

    private int activepos = -1;

    public MenuAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    public void setListener(MenuListener listener) {
        mListener = listener;
    }

    public void setActivePosition(int activePosition) {
        activepos = activePosition;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof Item ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof Item;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Object item = getItem(position);

        if (item instanceof Category) {
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.menu_row_category, parent, false);
            }

            ((TextView) v).setText(((Category) item).title);

        } else {
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.menu_row_item, parent, false);
            }

            TextView tv = (TextView) v;
            tv.setText(((Item) item).title);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(((Item) item).icon, 0, 0, 0);
            } else {
                tv.setCompoundDrawablesWithIntrinsicBounds(((Item) item).icon, 0, 0, 0);
            }
        }

        v.setTag(R.id.mdActiveViewPosition, position);

        if (position == activepos) {
            mListener.onActiveViewChanged(v);
        }
        return v;
    }
}
