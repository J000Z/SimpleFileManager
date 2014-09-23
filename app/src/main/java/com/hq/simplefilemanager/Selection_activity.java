package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Selection_activity extends ActionBarActivity{



    File currentDirectory = null;
    File[] currentFiles;
    ListView listView;
    InteractiveArrayAdapter adapter;
    ActionMode Mode = null;
    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        selectedIndex = -1;
        listView = (ListView) findViewById(R.id.listView);
        currentDirectory = new File(getIntent().getStringExtra("currentDirectory"));
        currentFiles = getDirectory(currentDirectory.getAbsolutePath());
        inflateListView(currentFiles);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println(currentFiles[position].getName() + " clicked");
                /*
                if (Mode != null) {
                    if (!adapter.getFile(position).isDirectory()) {return;}
                    listView.setItemChecked(selectedIndex, false);
                    listView.setItemChecked(position, true);
                    selectedIndex = position;
                    return;
                }*/

                if (currentFiles[position].isDirectory()) {
                    currentDirectory = currentFiles[position];
                    currentFiles = getDirectory(currentDirectory.getAbsolutePath());
                    inflateListView(currentFiles);
                } /*else {
                    listView.setItemChecked(position,false);
                }*/


            }
        });

        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        /*
        final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.action_singleselection, menu);
                return true;
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Return false if nothing is done
            }

            // Called when the user selects a contextual menu item
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.select:
                        Intent intent = getIntent();
                        intent.putExtra("selectedDirectory",adapter.getFile(selectedIndex).getAbsolutePath());
                        Selection_activity.this.setResult(0,intent);
                        Selection_activity.this.finish();
                        listView.setItemChecked(selectedIndex,false);
                        selectedIndex = -1;
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listView.setItemChecked(selectedIndex,false);
                selectedIndex = -1;
                Mode = null;
            }
        };



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Mode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined above
                if (adapter.getFile(i).isDirectory()) {
                    Mode = startActionMode(mActionModeCallback);
                    view.setSelected(true);
                    listView.setItemChecked(i, true);
                    selectedIndex = i;
                    return true;
                }

                return false;
            }
        });
        */

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Select a folder");
    }

    @Override
    public Intent getSupportParentActivityIntent () {
        System.out.println("up button");
        Selection_activity.this.setResult(-1);
        Selection_activity.this.finish();
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selection_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            /*
            case R.id.cancel:
                Selection_activity.this.setResult(-1);
                Selection_activity.this.finish();
                return true;
            */
            case R.id.select:
                Intent intent = getIntent();
                intent.putExtra("selectedDirectory",currentDirectory.getAbsolutePath());
                Selection_activity.this.setResult(0,intent);
                Selection_activity.this.finish();
                return true;
            case R.id.up:
                toParentFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toParentFolder();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void toParentFolder() {
        if (currentDirectory == null || currentDirectory.equals(Environment.getExternalStorageDirectory())) {
            Toast.makeText(getApplicationContext(), "This is the top folder.", Toast.LENGTH_SHORT).show();
        } else {
            currentDirectory = currentDirectory.getParentFile();
            currentFiles = getDirectory(currentDirectory.getAbsolutePath());
            inflateListView(currentFiles);
        }
    }

    public File[] getDirectory(String path) {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File folder = new File(path);
                return folder.listFiles();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File[] getDirectory(){
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File sdCardDir = Environment.getExternalStorageDirectory();
                return getDirectory(sdCardDir.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void inflateListView(File[] files){


        List<fileItem> file_list = new ArrayList<fileItem>();
        for (int i=0; i<files.length; i++){
            file_list.add(new fileItem(files[i]));
        }
        adapter = new InteractiveArrayAdapter(this,file_list);
        listView.setAdapter(adapter);

    }

}
