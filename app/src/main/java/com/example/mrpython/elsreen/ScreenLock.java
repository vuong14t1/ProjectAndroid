package com.example.mrpython.elsreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrpython.elsreen.module.game.Data.GameBase;
import com.example.mrpython.elsreen.module.game.Data.Player;
import com.example.mrpython.elsreen.module.game.Data.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScreenLock extends AppCompatActivity {
    // GUI
    private TextView txtName, txtLevel, txtQuestion;
    private RadioGroup rdGroup;
    private RadioButton rdAnswerA, rdAnswerB, rdAnswerC, rdAnswerD;
    private Button btnUnLock;

    // Logic
    GameBase gameBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);
        startService(new Intent(this, MainService.class));
        this.addControls();
        this.addEvents();
        this.readData();
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
        this.rdGroup = findViewById(R.id.rdGroup);
        this.rdAnswerA = findViewById(R.id.rdAnswerA);
        this.rdAnswerB = findViewById(R.id.rdAnswerB);
        this.rdAnswerC = findViewById(R.id.rdAnswerC);
        this.rdAnswerD = findViewById(R.id.rdAnswerD);

        this.btnUnLock = findViewById(R.id.btnUnLock);
        //
        this.gameBase = new GameBase();
        this.updateGUI();
    }
    public void updateGUI(){
        this.updateInfo();
        this.updateQuestion();
    }
    public void updateQuestion(){
        Question qs = this.gameBase.getQuestion();
        qs.shuffleAnswer();
        this.txtQuestion.setText(String.valueOf(qs.getQuestion()));
        this.rdAnswerA.setText(String.valueOf(qs.getListAnswer().get(0)));
        this.rdAnswerB.setText(String.valueOf(qs.getListAnswer().get(1)));
        this.rdAnswerC.setText(String.valueOf(qs.getListAnswer().get(2)));
        this.rdAnswerD.setText(String.valueOf(qs.getListAnswer().get(3)));
    }
    public void updateInfo(){
        Player pl = this.gameBase.getPlayer();
        this.txtName.setText(String.valueOf(pl.getName()));
        this.txtLevel.setText(String.valueOf("Level : " + pl.getLevel()));
    }
    public void addEvents(){
        this.btnUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processCheck();
            }
        });
    }
    public void processCheck(){
        int idCheckGroup = rdGroup.getCheckedRadioButtonId();
        String result = "";
        switch (idCheckGroup){
            case R.id.rdAnswerA:
                result = rdAnswerA.getText().toString();
                break;
            case R.id.rdAnswerB:
                result = rdAnswerB.getText().toString();
                break;
            case R.id.rdAnswerC:
                result = rdAnswerC.getText().toString();
                break;
            case R.id.rdAnswerD:
                result = rdAnswerD.getText().toString();
                break;
        }
        if(this.gameBase.getQuestion().isResult(result)){
            finish();
        }else{
            Toast.makeText(this, "Ban da tra loi sai", Toast.LENGTH_SHORT).show();
        }
    }
}
