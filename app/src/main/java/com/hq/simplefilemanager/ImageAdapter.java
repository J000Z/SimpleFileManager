package com.hq.simplefilemanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by jack on 10/14/14.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Drawable[] images;
    private int size;

    public ImageAdapter(Context c, Drawable[] i_s, int s) {
        mContext = c;
        images = i_s;
        size = s;
    }

    public int getCount() {
        return images.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new GridView.LayoutParams(size,size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setClickable(false);
        imageView.setImageDrawable(images[position]);
        return imageView;
    }
}
