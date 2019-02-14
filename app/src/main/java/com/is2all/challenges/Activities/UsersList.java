package com.is2all.challenges.Activities;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.is2all.challenges.models.User;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UsersList extends AppCompatActivity implements OnInviteListener {
    private RecyclerView mRvList;
    private UserAdapter adapter;
    private Toolbar toolbar;
    private View mData;
    private ProgressBar mProgress;
    private Button mBtnRetry;
    private TextView mTvError;
    private View mErrorHolder;

    private ArrayList<User> users = new ArrayList<>();

    public static final String FIREBASE_CLOUD_FUNCTIONS_BASE = "https://us-central1-tictactoe-64902.cloudfunctions.net";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Users that you can play with");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_ATOP);
        mRvList = findViewById(R.id.rv_list);
        mData = findViewById(R.id.v_data);
        mProgress = findViewById(R.id.progress_bar);
        mErrorHolder = findViewById(R.id.root_error);
        mTvError = findViewById(R.id.tv_error);
        mBtnRetry = findViewById(R.id.btn_retry);


//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
//        adapter = new UserAdapter(this,users,this);
//        mRvList.setLayoutManager(new LinearLayoutManager(this));
//        mRvList.setAdapter(adapter);
        getUsers();

    }

    public void showData(ArrayList<User> users) {
        adapter = new UserAdapter(this, users, this);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.setAdapter(adapter);
        showView(ViewMode.DATA);
    }

    boolean isCon = false;
    public void getUsers() {
        showView(ViewMode.PROGRESS);
        isCon = false;
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 1;
                        Log.d("getUsers_", dataSnapshot.toString());
                        isCon = true;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.d("List_", "id: " + user.getId() + " , name: " + user.getName() + " , email: " + user.getEmail());
                            users.add(user);
                        }
                        showData(users);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("getUsers_", databaseError.toString());
                        showView(ViewMode.NO_CONNICTION);

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

                break;
            case ViewMode.PROGRESS:
                mProgress.setVisibility(View.VISIBLE);
                mData.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.GONE);


                break;
            case ViewMode.NO_CONNICTION:
                mProgress.setVisibility(View.GONE);
                mData.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.VISIBLE);

                break;
            case ViewMode.NO_PROXY:
                mProgress.setVisibility(View.GONE);
                mData.setVisibility(View.GONE);
                mErrorHolder.setVisibility(View.VISIBLE);

                break;
        }
    }

    @Override
    public void onUserInviteListner(final User user) {
        Toast.makeText(this, "Fdsf: "+user.getName()+" , "+user.getEmail(), Toast.LENGTH_SHORT).show();


        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("users")
                .child(Utils.getCurrentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User me =  dataSnapshot.getValue(User.class);

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
                                Log.d("result_","failure: "+e.getMessage());

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.d("result_","response: "+response.message());
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("result_","error: "+databaseError.getMessage());

                    }
                });
    }
}
