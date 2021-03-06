package com.example.mrpython.elsreen;


import android.app.Activity;
import android.app.AlertDialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.example.mrpython.elsreen.module.game.Data.GameBase;
import com.example.mrpython.elsreen.module.game.Data.Player;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button btnStartLockScreen;
    TextView tvName, tvLevel, tvMode;
    EditText txtInputName;
    LinearLayout lnGetName;
    GameBase gameBase;
    Player player;
    public BroadcastReceiver lockScreenReceiver = null;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef ;
    private Button btnLearningMode;
    private  SwipeButton btnSwiping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignView();
        loadData();
        this.initFirebase();
//        this.initSwiping();
        this.showLearningMode(this.gameBase.getLearningMode());

        btnLearningMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLearningMode.performLongClick();
            }
        });

        registerForContextMenu(btnLearningMode);
    }
    public void initSwiping(){
        btnSwiping =  (SwipeButton) findViewById(R.id.btnSwiping);
        btnSwiping.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                Log.d("abc", active + "" );
            }
        });

    }


    public void initFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("TopPlayer");

    }

    private void assignView()
    {
        btnStartLockScreen = (Button)findViewById(R.id.btnStartLockScreen);
        tvName = (TextView)findViewById(R.id.tvName);
        tvLevel = (TextView)findViewById(R.id.tvLevel);
        gameBase = GameBase.getGameBase(this);
        player = gameBase.getPlayer();
        txtInputName = (EditText)findViewById(R.id.txtInputName);
        lnGetName = (LinearLayout)findViewById(R.id.lnGetName);
        tvMode = (TextView)findViewById(R.id.tvMode);
        btnLearningMode = (Button) findViewById(R.id.btnLearningMode);
    }

    private void loadData()
    {
        player.loadData();

        if (player.getName().equals("Player"))
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

        if (!player.getServiceStatus())
            btnStartLockScreen.setText("Start");
        else
            btnStartLockScreen.setText("Stop");

    }

    public void toggleLockScreen(View view) {
        if (!player.getServiceStatus())     //service not started
        {
            player.setServiceStatus(true);
            player.saveData();

            btnStartLockScreen.setText("Stop");

            //luu info detal len firebase
            sendSaveInfoPlayer();
            startService(new Intent(MainActivity.this, MainService.class));
            System.exit(0);
        }
        else
        {
            showStopScreenDialog();
        }

    }

    private void showStopScreenDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disable lockscreen");
        builder.setMessage("Are you sure?");
        builder.setCancelable(true);
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                player.setServiceStatus(false);
                player.saveData();
                btnStartLockScreen.setText("Start");

                //ungesister service
                stopService(new Intent(getBaseContext(), MainService.class));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void sendSaveInfoPlayer(){
        myRef.child(gameBase.getPlayer().getId()).setValue(gameBase.getPlayer());
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setName(View view) {
        hideKeyboard(this);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_context_mode, menu);
        LayoutInflater headerInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup header = (ViewGroup) headerInflater.inflate(R.layout.title_mode_menu, null);
        menu.setHeaderView(header);

        // align center content item of menu
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString content = new SpannableString(item.getTitle());
            content.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, content.length(), 0);
            item.setTitle(content);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnAll:
                this.gameBase.setLearningMode(0);
                tvMode.setText("All");
                break;
            case R.id.mnVocabulary:
                this.gameBase.setLearningMode(1);
                tvMode.setText("Vocabulary");
                break;
            case R.id.mnGrammar:
                this.gameBase.setLearningMode(2);
                tvMode.setText("Grammar");
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void topPlayer(View view) {
        Intent intent = new Intent(MainActivity.this, TopPlayersActivity.class);
        startActivity(intent);
    }

    public void showLearningMode(int mode) {
        switch (mode) {
            case 0:
                tvMode.setText("All");
                break;
            case 1:
                tvMode.setText("Vocabulary");
                break;
            case 2:
                tvMode.setText("Grammar");
                break;
        }
    }
      
    public void shareImage(View view) {
        Bitmap bitmap = takeScreenshot();
        saveBitmap(bitmap);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareDialog.show(this,content);

    }
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }
    public void saveBitmap(Bitmap bitmap) {
        Random rand = new Random();
        String name = rand.nextInt(10000000) + "screenshot.png";
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/" + name);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "Saved image ", Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
}

