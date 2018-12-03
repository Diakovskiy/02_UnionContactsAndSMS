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

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            reallyExportContactsAndSMS();
        }
    }

    public void onImportContacts(View v){
        reallyImportContacts();

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


    public void reallyExportContactsAndSMS(){

        SimplePhoneNumber.getSimplePhoneNumbers(this);
        Toast.makeText(this, "Export sucess", Toast.LENGTH_LONG).show();

    }


    public void reallyImportContacts(){

        String name = "MY_DDD1";

        String phone = "0666285092";

        ArrayList<ContentProviderOperation> op = new ArrayList<ContentProviderOperation>();

        /* Добавляем пустой контакт */
        op.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        /* Добавляем данные имени */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        /* Добавляем данные телефона */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, op);
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }


    }

    public void reallyImportContacts2(){

        String newContact = "MY_NEW_CONTACT";


        ContentValues contactValues = new ContentValues();
        contactValues.put(ContactsContract.RawContacts.ACCOUNT_NAME, newContact);
        contactValues.put(ContactsContract.RawContacts.ACCOUNT_TYPE, newContact);
        Uri newUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contactValues);
        long rawContactsId = ContentUris.parseId(newUri);

        contactValues.clear();

        contactValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactsId);
        contactValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

        contactValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newContact);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contactValues);

        Toast.makeText(getApplicationContext(), newContact + " добавлен в список контактов", Toast.LENGTH_LONG).show();


        //Toast.makeText(this, "Really import contacts", Toast.LENGTH_LONG).show();

    }


}
