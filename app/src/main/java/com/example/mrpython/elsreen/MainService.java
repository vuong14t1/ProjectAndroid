package com.example.mrpython.elsreen;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by NhanIT on 3/8/2018.
 */

public class MainService extends Service {
    public BroadcastReceiver lockScreenReceiver = null;
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        lockScreenReceiver = new StartMyServiceReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, lockFilter);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(lockScreenReceiver);
        super.onDestroy();
    }
}
