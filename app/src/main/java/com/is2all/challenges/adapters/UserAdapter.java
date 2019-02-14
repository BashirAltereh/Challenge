package com.is2all.challenges.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.is2all.challenges.Helper.CircleImageView;
import com.is2all.challenges.OnInviteListener;
import com.is2all.challenges.R;
import com.is2all.challenges.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private OnInviteListener onInviteListener;

    public UserAdapter(Context context , ArrayList<User> users , OnInviteListener onInviteListener){
        this.context = context;
        this.users = users;
        this.onInviteListener = onInviteListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          Holder viewHolder = (Holder) holder;
          viewHolder.setData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    public class Holder extends RecyclerView.ViewHolder{
        private TextView mTvUserName,mTvEmail;
        private Button mBtnInvite;
        private CircleImageView mIvUserPhoto;

        public Holder(View itemView) {
            super(itemView);
            mTvEmail = itemView.findViewById(R.id.tv_email);
            mTvUserName = itemView.findViewById(R.id.tv_user_name);
            mBtnInvite = itemView.findViewById(R.id.btn_invite);
            mIvUserPhoto = itemView.findViewById(R.id.iv_user_icon);
        }

        public void setData(final User user){
          mTvEmail.setText(user.getEmail());
          mTvUserName.setText(user.getName());
          mBtnInvite.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  onInviteListener.onUserInviteListner(user);
              }
          });
          String id = user.getId();
          String url = "https://graph.facebook.com/"+id+"/picture?type=large";  //API form facebook to get user's photo
            Picasso.get().load(url).into(mIvUserPhoto);

        }
    }
}
