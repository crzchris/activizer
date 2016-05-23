package com.example.ce.activizor;

import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CE on 23/3/2016.
 */
public class AppHelper {

    // GENERAL
    public static final String PROJECT = "com.ce.example.fittrack";

    // FORMAT
    public static final String DATEFORMAT = "yyyy-MM-dd";
    public static final String TIMEFORMAT = "HH:mm";

    // PHP directory
    public static final String PHP_INSERT_USER = "insert_user.php";
    public static final String PHP_INSERT_APT = "insert_apt.php";
    public static final String PHP_INSERT_USER_ACT = "insert_user_activity.php";

    // INTENTS
    public static final String ACT_MENU = "Menu";
    public static final String ACT_ACTIVITY_DETAILS = "Appointments";

    // BUNDLE
    public static final String BUNDLE_ACTIVITYNAME = "activityName";

    // SHARED PREFS
    public static final String SP_USERNAME = "username";

    public Date stringToJavaDate(String dateString) {

        Date javaDate =  new Date();

        try {

            DateFormat formatter = new SimpleDateFormat(this.DATEFORMAT);
            javaDate = formatter.parse(dateString);
            System.out.println(javaDate);

        } catch (java.text.ParseException e) {

            e.printStackTrace();
        }

        return javaDate;

    }

    public String javaDateToString(Date date) {

        String dateString =  new String();

        SimpleDateFormat sdf = new SimpleDateFormat(this.DATEFORMAT);
        dateString = sdf.format(new Date());

        return dateString;

    }

    static public String datePiecesToString(int year, int month, int day) {

          return Integer.toString(year) + "-" + addZero(month) + "-" + addZero(day);

    }

    static private String addZero(int number) {

        if (number > 9) {

            return Integer.toString(number);

        } else {

            return "0" + Integer.toString(number);

        }

    }

    public void showToastMessage(Context c, String msg) {

        Toast toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);

        toast.show();

    }


}




