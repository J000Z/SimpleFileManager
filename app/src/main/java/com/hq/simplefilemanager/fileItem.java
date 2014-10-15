package com.hq.simplefilemanager;

import java.io.File;

/**
 * Created by jack on 9/1/14.
 */
public class FileItem {
    File f;
    boolean isSelected;

    FileItem(File f){
        this.f = f;
        isSelected = false;
    }

    String getName(){
        return f.getName();
    }

    boolean equals(FileItem ff){
        return f.equals(ff.f);
    }

    void setSelected(boolean x){
        isSelected = x;
    }

    void delete() {f.delete();}
}
