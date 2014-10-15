package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class SettingsFileTypeListActivity extends Activity
                              implements NoticeDialogFragment.NoticeDialogListener{

    AppPreferenceManager p_manager;
    int OPEN_SETTING_REQUEST = 1;
    int SETTING_CHANGED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_list);
        //typeListActivity.context = getApplicationContext();



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
        inflater.inflate(R.menu.type_list_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add:
                DialogFragment dialog2 = new NoticeDialogFragment();
                dialog2.show(getFragmentManager(), "add_preference");
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
        if (operation.equals("add_preference")) {
            if (input.contains(" ") && !input.startsWith(".")){
                return;
            }
            System.out.println("Preference: add type " + input);
            if (input.startsWith(".") && input.length()!=0 && !input.contains("\\s+")) {
                p_manager.addType(input);
                refresh();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == OPEN_SETTING_REQUEST) {
            // Make sure the request was successful
            if (resultCode == SETTING_CHANGED) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                String file_type = data.getStringExtra("file_type");
                refresh();
                // Do something with the contact here (bigger example below)
            }
        }
    }

    public void refresh(){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_LinearLayout);

        mainLayout.removeAllViews();
        String[] default_cat = {"Video", "Audio", "Text"};
        addTypeViews(mainLayout,default_cat);

        View horizontal_line = getLayoutInflater().inflate(R.layout.horizontal_line, null);
        mainLayout.addView(horizontal_line);

        String[] file_types = p_manager.getTypes();
        addTypeViews(mainLayout,file_types);
    }

    public void addTypeViews(LinearLayout layout, final String[] types){
        for (int i=0; i<types.length; i++) {
            type_view v = new type_view(this,types[i]);
            v.setTag(types[i]);
            v.setClickable(true);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Open preference for " + view.getTag());
                    Intent intent = new Intent(SettingsFileTypeListActivity.this, SettingsFileTypeSoloActivity.class);
                    intent.putExtra("file_type", (String) view.getTag());
                    //startActivity(intent);
                    startActivityForResult(intent, OPEN_SETTING_REQUEST);//request_code
                }
            });

            layout.addView(v);
        }
    }



}