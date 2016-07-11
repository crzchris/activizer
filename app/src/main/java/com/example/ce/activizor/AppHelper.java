package com.example.ce.activizor;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Tools.AsyncFinishListener;


/**
 * Created by CE on 23/3/2016.
 */
public class AppHelper {

    // AIzaSyAC99ZIE9kZPW1kXMpP8UslNod-HVRJUSs

    // GENERAL
    public static final String PROJECT = "com.ce.example.activizor";
    public static final String LOGTAG = "AppHelper: ";

    //URLs
//    public static final String SERVER_URL = "http://10.0.2.2/phptest/";
    public static final String SERVER_URL = "http://activizer.net/service/";


    // FORMAT
    public static final String DATEFORMAT = "yyyy-MM-dd";
    public static final String DATEFORMATWEEKDAYS = "E";
    public static final String DATETIMEFORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String TIMEFORMAT = "HH:mm";

    // DATEFORMATS
    public static SimpleDateFormat SDF_DEFAULT = new SimpleDateFormat(DATEFORMAT);
    public static SimpleDateFormat SDF_DATETIME = new SimpleDateFormat(DATETIMEFORMAT);

    // PHP directory
    public static final String PHP_LOGIN = "login.php";
    public static final String PHP_INSERT_ROW = "insert_row.php";
    public static final String PHP_INSERT_USER = "insert_user.php";
    public static final String PHP_INSERT_EVENT = "insert_event.php";
    public static final String PHP_INSERT_USER_EVENT = "insert_user_event.php";
    public static final String PHP_INSERT_USER_ACT = "insert_useractivity.php";
    public static final String PHP_GET_EVENTS = "get_events.php";
    public static final String PHP_SYNC_DB = "sync_db.php";
    public static final String PHP_INSERT_FRIEND_INVITE = "insert_invite.php";
    public static final String PHP_INSERT_USER_CONTACT = "insert_user_contact.php";
    public static final String PHP_INSERT_EVENT_COMMENT = "insert_comment.php";
    public static final String PHP_DELETE_BY_KEY = "delete_by_key.php";
    public static final String PHP_UPDATE_USER_ACT_TAGS = "update_user_act_tags.php";
    public static final String PHP_INSERT_USER_ACT_LOCATION = "insert_user_act_loc.php";

    // BUNDLE
    public static final String BUNDLE_ACTIVITYNAME = "activityName";
    public static final String BUNDLE_EVENT_ID = "event_id";

    // SHARED PREFS
    public static final String SP_USERNAME = "username";
    public static final String SP_USERID = "userid";

    // ACTIVITIES
    public static final Class CL_WELCOME = ScreenWelcome.class;
    public static final Class CL_LOGIN = ScreenLogin.class;
    public static final Class CL_MENU = ScreenMenu.class;
    public static final Class CL_CALENDAR = ScreenCalendar.class;
    public static final Class CL_EVENT_DETAILS = ScreenEventDetails.class;
    public static final Class CL_SOCIAL = ScreenSocial.class;
    public static final Class CL_NOTIFICATIONS = ScreenNotifications.class;
    public static final Class CL_WHATSON = ScreenWhatsOn.class;
    public static final Class CL_ACTIVITIES = ScreenActivities.class;
    public static final Class CL_ADDEVENT = ScreenAddEvent.class;

    // SERVER DATABASE TABLES
    public static final int DB_VERSION = 30;

    // activities
    public static final String DB_ACTIVITY_TABLE = "activities";
    public static final String DB_ACTIVITY_ID = "act_id";
    public static final String DB_ACTIVITY_NAME = "act_name";

    // users
    public static final String DB_USERS_TABLE = "users";
    public static final String DB_USER_ID = "user_id";
    public static final String DB_USER_NAME = "user_name";

