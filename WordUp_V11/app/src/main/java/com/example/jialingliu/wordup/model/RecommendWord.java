package com.example.jialingliu.wordup.model;

import org.litepal.crud.DataSupport;

/**
 * Created by sagejoyoox on 5/1/16.
 */
public class RecommendWord extends DataSupport {
    private String word;
    private String phonetic;
    private String speech;
    private String explanation;
    private String example;

    public RecommendWord(Word word) {
        this.setWord(word.getWord());
        this.setExample(word.getExample());
        this.setExplanation(word.getExplanation());
        this.setPhonetic(word.getPhonetic());
        this.setSpeech(word.getSpeech());
    }

    public long getId() {
        return this.getBaseObjId();
    }

    public RecommendWord() {
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

}
