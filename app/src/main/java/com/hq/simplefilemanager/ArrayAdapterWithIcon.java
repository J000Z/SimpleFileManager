package com.hq.simplefilemanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jack on 10/14/14.
 */
public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

    private List<Drawable> images;
    private List<String> items;

    public ArrayAdapterWithIcon(Context context, List<String> items, List<Drawable> images) {
        super(context, R.layout.intentpicker, R.id.text, items);
        this.images = images;
        this.items = items;
    }

    public ArrayAdapterWithIcon(Context context, String[] items, Drawable[] images) {
        super(context, R.layout.intentpicker, R.id.text, items);
        this.images = Arrays.asList(images);
        this.items = Arrays.asList(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        //TextView textView = (TextView) view.findViewById(R.id.text);
        //textView.setText(items.get(position));

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageDrawable(images.get(position));

        return view;
    }

}