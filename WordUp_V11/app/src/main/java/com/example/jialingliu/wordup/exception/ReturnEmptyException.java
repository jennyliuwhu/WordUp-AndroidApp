package com.example.jialingliu.wordup.exception;

/**
 * Created by sagejoyoox on 4/30/16.
 */
public class ReturnEmptyException extends Exception{
    private String err;

    public void printErr() {
        System.out.println("The error is: " + err);
    }

    public String getErr() {
        return err;
    }
    public void setErr(String err) {
        this.err = err;
    }
}