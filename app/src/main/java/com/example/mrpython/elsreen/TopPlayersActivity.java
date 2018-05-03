package com.example.mrpython.elsreen;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mrpython.elsreen.module.game.Data.Player;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopPlayersActivity extends AppCompatActivity {
    private ListView lvTopPlayers;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mData;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef ;
    private Button btnLearningMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);
        this.addControls();
        initFirebase();
        handleGetDataPlayer();
    }
    public void addControls(){
        lvTopPlayers = findViewById(R.id.lvTopPlayers);
        mData = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData);
        lvTopPlayers.setAdapter(mAdapter);
    }
    public void initFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("TopPlayer");
    }

    public void handleGetDataPlayer(){
        Query queryRef = myRef.orderByChild("level").limitToLast(5);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class);
                String content = "Name : " + p.getName() + "\n" + "Level : " + p.getLevel();
                mData.add(0,content);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
