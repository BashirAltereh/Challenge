package com.is2all.as2ila_jawab;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class windows_asila extends AppCompatActivity implements View.OnClickListener {

    private InterstitialAd mInterstitialAd;
    private List<item> mDataList;
    private databaseClass mdata;
    Button mBtnFistAnswer, mBtnSecondAnswer, mBtnThirdAnswer, mBtnFourthAnswer, btn6, btn7, btnTimer;
    String msgend;

    TextView txtFalse, txtTrue, mTvQuestion;

    static Random rand = new Random(); // static li 3adam tikrar ra9m
    int rnd, id, sizeData, count = 20, correctAnswer, point = 3;
    Handler handler = new Handler();
    MediaPlayer media_false, media_true;
    CheckBox box_vol;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windows_asila);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void init() {
        //////// Start // Ads Admob Interstitial ///////////////
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("code admob");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                timer();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        //////// End // Ads Admob Interstitial ///////////////

        mTvQuestion = findViewById(R.id.tv_question);
        mBtnFistAnswer = findViewById(R.id.btn_first_answer);
        mBtnSecondAnswer = findViewById(R.id.btn_second_answer);
        mBtnThirdAnswer = findViewById(R.id.btn_third_answer);
        mBtnFourthAnswer = findViewById(R.id.btn_fourth_answer);

        mBtnFistAnswer.setOnClickListener(this);
        mBtnSecondAnswer.setOnClickListener(this);
        mBtnThirdAnswer.setOnClickListener(this);
        mBtnFourthAnswer.setOnClickListener(this);

        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btnTimer = findViewById(R.id.btnTimer);

        txtFalse = findViewById(R.id.txtFalse);
        txtTrue =  findViewById(R.id.txtTrue);


        media_true = MediaPlayer.create(this, R.raw.sound_true);
        media_false = MediaPlayer.create(this, R.raw.sound_false_2);


        box_vol = findViewById(R.id.box_vol);

        mdata = new databaseClass(this);

        ///////////////////////"جلب قيم من زر return  "//////
        Bundle b = getIntent().getExtras();
        boolean rtn = b.getBoolean("rtn");

        //// نسخ قاعدة البينات الى البرنامج ////////////////
        File database = getApplicationContext().getDatabasePath(databaseClass.DBNAME);
        if (false == database.exists()) {
            mdata.getReadableDatabase();
            //Copy db
            if (copyDatabase(this)) {
                Toast.makeText(this, "نسخ قاعدة البيانات", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "تعدر نسخ قاعدة البيانات", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        ////// جلب معلومات قاعدة الببانات الى list //////////
        mDataList = mdata.getListProduct();
        sizeData = mDataList.size();

        if (rtn) {
            clearState();
        } else {
            loadState();
        }

        timer();
        if (mDataList.size() <= 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("لقد أنهيت جميع المراحل\n انتظر الاصدار القادم \n وإلا أعد المحاولة إذا كانت أكثر اجوبتك خاطئة");
            builder.setPositiveButton("إعادة المحاولة", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    restarteGame();
                }
            });
            builder.setNegativeButton("أرسل التطبيق", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    share();
                }
            });
            builder.show();

            btn6.setText(sizeData + "/" + sizeData);
            handler.removeCallbacks(run);
        } else {
            table();
            btn6.setText(txtTrue.getText().toString() + "/" + sizeData);
            btn7.setText("+نقط : " + point);
        }
    }

    //////////////Timer--------------------------
    Runnable run = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            timer();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void timer() {
        handler.postDelayed(run, 1000);
        btnTimer.setText(String.valueOf(count));
        if (count < 10)
            btnTimer.setTextColor(getResources().getColor(R.color.red));
        else
            btnTimer.setTextColor(getResources().getColor(R.color.black));
        count--;
        if (mDataList.size() <= 1) {
            handler.removeCallbacks(run);
        } else {
            if (count == 0) {
                table();
                count = 20;
            }
        }
    }

    //------------timer////////////////
    public void addpoint(View view) {
        MediaPlayer mediaaddpoint = MediaPlayer.create(this, R.raw.sound_click);
        mediaaddpoint.start();

        if (point == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("لم يتبقى أي نقط \n يمكنك إضافة نقط عن طريق زيارة موقعنا أو مشاركة البرنامج بالضغط على زر إضافة نقط");
            builder.setPositiveButton("إضافة نقط", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    addpoint();
                }
            });
            builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            handler.removeCallbacks(run);
            count = 20;
            point--;
            btn7.setText("+نقط : " + point);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("الجواب الصحيح : \n رقم: " + correctAnswer);
            builder.setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.show();
        }
    }

    public void addpoint() {
        Intent addPoin = new Intent(this, addPoint.class);
        startActivity(addPoin);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void table() {

        handler.removeCallbacks(run);
        count = 20;
        rnd = rand.nextInt(mDataList.size());
        id = mDataList.get(rnd).ID;
        mTvQuestion.setText(mDataList.get(rnd).Question);

        mBtnFistAnswer.setText(mDataList.get(rnd).Answer_1);
        mBtnSecondAnswer.setText(mDataList.get(rnd).Answer_2);
        mBtnThirdAnswer.setText(mDataList.get(rnd).Answer_3);
        mBtnFourthAnswer.setText(mDataList.get(rnd).Answer_4);

        mBtnFistAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_a));
        mBtnSecondAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_b));
        mBtnThirdAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_c));
        mBtnFourthAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_d));

        mBtnFistAnswer.setEnabled(true);
        mBtnSecondAnswer.setEnabled(true);
        mBtnThirdAnswer.setEnabled(true);
        mBtnFourthAnswer.setEnabled(true);

        correctAnswer = mDataList.get(rnd).ID_answer;
        timer();
        if (mInterstitialAd.isLoaded() & rnd == 2 || rnd == 4 || rnd == 8 || rnd == 16 || rnd == 32 || rnd == 64 || rnd == 128) {
            handler.removeCallbacks(run);
            mInterstitialAd.show();
        }
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(databaseClass.DBNAME);
            String outFileName = databaseClass.DBLOCATION + databaseClass.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity", "DB copied");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    ///////////"إذا كان الجواب صحيح طبق هذه الدالة"/////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void BtnTrue() {
