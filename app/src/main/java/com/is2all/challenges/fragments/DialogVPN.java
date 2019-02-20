package com.is2all.challenges.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        View view = inflater.inflate(R.layout.dialog_vpn, container, false);
        imageView = view.findViewById(R.id.iv_vpn);
        mBtnOPenVPN = view.findViewById(R.id.btn_open_vpn);

        final String appPackageName = "com.psiphon3"; // getPackageName() from Context or Activity object
        PackageManager pm = activity.getPackageManager();
        final boolean isInstalled = isPackageInstalled(appPackageName, pm);

        if (isInstalled)
            mBtnOPenVPN.setText(getString(R.string.open_vpn));
        else
            mBtnOPenVPN.setText(getString(R.string.downlaod_vpn));


        mBtnOPenVPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInstalled) {

                    Intent launchApplication = getActivity().getPackageManager().getLaunchIntentForPackage(appPackageName);
                    startActivity(launchApplication);
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
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

        getDialog().getWindow().setLayout((int) (dimention.getWidth() / 1.1), (int) (dimention.getHeight() / 2));
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
