package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FilePickerActivity extends ActionBarActivity{



    File currentDirectory = null;
    File[] currentFiles;
    ListView listView;
    FilesArrayAdapter adapter;
    ActionMode Mode = null;
    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

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

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Select a folder");
    }

    @Override
    public Intent getSupportParentActivityIntent () {
        System.out.println("up button");
        FilePickerActivity.this.setResult(-1);
        FilePickerActivity.this.finish();
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file_picker_activity, menu);
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
                FilePickerActivity.this.setResult(0,intent);
                FilePickerActivity.this.finish();
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
        List<File> sortedFiles = AppPreferenceManager.sortByPreference(files, this);
        currentFiles = sortedFiles.toArray(new File[sortedFiles.size()]);
        adapter = new FilesArrayAdapter(this, FileItem.fromFileList(sortedFiles));
        listView.setAdapter(adapter);
    }

}
