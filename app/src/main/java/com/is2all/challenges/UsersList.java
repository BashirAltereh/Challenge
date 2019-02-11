package com.is2all.challenges;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.is2all.challenges.adapters.UserAdapter;
import com.is2all.challenges.models.User;

import java.util.ArrayList;

public class UsersList extends AppCompatActivity implements OnInviteListener {
    private RecyclerView mRvList;
    private UserAdapter adapter;
    private ArrayList<User> users = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mRvList = findViewById(R.id.rv_list);
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        users.add(new User("bashiralterh@gmail.com","bashir","32232542"));
        adapter = new UserAdapter(this,users,this);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.setAdapter(adapter);

    }


    @Override
    public void onUserInviteListner() {
        Toast.makeText(this,"Fdsf",Toast.LENGTH_SHORT).show();
    }
}
