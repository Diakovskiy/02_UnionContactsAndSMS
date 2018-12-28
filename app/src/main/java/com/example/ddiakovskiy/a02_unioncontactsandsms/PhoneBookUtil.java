package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


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
        return telName +" ; "+ telNumber;
    }
}

public class PhoneBookUtil {

    private static final String TAG = "PhoneBookUtil";

    static public void exploreUri(ArrayList<SimpleContactData> phoneNumbers, ContentResolver resolver, Uri contactUri,String filterAccountType, String accountTypeColumnName, String displayNameColumnName, String phoneNumber1ColumnName, String phoneNumber2ColumnName) {
        Cursor cursor = resolver.query(contactUri, null,
                null, null, null);

        if (cursor.moveToFirst()){
            do {
                if (!filterAccountType.equals("")){
                    int indexOfAccountType = cursor.getColumnIndex(accountTypeColumnName);
                    String account_type = cursor.getString(indexOfAccountType);
                    if(!account_type.equals(filterAccountType)){
                        continue;
                    }
                }

                Log.d(TAG, "-------------------------------------------------------------------");

                String displayName = null;
                String phoneNumber = null;

                int dispNameId  = cursor.getColumnIndex(displayNameColumnName);
                int data4Id     = cursor.getColumnIndex(phoneNumber1ColumnName);
                int data1Id     = cursor.getColumnIndex(phoneNumber2ColumnName);

                displayName =cursor.getString(dispNameId);
                phoneNumber =cursor.getString(data4Id);

                if (phoneNumber == null){
                    phoneNumber =cursor.getString(data1Id);
                }

                if (phoneNumber == null){
                    Log.d(TAG, "exploreUri: it is null");
                    phoneNumber = "DATA NOT FOUND";
                } else {

                }

                Log.d(TAG, "detected " + displayName + ":" + phoneNumber);

                phoneNumbers.add(new SimpleContactData(displayName, phoneNumber));

                for(int i=0;i<cursor.getColumnCount();i++){
                    Log.d(TAG, ""+cursor.getColumnName(i) + ": " + cursor.getString(i));
                }

            } while (cursor.moveToNext());

            cursor.close();

        }
    }

    static String ExportPhoneNumbersToFile(Activity activity){

        ArrayList<SimpleContactData> phoneNumbers = new ArrayList<>();

        ReadContactsFromPhone(activity, phoneNumbers);

        SaveContactsToFile(activity, phoneNumbers, "MY_ContactsDB");

    }


    static void ReadContactsFromPhone(Activity activity, ArrayList<SimpleContactData> phoneNumbers){

        ContentResolver resolver = activity.getContentResolver();

        //account_type: com.google
        exploreUri(phoneNumbers,
                resolver,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                "vnd.android.cursor.item/phone_v2",
                "mimetype",
                "display_name",
                "data4",
                "data1");


    }

    static void SaveContactsToFile(Activity activity, ArrayList<SimpleContactData> phoneNumbers, String filename) {

        String csvExportFolder = Environment.getExternalStorageDirectory()+ "/";
        String csvFileName = null;
        File csvFile       = null;
        int fileCount = 1;

        do {
            csvFileName = csvExportFolder + filename + fileCount + ".csv";
            csvFile = new File(csvFileName);
            fileCount = fileCount + 1;
        } while (csvFile.exists());

        try {

            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFileName));
            for(SimpleContactData phoneNumber: phoneNumbers){

                String []stringLine = new String[2];

                stringLine[0] = phoneNumber.getTelName();
                stringLine[1]=  phoneNumber.getTelNumber();

                csvWriter.writeNext(stringLine);
            }

            csvWriter.close();

            String report = "Sucessfully exported " + phoneNumbers.size()+" entries to file " + csvFileName;
            Toast.makeText(activity, report, Toast.LENGTH_LONG).show();
            Log.d(TAG, "saveToFile: "+report);

        }catch (IOException exp){
            exp.printStackTrace();
        }


    }


    static void CreateNewRecordInPhone(Activity activity, SimpleContactData phoneNumber) throws OperationApplicationException, RemoteException {

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
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, phoneNumber.getTelName())
                .build());

        /* Добавляем данные телефона */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber.getTelNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, op);
    }

    static String ImportPhoneNumbersFromFile(Activity activity, Integer ErrorStatus) throws Exception {

        String report = "";

        int recordsInFile         = 0;
        int recordsImported       = 0;
        String[] csvLine          = null;

        //*** 1) read contacts from file ---------------------------------------------------------------------------------------------------------------------------------------
        ArrayList<SimpleContactData> phoneNumbersFromFile = new ArrayList<>();
        String filename                                   = FileUtil.getPhoneNumbersImportFileName();

        File fileToRead                                   = new File(filename);
        if (!fileToRead.exists()){
            report      = "-- file " + filename +" not exist --";
            ErrorStatus = ErrorStatus + 1;
            return report;
        }

        CSVReader reader                          = new CSVReader(new FileReader(filename));
        while ((csvLine = reader.readNext())     != null) {
            SimpleContactData inp                 = new SimpleContactData(csvLine[0], csvLine[1]);
            phoneNumbersFromFile.add(inp);
        }
        recordsInFile = phoneNumbersFromFile.size();

        if (recordsInFile == 0){
           report      = "-- have found NO records on file " + filename + "--";
           ErrorStatus = ErrorStatus + 1;
           return report;

        } else {
           Log.d(TAG, "sucessfully read " + recordsInFile + " contacts from file " + filename);
        }

        //*** 2) read contacts from phonebook ----------------------------------------------------------------------------------------------------------------------------------
        ArrayList<SimpleContactData> currentPhonebook = new ArrayList<>();
        ReadContactsFromPhone(activity, currentPhonebook);


        //*** 3) contacts from phone left join contacts from file  --------------------------------------------------------------------------------------------------------------

        for(SimpleContactData phoneNumberToAdd: phoneNumbersFromFile) {

            // check if really new
            boolean phoneNumberIsReallyNew = true;
            for (SimpleContactData phoneNumberInPhoneBook : currentPhonebook) {
                if (phoneNumberToAdd.getTelNumber().equals(phoneNumberInPhoneBook.getTelNumber())) {
                    phoneNumberIsReallyNew = false;
                    break;
                }
            }

            if (phoneNumberIsReallyNew) {
                Log.d(TAG, "creating " + phoneNumberToAdd.getTelName() + " " + phoneNumberToAdd.getTelNumber());
                CreateNewRecordInPhone(activity, phoneNumberToAdd);
                recordsImported++;
            }
        }

        report   = "" + recordsImported + " of " + recordsInFile + " contacts imported";
        return report;
    }

}
