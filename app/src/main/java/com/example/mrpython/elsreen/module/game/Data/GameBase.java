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
    private static GameBase gameBase;

    public GameBase(Context context) {
        this.setContext(context);
        this.player = new Player(context);
        listQuestion = new ArrayList<>();
        this.setData();
    }

    public static GameBase getGameBase(Context context)
    {
        if (gameBase  == null)
            gameBase = new GameBase(context);
        else
            gameBase.setContext(context);
        return gameBase;
    }

    public void setContext(Context context) {
        this.context = context;
//        this.player.setContext(context);
    }

    public void setData() {
        this.player.loadData();
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

    //Tăng kinh nghiệm, trả lời đúng 1 câu được tăng 1 điểm kinh nghiệm
    //Trả lời đúng 3 câu liên tiếp tăng 2 điểm kinh nghiệm.
    //Trả lời đúng 5 câu liên tiếp tăng 3 điểm kinh nghiệm
    //Hiện đang để tăng 4 điểm để test
    public void expUp(int numberOfTrueAnswers)
    {
        int point;
        if (numberOfTrueAnswers < 3)
            point = 4;
        else
            if (numberOfTrueAnswers < 5)
                point = 2;
            else
                point = 3;
        player.setCurExp(player.getCurExp() + point);

        checkLevelUp();
    }

    private void checkLevelUp()
    {
        if (player.getCurExp() > 9)
        {
            player.setLevel(player.getLevel() + 1);
            player.setCurExp(player.getCurExp()%10);
            player.saveData();
        }
    }
}