    // events
    public static final String DB_EVENTS_TABLE = "events";
    public static final String DB_EVENT_ID = "event_id";
    public static final String DB_EVENT_USER_ID = "user_id";
    public static final String DB_EVENT_ACTIVITY_ID = "act_id";
    public static final String DB_EVENT_DATE = "event_date";
    public static final String DB_EVENT_STARTTIME = "event_start_time";
    public static final String DB_EVENT_ENDTIME = "event_end_time";
    public static final String DB_EVENT_LOCATION = "event_location";
    public static final String DB_EVENT_ISPUBLIC = "event_ispublic";

    // user activities
    public static final String DB_USER_ACTIVITY_TABLE = "user_activities";
    public static final String DB_USER_ACTIVITY_ID = "user_act_id";
    public static final String DB_USER_ACTIVITY_USER_ID = "user_id";
    public static final String DB_USER_ACTIVITY_ACT_ID = "act_id";

    // user events
    public static final String DB_USER_EVENTS_TABLE = "user_events";
    public static final String DB_USER_EVENT_ID = "user_event_id";
    public static final String DB_USER_EVENT_USER_ID = "user_id";
    public static final String DB_USER_EVENT_EVENT_ID = "event_id";

    // comments
    public static final String DB_COMMENTS_TABLE = "comments";
    public static final String DB_COMMENT_ID = "comment_id";
    public static final String DB_COMMENT_TEXT = "comment_text";
    public static final String DB_COMMENT_USER_ID = "user_id";
    public static final String DB_COMMENT_EVENT_ID = "event_id";
    public static final String DB_COMMENT_TIME = "comment_time";

    // SOCIAL
    // user_contacts
    public static final String DB_USER_CONTACTS_TABLE = "user_contacts";
    public static final String DB_USER_CONTACT_ID = "user_contact_id";
    public static final String DB_USER_CONTACT_USER_ID_1 = "user_id_1";
    public static final String DB_USER_CONTACT_USER_ID_2 = "user_id_2";

    // friend invites
    public static final String DB_NOT_INV_FRIEND_TABLE = "notifications_friend_invite";
    public static final String DB_NOT_INV_FRIEND_ID = "notification_inv_friend_id";
    public static final String DB_NOT_INV_FRIEND_REC_USER_ID = "notification_inv_friend_rec_user_id";
    public static final String DB_NOT_INV_FRIEND_SENDER_USER_ID = "notification_inv_friend_sender_user_id";

    // event invites
    public static final String DB_NOT_INV_EVENT_TABLE = "notifications_event_invite";
    public static final String DB_NOT_INV_EVENT_ID = "notification_inv_event_id";
    public static final String DB_NOT_INV_EVENT_REC_USER_ID = "rec_user_id";
    public static final String DB_NOT_INV_EVENT_SENDER_USER_ID = "sender_user_id";
    public static final String DB_NOT_INV_EVENT_EVENT_ID = "event_id";

    // user activity tags
    public static final String DB_USER_CONTACT_ACT_TAGS_TABLE = "user_contact_act_tags";
    public static final String DB_USER_CONTACT_ACT_TAG_ID = "user_contact_act_tag_id";
    public static final String DB_USER_CONTACT_ACT_TAG_ACT_ID = "act_id";
    public static final String DB_USER_CONTACT_ACT_TAG_USER_ID = "user_id";
    public static final String DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID = "tagged_user_id";

    // user activity location
    public static final String DB_USER_ACT_LOCATIONS_TABLE = "user_act_locations";
    public static final String DB_USER_ACT_LOCATION_ID = "user_act_location_id";
    public static final String DB_USER_ACT_LOCATION_USER_ID = "user_id";
    public static final String DB_USER_ACT_LOCATION_ACT_ID = "act_id";
    public static final String DB_USER_ACT_LOCATION_LOCATION = "location";


    public static String getUserId(Context context) {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getString(AppHelper.SP_USERID, "");

    }

    public static String getUserName(Context context) {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getString(AppHelper.SP_USERNAME, "");

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


    public static void showToastMessage(Context c, String msg) {

        Toast toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);

        toast.show();

    }

