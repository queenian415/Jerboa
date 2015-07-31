package com.jebora.jebora.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.file.FileMetadataDirectory;
import com.jebora.jebora.App;
import com.jebora.jebora.UserRecorder;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by danchen on 15-07-01.
 * This class cannot use methods in UserRecorder because UserRecorder may not be initialized
 */
public class FileInfo {

    public static File getUserDirectory(Context context) {
        File extFile = context.getExternalFilesDir(null);
        if(!extFile.exists()){
            if(!extFile.mkdir()){
                Log.e("IO Error", "Error cannot make jerboa dir");
            }
        }

        String userId = ParseUser.getCurrentUser().getObjectId();
        String userPath = extFile.getAbsolutePath() + File.separator + userId;
        File userDirectory = null;
        try{
            userDirectory = FileInfo.newDir(userPath);
        }catch (Exception e){
            e.printStackTrace();
        }

        return userDirectory;
    }

    public static File getUserKidDirectory(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(App.PREFIX + "KIDID", 0);
        String kidId = sharedPreferences.getString("kidid", null);

        File userDirectory = getUserDirectory(context);

        if (kidId == null) {
            // User has no kid, use
            return userDirectory;
        }

        // user's kid directory
        String kidPath = userDirectory.getAbsolutePath() + File.separator + kidId;
        File kidDirectory = null;
        try {
            kidDirectory = FileInfo.newDir(kidPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kidDirectory;
    }

    public static File getUserCompressedDirectory(Context context) {
        File userDir = getUserDirectory(context);

        // compressed directory
        String compressedPath = userDir.getAbsolutePath() + File.separator + "Compressed";
        try {
            return newDir(compressedPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getUserKidCompressedDirectory(Context context) {
        File kidDir = getUserKidDirectory(context);

        // compressed directory
        String compressedPath = kidDir.getAbsolutePath() + File.separator + "Compressed";
        try {
            return newDir(compressedPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File newDir(String directoryName) throws Exception{
        File dir = new File(directoryName);
        if(!dir.exists()){
            if(!dir.mkdir()){
                throw new IOException("Error: cannot create directory: " + directoryName);
            }
        }
        return dir;
    }

    public static File newFile(String fileName) throws Exception{
        File file = new File(fileName);
        if(!file.exists()){
            if(!file.createNewFile()){
                throw new IOException("Error: cannot create file: " + fileName);
            }

        }
        return file;
    }

    public static boolean copyFile(File src, File dst) throws IOException{
        if(!src.exists() || !dst.exists()){
            return false;
        }
        FileChannel srcFC = new FileInputStream(src).getChannel();
        FileChannel dstFC = new FileOutputStream(dst).getChannel();
        if(dstFC != null && srcFC != null){
            dstFC.transferFrom(srcFC, 0, srcFC.size());
        }
        if(srcFC != null){
            srcFC.close();
        }
        if(dstFC != null){
            dstFC.close();
        }
        return true;
    }

    public static String getRealPathFromURI(Uri contentUri, Context context) {
        String result = null;
        Cursor cursor = null;
        final String docID = DocumentsContract.getDocumentId(contentUri);
        final String id = docID.split(":")[1];
        final String sel = MediaStore.Images.Media._ID + "=?";
        String[] projection = { MediaStore.Images.Media.DATA };
        try{
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String []{id}, null);
            if(cursor != null && cursor.moveToFirst()){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return result;
    }

    public static Date getLastModifiedTime(File f){
        Date d = null;
        try{
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            FileMetadataDirectory directory = metadata.getFirstDirectoryOfType(FileMetadataDirectory.class);
            d = directory.getDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE);
        } catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }

    public static byte[] getFileByteArray(String path) {
        byte[] data = null;
        try {
            FileInputStream in = new FileInputStream(path);
            BufferedInputStream buf = new BufferedInputStream(in);
            data = new byte[buf.available()];
            buf.read(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<String> loadLocalCompressedImagesPath(Context mContext, boolean loadUserOnly) {
        List<String> imagesList = new ArrayList<>();

        if (loadUserOnly) {
            UserRecorder.setPreferredKid(null);
        }

        File dir = new File(getUserKidCompressedDirectory(mContext).toString());
        File dirFiles[] = dir.listFiles();

        for (File image : dirFiles) {
            if (image.isFile()) {
                // Make sure it's a JPEG image
                String filename = image.getName();
                String ext = filename.substring(filename.lastIndexOf('.') + 1);
                if (ext.equals("jpg")) {
                    imagesList.add(image.getAbsolutePath());
                }
            }
        }

        if (UserRecorder.getPreferredKidId() != null || loadUserOnly) {
            return imagesList;
        } else {
            // Load all images, user's compressed images already loaded into list
            File userDir = new File(FileInfo.getUserDirectory(mContext).toString());
            File userFiles[] = userDir.listFiles();

            for (File userFile : userFiles) {
                if (userFile.isDirectory() && !userFile.getName().equals("Compressed")) {
                    // Get into kid's directory
                    File kidFiles[] = userFile.listFiles();
                    for (File kidFile : kidFiles) {
                        if (kidFile.isDirectory() && kidFile.getName().equals("Compressed")) {
                            // Load kid's compressed images
                            File images[] = kidFile.listFiles();
                            for (File image : images) {
                                // Make sure it's a JPEG image
                                String filename = image.getName();
                                String ext = filename.substring(filename.lastIndexOf('.') + 1);
                                if (ext.equals("jpg")) {
                                    imagesList.add(image.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
            return imagesList;
        }
    }

    public static String getOriginalFromCompressed(String compressedPath){
        if(compressedPath.contains("Compressed/")){
            return compressedPath.replace("Compressed/", "");
        }
        else{
            return compressedPath;
        }
    }

    // Written on July 31,2015, not tested use with caution
    public static String getCompressedFromOriginal(String originalPath){
        String compressedPath = "";
        for(String str :  originalPath.split("/")){
            if (str.endsWith(".jpg")){
                compressedPath += ("/Compressed/" + str);
            }
            else if(str.isEmpty()){
                // Do not do anything if empty delimited part
            }
            else{
                compressedPath += "/" + str;
            }

        }
        return compressedPath;
    }

    public static Bitmap compressImage(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 256;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}
