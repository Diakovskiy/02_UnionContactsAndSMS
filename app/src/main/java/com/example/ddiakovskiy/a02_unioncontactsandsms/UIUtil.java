package com.example.ddiakovskiy.a02_unioncontactsandsms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

public class UIUtil {

    public static void showFatalError(Activity activity, String strMsg){
        showMessage(activity, "Fatal error", strMsg);

    }

    public static void showOKResult(Activity activity, String strMsg){
        showMessage(activity, "OK", strMsg);

    }




    public static void showMessage(Activity activity, String strTitle, String strMsg) {

        Dialog.OnClickListener myClickListener = new Dialog.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // положительная кнопка
                    case Dialog.BUTTON_POSITIVE:
                        break;
                    // негативная кнопка
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                    // нейтральная кнопка
                    case Dialog.BUTTON_NEUTRAL:
                        break;
                }
            }
        };



        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setTitle(strTitle);
        adb.setMessage(strMsg);
        adb.setIcon(android.R.drawable.ic_dialog_info);
        adb.setPositiveButton("OK", myClickListener);
        //adb.setNegativeButton("NO", myClickListener);
        //adb.setNeutralButton("NEYTRAL", myClickListener);

        AlertDialog alert =  adb.create();
        alert.show();











    }

}
