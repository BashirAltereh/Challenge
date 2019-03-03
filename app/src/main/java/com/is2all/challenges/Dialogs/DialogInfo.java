package com.is2all.challenges.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.is2all.challenges.Helper.CommunicationType;
import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.OnNeedCommunicate;
import com.is2all.challenges.R;
import com.is2all.challenges.models.Dimention;

@SuppressLint("ValidFragment")
public class DialogInfo extends DialogFragment implements View.OnClickListener {
    private TextView mTvPhone, mTvEmail, mTvFacebook;
    private SimpleDraweeView mIvMyPhoto;
    private FrameLayout mVHover;
    private OnNeedCommunicate onNeedCommunicate;
    private Context context;
    private View mVData;


    public DialogInfo(Context context, OnNeedCommunicate onNeedCommunicate) {
        this.context = context;
        this.onNeedCommunicate = onNeedCommunicate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);

        return dialog;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        View view = inflater.inflate(R.layout.dialog_info, container, false);

        mTvPhone = view.findViewById(R.id.tv_call);
        mTvEmail = view.findViewById(R.id.tv_email);
        mTvFacebook = view.findViewById(R.id.tv_facebook);
        mIvMyPhoto = view.findViewById(R.id.iv_dev);
        mVHover = view.findViewById(R.id.v_hover);
        mVData = view.findViewById(R.id.v_data);

        mVHover.setOnClickListener(this);
        mTvPhone.setOnClickListener(this);
        mTvEmail.setOnClickListener(this);
        mTvFacebook.setOnClickListener(this);
        mIvMyPhoto.setOnClickListener(this);


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
        Dimention dimention = Utils.getDimention(getActivity());

//        getDialog().getWindow().setLayout((int) (dimention.getWidth() / 1.3), (int) (dimention.getHeight() / 1.9));
        ViewGroup.LayoutParams layoutParams = mIvMyPhoto.getLayoutParams();
        layoutParams.width = dimention.getWidth() / 3;
        layoutParams.height = (int) (dimention.getHeight() / 4.2);
        mIvMyPhoto.setLayoutParams(layoutParams);

//        mVHover.setLayoutParams(layoutParams);

    }

    public void sendAppMsg(View view) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = " message you want to share..";
        // change with required  application package

        intent.setPackage("com.whatsapp");
        if (intent != null) {
            intent.putExtra(Intent.EXTRA_TEXT, text);//
            startActivity(Intent.createChooser(intent, text));
        } else {

//            Toast.makeText(, "App not found", Toast.LENGTH_SHORT)
//                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.tv_email:
                onNeedCommunicate.OnNeedCommunicateListner(CommunicationType.EMAIL);
                break;
            case R.id.tv_facebook:
                onNeedCommunicate.OnNeedCommunicateListner(CommunicationType.FACEBOOK);

                break;
            case R.id.tv_call:
                onNeedCommunicate.OnNeedCommunicateListner(CommunicationType.PHONE_NUMBER);

        }
    }
}
