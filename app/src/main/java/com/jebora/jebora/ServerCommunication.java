package com.jebora.jebora;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by Tiffanie on 15-05-25.
 */
public class ServerCommunication {

    private final String TAG = "ServerCommunication";
    public String signUp(String username, String password) {
        Log.d(TAG, "signUp");

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
                e.printStackTrace();
                return SignUp.SIGNUP_ERROR;
            }
        }
    }

    public Boolean logIn(String username, String password) {
        Log.d(TAG, "logIn");

        try {
            ParseUser.logIn(username, password);
            return true;
        } catch (ParseException e) {
            Log.d(TAG, "logIn failed: " + e.getMessage());
            return false;
        }
    }

    public String addKid(String kidName, String kidBirthday, String kidGender, String kidRelation) {
        Log.d(TAG, "addKid");

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

    public void saveImage(Context context, Bitmap src, String filename) {
        Log.d(TAG, "saveImage");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        final ParseFile image = new ParseFile("image" + ".png", data);
        final Context mContext = context;
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Image saved successfully, now link image to user and kid
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    // Get current kid
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(App.PREFIX + "KIDID", 0);
                    String kidId = sharedPreferences.getString("kidid", null);
                    boolean isKid = true;
                    if (kidId == null) {
                        isKid = false;
                    }

                    ParseObject parseObject = new ParseObject("Image");
                    parseObject.put("image", image);
                    parseObject.put("user", currentUser);
                    if (isKid)
                        parseObject.put("kid", ParseObject.createWithoutData("Kid", kidId));
                    parseObject.put("isKid", isKid);
                    parseObject.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
