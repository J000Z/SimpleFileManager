package com.hq.simplefilemanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jack on 8/31/14.
 */
public class SettingsFileTypeListAdapter extends ArrayAdapter<String>{
    boolean[] selectList = null;
    List<String> list = null;
    Context context;
    LayoutInflater inflater;
    PackageManager pk;
    AppPreferenceManager p_manager;
    int imgSize;

    public SettingsFileTypeListAdapter(int layout, Context context, List<String> list, PackageManager pk, AppPreferenceManager p_manager) {
        super(context, layout, list);
        this.context = context;
        this.list = list;
        this.selectList = new boolean[list.size()];
        inflater = LayoutInflater.from(context);
        this.pk = pk;
        imgSize = context.getResources().getDimensionPixelSize(R.dimen.gridViewImageWidth);
        this.p_manager = p_manager;
    }

    private class ViewHolder {
        private TextView text;
        private LinearLayout linearLayout;
    }

    public void setSelection(int position, boolean checked) {
        selectList[position] = checked;
    }

    public boolean isSelected(int position) {
        return selectList[position];
    }

    public void unselectAll() {
        for (int i=0; i<selectList.length; i++)
            setSelection(i, false);
    }

    public int selectedCount() {
        int count = 0;
        for (int i=0; i<selectList.length; i++)
            if (isSelected(i))
                count++;
        return count;
    }

    public List<String> getSelected() {
        List<String> r = new ArrayList<String>();
        for (int i=0; i<selectList.length; i++)
            if (isSelected(i))
                r.add(list.get(i));
        return r;
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.settings_file_type_list_adapter, null);
            holder.text = (TextView) view.findViewById(R.id.text);
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        String type = list.get(position);
        holder.text.setText(type);
        ArrayList<Drawable> icons = getIcons(type);
        holder.linearLayout.removeAllViews();
        for (Drawable icon : icons) {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(icon);
            imageView.setLayoutParams(new GridView.LayoutParams(imgSize,imgSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
            holder.linearLayout.addView(imageView);
        }
        return view;
    }

    public ArrayList<Drawable> getIcons(String file_type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File("/tmp/xxx" + file_type));
        intent.setDataAndType(uri, MimeTypeManager.getMimeType(file_type));
        List<ResolveInfo> resInfo = pk.queryIntentActivities(intent, 0);
        ArrayList<Drawable> icons = new ArrayList<Drawable>();
        List<String> apps = new ArrayList<String>();
        if (p_manager.getAppsOfType(file_type) != null) {
            apps = Arrays.asList(p_manager.getAppsOfType(file_type));
        }
        for (int i = 0; i < resInfo.size(); i++) {
            String names = resInfo.get(i).activityInfo.name;
            String packageNames = resInfo.get(i).activityInfo.packageName;
            if (apps.contains(p_manager.encodeInfo(packageNames, names))) {
                icons.add(resInfo.get(i).loadIcon(pk));
            }
        }
        return icons;
    }

}
