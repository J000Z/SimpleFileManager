package com.hq.simplefilemanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jack on 10/14/14.
 */
public class ArrayAdapterWithIconAndCheckBox extends ArrayAdapter<String> {

    private List<Drawable> images;
    private List<Boolean> checkedItems;

    public ArrayAdapterWithIconAndCheckBox(Context context, List<String> items, List<Drawable> images) {
        super(context, R.layout.intentpicker_checkbox, R.id.text, items);
        this.images = images;
    }

    public ArrayAdapterWithIconAndCheckBox(Context context, String[] items, Drawable[] images, Boolean[] checkedItems) {
        super(context, R.layout.intentpicker_checkbox, R.id.text, items);
        this.images = Arrays.asList(images);
        this.checkedItems = Arrays.asList(checkedItems);
    }

    public List<Boolean> getCheckedItems(){
        return checkedItems;
    }

    private class ViewHolder {
        private ImageView iconView;
        private CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = super.getView(position, view, parent);
            holder.iconView = (ImageView) view.findViewById(R.id.icon);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("checked");
                    checkedItems.set(position,!checkedItems.get(position));
                }
            });
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //View view = super.getView(position, convertView, parent);

        //ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        holder.iconView.setImageDrawable(images.get(position));

        //CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        holder.checkBox.setChecked(checkedItems.get(position));
        holder.checkBox.setTag(position);
        System.out.println("set [" + position + "] " + checkedItems.get(position));


        view.setTag(holder);

        return view;
    }

}