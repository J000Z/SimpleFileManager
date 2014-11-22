package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity
                        implements NoticeDialogFragment.NoticeDialogListener {



    private ShareActionProvider mShareActionProvider;
    File currentDirectory = null;
    File[] currentFiles;
    ListView listView;
    FilesArrayAdapter adapter;
    ActionMode Mode = null;
    boolean exitByBackButton = false;
    private static Context context;

    static int cut_request = 0;
    static int copy_request = 1;
    static int preference_request = 3;

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
        setContentView(R.layout.main_activity);
        MainActivity.context = getApplicationContext();

        listView = (ListView) findViewById(R.id.listView);
        currentFiles = getDirectory();
        inflateListView(currentFiles);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            currentDirectory = Environment.getExternalStorageDirectory();
        }

        //click on the file or folder item in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println(currentFiles[position].getName() + " clicked");
                if (currentFiles[position].isDirectory()) {
                    //enter the directory
                    currentDirectory = currentFiles[position];
                    currentFiles = getDirectory(currentDirectory.getAbsolutePath());
                    inflateListView(currentFiles);
                    exitByBackButton = false;
                } else {
                    //open the file
                    openFile(currentFiles[position]);
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
                setShareIntent((((FilesArrayAdapter) listView.getAdapter()).getSelectedFiles()), mode.getMenu());
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
                        startActivityForResult(intent1, cut_request);//0 for cut
                        return true;
                    case R.id.copy:
                        Mode = mode;
                        Intent intent2 = new Intent(MainActivity.this,FilePickerActivity.class);
                        intent2.putExtra("currentDirectory",currentDirectory.getAbsolutePath());
                        startActivityForResult(intent2,copy_request);//1 for copy
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
                inflater.inflate(R.menu.menu_main_activity_multi_selection, menu);
                MenuItem item = menu.findItem(R.id.share);
                // Fetch and store ShareActionProvider
                mShareActionProvider = (ShareActionProvider) item.getActionProvider();
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
        if (requestCode == preference_request) {
            if (resultCode == 1) {
                currentFiles = getDirectory(currentDirectory.getAbsolutePath());
                inflateListView(currentFiles);
            }
            return;
        }
        if (resultCode == -1) {return;}
        Bundle data = intent.getExtras();
        String selectedDirectory = data.getString("selectedDirectory");
        System.out.println("get >> " + selectedDirectory);
        if (requestCode == cut_request && resultCode == 0) { //for cut
            fileTask task = new fileTask();
            task.execute(new FileOperation("cut",adapter.getSelectedFiles(),new File(selectedDirectory)));
            currentDirectory = new File(selectedDirectory);
            Mode.finish();
        }
        if (requestCode == copy_request && resultCode == 0) { //for copy
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
        inflater.inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsMainActivity.class);
                startActivityForResult(intent, preference_request);
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
        System.out.println("inflateView");
        List<File> sortedFiles = AppPreferenceManager.sortByPreference(files, this);
        currentFiles = sortedFiles.toArray(new File[sortedFiles.size()]);
        adapter = new FilesArrayAdapter(this, FileItem.fromFileList(sortedFiles));
        listView.setAdapter(adapter);
    }

    public void openFile(File f) {
        Uri uri = Uri.fromFile(f);
        String MimeType = MimeTypeManager.getMimeType(f);
        String postFix = MimeTypeManager.getPostFix(f.getName());;
        AppPreferenceManager p_manager = new AppPreferenceManager(getSharedPreferences("app_preference",MODE_PRIVATE));
        if (p_manager.isFileTypeExist(postFix)) {
            String[] apps = p_manager.getAppsOfType(postFix);
            if (apps.length == 1) { //open directly
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, MimeType);
                intent.setClassName(p_manager.decodePackageName(apps[0]), p_manager.decodeName(apps[0]));
                startActivity(intent);
                return;
            } else {
                openWithDialog(f, apps, p_manager);
                return;
            }
        } else {
            openWithDialog(f);
            return;
        }
    }

    public void openWithDialog(File file) { //the default one
        final File f = file;
        PackageManager pk = getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final String type = MimeTypeManager.getMimeType(f);
        Uri uri = Uri.fromFile(f);
        intent.setDataAndType(uri, type);
        List<ResolveInfo> resInfo = pk.queryIntentActivities(intent, 0);
        int length = resInfo.size();
        final String[] names = new String[length];
        final String[] packageNames = new String[length];
        final String[] labels = new String[length];
        final Drawable[] icons = new Drawable[length];
        for (int i = 0; i < resInfo.size(); i++) {
            names[i] = resInfo.get(i).activityInfo.name;
            packageNames[i] = resInfo.get(i).activityInfo.packageName;
            labels[i] = (String) resInfo.get(i).loadLabel(pk);
            icons[i] = resInfo.get(i).loadIcon(pk);
        }
        final ArrayAdapterWithIcon listAdapter = new ArrayAdapterWithIcon(this, labels, icons);
        final myInt selectIndex = new myInt(-1);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Open with")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        if (index == selectIndex.get()) {
                            intent.setClassName(packageNames[selectIndex.get()], names[selectIndex.get()]);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                        System.out.println("click: " + packageNames[index] + " " + names[index]);
                        listAdapter.selectItem(index);
                        selectIndex.set(index);
                    }
                })
                .setPositiveButton("Always", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String postFix = MimeTypeManager.getPostFix(f.getName());
                        System.out.println("add app to type: " + postFix);
                        AppPreferenceManager p_manager = new AppPreferenceManager(getSharedPreferences("app_preference",MODE_PRIVATE));
                        p_manager.addAppOfType(postFix, p_manager.encodeInfo(packageNames[selectIndex.get()], names[selectIndex.get()]));
                        intent.setClassName(packageNames[selectIndex.get()], names[selectIndex.get()]);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("Just once", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent.setClassName(packageNames[selectIndex.get()], names[selectIndex.get()]);
                        startActivity(intent);
                    }
                });
        dialog.show();
    }

    public class myInt{
        private int v;

        myInt(int v) {
            this.v = v;
        }

        public void set(int v) {
            this.v = v;
        }

        public int get() {
            return v;
        }
    }

    public void openWithDialog(File file, String[] apps_array, AppPreferenceManager p_manager) {
        List<String> apps = Arrays.asList(apps_array);
        final File f = file;
        PackageManager pk = getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = MimeTypeManager.getMimeType(f);
        Uri uri = Uri.fromFile(f);
        intent.setDataAndType(uri, type);
        List<ResolveInfo> resInfo = pk.queryIntentActivities(intent, 0);
        final List<String> names = new ArrayList<String>();
        final List<String> packageNames = new ArrayList<String>();
        final List<String> labels = new ArrayList<String>();
        final List<Drawable> icons = new ArrayList<Drawable>();
        for (int i = 0; i < resInfo.size(); i++) {
            String packageName = resInfo.get(i).activityInfo.packageName;
            String name = resInfo.get(i).activityInfo.name;
            if (apps.contains(p_manager.encodeInfo(packageName, name))) {
                names.add(name);
                packageNames.add(packageName);
                labels.add((String) resInfo.get(i).loadLabel(pk));
                icons.add(resInfo.get(i).loadIcon(pk));
            }
        }
        System.out.print(labels);
        final ArrayAdapterWithIcon listAdapter = new ArrayAdapterWithIcon(this, labels, icons);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Open with")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        intent.setClassName(packageNames.get(index), names.get(index));
                        startActivity(intent);
                        dialog.dismiss();
                        System.out.println("click: " + packageNames.get(index) + " " + names.get(index));
                    }
                })
                .setNegativeButton("More", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openWithDialog(f);
                    }
                });
        dialog.show();
    }

    private void setShareIntent(List<File> files, Menu menu) {
        if (mShareActionProvider == null)
            return;
        MenuItem shareButton = menu.findItem(R.id.share);
        if (files.size() == 1) {
            File f = files.get(0);
            if (f.isDirectory()) {
                shareButton.setVisible(false);
                return;
            } else {
                Uri uri = Uri.fromFile(f);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setDataAndType(uri, MimeTypeManager.getMimeType(f));
                mShareActionProvider.setShareIntent(intent);
                shareButton.setVisible(true);
                return;
            }
        } else {
            ArrayList<Uri> theUris = new ArrayList<Uri>();
            String theOverallMIMEtype = null;
            String theMIMEtype = null;
            String theOverallMIMEcategory = null;
            String theMIMEcategory = null;
            for (File f : files) {
                if (f.isDirectory()) {
                    shareButton.setVisible(false);
                    return;
                }
                theMIMEtype = MimeTypeManager.getMimeType(f.getName());
                theMIMEcategory = MimeTypeManager.getMIMECategory(theMIMEtype);
                if (theOverallMIMEtype!=null) {
                    if (!theOverallMIMEtype.equals(theMIMEtype)) {
                        if (!theOverallMIMEcategory.equals(theMIMEcategory)) {
                            theOverallMIMEtype = "multipart/mixed";
                            break;  //no need to keep looking at the various types
                        } else {
                            theOverallMIMEtype = theOverallMIMEcategory + "/*";
                        }
                    }
                } else {
                    theOverallMIMEtype = theMIMEtype;
                    theOverallMIMEcategory = MimeTypeManager.getMIMECategory(theOverallMIMEtype);
                }
                theUris.add(Uri.fromFile(f));
            }
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, theUris);
            intent.setType(theOverallMIMEtype);
            mShareActionProvider.setShareIntent(intent);
            shareButton.setVisible(true);
        }
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
    }*/
}
