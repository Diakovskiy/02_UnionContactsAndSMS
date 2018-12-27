package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.os.Environment;

public class FileUtil {

    public static String getSMSImportFileName(){

        return Environment.getExternalStorageDirectory()+ "/MY_SMSDB0.csv";
    }

    public static String getPhoneNumbersImportFileName(){

        return Environment.getExternalStorageDirectory()+ "/MY_ContactsDB0.csv";

    }



}
