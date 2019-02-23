package com.is2all.challenges.Activities;

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
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.is2all.challenges.Helper.databaseClass;
import com.is2all.challenges.R;
import com.is2all.challenges.models.item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class windows_asila extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences, sharedPreferencesUser;
    private List<item> mDataList;
    private databaseClass mdata;
    private int Points = 0;
    private TextView mBtnFistAnswer, mBtnSecondAnswer, mBtnThirdAnswer, mBtnFourthAnswer, btn6, btn7, btnTimer;
    private TextView mTvPoints;
    private ImageView mIvHintOne, mIvHintTwo, mIvHintThree, mIvHintFour, mIvHint;
    private View mVBacks;
    String msgend;

    TextView txtFalse, txtTrue, mTvQuestion;

    static Random rand = new Random(); // static li 3adam tikrar ra9m
    int rnd, id, sizeData, count = 20, correctAnswer;
    private boolean sound;
    private boolean firstQuestion = true;
    Handler handler = new Handler();
    MediaPlayer media_false, media_true;
    ImageView mIvSound;

    private int tempCount = 0;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windows_asila);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SwitchBackground();
        init();
//        startAnimation(1000);


        sharedPreferences = getSharedPreferences("SOUND", MODE_PRIVATE);
        sound = sharedPreferences.getBoolean("sound", true);

        if (sound)
            mIvSound.setImageResource(R.drawable.ic_sound);
        else
            mIvSound.setImageResource(R.drawable.ic_mute);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void init() {
        sharedPreferencesUser = getSharedPreferences("userInfo", MODE_PRIVATE);

        Points = sharedPreferencesUser.getInt("points", 10);

        mTvPoints = findViewById(R.id.tv_points);
        mTvQuestion = findViewById(R.id.tv_question);
        mBtnFistAnswer = findViewById(R.id.btn_first_answer);
        mBtnSecondAnswer = findViewById(R.id.btn_second_answer);
        mBtnThirdAnswer = findViewById(R.id.btn_third_answer);
        mBtnFourthAnswer = findViewById(R.id.btn_fourth_answer);



        mIvHintOne = findViewById(R.id.iv_hint_1);
        mIvHintTwo = findViewById(R.id.iv_hint_2);
        mIvHintThree = findViewById(R.id.iv_hint_3);
        mIvHintFour = findViewById(R.id.iv_hint_4);

        mIvHint = findViewById(R.id.iv_hint);

        mVBacks = findViewById(R.id.v_back);
//        Toast.makeText(this, "po: " + Points, Toast.LENGTH_SHORT).show();
        if (Points < 2)
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_off));
        else
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_on));

        mIvHint.setOnClickListener(this);

        mTvPoints.setText(String.valueOf(Points));

        mBtnFistAnswer.setOnClickListener(this);
        mBtnSecondAnswer.setOnClickListener(this);
        mBtnThirdAnswer.setOnClickListener(this);
        mBtnFourthAnswer.setOnClickListener(this);

        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btnTimer = findViewById(R.id.btnTimer);

        txtFalse = findViewById(R.id.txtFalse);
        txtTrue = findViewById(R.id.txtTrue);


        media_true = MediaPlayer.create(this, R.raw.sound_true);
        media_false = MediaPlayer.create(this, R.raw.sound_false_2);


        mIvSound = findViewById(R.id.iv_sound);
        mIvSound.setOnClickListener(this);

        mdata = new databaseClass(this);

        Bundle b = getIntent().getExtras();
        boolean rtn = b.getBoolean("rtn");

        File database = getApplicationContext().getDatabasePath(databaseClass.DBNAME);
        if (false == database.exists()) {
            mdata.getReadableDatabase();
            //Copy db
            if (copyDatabase(this)) {
//                Toast.makeText(this, "نسخ قاعدة البيانات", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "تعدر نسخ قاعدة البيانات", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mDataList = mdata.getListProduct();
        sizeData = mDataList.size();

        if (rtn) {
            clearState();
        } else {
            loadState();
        }

        timer();

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
            btnTimer.setTextColor(getResources().getColor(R.color.colorPrimary));
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void table() {
        Points = sharedPreferencesUser.getInt("points", 10);
        if (Points < 2)
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_off));
        else
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_on));

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
        mIvHint.setEnabled(true);

        correctAnswer = mDataList.get(rnd).ID_answer;
        if(!firstQuestion) {
            startAnimation(0);
        }
        firstQuestion = false;

        timer();

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
        sound = sharedPreferences.getBoolean("sound", true);
        Points = sharedPreferencesUser.getInt("points", 10);
        mTvPoints.setText(String.valueOf(Points + 2));

        SharedPreferences.Editor editor = sharedPreferencesUser.edit();
        editor.putInt("points", Points + 2);
        editor.commit();

        if (Points < 2)
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_off));
        else
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_on));

        if (sound) {
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
        sound = sharedPreferences.getBoolean("sound", true);
        Points = sharedPreferencesUser.getInt("points", 10);

        SharedPreferences.Editor editor = sharedPreferencesUser.edit();
        if (Points >= 2) {
            mTvPoints.setText(String.valueOf(Points - 2));
            editor.putInt("points", Points - 2);
            editor.commit();
        }
        Points = sharedPreferencesUser.getInt("points", 10);
        if (Points < 2)
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_off));
        else
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_on));


        if (sound) {
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
        Points = 10;
        SharedPreferences.Editor editor1 = sharedPreferencesUser.edit();
        editor1.putInt("points", Points);
        mTvPoints.setText(String.valueOf(Points));

        editor1.commit();

        SharedPreferences savechange = this.getSharedPreferences("savechange", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savechange.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();
        table();
/////////////////////// استرجاع النقط المضافة /////////////////////
        SharedPreferences savechange = this.getSharedPreferences("savechange", Context.MODE_PRIVATE);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.iv_sound && id != R.id.iv_hint) {
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
            mIvHint.setEnabled(false);
        }

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

            case R.id.iv_sound:
                sound = sharedPreferences.getBoolean("sound", true);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sound", !sound);
                editor.apply();
                if (sound)
                    mIvSound.setImageResource(R.drawable.ic_mute);
                else
                    mIvSound.setImageResource(R.drawable.ic_sound);
                break;
            case R.id.iv_hint:
                showHint(correctAnswer);
                break;
        }
    }

    boolean b = false;

    @Override
    public void onBackPressed() {
        if (b) {
            b = false;
            super.onBackPressed();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.exit_con));
            dialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    b = true;
                    onBackPressed();
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), null);
            dialog.create().show();
        }


    }

    public void showHint(int correctAnswer) throws IndexOutOfBoundsException {
        if (Points < 2)
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_off));
        else
            mIvHint.setBackground(getResources().getDrawable(R.drawable.ic_hint_on));

        Points = sharedPreferencesUser.getInt("points", 10);
        final int index = correctAnswer - 1;
        if (index < 0 || index > 3)
            throw new IndexOutOfBoundsException();
        else {
            if (Points >= 2) {
                final ImageView[] hints = new ImageView[]{mIvHintOne, mIvHintTwo, mIvHintThree, mIvHintFour};
                hints[index].setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                hints[index].startAnimation(animation);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hints[index].setVisibility(View.GONE);
                    }
                }, 1000);
                SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                editor.putInt("points", Points - 2);
                editor.commit();
                mTvPoints.setText(String.valueOf(Points - 2));

            } else
                Toasty.custom(this, getResources().getString(R.string.no_enogh_point), getResources().getDrawable(R.drawable.ic_points), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.white), Toasty.LENGTH_SHORT, true, true).show();
        }
    }

    public void startAnimation(long duration) {
        final Animation slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        final Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.scale);
        slideUp.setDuration(500);
        mBtnFistAnswer.setVisibility(View.GONE);
        mBtnSecondAnswer.setVisibility(View.GONE);
        mBtnThirdAnswer.setVisibility(View.GONE);
        mBtnFourthAnswer.setVisibility(View.GONE);

        mTvQuestion.setVisibility(View.GONE);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtnFistAnswer.setVisibility(View.VISIBLE);
                mBtnSecondAnswer.setVisibility(View.VISIBLE);
                mBtnThirdAnswer.setVisibility(View.VISIBLE);
                mBtnFourthAnswer.setVisibility(View.VISIBLE);

                mTvQuestion.setVisibility(View.VISIBLE);

                mBtnFistAnswer.startAnimation(slideRight);
                mBtnSecondAnswer.startAnimation(slideLeft);
                mBtnThirdAnswer.startAnimation(slideRight);
                mBtnFourthAnswer.startAnimation(slideLeft);

                mTvQuestion.startAnimation(slideUp);
            }
        }, duration);

    }

    public void SwitchBackground(){
        mVBacks = findViewById(R.id.v_back);
        final int []backs = new int[]{R.drawable.background,
                R.drawable.background1,
                R.drawable.background2,
                R.drawable.background3,
                R.drawable.background4,
                R.drawable.background5,
                R.drawable.background6,
                R.drawable.background7,};
        final android.os.Handler handler = new android.os.Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("TIMER_","SDfssdfsd");
                if(tempCount >= backs.length)
                    tempCount = 0;
                mVBacks.setBackgroundResource(backs[tempCount]);
                tempCount++;

            }
        };
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);

            }
        },100,12000);

    }
}
