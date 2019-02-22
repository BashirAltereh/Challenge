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

import com.is2all.challenges.Helper.GAME;
import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.OnStartGame;
import com.is2all.challenges.R;
import com.is2all.challenges.models.Dimention;

@SuppressLint("ValidFragment")
public class DialogStartGame extends DialogFragment {
    private View mVCreateNewGame, mVResume;
    private OnStartGame onStartGame;
    private Context context;
    private Activity activity;

    @SuppressLint("ValidFragment")
    public DialogStartGame(Context context, Activity activity, OnStartGame onStartGame) {
        this.context = context;
        this.activity = activity;
        this.onStartGame = onStartGame;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_start_game, container, false);
        mVCreateNewGame = view.findViewById(R.id.v_new_game);
        mVResume = view.findViewById(R.id.v_resume);
        mVCreateNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartGame.onStartGameListner(GAME.NEW_GAME);

            }
        });
        mVResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartGame.onStartGameListner(GAME.RESUME);

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

//        getDialog().getWindow().setLayout((int) (dimention.getWidth() / 1.3), dimention.getHeight() / 2);
    }
}

