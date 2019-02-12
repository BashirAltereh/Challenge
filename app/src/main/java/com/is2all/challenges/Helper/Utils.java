package com.is2all.challenges.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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
}
