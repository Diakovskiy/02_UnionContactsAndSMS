package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Objects;

class SimpleSMSData {

    public String type;
    public String date;
    public String number;
    public String body;

    @Override
    public String toString(){
        return ""+ type + " " + date + " " + number + " " + body;
    }

    public String[] getStringLine(){

        String []stringLine = new String[4];

        stringLine[0] = type;
        stringLine[1]=  date;
        stringLine[2]=  number;
        stringLine[3]=  body;

        return stringLine;

    }


    public void setFromCsvLine( String[] csvLine){

        type    = csvLine[0];
        date    = csvLine[1];
        number  = csvLine[2];
        body    = csvLine[3];

    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final SimpleSMSData other = (SimpleSMSData) obj;
        return    type.equals   (other.type)
                && date.equals  (other.date)
                && number.equals(other.number)
                && body.equals  (other.body);
    }

}

public class SMSUtil {

    static String TAG = "SMSUtil";



    public static ArrayList<SimpleSMSData> readFromPhone(Activity activity){

        ArrayList<SimpleSMSData> result = new ArrayList<>();

        Uri uriSms = Uri.parse("content://sms/");
        Context context = activity;
        Cursor cur = context.getContentResolver().query(uriSms, null,null,null,null);
        activity.startManagingCursor(cur);
        if (cur.getCount() > 0){
            while (cur.moveToNext()){
                SimpleSMSData smsData = new SimpleSMSData();
                smsData.type   = cur.getString(9);
                smsData.date   = cur.getString(4);
                smsData.number = cur.getString(2);
                smsData.body   = cur.getString(12);

                result.add(smsData);
            }
        }

        return result;
    }

    public static void ExportSMSToFile(Activity activity){

        String csvExportFolder = Environment.getExternalStorageDirectory()+ "/";
        String csvFileName     = null;
        File csvFile           = null;
        int fileCount          = 1;

        do {
            csvFileName        = csvExportFolder + "MY_SMSDB" + fileCount + ".csv";
            csvFile            = new File(csvFileName);
            fileCount          = fileCount + 1;
        } while (csvFile.exists());

        try {

            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFileName));

            ArrayList<SimpleSMSData> smsInPhone = SMSUtil.readFromPhone(activity);
            for(SimpleSMSData mySMS: smsInPhone){
                csvWriter.writeNext(mySMS.getStringLine());
            }
            csvWriter.close();

            String report = "Sucessfully exported " + smsInPhone.size()+" entries to file " + csvFileName;
            Toast.makeText(activity, report, Toast.LENGTH_LONG).show();
            Log.d(TAG, "saveToFile: "+report);

        }catch (IOException exp){
            exp.printStackTrace();
        }


    }

    public static void ImportSMSFromFile(Activity activity){

        String report                    = null;
        String[] csvLine;
        ArrayList<SimpleSMSData> smsFromFile = new ArrayList<>();

        try {

            //*** 1) read SMS from file ---------------------------------------------------------------------------------------------------------------------------------------
            String filename                           = FileUtil.getSMSImportFileName();

            CSVReader reader                          = new CSVReader(new FileReader(filename));
            while ((csvLine = reader.readNext())     != null) {

                SimpleSMSData smsToAdd = new SimpleSMSData();
                smsToAdd.setFromCsvLine(csvLine);
                smsFromFile.add(smsToAdd);
            }


            if (smsFromFile.size() == 0){
                report = "We have found NO records on file " + filename +" exiting";
                Log.d(TAG, report);
                Toast.makeText(activity, report, Toast.LENGTH_LONG).show();
                return;

            } else {
                Log.d(TAG, "sucessfully read "+smsFromFile.size()+" SMS from file "+filename);
            }

            //*** 2) read SMS from phone ----------------------------------------------------------------------------------------------------------------------------------
            ArrayList<SimpleSMSData> smsFromPhone = SMSUtil.readFromPhone(activity);

            //*** 3) SMS from phone left join SMS from file  --------------------------------------------------------------------------------------------------------------
            int recordsImported = 0;
            for(SimpleSMSData smsToAdd: smsFromFile){


                // check if really new
                boolean smsIsReallyNew = true;
                for (SimpleSMSData smsInPhone: smsFromPhone) {
                    if (smsToAdd.equals(smsInPhone)) {
                        smsIsReallyNew = false;
                        break;
                    }
                }

                if (smsIsReallyNew){
                    Log.d(TAG, "importing " + smsToAdd.toString());
                    CreateNewSMSRecordInPhone(activity, smsToAdd);
                    recordsImported++;
                }
            }

            report = "Sucessfully imported " + recordsImported + " from file " + filename;
            Log.d(TAG, report);
            Toast.makeText(activity, report, Toast.LENGTH_LONG).show();

        }catch (IOException e){
            e.printStackTrace();

        }catch (Exception e) {
            e.printStackTrace();

        }
    }


    public static void CreateNewSMSRecordInPhone(Activity activity, SimpleSMSData smsToAdd) {

        ContentValues cv     = new ContentValues();
        cv.put("address", smsToAdd.number);
        cv.put("body",    smsToAdd.body);
        cv.put("read",    smsToAdd.type); //"0" for have not read sms and "1" for have read sms
        cv.put("date",    smsToAdd.date);

        Uri smsFolderUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (smsToAdd.type.equals("1")) { //inbox
                smsFolderUri = Telephony.Sms.Inbox.CONTENT_URI;
            } else {
                smsFolderUri = Telephony.Sms.Sent.CONTENT_URI;
            }
        }else { //not sure, that it is working on android 4.0-4.3

            if (smsToAdd.type.equals("1")) { //inbox
                smsFolderUri = Uri.parse("content://sms/inbox");
            } else {
                smsFolderUri = Uri.parse("content://sms/sent");
            }
        }

        activity.getContentResolver().insert(smsFolderUri, cv);

    }

    public static ArrayList<SimpleSMSData> readFromFile(Activity activity, String filename){

        ArrayList<SimpleSMSData> smsList = new ArrayList<SimpleSMSData>();

        try {
            CSVReader reader = new CSVReader(new FileReader(filename));
            String[] csvLine;
            while ((csvLine = reader.readNext()) != null) {
                SimpleSMSData smsToAdd = new SimpleSMSData();
                smsToAdd.setFromCsvLine(csvLine);
                smsList.add(smsToAdd);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return smsList;
    }


    public static void showSmsList(ArrayList<SimpleSMSData> smsInPhone){

        for(SimpleSMSData mySMS: smsInPhone){
            Log.d(TAG, "SMS:"+ mySMS.toString());
        }
    }

    public static void insertListIntoPhone(Activity activity, ArrayList<SimpleSMSData> smsFromFile){

        String folderName = "inbox";


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Uri uri = Telephony.Sms.Sent.CONTENT_URI;
//            if(folderName.equals("inbox")){
//                uri = Telephony.Sms.Inbox.CONTENT_URI;
//            }
//            activity.getContentResolver().insert(uri, values);
//        }
//        else {
//            activity.getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
//        }





        for(SimpleSMSData mySMS: smsFromFile){

            //listSmsFromPhone.add(smsFromFile);
            ContentValues cv = new ContentValues();
            cv.put("type", mySMS.type);
            cv.put("date", mySMS.date);
            cv.put("address", mySMS.number);
            cv.put("body", mySMS.body);
            activity.getContentResolver().insert(Uri.parse("content://sms/inbox"), cv);


            Log.d(TAG, "inserting SMS:"+ mySMS.toString());
        }


        //

    }
}