    public static void printArrayMap(ArrayList<Map> list) {
        printArrayMap(list, "");
    }

    public static void printArrayMap(ArrayList<Map> list, String logTag) {

        for (Map<String, String> map : list) {

            for (Map.Entry<String,String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println(LOGTAG + "printArrayMap: key=" + key + ", value=" + value);
            }
        }
    }


    public static void printContentValues(ContentValues cv)
    {
        Set<Map.Entry<String, Object>> s=cv.valueSet();
        Iterator itr = s.iterator();

        while(itr.hasNext())
        {
            Map.Entry me = (Map.Entry)itr.next();
            String key = me.getKey().toString();
            Object value =  me.getValue();

            System.out.println(LOGTAG + "printContentValues: " + "key=:" + key + ", value=" + (String)(value == null?null:value.toString()));
        }
    }


    public static void syncTableAndListen(Context context, String tableName,
                                          String keyName, AsyncFinishListener listener) {

        ClassServerInt serverInt = new ClassServerInt(context);
        serverInt.setAsyncFinishListener(listener);
        serverInt.syncTable(tableName, keyName, true);

    }

    public static void syncTable(Context context, String tableName, String keyName) {

        System.out.println("DEBUG SYNC");
        ClassServerInt serverInt = new ClassServerInt(context);

        serverInt.setAsyncFinishListener(new AsyncFinishListener() {

            @Override
            public void processFinished(Boolean isFinished) {
//                        AppHelper.showToastMessage(context, "Sync Finished");
            }

            public void processFinished(Context context, Boolean isFinished, String tableName,
                                        String keyName, String goodIds) {

//                AppHelper.showToastMessage(context, "Sync Finished + delete");

                System.out.println(LOGTAG + "processFinished + delete");
                ClassDataBaseImage db = new ClassDataBaseImage(context);
                db.open();
                db.deleteByExcludedId(tableName, keyName, goodIds);

            }
        });

        serverInt.syncTable(tableName, keyName, true);

    }

    public static void syncDb(Context context){

        System.out.println("DEBUG SYNC");
        ClassServerInt serverInt = new ClassServerInt(context);

        serverInt.setAsyncFinishListener(new AsyncFinishListener() {

            @Override
            public void processFinished(Boolean isFinished) {
//                        AppHelper.showToastMessage(context, "Sync Finished");
            }

            public void processFinished(Context context, Boolean isFinished, String tableName,
                                        String keyName, String goodIds) {

//                AppHelper.showToastMessage(context, "Sync Finished + delete");

                System.out.println(LOGTAG + "processFinished + delete");
                ClassDataBaseImage db = new ClassDataBaseImage(context);
                db.open();
                db.deleteByExcludedId(tableName, keyName, goodIds);
                db.close();
            }

        });

        serverInt.syncTable(AppHelper.DB_USERS_TABLE, AppHelper.DB_USER_ID, true);
        serverInt.syncTable(AppHelper.DB_ACTIVITY_TABLE, AppHelper.DB_ACTIVITY_ID, true);
        serverInt.syncTable(AppHelper.DB_EVENTS_TABLE, AppHelper.DB_EVENT_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_ACTIVITY_TABLE, AppHelper.DB_USER_ACTIVITY_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_EVENTS_TABLE, AppHelper.DB_USER_EVENT_ID, true);
        serverInt.syncTable(AppHelper.DB_COMMENTS_TABLE, AppHelper.DB_COMMENT_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_CONTACTS_TABLE, AppHelper.DB_USER_CONTACT_ID, true);
        serverInt.syncTable(AppHelper.DB_NOT_INV_FRIEND_TABLE, AppHelper.DB_NOT_INV_FRIEND_ID, true);
        serverInt.syncTable(AppHelper.DB_NOT_INV_EVENT_TABLE, AppHelper.DB_NOT_INV_EVENT_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE, AppHelper.DB_USER_CONTACT_ACT_TAG_ID, true);

    }



}




