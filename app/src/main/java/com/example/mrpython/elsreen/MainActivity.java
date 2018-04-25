package com.example.mrpython.elsreen;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrpython.elsreen.module.game.Data.GameBase;
import com.example.mrpython.elsreen.module.game.Data.Player;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    Button btnStartLockScreen;
    TextView tvName, tvLevel;
    EditText txtInputName;
    LinearLayout lnGetName;
    GameBase gameBase;
    public BroadcastReceiver lockScreenReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignView();
        loadData();
//        _registerBroadCast();
//        startService(new Intent(this, MainService.class));
    }
    private void _registerBroadCast(){
        lockScreenReceiver = new StartMyServiceReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, lockFilter);
    }

    private void assignView()
    {
        btnStartLockScreen = (Button)findViewById(R.id.btnStartLockScreen);
        tvName = (TextView)findViewById(R.id.tvName);
        tvLevel = (TextView)findViewById(R.id.tvLevel);
        gameBase = GameBase.getGameBase(this);
        txtInputName = (EditText)findViewById(R.id.txtInputName);
        lnGetName = (LinearLayout)findViewById(R.id.lnGetName);
    }

    private void loadData()
    {
        Player player = gameBase.getPlayer();
        player.loadData();

        if (player.getName().equals(""))
        {
            lnGetName.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.INVISIBLE);
            btnStartLockScreen.setVisibility(View.GONE);
        }
        else
        {
            tvName.setText(player.getName());
            tvLevel.setText(player.getLevel() + "");
        }
    }




    //Paste this code to lockscreen
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_HOME)
//        {
////            Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
//        }
//
//        if(keyCode==KeyEvent.KEYCODE_BACK)
//        {
////            Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_MENU)
//        {
////            Toast.makeText(getApplicationContext(), "Menu", Toast.LENGTH_SHORT).show();
//        }
//
//        return false;
//    }

    public void toggleLockScreen(View view) {
        if (btnStartLockScreen.getText().toString().equals("Start"))
        {
            gameBase.getPlayer().saveData();
            btnStartLockScreen.setText("Stop");
            startService(new Intent(MainActivity.this, MainService.class));
            System.exit(0);
        }
        else
        {
            //disable lockscreen
        }

    }

    public void setName(View view) {
        String name = txtInputName.getText().toString();
        if (!name.isEmpty())
        {
            gameBase.getPlayer().setName(name);
            tvName.setText(name);
            lnGetName.setVisibility(View.INVISIBLE);
            tvName.setVisibility(View.VISIBLE);
            btnStartLockScreen.setVisibility(View.VISIBLE);
        }
    }
}

