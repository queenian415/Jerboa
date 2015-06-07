package com.jebora.jebora;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Tiffanie on 15-05-25.
 */
public class ServerCommunication {

    public String signUp(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        try {
            user.signUp();
            return user.getObjectId();
        } catch (ParseException e) {
            if (e.getCode() == ParseException.USERNAME_TAKEN) {
                return SignUp.SIGNUP_EXISTS;
            } else {
                Log.d("signUp", e.getMessage());
                return SignUp.SIGNUP_ERROR;
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

    public String addKid(String kidName, String kidBirthday, String kidGender, String kidRelation) {
        // Store kid's info in Parse
        ParseObject kid = new ParseObject("Kid");
        kid.put("kidName", kidName);
        kid.put("kidBirthday", kidBirthday);
        kid.put("kidGender", kidGender);
        kid.put("kidRelation", kidRelation);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Create parent - kid relationship
            kid.put("kidParent", currentUser);
        } else {
            return SignUp_2.SIGNUP2_ERROR;
        }

        try {
            kid.save();
        } catch (ParseException e) {
            Log.d("addKid", e.getMessage());
            return SignUp_2.SIGNUP2_ERROR;
        }
        return kid.getObjectId();
    }
}
