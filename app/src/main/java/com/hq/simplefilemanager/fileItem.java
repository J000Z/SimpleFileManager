package com.hq.simplefilemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public static List<FileItem> fromFileList(List<File> files) {
        ArrayList<FileItem> fileItems = new ArrayList<FileItem>();
        for (File x : files)
            fileItems.add(new FileItem(x));
        return fileItems;
    }
}
