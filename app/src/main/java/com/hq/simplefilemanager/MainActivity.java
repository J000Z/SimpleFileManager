package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity
                        implements NoticeDialogFragment.NoticeDialogListener {



    File currentDirectory = null;
    File[] currentFiles;
    ListView listView;
    FilesArrayAdapter adapter;
    ActionMode Mode = null;
    boolean exitByBackButton = false;
    private static Context context;

    public class fileTask extends AsyncTask<FileOperation, Void, Integer> {
        ProgressDialogFragment dialog = new ProgressDialogFragment();
        FileOperation fileOperation;

        @Override
        protected Integer doInBackground(FileOperation... fileOperations) {
            this.fileOperation = fileOperations[0];
            return this.fileOperation.execute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                if (fileOperation.isDelete()) {
                    adapter.remove(fileOperation.sourceFiles);
                }
                if (fileOperation.isCopy() || fileOperation.isCut()) {
                    currentFiles = getDirectory(currentDirectory.getAbsolutePath());
                    inflateListView(currentFiles);
                }
            }
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {

            dialog.show(getFragmentManager(),"NoticeDialogFragment");

        }
    }



    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        System.out.println("Delete confirmed");
        fileTask task = new fileTask();
        task.execute(new FileOperation("delete",adapter.getSelectedFiles()));
        Mode.finish();
    }

    @Override
    public void onDialogGetInput(String operation, String input) {
        if (operation.equals("file")) {
            File newFile = new File(currentDirectory.getAbsolutePath() + "/" + input);
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (operation.equals("folder")) {
            File newFile = new File(currentDirectory.getAbsolutePath() + "/" + input);
            newFile.mkdir();
        }
        if (operation.equals("edit")) {
            File f = adapter.getSelectedFiles().get(0);
            File new_f = new File(f.getParentFile().getAbsolutePath() + "/" + input);
            f.renameTo(new_f);
            //System.out.println(f.getName()+" ---rename---> "+input);
            Mode.finish();
        }
        currentFiles = getDirectory(currentDirectory.getAbsolutePath());
        inflateListView(currentFiles);
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SharedPreferences p = getApplicationContext().getSharedPreferences("app_preference", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        MainActivity.context = getApplicationContext();

        listView = (ListView) findViewById(R.id.listView);
        currentFiles = getDirectory();
        inflateListView(currentFiles);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            currentDirectory = Environment.getExternalStorageDirectory();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println(currentFiles[position].getName() + " clicked");
                if (currentFiles[position].isDirectory()) {
                    currentDirectory = currentFiles[position];
                    currentFiles = getDirectory(currentDirectory.getAbsolutePath());
                    inflateListView(currentFiles);
                    exitByBackButton = false;
                } else {
                    Uri uri = Uri.fromFile(currentFiles[position]);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, MimeTypeManager.getMimeType(currentFiles[position]));
                    startActivity(intent);
                }
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                System.out.println("statechanged " + adapter.getFile(position).getName());
                ((FilesArrayAdapter)listView.getAdapter()).setSelection(position, checked);
                if (adapter.selectedCount() == 1) {
                    mode.getMenu().findItem(R.id.edit).setVisible(true);
                } else {
                    mode.getMenu().findItem(R.id.edit).setVisible(false);
                }
                if (adapter.isAllSelected()){
                    mode.getMenu().findItem(R.id.select).setTitle("Unselect all");
                } else {
                    mode.getMenu().findItem(R.id.select).setTitle("Select all");
                }
                //final int checkedCount = ((InteractiveArrayAdapter)listView.getAdapter()).selectedCount();
                //mode.setTitle(checkedCount + " Selected");
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.cut:
                        Mode = mode;
                        Intent intent1 = new Intent(MainActivity.this,FilePickerActivity.class);
                        intent1.putExtra("currentDirectory",currentDirectory.getAbsolutePath());
                        startActivityForResult(intent1,0);//0 for cut
                        return true;
                    case R.id.copy:
                        Mode = mode;
                        Intent intent2 = new Intent(MainActivity.this,FilePickerActivity.class);
                        intent2.putExtra("currentDirectory",currentDirectory.getAbsolutePath());
                        startActivityForResult(intent2,1);//1 for copy
                        return true;
                    case R.id.delete:
                        DialogFragment dialog = new NoticeDialogFragment();
                        dialog.show(getFragmentManager(),"confirm");
                        Mode = mode;
                        return false;
                    case R.id.select:
                        if (adapter.isAllSelected()) {
                            for (int i = 0; i < adapter.size(); i++) {
                                listView.setItemChecked(i,false);
                                adapter.setSelection(i, false);
                            }
                        } else {
                            for (int i = 0; i < adapter.size(); i++) {
                                if (!adapter.isSelected(i)) {
                                    listView.setItemChecked(i,true);
                                    adapter.setSelection(i,true);
                                }
                            }
                        }
                        return true;
                    case R.id.edit:
                        Mode = mode;
                        DialogFragment dialog3 = new NoticeDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("input", adapter.getSelectedFiles().get(0).getName());
                        dialog3.setArguments(args);
                        dialog3.show(getFragmentManager(),"edit");
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.action_multiselection, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                ((FilesArrayAdapter)listView.getAdapter()).unselectAll();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == -1) {return;}
        Bundle data = intent.getExtras();
        String selectedDirectory = data.getString("selectedDirectory");
        System.out.println("get >> " + selectedDirectory);
        if (requestCode == 0 && resultCode == 0) { //for cut
            fileTask task = new fileTask();
            task.execute(new FileOperation("cut",adapter.getSelectedFiles(),new File(selectedDirectory)));
            currentDirectory = new File(selectedDirectory);
            Mode.finish();
        }
        if (requestCode == 1 && resultCode == 0) { //for copy
            fileTask task = new fileTask();
            task.execute(new FileOperation("copy",adapter.getSelectedFiles(),new File(selectedDirectory)));
            currentDirectory = new File(selectedDirectory);
            Mode.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsMainActivity.class);
                startActivity(intent);
                //SettingsFragment settingsFragment = new SettingsFragment();
                //settingsFragment
                //settingsFragment.show(getFragmentManager(),"NoticeDialogFragment");
                return true;
            case R.id.up:
                toParentFolder();
                return true;
            case R.id.new_file:
                DialogFragment dialog1 = new NoticeDialogFragment();
                dialog1.show(getFragmentManager(),"input_file");
                return true;
            case R.id.new_folder:
                DialogFragment dialog2 = new NoticeDialogFragment();
                dialog2.show(getFragmentManager(),"input_folder");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toParentFolder() {
        if (currentDirectory == null || currentDirectory.equals(Environment.getExternalStorageDirectory())) {
            Toast.makeText(getApplicationContext(), "This is the top folder.", Toast.LENGTH_SHORT).show();
            exitByBackButton = true;
        } else {
            currentDirectory = currentDirectory.getParentFile();
            currentFiles = getDirectory(currentDirectory.getAbsolutePath());
            inflateListView(currentFiles);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitByBackButton) {
                finish();
                return true;
            } else {
                toParentFolder();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
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

        List<FileItem> file_list = new ArrayList<FileItem>();
        for (int i=0; i<files.length; i++){
            file_list.add(new FileItem(files[i]));
        }
        adapter = new FilesArrayAdapter(this,file_list);
        listView.setAdapter(adapter);

    }



}
