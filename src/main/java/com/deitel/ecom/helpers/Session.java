package com.deitel.ecom.helpers;

import android.app.Activity;
import android.content.Intent;

import com.deitel.ecom.activities.LoginActivity;

public class Session {
    // Store session variables
    public static int userId = -1;
    public static String role = "";
    public static boolean loggedIn = false;

    // Clear session variables
    public static void clear() {
        userId = -1;
        role = "";
        loggedIn = false;
    }

    // Logout from current session
    public static void logout(Activity activity) {
        Session.clear();
        Intent i = new Intent(activity, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(i);
        activity.finish();
    }
}