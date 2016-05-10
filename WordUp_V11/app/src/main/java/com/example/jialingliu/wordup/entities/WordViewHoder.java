package com.example.jialingliu.wordup.entities;

import android.widget.TextView;

/**
 * Created by jialingliu on 4/24/16.
 */
public class WordViewHoder {
    int id;
    public TextView wordTextView;
    public TextView phoneticTextView;
    public TextView speechTextView;
    public TextView explanationTextView;
    public TextView exampleTextView;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}