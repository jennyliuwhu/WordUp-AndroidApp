package com.example.jialingliu.wordup.model;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

/**
 * Created by jialingliu on 4/17/16.
 */
public class Word extends DataSupport {
    private int id;
    private String word;
    private String phonetic;
    private String speech;
    private String explanation;
    private String example;

    public Word(){}

    public Word(String word, String phonetic, String speech, String explanation, String example) {
        this.word = word;
        this.phonetic = phonetic;
        this.speech = speech;
        this.explanation = explanation;
        this.example = example;
    }

    public long getId() {
        return this.getBaseObjId();
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String toGson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toGson();
    }

    public static void main(String[] d) {
        Word word1 = new Gson().fromJson("{\"word\":\"essence\",\"phonetic\":\"[ˈesns] \",\"speech\":\"n. \",\"explanation\":\"本质，实质；精髓\",\"example\":\"eg. The essence of flat, is super curved.\\n平坦的本质，是极致的曲面。 \\n\\n \"}", Word.class);
        word1.setId(1);
        System.out.printf("-->" + new Word().toGson());
    }
}
