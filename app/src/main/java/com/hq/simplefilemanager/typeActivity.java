package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class typeActivity extends FragmentActivity
                          implements SelectGridDialogFragment.NoticeDialogListener{

    AppPreferenceManager p_manager;
    String file_type;
    GridLayout gridlayout_open;
    GridLayout gridlayout_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_setting);

        file_type = getIntent().getStringExtra("file_type");
        TextView file_type_view = (TextView)findViewById(R.id.file_type);
        file_type_view.setText(file_type);



        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Application preference");
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(true);
        gridlayout_open = (GridLayout) findViewById(R.id.gridlayout_open);
        gridlayout_share = (GridLayout) findViewById(R.id.gridlayout_share);
        p_manager = new AppPreferenceManager(getSharedPreferences("app_preference",MODE_PRIVATE));
        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.type_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.delete:
                p_manager.removeType(file_type);
                finishActivity(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogGetInput(String operation, ResolveInfo info) {
        if (operation.equals("add_app")) {
            System.out.println("Preference: add app " + info.activityInfo.name);
            //p_manager.addType(input);
            refresh();
        }

    }

    public void refresh(){
        gridlayout_open.addView(getImageView(R.drawable.plus));
    }

    public ImageView getImageView(int resource){
        ImageView imageView = new ImageView(this);
        //setting image resource
        imageView.setImageResource(resource);
        //setting image position
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new SelectGridDialogFragment();
                Bundle args = new Bundle();
                args.putString("file_type", file_type);
                dialog.show(getSupportFragmentManager(),"SelectGridDialogFragment");
            }
        });
        return imageView;
    }





}
