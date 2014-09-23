package com.hq.simplefilemanager;

import android.content.SharedPreferences;

/**
 * Created by jack on 9/18/14.
 */
public class AppPreferenceManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String TypeList_key = "TypeList";

    AppPreferenceManager(SharedPreferences p){
        preferences = p;
        editor = p.edit();
    }

    public boolean isFileTypeExist(String file_type) {
        return preferences.contains(file_type);
    }

    public String[] getTypes(){
        String t_slist = preferences.getString(TypeList_key,null);
        System.out.println(TypeList_key + " : " + t_slist);
        if (t_slist == null) {
            return new String[0];
        } else {
            return t_slist.split(" ");
        }

    }

    public void addType(String file_type) {
        if (file_type.contains(" ")) {
            return;
        }
        if (isFileTypeExist(file_type)) {
            return;
        }
        String s_list = preferences.getString(TypeList_key, null);
        if (s_list == null) {
            editor.putString(TypeList_key, file_type);
        } else {
            editor.putString(TypeList_key,s_list+" "+file_type);
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
        if (app_slist == null) {
            return null;
        }
        if (app_slist.length() == 0) {
            return new String[0];
        }
        String[] apps = app_slist.split(" ");
        return apps;
    }

    public void setAppOfType(String file_type, String[] apps) {
        if (!isFileTypeExist(file_type)) {
            return;
        }
        String ss_list = new String();
        for (int i=0; i<apps.length; i++) {
            if (i!=0) {
                ss_list += " ";
            }
            ss_list += apps[i];
        }
        editor.putString(file_type,ss_list);
        editor.commit();
    }

    public void removeType(String file_type) {
        if (!isFileTypeExist(file_type)){
            return;
        }
        String s_list = preferences.getString(TypeList_key, null);
        String[] types = s_list.split(" ");
        String ss_list = new String();
        for (int i=0; i<types.length; i++) {
            if (types[i].equals(file_type)){
                continue;
            }
            if (ss_list.length() !=0 ){
                ss_list += " ";
            }
            ss_list += types[i];
        }
        editor.putString(TypeList_key,ss_list);
        editor.remove(file_type);
        editor.commit();
    }
}
