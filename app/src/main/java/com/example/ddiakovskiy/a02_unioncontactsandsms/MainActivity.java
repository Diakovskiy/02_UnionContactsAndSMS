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
import android.provider.Telephony;
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

        if (isNeedToGetPermission()) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS_AND_SMS);
        } else {
            reallyExportContactsAndSMS();
        }
    }

    boolean isNeedToGetPermission(){

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED);

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
                UIUtil.showFatalError(this, "Until you do not grant permissions, we cannot do this job");
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

        int ErrorStatus      = 0;
        String statusMessage = "";

        PhoneBookUtil.ImportPhoneNumbersFromFile(this, ErrorStatus, statusMessage);
        SMSUtil.ImportSMSFromFile(this);

        if (ErrorStatus == 0) {
            UIUtil.showOKResult(this, statusMessage);
        } else {
            UIUtil.showFatalError(this, statusMessage);
        }


    }

    public void onInstallAsSMSApp(View v){

        if (isNeedToSetDefaultSMSApp()){

            final String myPackageName      = getPackageName();
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
            startActivityForResult(intent, 1);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Activity mActivity = this;

        if(isNeedToSetDefaultSMSApp()){
            UIUtil.showFatalError(this, "something goes wrong, i can't change default sms app");
        }else {
            Toast.makeText(this, "SUCESSFULLY SET SMS APPLICATION", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean isNeedToSetDefaultSMSApp(){

        boolean canWriteSMS = false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            canWriteSMS = true;
        }else {

            final String myPackageName      = getPackageName();
            final String defaultPackageName = Telephony.Sms.getDefaultSmsPackage(this);

            if (defaultPackageName.equals(myPackageName)) {
                canWriteSMS = true;
            }else {
                canWriteSMS = false;
            }
        }

        return !canWriteSMS;

    }

}
