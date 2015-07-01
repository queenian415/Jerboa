package com.jebora.jebora.Utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.file.FileMetadataDirectory;

import java.io.File;
import java.util.Date;

/**
 * Created by danchen on 15-07-01.
 */
public class FileInfo {

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
