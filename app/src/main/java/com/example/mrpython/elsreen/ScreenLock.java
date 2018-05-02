package com.example.mrpython.elsreen;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrpython.elsreen.module.game.Data.GameBase;
import com.example.mrpython.elsreen.module.game.Data.Player;
import com.example.mrpython.elsreen.module.game.Data.Question;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ScreenLock extends AppCompatActivity implements View.OnClickListener {
    // GUI
    private TextView txtName, txtLevel, txtQuestion;
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
    private LinearLayout linBackground;
    private Random random;
    private Resources res;
    private TypedArray listBackgrounds;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef ;
    // Logic
    private GameBase gameBase;
    private Question currentQuestion;
    private int numberOfTrueAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);
        startService(new Intent(this, MainService.class));
        this.changeBackground();
        this.initFirebase();
        this.addControls();
        this.addEvents();
        this.readData();
    }
    public void initFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("TopPlayer");
        /*String userId = "234";
        Player p = new Player(this);
        p.setData(userId,"Vuong", 123,1,2);
        Map newUserData = new HashMap();
        newUserData.put("scores", 4444);
        myRef.child("234").updateChildren(newUserData);*/

    }

    public void changeBackground() {
        linBackground = (LinearLayout) findViewById(R.id.screenWrapper);
        random = new Random();
        res = getResources();
        listBackgrounds = res.obtainTypedArray(R.array.listBackgrounds);

        int randomInt = random.nextInt(listBackgrounds.length());
        int drawableID = listBackgrounds.getResourceId(randomInt, 1);
//        linBackground.setBackgroundResource(drawableID);
    }

    //Paste this code to lockscreen
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME)
        {
            Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
        }

        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
        }

        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
    public void readData(){
        String data;
        try{
            InputStream in= openFileInput("F:\\test\\data.txt");
            InputStreamReader inreader=new InputStreamReader(in);
            BufferedReader bufreader=new BufferedReader(inreader);
            StringBuilder builder=new StringBuilder();
            if(in != null)
            {
                try
                {
                    while((data=bufreader.readLine())!=null)
                    {
                        builder.append(data);
                        builder.append("\n");
                    }
                    in.close();
                    Toast.makeText(this, builder.toString(), Toast.LENGTH_SHORT).show();
                }
                catch(IOException ex){
                    Log.e("ERROR", ex.getMessage());
                }
            }
        }catch(Exception ex){

        }

    }
    public void addControls(){
        this.txtName = (TextView) findViewById(R.id.txtName);
        this.txtLevel = findViewById(R.id.txtLevel);
        this.txtQuestion = findViewById(R.id.txtQuestion);
//        this.rdGroup = findViewById(R.id.rdGroup);
        this.btnAnswerA = findViewById(R.id.btnAnswerA);
        this.btnAnswerB = findViewById(R.id.btnAnswerB);
        this.btnAnswerC = findViewById(R.id.btnAnswerC);
        this.btnAnswerD = findViewById(R.id.btnAnswerD);

        this.gameBase =  GameBase.getGameBase(this, 0);
        this.updateGUI();
    }
    public void updateGUI(){
        this.updateInfo();
        this.updateQuestion();
    }
    public void updateQuestion(){
        this.currentQuestion = this.gameBase.getRandomQuestion(2);
        this.currentQuestion.shuffleAnswer();
        this.txtQuestion.setText(String.valueOf(this.currentQuestion.getQuestion()));
        this.btnAnswerA.setText(String.valueOf(this.currentQuestion.getListAnswer().get(0)));
        this.btnAnswerB.setText(String.valueOf(this.currentQuestion.getListAnswer().get(1)));
        this.btnAnswerC.setText(String.valueOf(this.currentQuestion.getListAnswer().get(2)));
        this.btnAnswerD.setText(String.valueOf(this.currentQuestion.getListAnswer().get(3)));
    }
    public void updateInfo(){
        Player pl = this.gameBase.getPlayer();
        this.txtName.setText(String.valueOf(pl.getName()));
        this.txtLevel.setText(String.valueOf("Level : " + pl.getLevel()));
    }
    public void addEvents(){
        this.btnAnswerA.setOnClickListener(this);
        this.btnAnswerB.setOnClickListener(this);
        this.btnAnswerC.setOnClickListener(this);
        this.btnAnswerD.setOnClickListener(this);
    }
    public void processCheck(int id){
        String result = "";
        switch (id){
            case R.id.btnAnswerA:
                result = btnAnswerA.getText().toString();
                break;
            case R.id.btnAnswerB:
                result = btnAnswerB.getText().toString();
                break;
            case R.id.btnAnswerC:
                result = btnAnswerC.getText().toString();
                break;
            case R.id.btnAnswerD:
                result = btnAnswerD.getText().toString();
                break;
        }
        if(this.currentQuestion.isResult(result)){
            numberOfTrueAnswers ++;
            gameBase.expUp(numberOfTrueAnswers);
            sendCorrectAnswers();
            finish();
        }else{
            numberOfTrueAnswers = 0;
            Toast.makeText(this, "Ban da tra loi sai", Toast.LENGTH_SHORT).show();
            updateQuestion();
        }
    }
    //update info len firebase moi khi tra loi dung
    public void sendCorrectAnswers(){
        Map newUserData = new HashMap();
        newUserData.put("scores", gameBase.getPlayer().getScores());
        newUserData.put("exp", gameBase.getPlayer().getCurExp());
        newUserData.put("level", gameBase.getPlayer().getLevel());
        myRef.child(gameBase.getPlayer().getId()).updateChildren(newUserData);
    }
    @Override
    public void onClick(View view) {
        processCheck(view.getId());
    }
}