//        Toast toast = Toast.makeText(this, "صـحيح", Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP, 0, 90);
//        toast.show();

        int m = Integer.valueOf(txtTrue.getText().toString()) + 1;
        txtTrue.setText("" + m);
        btn6.setText(m + "/" + sizeData);

        if (mDataList.size() <= 1) {
            msgEnd();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msgend +
                    "عدد الاجوبة الصحيحة: " + txtTrue.getText().toString() + "\n" +
                    "عدد الأجوبة الخاطئة: " + txtFalse.getText().toString());
            builder.setPositiveButton("إعادة المحاولة", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    restarteGame();
                }
            });
            builder.setNegativeButton("أرسل التطبيق", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    share();
                }
            });
            builder.show();
            handler.postDelayed(run, 1000);
        } else {
            saveState(); ////"حفظ التغييرات ////////
            mDataList.remove(rnd);
            table();
        }

    }

    ///////////////"إذا كان الجواب خطأ طبق هذه الدالة"//////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void BtnFalse() {
//        Toast toast = Toast.makeText(this, "خطـأ", Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP, 0, 90);
//        toast.show();


        int m = Integer.valueOf(txtFalse.getText().toString()) + 1;
        txtFalse.setText("" + m);

        if (mDataList.size() <= 1) {
            msgEnd();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("الجوب الصحيح هو: " + correctAnswer + "\n" + msgend +
                    "عدد الاجوبة الصحيحة: " + txtTrue.getText().toString() + "\n" +
                    "عدد الأجوبة الخاطئة: " + txtFalse.getText().toString());
            builder.setPositiveButton("إعادة المحاولة", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    restarteGame();
                }
            });
            builder.setNegativeButton("أرسل التطبيق", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    share();
                }
            });
            builder.show();
            handler.postDelayed(run, 1000);
        } else {
            id = -1; /////// هذا الرقم لكي لا يتم تخزين قيمة موجودة لان الجواب خطأ ////////
            saveState(); ////"حفظ التغييرات ////////
            table();
        }

    }

    public void trueNextQuestion() {
        if (!box_vol.isChecked()) {
            media_true.start();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                BtnTrue();
            }
        }, 2000);
    }

    public void falseNextQuestion() {
        if (!box_vol.isChecked()) {
            media_false.start();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                BtnFalse();
            }
        }, 2000);
    }

    //////////// دالة الرجوع للبداية بعد انتهاء المراحل //////////////
    public void restarteGame() {
        Intent mainactivity = new Intent(this, MainActivity.class);
        startActivity(mainactivity);
    }

    ///////////////////الرسالة التي تظهر بعض انتهاء المراحل /////////////////
    public void msgEnd() {
        int txttrue = Integer.valueOf(txtTrue.getText().toString());
        int txtfalse = Integer.valueOf(txtFalse.getText().toString());

        if (txttrue > txtfalse) {
            msgend = "رائع لقد أنهيت المراحل بشكل جيد. \n ";
        }
        if (txttrue <= txtfalse) {
            msgend = "كنت ضعيف في الإجابة.\n ";
        }
    }

    public void share() {
        Intent myintent = new Intent(Intent.ACTION_SEND);
        myintent.setType("text/plain");
        String body = "تطبيق نسألك وانت تجيب رائع  \n" + "\n" +
                "https://play.google.com/store/apps/com.is2all.as2ila_jawab";
        String sub = "تطبيق نسالك وانت تجيب \n";
        myintent.putExtra(Intent.EXTRA_SUBJECT, sub);
        myintent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(myintent, "مشاركة البرنامج"));
    }

    ////////////////////////// "حفظ التغييرات بالبرنامج" //////////////////////////////////
    public void saveState() {
        SharedPreferences savechange = this.getSharedPreferences("savechange", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savechange.edit();

        editor.putString("txtTrue", txtTrue.getText().toString());
        editor.putString("txtFalse", txtFalse.getText().toString());
        editor.putString("btn6", btn6.getText().toString());

        editor.putInt("Point", point);
        editor.putInt("list" + id, id);
        editor.apply();
    }

    ////////////////////////// "جلب التغييرات السابقة للبرنامج"//////////////////////////////////
    public void loadState() {
        SharedPreferences savechange = this.getSharedPreferences("savechange", Context.MODE_PRIVATE);

        String txttrue = savechange.getString("txtTrue", "0");
        txtTrue.setText(txttrue);
        String txtfalse = savechange.getString("txtFalse", "0");

        txtFalse.setText(txtfalse);
        String butn6 = savechange.getString("btn6", sizeData + "/" + sizeData);
        btn6.setText(butn6);

        int Point = savechange.getInt("Point", point);
        this.point = Point;

        int i = 0;
        int data = mDataList.size();
        while (i < data) {
            try {
                int ii = 0;
                while (ii < data) {
                    int x = mDataList.get(ii).ID;
                    int listvale = savechange.getInt("list" + x, -1);

                    if (listvale == x) {
                        mDataList.remove(ii);
                    }
                    ii++;
                }

            } catch (Exception e) {
            }
            i++;
        }
    }

    public void clearState() {
        SharedPreferences savechange = this.getSharedPreferences("savechange", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savechange.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();
        table();
/////////////////////// استرجاع النقط المضافة /////////////////////
        SharedPreferences savechange = this.getSharedPreferences("savechange", Context.MODE_PRIVATE);
        int Point = savechange.getInt("Point", point);
        this.point = Point;
        btn7.setText("+نقط : " + point);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (correctAnswer) {
            case 1:
                mBtnFistAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_a_true));
                break;
            case 2:
                mBtnSecondAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_b_true));
                break;
            case 3:
                mBtnThirdAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_c_true));
                break;
            case 4:
                mBtnFourthAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_d_true));
                break;
        }

        mBtnFistAnswer.setEnabled(false);
        mBtnSecondAnswer.setEnabled(false);
        mBtnThirdAnswer.setEnabled(false);
        mBtnFourthAnswer.setEnabled(false);

        int id = v.getId();
        switch (id) {
            case R.id.btn_first_answer:
                if (correctAnswer == 1) {
                    mBtnFistAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_true));
                    trueNextQuestion();
                } else {
                    mBtnFistAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_b_false));
                    falseNextQuestion();
                }
                break;
            case R.id.btn_second_answer:
                if (correctAnswer == 2) {
                    mBtnSecondAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_true));
                    trueNextQuestion();

                } else {
                    mBtnSecondAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_b_false));
                    falseNextQuestion();
                }
                break;

            case R.id.btn_third_answer:

                if (correctAnswer == 3) {
                    mBtnThirdAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_true));
                    trueNextQuestion();

                } else {
                    mBtnThirdAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_b_false));
                    falseNextQuestion();
                }

                break;

            case R.id.btn_fourth_answer:

                if (correctAnswer == 4) {
                    mBtnFourthAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_true));
                    trueNextQuestion();
                } else {
                    mBtnFourthAnswer.setBackground(getResources().getDrawable(R.drawable.change_btn_b_false));
                    falseNextQuestion();
                }

                break;

        }
    }
}
