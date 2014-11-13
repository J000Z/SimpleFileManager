package com.hq.simplefilemanager;

import android.content.Context;
import android.graphics.Color;
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
public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

    private List<Drawable> images;
    private int selectIndex = -1;

    public ArrayAdapterWithIcon(Context context, List<String> items, List<Drawable> images) {
        super(context, R.layout.intentpicker, R.id.text, items);
        this.images = images;
    }

    public ArrayAdapterWithIcon(Context context, String[] items, Drawable[] images) {
        super(context, R.layout.intentpicker, R.id.text, items);
        this.images = Arrays.asList(images);
    }

    private class ViewHolder {
        private ImageView iconView;
    }

    public void selectItem(int index) {
        selectIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = super.getView(position, view, parent);
            holder.iconView = (ImageView) view.findViewById(R.id.icon);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.iconView.setImageDrawable(images.get(position));
        view.setTag(holder);
        if (position == selectIndex) {
            view.setBackgroundColor(Color.parseColor("#31abd4"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

}