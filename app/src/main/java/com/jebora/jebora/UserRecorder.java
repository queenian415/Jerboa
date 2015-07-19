package com.jebora.jebora;

import android.content.Context;
import android.content.SharedPreferences;

import com.jebora.jebora.Utils.FileInfo;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private static String kidFileName;
    private static HashMap<String, String> kidList;
    private static List<File> imagesNotInServer;  // ONLY MODIFIED WITH SYNCHRONIZED METHODS
    private static Thread saveToServerThread;

    // User status identifier
    public static final int NEW_USER = 1;
    public static final int RETURNED_USER = 2;

    public static boolean firstTimeLogIn;

    public UserRecorder(Context c, int status) {
        mContext = c;
        ParseUser user = ParseUser.getCurrentUser();
        userId = user.getObjectId();
        username = user.getUsername();
        kidFileName = userId + ".txt";
        firstTimeLogIn = false;
        imagesNotInServer = new ArrayList<>();
        kidList = new HashMap<>();

        /***
         * Read or create kid list file, which stores all the kids of one user locally
         */
        File file = mContext.getFileStreamPath(kidFileName);
        if (!file.exists()) {
            try {
                // Only new user or new phone would not have kid list
                FileOutputStream fos = mContext.openFileOutput(kidFileName, mContext.MODE_PRIVATE);
                fos.close();

                if (status == RETURNED_USER) {
                    // returned user get here only when they switch to a new phone
                    firstTimeLogIn = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileInputStream fis = mContext.openFileInput(kidFileName);
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

        /***
         * Get all images and sync up to server if applicable
         */
        if (!firstTimeLogIn) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    addAllToImagesNotInServerList();
                }
            };
            saveToServerThread = new Thread(task, "add sync images to server in UserRecorder constructor");
            saveToServerThread.start();
        }
    }

    // Clean up user info when switching user
    public static void cleanUpThread() {
        // Wait until this class' thread exits
        if (saveToServerThread != null) {
            try {
                saveToServerThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<File> getAllImages() {
        File dir = new File(FileInfo.getUserDirectory(mContext).toString());
        File files[] = dir.listFiles();
        List<File> imagesList = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String filename = files[i].getName();
                // Make sure it's a JPEG image
                String ext = filename.substring(filename.lastIndexOf('.') + 1);
                if (ext.equals("jpg")) {
                    imagesList.add(files[i]);
                }
            } else {
                // in kid's directory, iterate to get kid's images
                File kidFiles[] = files[i].listFiles();
                for (int j = 0; j < kidFiles.length; i++) {
                    if (kidFiles[j].isFile()) {
                        String name = kidFiles[j].getName();
                        String ext = name.substring(name.lastIndexOf('.') + 1);
                        if (ext.equals("jpg")) {
                            imagesList.add(kidFiles[j]);
                        }
                    }
                }
            }
        }
        return imagesList;
    }

    public static void addOneKid(String kidId, String kidName) {
        if (kidList == null) {
            kidList = new HashMap<>();
        }

        try {
            FileOutputStream fos = mContext.openFileOutput(kidFileName, Context.MODE_APPEND);
            String newLine = kidId + ':' + kidName + "\n";
            fos.write(newLine.getBytes());
            fos.close();
            kidList.put(kidId, kidName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateKidList(HashMap<String, String> list) {
        try {
            FileOutputStream fos = mContext.openFileOutput(kidFileName, Context.MODE_PRIVATE);

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

    public static boolean isFirstTimeLogIn() { return firstTimeLogIn; }

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

    public static synchronized void addAllToImagesNotInServerList() {
        imagesNotInServer.addAll(getAllImages());
    }

    public static synchronized void addToImagesNotInServerList(File image) {
        imagesNotInServer.add(image);
    }

    public static synchronized void deleteFromImagesNotInServerList(File image) {
        imagesNotInServer.remove(image);
    }

    public static synchronized List<File> getImagesNotInServerList() {
        // return a new list to avoid other threads iterate over the same list
        // while UserRecorder is modifying the list
        List<File> images = new ArrayList<>();
        images.addAll(imagesNotInServer);
        return images;
    }

    /**
     *  The following print functions are for debugging purposes
     */
    public static void printFile() {
        System.out.println("file: ");
        try {
            FileInputStream fis = mContext.openFileInput(kidFileName);
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
