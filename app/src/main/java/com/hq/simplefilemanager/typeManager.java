package com.hq.simplefilemanager;

import java.io.File;

/**
 * Created by jack on 9/25/14.
 */
public class typeManager {
    public static String getMimeType(File url){
        return getMimeType(url.getName());
    }

    public static String getMimeType(String file_name){
        if (file_name.contains(".doc") || file_name.contains(".docx")) {
            // Word document
            return "application/msword";
        } else if(file_name.contains(".pdf")) {
            // PDF file
            return "application/pdf";
        } else if(file_name.contains(".ppt") || file_name.contains(".pptx")) {
            // Powerpoint file
            return "application/vnd.ms-powerpoint";
        } else if(file_name.contains(".xls") || file_name.contains(".xlsx")) {
            // Excel file
            return "application/vnd.ms-excel";
        } else if(file_name.contains(".zip") || file_name.contains(".rar")) {
            // WAV audio file
            return "application/x-wav";
        } else if(file_name.contains(".rtf")) {
            // RTF file
            return "application/rtf";
        } else if(file_name.contains(".wav") || file_name.contains(".mp3")) {
            // WAV audio file
            return "audio/x-wav";
        } else if(file_name.contains(".gif")) {
            // GIF file
            return "image/gif";
        } else if(file_name.contains(".jpg") || file_name.contains(".jpeg") || file_name.contains(".png")) {
            // JPG file
            return "image/jpeg";
        } else if(file_name.contains(".txt")) {
            // Text file
            return "text/plain";
        } else if(file_name.contains(".3gp") || file_name.contains(".mpg") || file_name.contains(".mpeg") || file_name.contains(".mpe") || file_name.contains(".mp4") || file_name.contains(".avi")) {
            // Video files
            return "video/*";
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            return "*/*";
        }
    }
}
