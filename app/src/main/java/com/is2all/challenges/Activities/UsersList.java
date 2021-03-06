package com.is2all.challenges.Activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.Helper.ViewMode;
import com.is2all.challenges.OnInviteListener;
import com.is2all.challenges.R;
import com.is2all.challenges.adapters.UserAdapter;
import com.is2all.challenges.Dialogs.DialogVPN;
import com.is2all.challenges.models.User;

import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UsersList extends AppCompatActivity implements View.OnClickListener, OnInviteListener, SwipeRefreshLayout.OnRefreshListener {
    private static ShimmerRecyclerView mRvList;
    private UserAdapter adapter;
    private Toolbar toolbar;
    private View mData;
    private ProgressBar mProgress;
    private Button mBtnRetry;
    private TextView mTvError;
    private ImageView mIvError;
    private View mErrorHolder;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DialogVPN dialogVPN;
    private Activity activity;

    private Boolean firstEnter = true;
    private Boolean isNeedVPN = true;

    private ArrayList<User> users = new ArrayList<>();

    public static final String FIREBASE_CLOUD_FUNCTIONS_BASE = "https://us-central1-tictactoe-64902.cloudfunctions.net";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_out_down);
        loadData();
    }


    public void init() {
        activity = this;
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.users);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        mRvList = findViewById(R.id.rv_list);

        mData = findViewById(R.id.v_data);
        mProgress = findViewById(R.id.progress_bar);
        mErrorHolder = findViewById(R.id.root_error);
        mTvError = findViewById(R.id.tv_error);
        mBtnRetry = findViewById(R.id.btn_retry);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        mIvError = findViewById(R.id.iv_error);
        swipeRefreshLayout.setOnRefreshListener(this);
        mBtnRetry.setOnClickListener(this);
        checkProxy();

    }

    public void loadData() {
        mRvList.showShimmerAdapter();

        isNeedVPN = true;
        //showView(ViewMode.PROGRESS);
        if (isNetworkConnected()) {
            getUsers();
        }
        else {
            isNeedVPN = false;
            showView(ViewMode.NO_CONNICTION);
        }

    }

    public void showData(ArrayList<User> users) {
        adapter = new UserAdapter(this, users, this);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.setAdapter(adapter);
//        RecyclerViewAnimation.runLayoutAnimation(mRvList);
        swipeRefreshLayout.setRefreshing(false);
        isNeedVPN = false;
        showView(ViewMode.DATA);
    }


    public void getUsers() {
        users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 1;
                        Log.d("getUsers_", dataSnapshot.toString());

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.d("List_", "id: " + user.getId() + " , name: " + user.getName() + " , email: " + user.getEmail());
                            users.add(user);
                        }
                        int MILLES = (firstEnter) ? 1500 : 0;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isNeedVPN = false;
                                showData(users);

                            }
                        }, MILLES);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("getUsers_", databaseError.toString());
                        showView(ViewMode.NO_CONNICTION);

                    }

                    @Override
                    protected void finalize() throws Throwable {
                        super.finalize();
                        Log.d("getUsers_", "finalize");

                    }

                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showView(int viewMode) {
        switch (viewMode) {
            case ViewMode.DATA:
                mData.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);

                break;
            case ViewMode.PROGRESS:
                mProgress.setVisibility(View.VISIBLE);
                mData.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);


                break;
            case ViewMode.NO_CONNICTION:
                mProgress.setVisibility(View.GONE);
                mData.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);


                break;
            case ViewMode.NO_PROXY:
                mProgress.setVisibility(View.GONE);
                mData.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);


                break;
        }
    }

    @Override
    public void onUserInviteListner(final User user) {
//        Toast.makeText(this, "Fdsf: " + user.getName() + " , " + user.getEmail(), Toast.LENGTH_SHORT).show();
//        Toasty.normal(this, "Coming soon").show();
        Toasty.custom(this, R.string.soon, getResources().getDrawable(R.drawable.ic_wait), getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.white), Toasty.LENGTH_SHORT, true,true).show();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("users")
                .child(Utils.getCurrentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User me = dataSnapshot.getValue(User.class);

                        OkHttpClient client = new OkHttpClient();

                        String to = user.getPushId();

                        Request request = new Request.Builder()
                                .url(String
                                        .format("%s/sendNotification?to=%s&fromPushId=%s&fromId=%s&fromName=%s&type=%s",
                                                FIREBASE_CLOUD_FUNCTIONS_BASE,
                                                to,
                                                me.getPushId(),
                                                Utils.getCurrentUserId(),
                                                me.getName(),
                                                "invite"))
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("resultt_", "failure: " + e.getMessage());

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.d("resultt_", "response: " + response.message());
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("resultt_", "error: " + databaseError.getMessage());

                    }
                });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_retry:
                loadData();
                break;
        }
    }

    public void checkProxy() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNeedVPN) {
                    showView(ViewMode.NO_PROXY);
                    mTvError.setText(getString(R.string.need_vpn));
                    mIvError.setImageResource(R.drawable.ic_vpn);
                    dialogVPN = new DialogVPN(activity);
                    try {

                    dialogVPN.show(getSupportFragmentManager(), "");
                    }
                    catch (Exception e){}
                }

            }
        }, 10000);
    }
}
