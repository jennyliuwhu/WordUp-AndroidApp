package com.example.jialingliu.wordup.ws.remote;

import com.example.jialingliu.wordup.server.App;

/**
 * Created by jialingliu on 4/1/16.
 */
public class Api {
    public static String LOGIN_PREFIX = "http://%s:8080/WordUp/login?%s";

    public static String REGISTER_PREFIX = "http://%s:8080/WordUp/register?%s,%s";

    public static String SEARCH_PREFIX = "http://%s:8080/WordUp/search?word=%s";

    public static String RECOMMEND_WORDS = "hello,friend,up,people,project,obtain,dream,offer,love,pretty,brilliant,cloud,rainbow,brain";

    public static String RECOMMENDAPI = String.format("http://%s:8080/WordUp/recommend?words=%s", App.ip, RECOMMEND_WORDS);

    public static final String GET_NOTE = String.format("http://%s:8080/WordUp/note", App.ip);

    public static String getLoginAPI (String name){
        return String.format(LOGIN_PREFIX, App.ip, name);
    }

    public static String getRegisterAPI (String name, String password){
        return String.format(REGISTER_PREFIX, App.ip, name, password);
    }

    public static String getSearchAPI (String word){
        return String.format(SEARCH_PREFIX, App.ip, word);
    }
}
