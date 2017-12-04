package com.company;

/**
 * Created by mmignoni on 2017-11-09.
 */
public class MyUtils {

    public static String FileExtensionGetter(String filePath){
        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i+1);
        }
        return extension;
    }

    public static String FileExtensionChange(String filePath, String newExtension){
        String withoutExtension = filePath.substring(0, filePath.lastIndexOf('.'));
        return withoutExtension + newExtension;
    }



}
