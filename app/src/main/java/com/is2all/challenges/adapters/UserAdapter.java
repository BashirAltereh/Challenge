package com.is2all.challenges.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.is2all.challenges.OnInviteListener;
import com.is2all.challenges.R;
import com.is2all.challenges.models.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private OnInviteListener onInviteListener;

    public UserAdapter(Context context, ArrayList<User> users, OnInviteListener onInviteListener) {
        this.context = context;
        this.users = users;
        this.onInviteListener = onInviteListener;
        Fresco.initialize(context);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
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


    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTvUserName, mTvEmail;
        private Button mBtnInvite;
        private SimpleDraweeView mIvUserPhoto;

        public Holder(View itemView) {
            super(itemView);
            mTvEmail = itemView.findViewById(R.id.tv_email);
            mTvUserName = itemView.findViewById(R.id.tv_user_name);
            mBtnInvite = itemView.findViewById(R.id.btn_invite);
            mIvUserPhoto = itemView.findViewById(R.id.iv_user_icon);
        }

        public void setData(final User user) {
            mTvEmail.setText(user.getEmail());
            mTvUserName.setText(user.getName());
            mBtnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInviteListener.onUserInviteListner(user);
                }
            });
            String id = user.getId();
            final String url = "https://graph.facebook.com/" + id + "/picture?type=large";  //API form facebook to get user's photo
//            Picasso.get().load(url).error(R.drawable.ic_profile_male).into(mIvUserPhoto);
            Log.d("URL_","url : "+url);
            mIvUserPhoto.setImageURI(url);

            mIvUserPhoto.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {
                    Toast.makeText(context,"fdsf",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            mIvUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intentGallery = new Intent(Intent.ACTION_VIEW);
                        intentGallery.setDataAndType(Uri.parse(url), "image/*");
                        context.startActivity(Intent.createChooser(intentGallery, "Open [App] images"));
                    }
                    catch (Exception e){

                    }
                }
            });
        }
    }
}
