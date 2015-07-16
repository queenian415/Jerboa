package com.jebora.jebora.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.parse.ParseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

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
}
