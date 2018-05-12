package com.example.mrpython.elsreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.example.mrpython.elsreen.module.game.Data.GameBase;
import com.example.mrpython.elsreen.module.game.Data.Player;
import com.example.mrpython.elsreen.module.game.Data.Question;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ScreenLock extends AppCompatActivity implements View.OnClickListener {
    // GUI
    private TextView txtName, txtLevel, txtQuestion, txtSOS;
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
    private SwipeButton enableButton;
    private LinearLayout linBackground;
    private Random random;
    private Resources res;
    private TypedArray listBackgrounds;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef ;
    // Logic
    private GameBase gameBase;
    private Player player;
    private Question currentQuestion;
    private static int numberOfTrueAnswers = 0;
    boolean isPressButton = false;  //Button khi show dialog Giải thích

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
        linBackground.setBackgroundResource(drawableID);
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
        this.txtSOS = findViewById(R.id.txtSOS);
//        this.rdGroup = findViewById(R.id.rdGroup);
        this.btnAnswerA = findViewById(R.id.btnAnswerA);
        this.btnAnswerB = findViewById(R.id.btnAnswerB);
        this.btnAnswerC = findViewById(R.id.btnAnswerC);
        this.btnAnswerD = findViewById(R.id.btnAnswerD);
        this.enableButton = (SwipeButton) findViewById(R.id.btnSwiping);

        enableButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                onSOSClick();
            }
        });

        this.gameBase =  GameBase.getGameBase(this);
        player = gameBase.getPlayer();
        this.updateGUI();
    }

    public void updateGUI(){
        this.updateInfo();
        this.updateQuestion();
        player.loadNumOfSOS();
    }

    public void updateQuestion(){
        this.currentQuestion = this.gameBase.getRandomQuestion(this.gameBase.getLearningMode());
        this.currentQuestion.shuffleAnswer();
        this.txtQuestion.setText(String.valueOf(this.currentQuestion.getQuestion()));
        this.btnAnswerA.setBackgroundResource(R.drawable.button_custom);
        this.btnAnswerA.setText(String.valueOf(this.currentQuestion.getListAnswer().get(0)));
        this.btnAnswerB.setBackgroundResource(R.drawable.button_custom);
        this.btnAnswerB.setText(String.valueOf(this.currentQuestion.getListAnswer().get(1)));
        this.btnAnswerC.setBackgroundResource(R.drawable.button_custom);
        this.btnAnswerC.setText(String.valueOf(this.currentQuestion.getListAnswer().get(2)));
        this.btnAnswerD.setBackgroundResource(R.drawable.button_custom);
        this.btnAnswerD.setText(String.valueOf(this.currentQuestion.getListAnswer().get(3)));
    }

    public void updateInfo(){
        this.txtName.setText(String.valueOf(player.getName()));
        this.txtLevel.setText(String.valueOf("Level : " + player.getLevel()));
        txtSOS.setText("SOS(" + GameBase.numOfSOS + ")");
    }

    public void addEvents(){
        this.btnAnswerA.setOnClickListener(this);
        this.btnAnswerB.setOnClickListener(this);
        this.btnAnswerC.setOnClickListener(this);
        this.btnAnswerD.setOnClickListener(this);
    }

    public void processCheck(int id){
        final Button btnResult = (Button) findViewById(id);
        String rs = btnResult.getText().toString();
        String answer = currentQuestion.getResult();

        if (answer.equals(btnAnswerA.getText().toString())) {
            btnAnswerA.setBackgroundResource(R.drawable.correct_button);
        } else if (answer.equals(btnAnswerB.getText().toString())) {
            btnAnswerB.setBackgroundResource(R.drawable.correct_button);
        }else if (answer.equals(btnAnswerC.getText().toString())) {
            btnAnswerC.setBackgroundResource(R.drawable.correct_button);
        } else {
            btnAnswerD.setBackgroundResource(R.drawable.correct_button);
        }

        if (!currentQuestion.isResult(rs)) {

            final Thread refreshGUI = new Thread() {
                @Override
                public void run() {
                    try {
                        ScreenLock.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                numberOfTrueAnswers = 0;

                            }
                        });
                    } catch (Exception e) {
                    }
                }
            };
            Thread refreshThread = new Thread() {
                @Override
                public void run() {
                    try {
                        ScreenLock.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnResult.setBackgroundResource(R.drawable.button_error);
                            }
                        });
                        Thread.sleep(2000);

                        refreshGUI.start();
                    } catch (InterruptedException e) {
                    }
                }
            };
            boolean check = true;
            refreshThread.start();
            showExplain(currentQuestion.getExplain());

        } else {
            numberOfTrueAnswers ++;
            gameBase.expUp(numberOfTrueAnswers);
            sendCorrectAnswers();
            finish();
        }
    }
    //update info len firebase moi khi tra loi dung
    public void sendCorrectAnswers(){
        Map newUserData = new HashMap();
        newUserData.put("scores", gameBase.getPlayer().getScores());
        newUserData.put("curExp", gameBase.getPlayer().getCurExp());
        newUserData.put("level", gameBase.getPlayer().getLevel());
        myRef.child(gameBase.getPlayer().getId()).updateChildren(newUserData);
    }
    @Override
    public void onClick(View view) {
        processCheck(view.getId());

    }

    public void onSOSClick() {
        if (GameBase.numOfSOS > 0)
        {
            GameBase.numOfSOS--;
            player.saveNumOfSOS();
            finish();
        }
    }

    public void showExplain(String explain){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Giải thích");
        builder.setMessage(explain);
        builder.setCancelable(true);
        builder.setNegativeButton("Đã hiểu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isPressButton = true;
                updateGUI();
                dialogInterface.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
//        wmlp.x = 100;   //x position
//        wmlp.y = -20;   //y position

        alertDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPressButton)
                    updateGUI();
                alertDialog.dismiss();
            }
        }, 7000);


    }
}
