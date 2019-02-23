package com.is2all.challenges.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.R;
import com.is2all.challenges.models.Dimention;

@SuppressLint("ValidFragment")
public class DialogVPN extends DialogFragment {
    Activity activity;
    private ImageView imageView;
    private Button mBtnOPenVPN;

    public DialogVPN(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        View view = inflater.inflate(R.layout.dialog_vpn, container, false);
        imageView = view.findViewById(R.id.iv_vpn);
        mBtnOPenVPN = view.findViewById(R.id.btn_open_vpn);

        final String appPackageName1 = "com.psiphon3"; // package name
        final String appPackageName2 = "com.psiphon3.subscription"; // package name

        PackageManager packageManager = activity.getPackageManager();
        final boolean isInstalled = isPackageInstalled(appPackageName1, appPackageName2, packageManager);

        if (isInstalled)
            mBtnOPenVPN.setText(getString(R.string.open_vpn));
        else
            mBtnOPenVPN.setText(getString(R.string.downlaod_vpn));


        mBtnOPenVPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInstalled) {
                    try {
                        Intent launchApplication = getActivity().getPackageManager().getLaunchIntentForPackage(appPackageName1);
                        startActivity(launchApplication);
                    } catch (Exception e) {
                        Intent launchApplication = getActivity().getPackageManager().getLaunchIntentForPackage(appPackageName2);
                        startActivity(launchApplication);
                    }
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName2)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName2)));
                    }
                }
                dismiss();
            }
        });
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

//        getDialog().getWindow().setLayout((int) (dimention.getWidth() / 1.1), (int) (dimention.getHeight() / 2));
    }

    private boolean isPackageInstalled(String packagename1, String packagename2, PackageManager packageManager) {
        boolean isInsall1 = false;
        boolean isInsall2 = false;
        try {
            packageManager.getPackageInfo(packagename1, 0);
            isInsall1 = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInsall1 = false;
        }
        try {
            packageManager.getPackageInfo(packagename2, 0);
            isInsall2 = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInsall2 = false;
        }
        return isInsall1 || isInsall2;
    }
}
