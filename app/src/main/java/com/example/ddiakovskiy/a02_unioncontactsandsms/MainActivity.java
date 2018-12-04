package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;
import android.view.View;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends Activity {

    static String TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onExportContactsAndSMS(View v){

        Log.d(TAG, "onExportContacts: exporting");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            reallyExportContactsAndSMS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                reallyExportContactsAndSMS();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onImportContacts(View v){
        reallyImportContacts();
    }

    public void reallyExportContactsAndSMS(){

        PhoneBookUtil.ExportPhoneNumbersToFile(this);

    }

    public void reallyImportContacts(){

        PhoneBookUtil.ImportPhoneNumbersFromFile(this);



    }

}
