package com.example.jialingliu.wordup.model;

/**
 * Created by jialingliu on 4/14/16.
 */
public class Item {

    public String title;
    public int icon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Item(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }
}
