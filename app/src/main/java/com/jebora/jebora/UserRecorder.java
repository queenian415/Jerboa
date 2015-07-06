package com.jebora.jebora;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tiffanie on 15-06-28.
 *
 * This class stores and retrieves user's kids list in "kidId:kidName" format
 * in a text file.
 *
 * This class also contains other helper functions to retrieve user and kid info
 */
public class UserRecorder {

    private static Context mContext;
    private static String userId;
    private static String username;
    private static String fileName;
    private static HashMap<String, String> kidList;

    public UserRecorder(Context c) {
        mContext = c;
        ParseUser user = ParseUser.getCurrentUser();
        userId = user.getObjectId();
        username = user.getUsername();
        fileName = userId + ".txt";

        // read kid list, create one if not exist
        File file = mContext.getFileStreamPath(fileName);
        if (!file.exists()) {
            try {
                FileOutputStream fos = mContext.openFileOutput(fileName, mContext.MODE_PRIVATE);
                fos.close();
                kidList = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                kidList = new HashMap<>();
                FileInputStream fis = mContext.openFileInput(fileName);
                InputStreamReader in = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(in);
                String line = reader.readLine();
                while (line != null) {
                    String[] tokens = line.split("[:]");
                    kidList.put(tokens[0], tokens[1]);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addOneKid(String kidId, String kidName) {
        if (kidList == null) {
            kidList = new HashMap<>();
        }

        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_APPEND);
            String newLine = kidId + ':' + kidName + "\n";
            fos.write(newLine.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kidList.put(kidId, kidName);
    }

    public static void updateKidList(HashMap<String, String> list) {
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);

            if (list != null) {
                for (Map.Entry<String, String> entry : list.entrySet()) {
                    String newLine = entry.getKey() + ':' + entry.getValue() + "\n";
                    fos.write(newLine.getBytes());
                }
            }

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kidList = list;
    }

    public static HashMap<String, String> getKidList() { return kidList; }

    public static String getUserId() { return userId; }

    public static String getUsername() { return username; }

    public static boolean hasLocalKidList() { return kidList != null; }

    public static String getPreferredKidId() {
        SharedPreferences pref = mContext.getSharedPreferences(App.PREFIX + "KIDID", 0);
        return pref.getString("kidid", null);
    }

    public static String getPreferredKidName() {
        SharedPreferences pref = mContext.getSharedPreferences(App.PREFIX + "KIDID", 0);
        String id = pref.getString("kidid", null);
        if (id != null)
            return kidList.get(id);
        else
            return null;
    }

    public static void setPreferredKid(String kidId) {
        SharedPreferences user = mContext.getSharedPreferences(App.PREFIX + "KIDID", 0);
        SharedPreferences.Editor editor = user.edit();
        editor.putString("kidid", kidId);
        editor.commit();
    }

    /**
     *  The following print functions are for debugging purposes
     */
    public static void printFile() {
        System.out.println("file: ");
        try {
            FileInputStream fis = mContext.openFileInput(fileName);
            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(in);
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printKidListObject() {
        System.out.println("kidListObject: ");
        if (kidList != null) {
            for (Map.Entry<String, String> entry: kidList.entrySet()) {
                System.out.println(entry.getKey() + ':' + entry.getValue());
            }
        } else {
            System.out.println("null");
        }
    }
}
