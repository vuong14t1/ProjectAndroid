package com.example.mrpython.elsreen.module.game.Data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

/**
 * Created by vuong on 3/25/2018.
 */

public class Player {
    private String uName;
    private long scores;
    private int level;
    private long curExp;
    private Context context;
    private String id;
    private Boolean serviceStatus;  // true = started, false = not started

    public  Player(Context context){
        this.context = context;
        scores = 0;
        level = 1;
        curExp = 0;
        Random rand =  new Random();
        int randomMode = rand.nextInt(10000000) + 1;
        id = String.valueOf(randomMode);
    }
    public Player(){
    }
    //region SET && GET
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setName(String uName) {
        if(uName == null) return;
        this.uName = uName;
    }

    public void setScores(long scores) {
        if(scores < 0) return;
        this.scores = scores;
    }

    public void setLevel(int level) {
        if(level < 0) return;
        this.level = level;
    }

    public void setCurExp(long curExp) {
        if(curExp < 0) return;
        this.curExp = curExp;
    }

    public void setServiceStatus(boolean status)
    {
        this.serviceStatus = status;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public String getName() {
        if(this.uName == null) return "User";
        return this.uName;
    }

    public long getScores() {
        return this.scores;
    }
    public  int getLevel() {
        return this.level;
    }
    public long getCurExp() {
        return this.curExp;
    }
    public boolean getServiceStatus()
    {
        return this.serviceStatus;
    }
    // endregion

    public void addScore(long s) {
        if(s <= 0 ) return;
        this.scores += s;
    }
    public void addCurExp(long e) {
        if(e <=0 ) return;
        this.curExp += e;
    }
    public void addLevel(int l) {
        if(l <= 0) {
            this.level ++;
        }else{
            this.level += l;
        }

    }

    public void setData(String id, String uName, long scores, int level, long curExp) {
        this.setId(id);
        this.setName(uName);
        this.setScores(scores);
        this.setLevel(level);
        this.setCurExp(curExp);
    }


    public void loadData()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);

        String success = sharedPreferences.getString("id", "-1");
        if(!success.equals("-1")){
            this.id = success;
        }
        this.uName = sharedPreferences.getString("name", "Player");
        this.level = sharedPreferences.getInt("level", 1);
        this.curExp = sharedPreferences.getLong("exp", 0);
        this.serviceStatus = sharedPreferences.getBoolean("serviceStatus", false);
        GameBase.numOfSOS = sharedPreferences.getInt("sos", 3);
    }

    public void loadNumOfSOS()
    {   SharedPreferences sharedPreferences = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);
        GameBase.numOfSOS = sharedPreferences.getInt("SOS", 3);
    }

    public void saveNumOfSOS()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SOS", GameBase.numOfSOS);
        editor.apply();
    }

    public void saveData()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gameData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", this.getId());
        editor.putString("name", this.uName);
        editor.putInt("level", this.level);
        editor.putLong("exp", this.curExp);
        editor.putBoolean("serviceStatus", this.serviceStatus);
        editor.apply();
    }

    @Override
    public String toString() {
        return uName ;
    }
}
