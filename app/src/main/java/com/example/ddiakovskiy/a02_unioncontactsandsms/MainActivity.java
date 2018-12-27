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

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS_AND_SMS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onExportContactsAndSMS(View v){

        Log.d(TAG, "onExportContacts: exporting");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_CONTACTS_AND_SMS);
        } else {
            reallyExportContactsAndSMS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS_AND_SMS) {

            boolean permissionGranted = true;
            for (int currentGruntResult: grantResults){
                if (currentGruntResult != PackageManager.PERMISSION_GRANTED){
                    permissionGranted = false;
                    break;
                }
            }


            if (permissionGranted) {
                reallyExportContactsAndSMS();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot do the job", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onImportContactsAndSMS(View v){
        reallyImportContactsAndSMS();
    }

    public void reallyExportContactsAndSMS(){

        PhoneBookUtil.ExportPhoneNumbersToFile(this);
        SMSUtil.ExportSMSToFile(this);
    }

    public void reallyImportContactsAndSMS(){

        PhoneBookUtil.ImportPhoneNumbersFromFile(this);
        SMSUtil.ImportSMSFromFile(this);


    }

}
