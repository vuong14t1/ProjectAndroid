package com.example.mrpython.elsreen.module.game.Data;

/**
 * Created by vuong on 3/25/2018.
 */

public class GameBase {
    private Player player;
    private Question question;
    public GameBase(){
        this.player = new Player();
        this.question = new Question();
        setData();
    }
    public void setData(){
        this.player.cheatData();
        this.question.cheatData();
    }
    //region GET
    public Player getPlayer(){
        return this.player;
    }
    public Question getQuestion(){
        return this.question;
    }
    //endregion
}
