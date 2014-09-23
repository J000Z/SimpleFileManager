package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class typeActivity extends Activity
                              implements NoticeDialogFragment.NoticeDialogListener{

    AppPreferenceManager p_manager;
    String file_type;

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
    public void onDialogGetInput(String operation, String input) {
        if (operation.equals("add_app")) {
            System.out.println("Preference: add app " + input);
            //p_manager.addType(input);
            refresh();
        }

    }

    public void refresh(){
    }





}
