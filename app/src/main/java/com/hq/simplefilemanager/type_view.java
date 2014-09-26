package com.hq.simplefilemanager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jack on 9/14/14.
 */
public class type_view extends LinearLayout {
    View view;

    public type_view(Context context) {
        super(context);
        view = inflate(context,R.layout.type_list_adpter,null);
    }

    public type_view(Context context, String type) {
        super(context);
        view = inflate(context,R.layout.type_list_adpter,null);
        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        set(type);
    }

    public void set(String type) {
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(type);

        LinearLayout listView = (LinearLayout) view.findViewById(R.id.listView);
        for (int i=0; i<3; i++) {
            ImageView icon = new ImageView(this.getContext());
            icon.setImageResource(R.drawable.ic_action_accept);
            listView.addView(icon);
        }

    }



}
