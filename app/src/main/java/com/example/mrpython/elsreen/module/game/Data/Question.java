package com.example.mrpython.elsreen.module.game.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by vuong on 3/25/2018.
 */

public class Question {
    private String question;
    private String result;
    private ArrayList<String> listAnswer;

    public  Question() {
        listAnswer = new ArrayList<String>();
    }
    //region SET && GET

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        if(question == null) return;
        this.question = question;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if(result == null) return;
        this.result = result;
    }

    public ArrayList<String> getListAnswer() {
        return listAnswer;
    }

    public void setListAnswer(ArrayList<String> listAnswer) {
        if(listAnswer == null) return;
        this.listAnswer = listAnswer;
    }
    //endregion
    public void addAnser(String ans) {
        if(ans == null) return;
        this.listAnswer.add(ans);
    }
    public boolean isResult(String re) {
        if(re == null) return false;
        if((this.listAnswer.indexOf(re)) == -1) return false;
        if(! re.trim().equals(this.result.trim())) return false;

        return true;
    }

    public void shuffleAnswer() {
        Collections.shuffle(this.listAnswer);
    }

    public void setData(String question, String result, ArrayList listAnswer) {
        this.cleanUp();
        this.setQuestion(question);
        this.setResult(result);
        this.setListAnswer(listAnswer);
    }
    private void cleanUp(){
        this.question = null;
        this.result = null;
        this.listAnswer = null;
        this.listAnswer = new ArrayList<String>();
    }
    public void  cheatData() {
        String question = "Tôi Tên là gì ?";
        String result = "oc cho";
        ArrayList<String> arr = new ArrayList<>();
        arr.add("oc cho");
        arr.add("oc cho 1");
        arr.add("oc cho 2");
        arr.add("oc cho 3");
        this.setData(question, result, arr);
    }
}
