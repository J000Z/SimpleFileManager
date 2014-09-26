package com.hq.simplefilemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

/**
 * Created by jack on 9/2/14.
 */
public class SelectGridDialogFragment extends DialogFragment {

    private final int Grid_Size = 9;
    private int NUM_PAGES;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    List<ResolveInfo> resolveInfos;

    String file_type;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogGetInput(String operation, ResolveInfo info);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle bundle = getActivity().getIntent().getExtras();
        file_type = bundle.getString("file_type");

        View v = inflater.inflate(R.layout.activity_screen_slide, container);
        setImageButtons();

        mPager = (ViewPager) v.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;
    }

    public View.OnClickListener ImageButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println(((ResolveInfo) view.getTag()).activityInfo.name);
            mListener.onDialogGetInput("view", ((ResolveInfo) view.getTag()));
        }
    };

    /*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        System.out.println("Fragment Tag: " + getTag());
        LayoutInflater inflater = getAct sivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_screen_slide, null);
        //String file_type = bundle.getString("file_type");

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), getActivity());
        mPager.setAdapter(mPagerAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText)view.findViewById(R.id.input);
                        mListener.onDialogGetInput("file", editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SelectGridDialogFragment.this.getDialog().cancel();
                    }
                });
        Dialog d = builder.create();



        return d;
    }*/

    private void setImageButtons(){
        PackageManager pk = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.setType(typeManager.getMimeType(file_type));
        intent.setType("*/*");
        resolveInfos = pk.queryIntentActivities(intent, 0);
        NUM_PAGES = (int) Math.ceil((double)resolveInfos.size()/Grid_Size);
        System.out.println("resolveInfos.size() = " + resolveInfos.size());
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
            fragment.setResolveInfos(resolveInfos.subList(position*Grid_Size,Math.min(resolveInfos.size(),(position+1)*Grid_Size)));
            fragment.setImageButtonListenser(ImageButtonOnClickListener);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}