package com.dstechnologies.vault_dstech;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
import java.net.URISyntaxException;

public class FileUtils {

    public static String getPath(Context context, Uri uri) throws URISyntaxException{
        if("content".equalsIgnoreCase(uri.getScheme())){
            String []projection={"_data"};
            Cursor cursor=null;

            try{
                cursor=context.getContentResolver().query(uri,projection ,null,null,null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);}

            } catch (Exception e) {
                // Eat it
            }
            }

            else if ("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();

        }
        return null;
        }

        public static byte[] getFileData(String path ){
            File file = new File(path);
            byte[] fileDataArray = new byte[(int) file.length()];

        if (fileDataArray.toString().length()!=0){
            return fileDataArray;
        }

        return null;
    }

    }
