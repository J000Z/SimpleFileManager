package com.hq.simplefilemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 8/31/14.
 */
public class InteractiveArrayAdapter extends ArrayAdapter<fileItem>{
    List<fileItem> list = null;
    Context context;
    LayoutInflater inflater;

    public InteractiveArrayAdapter(Context context, List<fileItem> list) {
        super(context, R.layout.directory, list);
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public File getFile(int i) {
        return list.get(i).f;
    }

    public void setSelection(fileItem file, boolean checked) {
        list.get(list.indexOf(file)).setSelected(checked);
    }

    public void setSelection(File file, boolean checked) {
        setSelection(new fileItem(file), checked);
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

    public List<fileItem> getSelectedfileItems(){
        System.out.println("getSelectedfileItems:" + selectedCount());
        List<fileItem> files = new ArrayList<fileItem>();
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
        List<fileItem> files = getSelectedfileItems();
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
        private CheckBox checkBox;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.directory, null);
            holder.text = (TextView) view.findViewById(R.id.text);
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
        //holder.checkBox.setChecked(list.get(position).isSelected);
        //holder.checkBox.setTag(list.get(position));
        view.setTag(holder);
        return view;
    }

}
