package com.example.jialingliu.wordup.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.model.RecommendWord;
import com.example.jialingliu.wordup.model.Word;
import com.example.jialingliu.wordup.ui.SplashActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jialingliu on 4/17/16.
 */
public class MySharedpreference {

    private Context context;
    private SharedPreferences sharedpreferences;

    public MySharedpreference(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(
                "userinfo", Context.MODE_PRIVATE
        );
    }

    public int getCurrentWordId() {
        return sharedpreferences.getInt(context.getString(R.string.current_word_id), 6);
    }

    public boolean setCurrentWordId(int currentWordId) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(context.getString(R.string.current_word_id), currentWordId);
        return editor.commit();
    }

    public boolean saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public boolean saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public String getString(String key) {
        return sharedpreferences.getString(key, null);
    }

    public Boolean getBoolean(String key) {
        return sharedpreferences.getBoolean(key, false);
    }

    /**
     * Set the honor, it is the number of notify count.
     *
     * @param honor count
     *
     * @return true is successful
     */
    public boolean saveHonor(int honor) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("honor", honor);
        return editor.commit();
    }

    public boolean saveYesterdayJson(String string) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("yesterday_json", string);
        return editor.commit();
    }

    public boolean saveTodayJson(String string) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("today_json", string);
        return editor.commit();
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> map = new HashMap<>();
        int honor = sharedpreferences.getInt("honor", 0);
        boolean adStatus = sharedpreferences.getBoolean("ad_status", true);
        map.put("honor", honor);
        map.put("ad_status", adStatus);
        return map;
    }

    public Map<String, String> getWordJson() {

        Map<String, String> map = new HashMap<>();
        //init
        RecommendWord rw = SplashActivity.getRecommendWord();
        Word word = new Word();
        word.setWord(rw.getWord());
        word.setPhonetic("");
        word.setSpeech(rw.getSpeech());
        word.setExplanation(rw.getExplanation());
        word.setExample(rw.getExample());

        String yesterdayJson = sharedpreferences.getString("yesterday_json", new Gson().toJson(word));
        String todayJson = sharedpreferences.getString("today_json", new Gson().toJson(word));
        System.out.println("original yesterday="+yesterdayJson);
        System.out.println("original today="+todayJson);
        map.put("yesterday_json", yesterdayJson);
        map.put("today_json", todayJson);
        return map;
    }
}
