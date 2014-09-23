package com.hq.simplefilemanager;

import java.io.File;

/**
 * Created by jack on 9/1/14.
 */
public class fileItem {
    File f;
    boolean isSelected;

    fileItem(File f){
        this.f = f;
        isSelected = false;
    }

    String getName(){
        return f.getName();
    }

    boolean equals(fileItem ff){
        return f.equals(ff.f);
    }

    void setSelected(boolean x){
        isSelected = x;
    }

    void delete() {f.delete();}
}
