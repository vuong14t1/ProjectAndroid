package com.example.mrpython.elsreen.module.game.Data;

import android.content.Context;

import com.example.mrpython.elsreen.MainActivity;
import com.example.mrpython.elsreen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vuong on 3/25/2018.
 */

public class GameBase {
    private Player player;
    private ArrayList<Question> listQuestion;
    private Context context;
    private int resourceDataId = R.raw.data;

    public GameBase(Context context) {
        this.setContext(context);
        this.player = new Player();
        listQuestion = new ArrayList<>();
        this.setData();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData() {
        this.player.cheatData();
        try {
            this.readJsonDataFile();
        } catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
    }
    //region GET
    public Player getPlayer() {
        return this.player;
    }

    public Question getRandomQuestion() {
        Random rand = new Random();
        int index = rand.nextInt(this.listQuestion.size());

        return this.listQuestion.get(index);
    }

    public void readJsonDataFile() throws IOException, JSONException {

        // read data on raw/data.json
        String data = readFile(this.context, this.resourceDataId);
        JSONArray jsonArrData = new JSONArray(data);

        for (int i = 0; i < jsonArrData.length(); i++) {
            JSONObject jsonData = jsonArrData.getJSONObject(i);

            int id = jsonData.getInt("id");
            String content = jsonData.getString("content");

            JSONArray arrAnswer = jsonData.getJSONArray("answers");
            ArrayList<String> answers = new ArrayList<>();

            for (int k = 0; k < arrAnswer.length(); k++) {
                answers.add(arrAnswer.getString(k));
            }

            String result = jsonData.getString("result");
            Question question = new Question();
            question.setData(content, result, answers);

            this.listQuestion.add(question);
        }
    }

    private String readFile(Context context, int resId) throws IOException {
        InputStream in = context.getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }
}
