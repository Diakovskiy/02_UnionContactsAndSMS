package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;






class PhoneNumber{
    String number;
    String type;

    public PhoneNumber(String phoneNumber,int phoneType){
        this.type   = ""+phoneType;
        this.number = phoneNumber;
    }
}

public class Contact {
    public String id;
    public String name;
    public ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();

    public static ArrayList<Contact> getContacts(Activity cn){

        ArrayList<Contact> resultContacts = new ArrayList<>();


        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor contacts = cn.managedQuery(contactUri, null, null, null, null);
        ContentResolver contentResolver = cn.getContentResolver();

        contacts.moveToFirst();

        do {
            Cursor cursor = contacts;
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                while (phoneCursor.moveToNext()) {
                    int phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                    logIT("" + name + " " + phoneNumber + " "+phoneType); //ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    Contact newContact = new Contact();
                    newContact.name = name;
                    newContact.phoneNumbers.add(new PhoneNumber(phoneNumber,phoneType));
                    resultContacts.add(newContact);
                }
                phoneCursor.close();
            }

        } while (contacts.moveToNext());

        contacts.close();




        return resultContacts;
    }





    public static void logIT(String logMessage){

        Log.d("ololo", logMessage);
        //getBaseContext()
       // Toast.makeText(getApp, logMessage, Toast.LENGTH_LONG).show();
    }

}
