package com.example.jialingliu.wordup.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.model.FavoriteWord;

import java.util.List;

/**
 * Created by jialingliu on 4/27/16.
 */
public class MyAdapter extends BaseAdapter {

    Context mContext;
    List<FavoriteWord> mList;

    public MyAdapter(Context context, List<FavoriteWord> wordList) {
        this.mContext = context;
        this.mList = wordList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public FavoriteWord getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoriteWord fw = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wordlist, null);
            TextView tv = (TextView) convertView;
            tv.setText((fw.getWord() + " " + fw.getSpeech() + " " + fw.getExplanation()));
            convertView.setTag(tv);
        } else {
            TextView tv = (TextView) convertView.getTag();
            tv.setText((fw.getWord() + " " + fw.getSpeech() + " " + fw.getExplanation()));
        }
        return convertView;
    }
}
