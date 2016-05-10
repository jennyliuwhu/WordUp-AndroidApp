package com.example.jialingliu.wordup.entities;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jialingliu on 4/24/16.
 */
public class MainViewPagerAdapter extends PagerAdapter {
    private List<View> viewlist;
    private List<String> titlelist;

    public List<View> getViewlist() {
        return viewlist;
    }

    public void setViewlist(List<View> viewlist) {
        this.viewlist = viewlist;
    }

    public List<String> getTitlelist() {
        return titlelist;
    }

    public void setTitlelist(List<String> titlelist) {
        this.titlelist = titlelist;
    }

    public MainViewPagerAdapter(List<View> viewlist, List<String> titlelist) {
        this.viewlist = viewlist;
        this.titlelist = titlelist;
    }

    public MainViewPagerAdapter() {
    }

    @Override
    public int getCount() {
        return viewlist.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(viewlist.get(position));
        return viewlist.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(viewlist.get(position));
    }
}