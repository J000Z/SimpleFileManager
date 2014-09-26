package com.hq.simplefilemanager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;

import java.util.List;

public class ScreenSlidePageFragment extends Fragment {
    List<ResolveInfo> resolveInfos;
    View.OnClickListener ImageButtonOnClickListener;

    public void setResolveInfos(List<ResolveInfo> resolveInfos){
        this.resolveInfos = resolveInfos;
    }

    public void setImageButtonListenser(View.OnClickListener ImageButtonOnClickListener){
        this.ImageButtonOnClickListener = ImageButtonOnClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        GridLayout gridLayout = (GridLayout) rootView.findViewById(R.id.gridLayout);
        PackageManager pk = getActivity().getPackageManager();

        for (int i=0; i<Math.min(9,resolveInfos.size()); i++) {
            ResolveInfo x = resolveInfos.get(i);
            ImageButton icon = new ImageButton(getActivity());
            icon.setImageDrawable(x.loadIcon(pk));
            ViewGroup.LayoutParams iconLayoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            icon.setLayoutParams(iconLayoutParams);
            icon.setTag(x);
            icon.setOnClickListener(ImageButtonOnClickListener);
            gridLayout.addView(icon);
            //System.out.println(x.activityInfo.name);
            //System.out.println(x.activityInfo.applicationInfo.packageName);
        }

        return rootView;
    }
}