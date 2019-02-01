package com.is2all.challenges;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        @SuppressLint("StringFormatInvalid") String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void start(View view) {
        MediaPlayer media1 = MediaPlayer.create(this, R.raw.sound_click);
        media1.start();

        Intent windows_asila = new Intent(this, windows_asila.class);
        windows_asila.putExtra("rtn", false);
        startActivity(windows_asila);
    }

    public void returen(View view) {
        MediaPlayer media2 = MediaPlayer.create(this, R.raw.sound_click);
        media2.start();

        Intent windows_asila = new Intent(this, windows_asila.class);
        windows_asila.putExtra("rtn", true);
        startActivity(windows_asila);
    }

    public void addpoint(View view) {
        MediaPlayer media3 = MediaPlayer.create(this, R.raw.sound_click);
        media3.start();

        Intent addPoin = new Intent(this, addPoint.class);
        startActivity(addPoin);
    }

    public void Share(View view) {
        MediaPlayer media4 = MediaPlayer.create(this, R.raw.sound_click);
        media4.start();

        Intent myintent = new Intent(Intent.ACTION_SEND);
        myintent.setType("text/plain");
        String body = "تطبيق نسألك وانت تجيب رائع  \n" + "\n" +
                "https://play.google.com/store/apps/com.is2all.as2ila_jawab";
        String sub = "تطبيق نسالك وانت تجيب \n";
        myintent.putExtra(Intent.EXTRA_SUBJECT, sub);
        myintent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(myintent, "مشاركة البرنامج"));
    }

}
