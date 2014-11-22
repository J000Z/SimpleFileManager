package com.hq.simplefilemanager;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jack on 8/31/14.
 */
public class FilesArrayAdapter extends ArrayAdapter<FileItem>{
    List<FileItem> list = null;
    Context context;
    LayoutInflater inflater;

    public FilesArrayAdapter(Context context, List<FileItem> list) {
        super(context, R.layout.files_array_adapter, list);
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public File getFile(int i) {
        return list.get(i).f;
    }

    public void setSelection(FileItem file, boolean checked) {
        list.get(list.indexOf(file)).setSelected(checked);
    }

    public void setSelection(File file, boolean checked) {
        setSelection(new FileItem(file), checked);
    }

    public void setSelection(int position, boolean checked) {
        list.get(position).setSelected(checked);
        System.out.println(list.get(position).getName() + " index:" + position + " selected total:" + selectedCount());
    }

    public boolean isAllSelected(){
        for (int i=0; i<list.size(); i++) {
            if (!list.get(i).isSelected) {
                return false;
            }
        }
        return true;
    }

    public boolean isSelected(int i) {
        return list.get(i).isSelected;
    }

    public int selectedCount() {
        int i = 0;
        for (int j=0; j<list.size(); j++) {
            if (list.get(j).isSelected) {
                i++;
            }
        }
        return i;
    }

    public void selectAll() {
        for (int i=0; i<list.size(); i++) {
            list.get(i).setSelected(true);
        }
    }

    public void unselectAll() {
        for (int i=0; i<list.size(); i++) {
            list.get(i).setSelected(false);
        }
    }

    public List<FileItem> getSelectedfileItems(){
        System.out.println("getSelectedfileItems:" + selectedCount());
        List<FileItem> files = new ArrayList<FileItem>();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).isSelected) {
                files.add(list.get(i));
            }
        }
        return files;
    }

    public List<File> getSelectedFiles() {
        List<File> files = new ArrayList<File>();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).isSelected) {
                files.add(list.get(i).f);
            }
        }
        return files;
    }

    public void remove(){
        List<FileItem> files = getSelectedfileItems();
        System.out.println("adapter remove " + files.size());
        for (int i=0; i<files.size(); i++) {
            System.out.println("adapter remove " + files.get(i).getName());
            list.remove(files.get(i));
        }
        notifyDataSetChanged();
    }

    public void remove(List<File> files){
        System.out.println("adapter remove " + files.size());
        for (int i=0; i<files.size(); i++) {
            for (int j=0; j<list.size(); j++) {
                if (list.get(j).f.equals(files.get(i))) {
                    System.out.println("adapter remove " + files.get(i).getName());
                    list.remove(j);
                    continue;
                }
            }
        }
        notifyDataSetChanged();
    }

    public int size(){
        return list.size();
    }

    private class ViewHolder {
        private TextView text;
        private ImageView icon;
        private CheckBox checkBox;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.files_array_adapter, null);
            holder.text = (TextView) view.findViewById(R.id.text);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            //holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            /*
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file  = (File) view.getTag();
                    selected[list.indexOf(file)] ^= true;
                    System.out.println(file.getName()+" checked");
                }
            });*/
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.text.setText(list.get(position).getName());
        if (list.get(position).f.isDirectory()) {
            holder.icon.setImageResource(R.drawable.file_type_folder);
        } else {
            holder.icon.setImageResource(R.drawable.file_type_file);
        }
        //holder.checkBox.setChecked(list.get(position).isSelected);
        //holder.checkBox.setTag(list.get(position));
        view.setTag(holder);
        return view;
    }

}
