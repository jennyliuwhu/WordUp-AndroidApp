package com.example.jialingliu.wordup.util;

import android.content.Context;
import android.widget.Toast;

import com.example.jialingliu.wordup.server.App;

/**
 * Created by jialingliu on 4/17/16.
 */
public class ToastUtils {

    Context context;

    public ToastUtils(Context context) {
        this.context = context;
    }

    public static void showShort(String message) {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String message) {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
