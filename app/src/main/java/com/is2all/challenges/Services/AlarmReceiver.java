package com.is2all.challenges.Services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("ShowToast")
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"received",Toast.LENGTH_SHORT).show();
    }
}
