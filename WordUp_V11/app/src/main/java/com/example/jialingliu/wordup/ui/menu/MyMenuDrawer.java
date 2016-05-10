package com.example.jialingliu.wordup.ui.menu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.entities.MenuAdapter;
import com.example.jialingliu.wordup.model.Category;
import com.example.jialingliu.wordup.model.Item;
import com.example.jialingliu.wordup.util.MySharedpreference;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jialingliu on 4/14/16.
 */
public abstract class MyMenuDrawer extends FragmentActivity implements MenuAdapter.MenuListener {

    private static final String STATE_ACTIVE_POSITION =
            "package com.example.jialingliu.wordup.activePosition";

    protected MenuDrawer menudrawer;

    protected MenuAdapter adapter;
    protected ListView    listview;

    private int activepos = 0;
    private int width;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        if (inState != null) {
            activepos = inState.getInt(STATE_ACTIVE_POSITION);
        }
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = (int) (metric.widthPixels / 10 * 4.62);
    }

    protected void initMenuDrawer() {
        menudrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
        List<Object> items = new ArrayList<Object>();
        MySharedpreference mySharedpreference = new MySharedpreference(this);
        String username = mySharedpreference.getString("username");
        if (username != null) {
            items.add(new Item(username, R.drawable.ic_notification));
        } else {
            items.add(new Item("Login/Register", R.drawable.ic_notification));
        }
        items.add(new Category("Words"));
        items.add(new Item("Search", R.drawable.icon_search_pressed));
        items.add(new Item("My Favorite", R.drawable.toolbar_fav_icon_res));
        items.add(new Item("Memorized", R.drawable.navigationbar_check));
        items.add(new Category("Others"));
        items.add(new Item("Contact", R.drawable.memu_share));
        items.add(new Item("Settings", R.drawable.menu_icon_setting));
        items.add(new Item("About", R.drawable.menu_about));
        items.add(new Category(" "));
        items.add(new Item("Exit", R.drawable.menu_exit));

        listview = new ListView(this);
        adapter = new MenuAdapter(this, items);
        adapter.setListener(this);
        adapter.setActivePosition(activepos);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(mItemClickListener);
        menudrawer.setupUpIndicator(this);
        menudrawer.setDrawerIndicatorEnabled(true);
        menudrawer.setMenuView(listview);
        menudrawer.setMenuSize(width);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initMenuDrawer();
    }

    protected abstract void onMenuItemClicked(int position, Item item);

    protected abstract int getDragMode();

    protected abstract Position getDrawerPosition();

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            activepos = position;
            menudrawer.setActiveView(view, position);
            adapter.setActivePosition(position);
            onMenuItemClicked(position, (Item) adapter.getItem(position));
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, activepos);
    }

    @Override
    public void onActiveViewChanged(View v) {
        menudrawer.setActiveView(v, activepos);
    }
}
