package com.is2all.challenges.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.R;
import com.is2all.challenges.models.Dimention;

@SuppressLint("ValidFragment")
public class DialogVPN extends DialogFragment {
    Activity activity;

    public DialogVPN(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);

        return inflater.inflate(R.layout.dialog_vpn, container, false);
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

        getDialog().getWindow().setLayout((int) (dimention.getWidth() / 1.1), (int) (dimention.getHeight() / 2));
    }
}
