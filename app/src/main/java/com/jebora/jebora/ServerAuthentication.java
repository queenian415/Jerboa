package com.jebora.jebora;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by Tiffanie on 15-05-12.
 */
public class ServerAuthentication {

    public String signUp(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        try {
            user.signUp();
            return SignUp.SIGNUP_SUCCEEDS;
        } catch (ParseException e) {
            if (e.getCode() == ParseException.USERNAME_TAKEN) {
                return SignUp.SIGNUP_EXISTS;
            } else {
                Log.d("signUp", e.getMessage());
                return null;
            }
        }
    }

    public Boolean logIn(String username, String password) {
        try {
            ParseUser.logIn(username, password);
            return true;
        } catch (ParseException e) {
            Log.d("logIn", e.getMessage());
            return false;
        }
    }
}

