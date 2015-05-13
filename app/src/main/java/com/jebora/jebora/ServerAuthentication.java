package com.jebora.jebora;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tiffanie on 15-05-12.
 */
public class ServerAuthentication {
    public String signUp(String username, String password, Context context) {
        // TODO: Use database and AccountManager later. Use Shared Preference for now
        SharedPreferences user = context.getSharedPreferences(SignUp.PREFIX + username, 0);
        if (!user.contains("username")) {
            SharedPreferences.Editor editor = user.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.commit();
            return SignUp.SIGNUP_SUCCEEDS;
        } else {
            return SignUp.SIGNUP_EXISTS;
        }
    }

    public Boolean logIn(String username, String password, Context context) {
        // TODO: Use database and AccountManager later. Use Shared Preference for now
        SharedPreferences user = context.getSharedPreferences(SignUp.PREFIX + username, 0);
        if (user.contains("username")) {
            if (user.getString("username", null).equals(username) &&
                user.getString("password", null).equals(password)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

