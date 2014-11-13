package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFileTypeListActivity extends ListActivity
                              implements NoticeDialogFragment.NoticeDialogListener{

    AppPreferenceManager p_manager;
    SettingsFileTypeListAdapter mAdapter;
    PackageManager pk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_list);
        //typeListActivity.context = getApplicationContext();
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Application preference");
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(true);
        pk = getPackageManager();
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

    public void refresh(){
        String[] file_types = p_manager.getTypes();
        mAdapter = new SettingsFileTypeListAdapter(R.layout.type_list, getApplicationContext(), Arrays.asList(file_types), pk, p_manager);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String type = mAdapter.getItem(position);
        System.out.println("Open preference for " + type);
        pickIntent(type);
        //Intent intent = new Intent(SettingsFileTypeListActivity.this, SettingsFileTypeSoloActivity.class);
        //intent.putExtra("file_type", type);
        //startActivityForResult(intent, OPEN_SETTING_REQUEST);//request_code
    }

    private void pickIntent(String type) {
        final String file_type = type;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File("/tmp/xxx" + file_type));
        intent.setDataAndType(uri, MimeTypeManager.getMimeType(file_type));

        List<ResolveInfo> resInfo = pk.queryIntentActivities(intent, 0);

        final String[] names = new String[resInfo.size()];
        final String[] packageNames = new String[resInfo.size()];
        final String[] labels = new String[resInfo.size()];
        Drawable[] icons = new Drawable[resInfo.size()];
        for (int i = 0; i < resInfo.size(); i++) {
            names[i] = resInfo.get(i).activityInfo.name;
            packageNames[i] = resInfo.get(i).activityInfo.packageName;
            labels[i] = (String) resInfo.get(i).loadLabel(pk);
            icons[i] = resInfo.get(i).loadIcon(pk);
        }

        final Boolean[] checkedItems = new Boolean[labels.length];
        if (p_manager.getAppsOfType(file_type) != null) {
            List<String> apps = Arrays.asList(p_manager.getAppsOfType(file_type));
            for (int i = 0; i < labels.length; i++) {
                if (apps.contains(encodeInfo(packageNames[i], names[i]))) {
                    checkedItems[i] = true;
                } else {
                    checkedItems[i] = false;
                }
            }
        } else {
            for (int i = 0; i < labels.length; i++) {
                checkedItems[i] = false;
            }
        }
        System.out.println(Arrays.asList(checkedItems));

        final ArrayAdapterWithIconAndCheckBox listAdapter = new ArrayAdapterWithIconAndCheckBox(this, labels, icons, checkedItems);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Select App")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        //String app = encodeInfo(packageNames[index], names[index]);
                        //p_manager.addAppOfType(file_type, app);
                        //refresh();
                        //System.out.println("select: " + packageNames[index] + " " + names[index]);
                        //Toast.makeText(activity, "Item Selected: " + item, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Boolean> checkedItems = listAdapter.getCheckedItems();
                        List<String> apps = new ArrayList<String>();
                        for (int j=0; j<checkedItems.size(); j++) {
                            if (checkedItems.get(j)) {
                                apps.add(encodeInfo(packageNames[j],names[j]));
                            }
                        }
                        String[] appsArray = new String[apps.size()];
                        apps.toArray(appsArray); // fill the array
                        p_manager.setAppOfType(file_type, appsArray);
                        refresh();
                        System.out.println(checkedItems);
                    }
                });
        dialog.show();
    }

    private String encodeInfo(String packageName, String name) {
        return "[" + packageName + " " + name + "]";
    }

    private String[] decodeInfo(String app) {
        String[] info = app.substring(1,app.length()-1).split(" ");
        return  info;
    }
}
