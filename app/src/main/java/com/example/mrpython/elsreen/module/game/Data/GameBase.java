package com.example.mrpython.elsreen.module.game.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

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
    private ArrayList<Question> listQuestionGrammar;
    private ArrayList<Question> listQuestionVocabulary;
    private Context context;
    private int resourceGrammarId = R.raw.grammar;
    private int resourceVocabularyId = R.raw.vocabulary;
    private static GameBase gameBase;
    private int learningMode;
    public static int numOfSOS;

    public GameBase(Context context) {
        this.setContext(context);
        this.player = new Player(context);
        this.listQuestionGrammar = new ArrayList<>();
        this.listQuestionVocabulary = new ArrayList<>();
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

    public void setLearningMode(int mode) {
        this.learningMode = mode;

        SharedPreferences sharedPreferences = context.getSharedPreferences("learningMode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("mode", this.learningMode);
        editor.apply();
    }

    public int getLearningMode() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("learningMode", Context.MODE_PRIVATE);
        this.learningMode = sharedPreferences.getInt("mode", 0);
        return this.learningMode;
    }

    public void setData() {
        this.player.loadData();
        try {
            this.readJsonDataFile(this.listQuestionVocabulary, this.resourceVocabularyId);
            this.readJsonDataFile(this.listQuestionGrammar, this.resourceGrammarId);
        } catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
    }
    //region GET
    public Player getPlayer() {
        return this.player;
    }

    public Question getRandomQuestion(int mode) {
        Random rand;
        int index;

        switch (mode) {
            case 0:
                rand =  new Random();
                int randomMode = rand.nextInt(2) + 1;
                return this.getRandomQuestion(randomMode);
            case 1:
                rand =  new Random();
                index = rand.nextInt(this.listQuestionVocabulary.size());
                return this.listQuestionVocabulary.get(index);
            case 2:
                rand =  new Random();
                index = rand.nextInt(this.listQuestionGrammar.size());
                return this.listQuestionGrammar.get(index);
            default:
                return this.getRandomQuestion(this.learningMode);
        }
    }

    public void readJsonDataFile(ArrayList<Question> listQuestion, int dataId) throws IOException, JSONException {

        // read data on raw/data.json
        String data = this.readFile(this.context, dataId);
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

            listQuestion.add(question);
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
        {
            point = 4;
        }
        else
        {
            numOfSOS++;
            player.saveNumOfSOS();

            if (numberOfTrueAnswers < 5)
            {
                point = 2;

            }
            else
                point = 3;
        }

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
