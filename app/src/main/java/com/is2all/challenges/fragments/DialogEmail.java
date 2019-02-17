package com.is2all.challenges.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.OnGetEmail;
import com.is2all.challenges.R;
import com.is2all.challenges.models.Dimention;

@SuppressLint("ValidFragment")
public class DialogEmail extends DialogFragment {
    private Context context;
    private Activity activity;
    private OnGetEmail onGetEmail;


    private Button mBtnOK;
    private EditText mEtEmail;

    public DialogEmail(Context context , Activity activity, OnGetEmail onGetEmail) {
        this.context = context;
        this.onGetEmail = onGetEmail;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_holder,container,false);
        mBtnOK = view.findViewById(R.id.bu_ok);
        mEtEmail = view.findViewById(R.id.et_email);
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEtEmail.getText().toString();
                if(Utils.isEmailValid(email)){
//                    Toast.makeText(context,getString(R.string.ok),Toast.LENGTH_SHORT).show();
                     onGetEmail.onGetEmailListner(email);
                }
                else
                    Toast.makeText(context,getString(R.string.not_valid_email),Toast.LENGTH_SHORT).show();
            }
        });
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.MyAnimation_Window);

    }
    @Override
    public void onResume() {
        super.onResume();
        Dimention dimention = Utils.getDimention(activity);

        getDialog().getWindow().setLayout((int) (dimention.getWidth() / 1.3), dimention.getHeight() / 4);
    }
    @Override
    public boolean isCancelable() {
        return false;
    }
}
