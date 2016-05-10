package com.example.jialingliu.wordup.server;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by jialingliu on 4/17/16.
 */
public class App extends LitePalApplication {
    // the private ip of your own computer
    public static final String ip = "";
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
