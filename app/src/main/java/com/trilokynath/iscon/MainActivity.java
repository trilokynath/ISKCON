package com.trilokynath.iscon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity {

    ListView list;
    ArrayAdapter<DATA> adapter;
    ArrayList<DATA> data;
    DataBaseHelper dataBaseHelper;
    ProgressDialog progressBar;
    int progressBarStatus = 0;
    long fileSize = 0;
    String ename = "";
    String emobile = "";
    String eaddress = "";
    EditText editText1,editText2,editText3;
    boolean isDialogeOpen;
    Dialog alertDialog = null;
    RadioGroup radioGroup;
    RadioButton radioButton;

    android.support.v7.app.ActionBar actionBar;
    TranslateAnimation shake;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, new RuntimeException(e));
            }
        });

        setContentView(R.layout.activity_main);


        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.actionbar, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(view);
        TextView t1 = view.findViewById(R.id.app_name);
        t1.setText("ISKCON TEMPLE");
        t1.setTextColor(Color.parseColor("#FFFFFF"));


        list = (ListView) findViewById(R.id.list);
        dataBaseHelper = new DataBaseHelper(this);
        data = new ArrayList<>();
        gettingPermissions();

        data = dataBaseHelper.getData();
        setAdapter(data);

        //   Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialoge();
            }
        });

        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                final int checkedItemCount = list.getCheckedItemCount();
                actionMode.setTitle(checkedItemCount + " Contacts Selected");

                list.getCheckedItemPositions();
                SparseBooleanArray a = list.getCheckedItemPositions();

                StringBuffer sb = new StringBuffer("");
                for (int ii = 0; ii < a.size(); ii++) {

                    if (a.valueAt(ii)) {
                        int idx = a.keyAt(ii);

                        if (sb.length() > 0)
                            sb.append(", ");


                        DATA hr = (DATA) list.getAdapter().getItem(idx);
                        sb.append(hr.getName());
                    }
                }
                //Toast.makeText(getApplicationContext(), sb + "", Toast.LENGTH_SHORT).show();


            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.selectlistview, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        /*delete selected items*/

                        new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog))
                                .setTitle("Delete Contacts?")
                                .setMessage("Do you really want to delete selected contacts?")
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        list.getCheckedItemPositions();
                                        SparseBooleanArray a = list.getCheckedItemPositions();
                                        Integer checkedIemsCount = list.getCheckedItemCount();

                                        for (int ii = 0; ii < a.size(); ii++) {
                                            if (a.valueAt(ii)) {
                                                int idx = a.keyAt(ii);
                                                Integer id = data.get(idx).getID();
                                                    dataBaseHelper.deleteContact(id);
                                            }
                                        }
                                            data = dataBaseHelper.getData();

                                        actionMode.finish();
                                        setAdapter(data);

                                        Toast.makeText(MainActivity.this, checkedIemsCount + " Items Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                        return false;

                    case R.id.action_send_mail:
                        list.getCheckedItemPositions();
                        SparseBooleanArray a = list.getCheckedItemPositions();
                        Integer checkedIemsCount = list.getCheckedItemCount();

                        StringBuffer sb = new StringBuffer("");
                        for (int ii = 0; ii < a.size(); ii++) {

                            if (a.valueAt(ii)) {
                                int idx = a.keyAt(ii);

                                if (sb.length() > 0)
                                    sb.append(", ");

                                DATA d = (DATA) list.getAdapter().getItem(idx);
                                sb.append(d.getMobile());
                            }
                        }
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+sb));
                        startActivity(smsIntent);
                        actionMode.finish();
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
    }

    public void openDialoge(){
        final View v1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.adddata, null);
        isDialogeOpen = true;
        if (v1.getParent() == null) {
            alertDialog = new Dialog(MainActivity.this, R.style.ThemeWithCorners);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setContentView(v1);
            alertDialog.show();
            alertDialog.setCancelable(false);
        }

        editText1 = v1.findViewById(R.id.input_name);
        editText2 = v1.findViewById(R.id.input_mobile);
        editText3 = v1.findViewById(R.id.input_address);
        radioGroup= (RadioGroup) v1.findViewById(R.id.radioGroup);
        Button save = v1.findViewById(R.id.save);
        ImageView close = v1.findViewById(R.id.close);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean save = false;

                int selectedId= radioGroup.getCheckedRadioButtonId();
                radioButton= (RadioButton) v1.findViewById(selectedId);
                String Person = radioButton.getText().toString();
                if(!editText1.getText().toString().trim().isEmpty()){
                    if(isMobileValid(editText2.getText().toString().trim())){
                        if(!editText3.getText().toString().trim().isEmpty()){
                            DATA data1 = new DATA(editText1.getText().toString().trim(), editText2.getText().toString().trim(), editText3.getText().toString().trim(), Person);
                            if(!dataBaseHelper.getRecord(editText2.getText().toString().trim())) {
                                dataBaseHelper.addRecords(data1);
                                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        data = dataBaseHelper.getData();
                                        setAdapter(data);
                                    }
                                });
                                editText1.setText("");
                                editText2.setText("");
                                editText3.setText("");
                                save = true;
                            }else{
                                Toast.makeText(getApplicationContext(), "Mobile Number Already Exist", Toast.LENGTH_SHORT).show();
                                editText2.startAnimation(shake);
                            }
                        }
                    }
                }
                if(!save) {
                    if (editText1.getText().toString().trim().isEmpty())
                        editText1.startAnimation(shake);
                    if (!isMobileValid(editText2.getText().toString().trim()))
                        editText2.startAnimation(shake);
                    if (editText3.getText().toString().trim().isEmpty())
                        editText3.startAnimation(shake);
                }

            }
        });

        final Dialog finalAlertDialog = alertDialog;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialogeOpen = false;
                finalAlertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                data = dataBaseHelper.getData();
                setAdapter(data);
                return true;

            case R.id.action_export_data:
                exportData();
                break;

            case R.id.clear_data:

                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog))
                        .setTitle("Delete Contacts?")
                        .setMessage("Do you really want to clear all contacts?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                exportData();
                                Toast.makeText(getApplicationContext(),"Backup is saved", Toast.LENGTH_SHORT).show();
                                dataBaseHelper.clearData();
                                data = dataBaseHelper.getData();
                                setAdapter(data); }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void exportData(){
        data = dataBaseHelper.getData();
        File sdcard0 = Environment.getExternalStoragePublicDirectory("");

        String fileName = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

        File file = new File(sdcard0, "Iskcon_Data_"+fileName+".csv");
        //    StringBuffer text = new StringBuffer("");

        List<String[]> strdata = new ArrayList<String[]>();

        for (DATA data1 : data)
            strdata.add(new String[]{data1.getName(), data1.getMobile(), data1.getAddress(), data1.getPerson()});
        Log.d("STATUS", "AFTER GETTING DATA");
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));
            Log.d("STATUS", "AFTER READING CSV READER");
            writer.writeAll(strdata);
            writer.close();

            notifyExporting(file);
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    MenuItem about;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview, menu);

        about = menu.findItem(R.id.about);

        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent myIntent = new Intent(getApplicationContext(), About.class);
                startActivityForResult(myIntent, 0);

                return false;
            }
        });

        final SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (s != null && !s.isEmpty()) {
                    //    setAdapter(dataBaseHelper.getSearchData(s));

                    progressBar = new ProgressDialog(MainActivity.this);
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Searching...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    progressBarStatus = 0;

                    fileSize = 0;
                    new Thread(new Runnable() {
                        public void run() {
                            final ArrayList<DATA> newData;
                            newData = new Searching().onSearch(dataBaseHelper.getData(), s);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (newData.isEmpty())
                                        Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();


                                    adapter = new MyListAdapter(MainActivity.this, R.layout.listview, newData);
                                    data = newData;
                                    MainActivity.this.<ListView>findViewById(R.id.list).setAdapter(adapter);

                                    progressBar.dismiss();
                                }
                            });


                        }
                    }).start();

                }
                //   Toast.makeText(getApplicationContext(),dataBaseHelper.getAlumniCount()+"",Toast.LENGTH_SHORT).show();
                //   Toast.makeText(getApplicationContext(),hrList.size()+"",Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // do your search on change or save the last string in search
                if (s.isEmpty()) {
                    if (adapter.getCount() != dataBaseHelper.getDATACount()) {
                        data = dataBaseHelper.getData();
                        setAdapter(data);
                    }
                    return false;
                }
                return true;
            }
        });
        // you can get query
        searchView.getQuery();

        return true;
    }


    public void onSearchContact(String s) {
        ArrayList<DATA> newData = new ArrayList<>();
        newData= new Searching().onSearch(dataBaseHelper.getData(), s);

        if (newData.isEmpty())
            Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();
        adapter = new MyListAdapter(this, R.layout.listview, newData);
        data = newData;
        list.setAdapter(adapter);
    }


    public void setAdapter(ArrayList<DATA> data) {
        if (adapter != null) adapter.clear();
        adapter = new MyListAdapter(MainActivity.this, R.layout.listview, data);
        list.setAdapter(adapter);
    }

    public void notifyExporting(File file) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_success))
                        .setContentTitle("File Exported Successfully!")
                        .setContentText("Check your file in INTERNAL STORAGE")
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent();
        notificationIntent.setAction(android.content.Intent.ACTION_VIEW);
        notificationIntent.setDataAndType(Uri.fromFile(file), getMimeType(file.getAbsolutePath()));

        //Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public boolean gettingPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION", "Permission is granted");
            } else {
                Log.v("GETTING PERMISSIONS", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSION", "Permission is granted");
            return true;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Peform your task here if any
                } else {
                    gettingPermissions();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    static boolean isMobileValid(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;
    }
    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "trilokynathwagh@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "T&P Contact App Bugs");
        emailIntent.putExtra(Intent.EXTRA_TEXT, e.toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
        finish();
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    @Override
    protected void onPause() {
        if(isDialogeOpen){
            ename = editText1.getText().toString();
            emobile = editText2.getText().toString();
            eaddress = editText3.getText().toString();
            alertDialog.cancel();
            alertDialog.dismiss();
            Log.d("STATUS", isDialogeOpen+"");
        }
        Log.d("ONPAUSE STATUS", isDialogeOpen+"");
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ONRESUME STATUS", isDialogeOpen+"");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(isDialogeOpen) {
            ename = editText1.getText().toString();
            emobile = editText2.getText().toString();
            eaddress = editText3.getText().toString();

            savedInstanceState.putBoolean("MyBoolean", isDialogeOpen);
            savedInstanceState.putString("ename", ename);
            savedInstanceState.putString("emobile", emobile);
            savedInstanceState.putString("eaddress", eaddress);
        }
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        isDialogeOpen = savedInstanceState.getBoolean("MyBoolean");
        String myString = savedInstanceState.getString("MyString");
        ename = savedInstanceState.getString("ename");
        emobile = savedInstanceState.getString("emobile");
        eaddress = savedInstanceState.getString("eaddress");
        if(isDialogeOpen){
            openDialoge();
            editText1.setText(ename);
            editText2.setText(emobile);
            editText3.setText(eaddress);
        }
    }
}

