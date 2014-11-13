package com.hq.simplefilemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFileTypeSoloActivity extends FragmentActivity{

    PackageManager pk;
    AppPreferenceManager p_manager;
    String file_type;
    GridView gridView_open;
    //GridLayout gridlayout_share;
    boolean SETTING_CHANGED = false;
    final Activity activity = (Activity) this;

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
        gridView_open = (GridView) findViewById(R.id.gridlayout_open);
        //gridlayout_share = (GridLayout) findViewById(R.id.gridlayout_share);
        p_manager = new AppPreferenceManager(getSharedPreferences("app_preference",MODE_PRIVATE));
        pk = getPackageManager();
        gridView_open.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ShowIntentPicker();
            }
        });
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
                SETTING_CHANGED = true;
                endActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh(){
        String[] apps = p_manager.getAppsOfType(file_type);
        Drawable[] icons = null;
        if (apps != null) {
            System.out.println("intent num: " + apps.length);
            icons = new Drawable[apps.length];
            for (int i=0; i<apps.length; i++) {
                String packageName = p_manager.decodeInfo(apps[i])[0];
                String name = p_manager.decodeInfo(apps[i])[1];
                System.out.println("[" + i + "] " + packageName + " " + name);
                Intent intent = new Intent();
                intent.setClassName(packageName, name);
                List<ResolveInfo> resInfo = pk.queryIntentActivities(intent, 0);
                icons[i] = resInfo.get(0).loadIcon(pk);
            }
        }
        int s = getResources().getDimensionPixelSize(R.dimen.gridViewImageWidth);
        gridView_open.setAdapter(new ImageAdapter(this, icons, s));
    }

    public void ShowIntentPicker() {
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
                if (apps.contains(p_manager.encodeInfo(packageNames[i], names[i]))) {
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
        final ArrayAdapterWithIconAndCheckBox listAdapter = new ArrayAdapterWithIconAndCheckBox(activity, labels, icons, checkedItems);

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setTitle("Select App")
                .setSingleChoiceItems(listAdapter, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        //String app = encodeInfo(packageNames[index], names[index]);
                        //p_manager.addAppOfType(file_type, app);
                        //SETTING_CHANGED = true;
                        //refresh();
                        System.out.println("select: " + packageNames[index] + " " + names[index]);
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
                                apps.add(p_manager.encodeInfo(packageNames[j], names[j]));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            endActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void endActivity(){
        int SETTING_CHANGED_FLAG = SETTING_CHANGED ? 1 : 0;
        Intent intent = getIntent();
        intent.putExtra("file_type",file_type);
        SettingsFileTypeSoloActivity.this.setResult(SETTING_CHANGED_FLAG,intent);
        SettingsFileTypeSoloActivity.this.finish();
    }

}
