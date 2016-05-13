package com.example.yasmin.myapplication.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BCastReceiver extends BroadcastReceiver {
    public BCastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARME", "Alarm executed at: " + new java.util.Date());
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
