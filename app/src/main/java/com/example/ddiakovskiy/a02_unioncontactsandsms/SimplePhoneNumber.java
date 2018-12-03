package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * Created by root on 21.04.2018.
 */

class SimpleContactData{

    public SimpleContactData(String telName,String telNumber){
        this.telName   = telName;
        this.telNumber = telNumber;
    }

    private String telNumber;
    private String telName;

    public String getTelNumber(){
        return telNumber;
    }

    public String getTelName() {
        return telName;
    }

    @Override
    public String toString(){
        return telName +" "+ telNumber;
    }
}

public class SimplePhoneNumber {

    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NUMBER = Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = Phone.CONTACT_ID;


    static public void getFromUri(List<SimpleContactData> phoneNumbers, ContentResolver resolver, Uri contactUri, String filterAccountType, String contactNameColumn, String contactNumberColumn) {
        Cursor cursor = resolver.query(contactUri, null,
                null, null, null);

        cursor.moveToFirst();
        do {
            if(filterAccountType.isEmpty()){
                for(int i=0;i<cursor.getColumnCount();i++){
                    System.out.println(cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
            }else {
                int indexOfAccountType = cursor.getColumnIndex("account_type");
                int indexNumber        = cursor.getColumnIndex(contactNumberColumn);
                int indexName          = cursor.getColumnIndex(contactNameColumn);

                String AccountType     = cursor.getString(indexOfAccountType);
                if (AccountType.equals(filterAccountType)){
                    System.out.println(cursor.getString(indexName) + ": " + cursor.getString(indexNumber));
                }



            }

        } while (cursor.moveToNext());

        cursor.close();
    }

    static public void get(Activity activity) {

        ContentResolver resolver = activity.getContentResolver();

        List<SimpleContactData> phoneNumbers = new ArrayList<>();


        getFromUri(phoneNumbers,resolver, ContactsContract.RawContacts.CONTENT_URI,"org.telegram.messenger","display_name","sync1"); //telegram
        //getFromUri(phoneNumbers,resolver, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,"","",""); //google


        //display_name: Паша Муж Марины Хозяйки Квартиры
        //телеграм sync1: 380503564643
        //account_type: org.telegram.messenger


        //account_name: mygsync1@gmail.com
        //account_type_and_data_set: com.google
        //account_type: com.google


        //Uri contactUri = ContactsContract.RawContacts.CONTENT_URI;
        //Uri contactUri = ContactsContract.RawContactsEntity.CONTENT_URI;
        //Uri contactUri = ContactsContract.Contacts.CONTENT_URI;



//
//        do {
//
//            //String AccountType = contacts.getString(indexOfAccountType);
//            //if (!AccountType.equals("org.telegram.messenger")){
//                for(int i=0;i<contacts.getColumnCount();i++){
//                    System.out.println(contacts.getColumnName(i) + ": " + contacts.getString(i));
//                }
//            //}
//
//            System.out.println("============\n\n");
//
//        } while (contacts.moveToNext());

        //contacts.close();



//        Cursor results = resolver.query(
//                uri,
//                projection,
//                selection,
//                selectionArgs,
//                null
//        );
//
//        // create array of Phone contacts and fill it
//        final ArrayList<Contact> phoneContacts = new ArrayList<>();
//
//        int indexName = results.getColumnIndex(PhoneLookup.DISPLAY_NAME);
//        int indexType = results.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//        int indexLabel = results.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
//        int indexNumber = results.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//
//
//
//        while (results.moveToNext()) {
//            int type = results.getInt(indexType);
//            String custom = results.getString(indexLabel);
//
//            Log.d("tg", "get: "+results.getString(indexName)+results.getString(indexNumber)+"tp"+results.getString(indexType));
//
//            //phoneContacts.add(phoneContact);
//        }
//        results.close();
        //return phoneContacts;
    }







    static void getSimplePhoneNumbers(Activity activity){

        get(activity);



//        List<String> contacts = new ArrayList<>();
//        // Get the ContentResolver
//        ContentResolver cr = activity.getContentResolver();
//        // Get the Cursor of all the contacts
//        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, null, null, null);
//
//        // Move the cursor to first. Also check whether the cursor is empty or not.
//        if (cursor.moveToFirst()) {
//            // Iterate through the cursor
//            do {
//                // Get the contacts name
//
//                int dn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//                int nm = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                //ContactsContract.Contacts.Data.
//
//
//                String name = cursor.getString(dn) + cursor.getString(nm);
//
//                Log.d("tg", "getSimplePhoneNumbers: "+name);
//
//                contacts.add(name);
//            } while (cursor.moveToNext());
//        }
//        // Close the curosor
//        cursor.close();
//
//        //return contacts;

    }

}
