package com.hq.simplefilemanager;

import android.content.SharedPreferences;

/**
 * Created by jack on 9/18/14.
 */
public class AppPreferenceManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String TypeList_key = "TypeList";
    String splitter_pattern = "\\|\\|";
    String splitter = "||";

    AppPreferenceManager(SharedPreferences p){
        preferences = p;
        editor = p.edit();
    }

    public boolean isFileTypeExist(String file_type) {
        if (file_type == null || file_type.length() == 0) return false;
        return preferences.contains(file_type);
    }

    public String[] getTypes(){
        String t_slist = preferences.getString(TypeList_key,null);
        System.out.println(TypeList_key + " : [" + t_slist + "]");
        if (t_slist == null) {
            return new String[0];
        } else {
            System.out.println("length: " + t_slist.split(splitter_pattern).length);
            return t_slist.split(splitter_pattern);
        }

    }

    public void addType(String file_type) {
        if (file_type == null || file_type.length() == 0 || !file_type.startsWith(".")){
            return;
        }
        if (file_type.contains(splitter)) {
            return;
        }
        if (isFileTypeExist(file_type)) {
            return;
        }
        String s_list = preferences.getString(TypeList_key, null);
        if (s_list == null) {
            editor.putString(TypeList_key, file_type);
        } else {
            editor.putString(TypeList_key, s_list+ splitter +file_type);
        }
        editor.putString(file_type,"");
        if (!editor.commit()){
            System.out.println("failed commit");
        } else {
            System.out.println("done commit");
        }
    }

    public String[] getAppsOfType(String file_type) {
        String app_slist = preferences.getString(file_type,null);
        if (app_slist == null || app_slist.length() == 0) {
            return null;
        }
        String[] apps = app_slist.split(splitter_pattern);
        return apps;
    }

    public void setAppOfType(String file_type, String[] apps) {
        if (!isFileTypeExist(file_type)) {
            return;
        }
        String ss_list = new String();
        for (int i=0; i<apps.length; i++) {
            if (i!=0) {
                ss_list += splitter;
            }
            ss_list += apps[i];
        }
        editor.putString(file_type,ss_list);
        editor.commit();
    }

    public void addAppOfType(String file_type, String app){
        if (!isFileTypeExist(file_type)) {
            addType(file_type);
        }
        String ss_list = preferences.getString(file_type, null);
        if (ss_list == null) {
            return;
        }
        if (ss_list.length() != 0) {
            ss_list += splitter;
        }
        ss_list += app;
        editor.putString(file_type, ss_list);
        editor.commit();
    }

    public void removeType(String file_type) {
        if (!isFileTypeExist(file_type)){
            return;
        }
        String s_list = preferences.getString(TypeList_key, null);
        String[] types = s_list.split(splitter_pattern);
        String ss_list = new String();
        for (int i=0; i<types.length; i++) {
            if (types[i].equals(file_type)){
                continue;
            }
            if (ss_list.length() !=0 ){
                ss_list += splitter;
            }
            ss_list += types[i];
        }
        editor.putString(TypeList_key,ss_list);
        editor.remove(file_type);
        editor.commit();
    }

    public String encodeInfo(String packageName, String name) {
        return "[" + packageName + " " + name + "]";
    }

    public String[] decodeInfo(String app) {
        String[] info = app.substring(1,app.length()-1).split(" ");
        return  info;
    }

    public String decodePackageName(String app) {
        return decodeInfo(app)[0];
    }

    public String decodeName(String app) {
        return decodeInfo(app)[1];
    }
}