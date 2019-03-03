package com.is2all.challenges.Helper;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.DisplayMetrics;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.is2all.challenges.Activities.QuestionsActivity;
import com.is2all.challenges.R;
import com.is2all.challenges.models.Dimention;
import com.is2all.challenges.models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static void savePushToken(String refreshedToken, String userId) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId)
                .child("pushId")
                .setValue(refreshedToken);
    }

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.isAnonymous()) {
            return "";
        } else {
            return currentUser.getUid();
        }
    }

    public static String convertToJSon(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        return json;
    }

    public static User convertToObject(String s) {
        Gson gson = new Gson();
        User user = gson.fromJson(s, User.class);
        return user;
    }

    public static Dimention getDimention(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return new Dimention(height, width);
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.app_name);
            String description = context.getString(R.string.do_want_play);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(AppConstants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void createNotification(Context context) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Uri uri = Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.dropped_spinner);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_ques)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getString(R.string.do_want_play))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(null);

        MediaPlayer media2 = MediaPlayer.create(context, R.raw.dropped_spinner);
        media2.start();


        //Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }


}
