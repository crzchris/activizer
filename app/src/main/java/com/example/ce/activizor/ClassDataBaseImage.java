package com.example.ce.activizor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CE on 17/5/2016.
 */
public class ClassDataBaseImage {

    private static final int DATABASE_VERSION = AppHelper.DB_VERSION;
    private static String DATABASE_NAME = "ServerImage";
    private String LOGTAG = "ClassDataBaseImage ";
    private DbHelper myHelper;
    private Context context;
    public SQLiteDatabase dataBaseImage;
    public DbQueries dbQueries;


    public ClassDataBaseImage(Context c) {

        context = c;
        dbQueries = new DbQueries();

    }


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            db.execSQL("CREATE TABLE " + AppHelper.DB_USERS_TABLE + " ("
                    + AppHelper.DB_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_NAME + " TEXT NOT NULL, "
                    + "UNIQUE(" + AppHelper.DB_USER_NAME + ") ON CONFLICT IGNORE);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_ACTIVITY_TABLE + " ("
                    + AppHelper.DB_ACTIVITY_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_ACTIVITY_NAME + " TEXT NOT NULL, "
                    + "UNIQUE(" + AppHelper.DB_ACTIVITY_NAME + ") ON CONFLICT IGNORE);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_EVENTS_TABLE + " ("
                    + AppHelper.DB_EVENT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_EVENT_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_EVENT_ACTIVITY_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_EVENT_DATE + " DATE NOT NULL, "
                    + AppHelper.DB_EVENT_STARTTIME + " TIME NOT NULL, "
                    + AppHelper.DB_EVENT_ENDTIME + " TIME, "
                    + AppHelper.DB_EVENT_LOCATION + " TEXT,"
                    + AppHelper.DB_EVENT_ISPUBLIC + " BOOLEAN );"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_USER_ACTIVITY_TABLE + " ("
                    + AppHelper.DB_USER_ACTIVITY_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_ACTIVITY_ACT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_ACTIVITY_USER_ID + " INTEGER NOT NULL, "
                    + "UNIQUE(" + AppHelper.DB_USER_ACTIVITY_ACT_ID + ", "
                    + AppHelper.DB_USER_ACTIVITY_USER_ID + ") ON CONFLICT IGNORE);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_USER_EVENTS_TABLE + " ("
                    + AppHelper.DB_USER_EVENT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_EVENT_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_EVENT_EVENT_ID + " INTEGER NOT NULL, "
                    + "UNIQUE(" + AppHelper.DB_USER_EVENT_USER_ID + ", "
                    + AppHelper.DB_USER_EVENT_EVENT_ID + ") ON CONFLICT IGNORE);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_COMMENTS_TABLE + " ("
                    + AppHelper.DB_COMMENT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_COMMENT_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_COMMENT_EVENT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_COMMENT_TEXT + " TEXT NOT NULL, "
                    + AppHelper.DB_COMMENT_TIME + " DATETIME NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_USER_CONTACTS_TABLE + " ("
                    + AppHelper.DB_USER_CONTACT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_CONTACT_USER_ID_1 + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_CONTACT_USER_ID_2 + " INTEGER NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_NOT_INV_FRIEND_TABLE + " ("
                    + AppHelper.DB_NOT_INV_FRIEND_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_NOT_INV_FRIEND_REC_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_NOT_INV_FRIEND_SENDER_USER_ID + " INTEGER NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_NOT_INV_EVENT_TABLE + " ("
                    + AppHelper.DB_NOT_INV_EVENT_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_NOT_INV_EVENT_REC_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_NOT_INV_EVENT_SENDER_USER_ID + " INTEGER NOT NULL,"
                    + AppHelper.DB_NOT_INV_EVENT_EVENT_ID + " INTEGER NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE + " ("
                    + AppHelper.DB_USER_CONTACT_ACT_TAG_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_CONTACT_ACT_TAG_ACT_ID + " INTEGER NOT NULL,"
                    + AppHelper.DB_USER_CONTACT_ACT_TAG_USER_ID + " INTEGER NOT NULL,"
                    + AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID + " INTEGER NOT NULL);"
            );

            db.execSQL("CREATE TABLE " + AppHelper.DB_USER_ACT_LOCATIONS_TABLE + " ("
                    + AppHelper.DB_USER_ACT_LOCATION_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_ACT_LOCATION_USER_ID + " INTEGER NOT NULL, "
                    + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + " TEXT NOT NULL, "
                    + AppHelper.DB_USER_ACT_LOCATION_LOCATION + " TEXT NOT NULL, "
                    + "UNIQUE(" + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + ", "
                    + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + ", "
                    + AppHelper.DB_USER_ACT_LOCATION_LOCATION + ") ON CONFLICT IGNORE);"
            );

        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_ACTIVITY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_USERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_EVENTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_USER_ACTIVITY_TABLE );
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_USER_EVENTS_TABLE );
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_COMMENTS_TABLE );
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_USER_CONTACTS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_NOT_INV_FRIEND_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_NOT_INV_EVENT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AppHelper.DB_USER_ACT_LOCATIONS_TABLE);
            onCreate(db);

        }
    }


    public ClassDataBaseImage open() {

        myHelper = new DbHelper(this.context);
        dataBaseImage = myHelper.getWritableDatabase();

        return this;

    }


    public void close(){

        myHelper.close();

    }


    public long addEntry(ContentValues dbEntries, String dbTable) {

        AppHelper.printContentValues(dbEntries);
        return dataBaseImage.insert(dbTable, null, dbEntries);

    }


    private String cursorGetFirst(Cursor c) {

        if (c.getCount() < 1) {

            return null;

        } else {

            c.moveToFirst();
            String id = c.getString(0);
            c.close();
            return id;

        }
    }


    private ArrayList<Map> cursorToMap(Cursor c, String[] columns) {

        ArrayList<Map> list = new ArrayList<Map>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            Map<String, String> map = new HashMap<>();

            for (String col : columns) {

                System.out.println(LOGTAG + "cursorToMap " + col + " " + c.getString(c.getColumnIndex(col)));
                map.put(col, c.getString(c.getColumnIndex(col)));

            }

            list.add(map);

        }

        c.close();

        return list;

    }


    public void deleteByExcludedId(String tableName, String keyName, String goodIdList) {

        String query = "DELETE FROM " + tableName + " WHERE " + keyName + " NOT IN " + goodIdList;

        System.out.println(LOGTAG + query);
        dataBaseImage.execSQL(query);

    }


    // Collection of sql queries
    public class DbQueries {


        public String getFirstById(String tableName, String columnName, String keyName, String key) {

            String query = "SELECT " + columnName + " FROM " + tableName
                    + " WHERE " + keyName + " = '" + key + "'";

            Cursor c = dataBaseImage.rawQuery(query, null);

            return  cursorGetFirst(c);

        }


        public String getUserIdByName(String userName) {

            String query = "SELECT " + AppHelper.DB_USER_ID + " FROM " + AppHelper.DB_USERS_TABLE
                    + " WHERE " + AppHelper.DB_USER_NAME + " = '" + userName + "'";

            Cursor c = dataBaseImage.rawQuery(query, null);

            return  cursorGetFirst(c);

        }


        public String getUserNameById(String userId) {

            String query = "SELECT " + AppHelper.DB_USER_NAME + " FROM " + AppHelper.DB_USERS_TABLE
                    + " WHERE " + AppHelper.DB_USER_ID + " = '" + userId + "'";

            Cursor c = dataBaseImage.rawQuery(query, null);

            return  cursorGetFirst(c);

        }


        public String getActIdByName(String actName) {

            String query = "SELECT " + AppHelper.DB_ACTIVITY_ID + " FROM " + AppHelper.DB_ACTIVITY_TABLE
                    + " WHERE " + AppHelper.DB_ACTIVITY_NAME + " = '" + actName + "'";

            Cursor c = dataBaseImage.rawQuery(query, null);

            return  cursorGetFirst(c);

        }


        public ArrayList<Map> getUserActivities(String userId) {

            String[] columns = { AppHelper.DB_ACTIVITY_ID,
                    AppHelper.DB_ACTIVITY_NAME };

            String query = "SELECT a." + AppHelper.DB_ACTIVITY_NAME + ", "
                    + " a." + AppHelper.DB_ACTIVITY_ID
                    + " FROM " + AppHelper.DB_USER_ACTIVITY_TABLE + " ua "
                    + " JOIN " + AppHelper.DB_ACTIVITY_TABLE + " a "
                    + " ON a." + AppHelper.DB_ACTIVITY_ID + " = ua." + AppHelper.DB_USER_ACTIVITY_ACT_ID
                    + " WHERE ua." + AppHelper.DB_USER_ACTIVITY_USER_ID + " like '" + userId + "'";

            System.out.print(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public Map getEventById(String id, Boolean userEventsOnly) {

            return getEventsByKey(AppHelper.DB_EVENT_ID, id, "%", userEventsOnly).get(0);

        }


        public ArrayList<Map> getNotificationsFriendsByUserId(String userId) {

            String[] columns = { AppHelper.DB_NOT_INV_FRIEND_SENDER_USER_ID,
                    AppHelper.DB_NOT_INV_FRIEND_ID };

            String query = "SELECT nf." + AppHelper.DB_NOT_INV_FRIEND_ID + ", "
                    + " nf." + AppHelper.DB_NOT_INV_FRIEND_SENDER_USER_ID
                    + " FROM " + AppHelper.DB_NOT_INV_FRIEND_TABLE + " nf "
                    + " WHERE nf." + AppHelper.DB_NOT_INV_FRIEND_REC_USER_ID + " = '" + userId + "'";

            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public ArrayList<Map> getNotificationsEventsByUSerId(String userId, String activity) {

            if (activity.toLowerCase().equals("all")) {activity = "%";};

            String[] columns = { AppHelper.DB_NOT_INV_EVENT_EVENT_ID,
                    AppHelper.DB_NOT_INV_EVENT_ID,
                    AppHelper.DB_EVENT_DATE,
                    AppHelper.DB_EVENT_STARTTIME,
                    AppHelper.DB_EVENT_ENDTIME,
                    AppHelper.DB_EVENT_LOCATION,
                    AppHelper.DB_ACTIVITY_NAME };

            String query = "SELECT ne." + AppHelper.DB_NOT_INV_EVENT_ID + ", "
                    + " ne." + AppHelper.DB_NOT_INV_EVENT_EVENT_ID + ", "
                    + " e." + AppHelper.DB_EVENT_DATE + ", "
                    + " e." + AppHelper.DB_EVENT_STARTTIME + ", "
                    + " e." + AppHelper.DB_EVENT_ENDTIME + ", "
                    + " e." + AppHelper.DB_EVENT_LOCATION + ", "
                    + " a." + AppHelper.DB_ACTIVITY_NAME
                    + " FROM " + AppHelper.DB_NOT_INV_EVENT_TABLE + " ne "
                    + " JOIN " + AppHelper.DB_EVENTS_TABLE + " e "
                    + " ON e." + AppHelper.DB_EVENT_ID + " = ne." + AppHelper.DB_NOT_INV_EVENT_EVENT_ID
                    + " JOIN " + AppHelper.DB_ACTIVITY_TABLE + " a "
                    + " ON a." + AppHelper.DB_ACTIVITY_ID + " = e." + AppHelper.DB_EVENT_ACTIVITY_ID
                    + " WHERE ne." + AppHelper.DB_NOT_INV_EVENT_REC_USER_ID + " = '" + userId + "'"
                    + " AND a." + AppHelper.DB_ACTIVITY_NAME + " LIKE '" + activity + "'";


            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public ArrayList<Map> getEventsByDate(String date, String activity, Boolean userEventsOnly) {

            if (activity.toLowerCase().equals("all")) {activity = "%";};
            return getEventsByKey(AppHelper.DB_EVENT_DATE, date, activity, userEventsOnly);

        }


        public ArrayList<Map> getEventsByKey(String key, String keyValue, String activity, Boolean userEventsOnly) {

            String[] columns = { AppHelper.DB_EVENT_ID,
                    AppHelper.DB_ACTIVITY_NAME,
                    AppHelper.DB_EVENT_DATE,
                    AppHelper.DB_EVENT_STARTTIME,
                    AppHelper.DB_EVENT_ENDTIME,
                    AppHelper.DB_EVENT_LOCATION,
                    AppHelper.DB_EVENT_USER_ID,
                    AppHelper.DB_EVENT_ISPUBLIC,
            };

            String query = "SELECT " + " e." + AppHelper.DB_EVENT_ID + ", "
                    + " a." + AppHelper.DB_ACTIVITY_NAME + ", "
                    + " e." + AppHelper.DB_EVENT_DATE + ", "
                    + " e." + AppHelper.DB_EVENT_STARTTIME + ", "
                    + " e." + AppHelper.DB_EVENT_ENDTIME + ", "
                    + " e." + AppHelper.DB_EVENT_LOCATION + ", "
                    + " e." + AppHelper.DB_EVENT_USER_ID + ", "
                    + " e." + AppHelper.DB_EVENT_ISPUBLIC
                    + " FROM " + AppHelper.DB_EVENTS_TABLE + " e "
                    + " JOIN " + AppHelper.DB_ACTIVITY_TABLE + " a "
                    + " ON a." + AppHelper.DB_ACTIVITY_ID + " = e." + AppHelper.DB_EVENT_ACTIVITY_ID
                    + " WHERE a." + AppHelper.DB_ACTIVITY_NAME + " LIKE '" + activity + "' "
                    + " AND e." + key + " LIKE '" + keyValue + "' ";


            if (userEventsOnly) {

                query += " AND ( SELECT ue." + AppHelper.DB_USER_EVENT_EVENT_ID
                        + " FROM " + AppHelper.DB_USER_EVENTS_TABLE + " ue "
                        + " WHERE ue." + AppHelper.DB_USER_EVENT_USER_ID + " = '" + AppHelper.getUserId(context) + "' "
                        + " AND ue." + AppHelper.DB_USER_EVENT_EVENT_ID + " = e." + AppHelper.DB_EVENT_ID
                        + " ) IS NOT NULL";

            }

            System.out.println("QUERY " + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public ArrayList<Map> getParticipantsByEvent(String eventId) {

            String[] columns = { AppHelper.DB_USER_NAME };

            String query = "SELECT u." + AppHelper.DB_USER_NAME
                    + " FROM " + AppHelper.DB_USER_EVENTS_TABLE + " ue "
                    + " JOIN " + AppHelper.DB_USERS_TABLE
                    + " u  ON u." + AppHelper.DB_USER_ID + " = ue." + AppHelper.DB_EVENT_USER_ID
                    + " WHERE ue." + AppHelper.DB_USER_EVENT_EVENT_ID + " = '" + eventId + "'";


            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public ArrayList<Map> getFriendsByUserId(String userId) {

            String[] columns = { AppHelper.DB_USER_NAME,
                    AppHelper.DB_USER_ID };

            String query = "SELECT u." + AppHelper.DB_USER_NAME + ", "
                    + "u." + AppHelper.DB_USER_ID
                    + " FROM " + AppHelper.DB_USER_CONTACTS_TABLE + " uc "
                    + " JOIN " + AppHelper.DB_USERS_TABLE
                    + " u  ON u." + AppHelper.DB_USER_ID + " = uc." + AppHelper.DB_USER_CONTACT_USER_ID_2
                    + " WHERE uc." + AppHelper.DB_USER_CONTACT_USER_ID_1 + " = '" + userId + "'";


            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public ArrayList<Map> getCommentsByEvent(String eventId) {

            String[] columns = { AppHelper.DB_USER_NAME,
                    AppHelper.DB_COMMENT_TEXT,
                    AppHelper.DB_COMMENT_TIME };

            String query = "SELECT c." + AppHelper.DB_COMMENT_TEXT + ", "
                    + " u." + AppHelper.DB_USER_NAME + ", "
                    + " c." + AppHelper.DB_COMMENT_TIME
                    + " FROM " + AppHelper.DB_COMMENTS_TABLE + " c "
                    + " JOIN " + AppHelper.DB_USERS_TABLE
                    + " u  ON u." + AppHelper.DB_USER_ID + " = c." + AppHelper.DB_COMMENT_USER_ID
                    + " WHERE c." + AppHelper.DB_COMMENT_EVENT_ID + " = '" + eventId + "'";


            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }

        public ArrayList<Map> getPublicEvents() {

            String[] columns = { AppHelper.DB_EVENT_ID,
                    AppHelper.DB_ACTIVITY_NAME,
                    AppHelper.DB_EVENT_DATE,
                    AppHelper.DB_EVENT_STARTTIME,
                    AppHelper.DB_EVENT_ENDTIME,
                    AppHelper.DB_EVENT_LOCATION,
                    AppHelper.DB_ACTIVITY_NAME };

            String query = "SELECT e." + AppHelper.DB_EVENT_ID + ", "
                    + " e." + AppHelper.DB_EVENT_ACTIVITY_ID + ", "
                    + " e." + AppHelper.DB_EVENT_DATE + ", "
                    + " e." + AppHelper.DB_EVENT_STARTTIME + ", "
                    + " e." + AppHelper.DB_EVENT_ENDTIME + ", "
                    + " e." + AppHelper.DB_EVENT_LOCATION + ", "
                    + " e." + AppHelper.DB_EVENT_USER_ID + ", "
                    + " a."  + AppHelper.DB_ACTIVITY_NAME
                    + " FROM " + AppHelper.DB_EVENTS_TABLE + " e "
                    + " JOIN " + AppHelper.DB_ACTIVITY_TABLE + " a "
                    + " ON e." + AppHelper.DB_EVENT_ACTIVITY_ID + " = a." + AppHelper.DB_ACTIVITY_ID
                    + " WHERE " + AppHelper.DB_EVENT_ISPUBLIC + " = '1'"
                    + " AND ( SELECT ue." + AppHelper.DB_USER_EVENT_EVENT_ID
                    + " FROM " + AppHelper.DB_USER_EVENTS_TABLE + " ue "
                    + " WHERE ue." + AppHelper.DB_USER_EVENT_USER_ID + " = '" + AppHelper.getUserId(context) + "' "
                    + " AND e." + AppHelper.DB_EVENT_DATE + " >= DATE() "
                    + " AND ue." + AppHelper.DB_USER_EVENT_EVENT_ID + " = e." + AppHelper.DB_EVENT_ID
                    + " ) IS NULL";


            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }


        public ArrayList<Map> getContactTagsByUserId(String userId, String taggedUserId) {

            String[] columns = { AppHelper.DB_USER_CONTACT_ACT_TAG_ACT_ID,
                    AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID };

            String query = "SELECT t." + AppHelper.DB_USER_CONTACT_ACT_TAG_ACT_ID + ", "
                    + "t." + AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID
                    + " FROM " + AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE + " t "
                    + " WHERE t." + AppHelper.DB_USER_CONTACT_ACT_TAG_USER_ID + " = '" + userId + "'"
                    + " AND t." + AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID
                    + " = '" + taggedUserId + "'";


            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }

        public ArrayList<Map> getContactActTags(String userId, String taggedUderId) {

            String[] columns = { AppHelper.DB_ACTIVITY_ID,
                    AppHelper.DB_ACTIVITY_NAME,
                    "is_tagged",
                    AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID,
                    AppHelper.DB_ACTIVITY_NAME };


            String query = "SELECT ua." + AppHelper.DB_ACTIVITY_ID + ","
                    + " a." + AppHelper.DB_ACTIVITY_NAME + ","
                    + " CASE WHEN ucat." + AppHelper.DB_USER_CONTACT_ACT_TAG_USER_ID + " IS NULL THEN 'false' ELSE 'true' END "
                    + " as is_tagged, "
                    + " ucat." + AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID
                    + " FROM " + AppHelper.DB_USER_ACTIVITY_TABLE + " ua "

                    + " JOIN " + AppHelper.DB_ACTIVITY_TABLE + " a "
                    + " ON a." + AppHelper.DB_ACTIVITY_ID + " = ua." + AppHelper.DB_USER_ACTIVITY_ACT_ID
                    + " LEFT JOIN ( SELECT " + AppHelper.DB_USER_CONTACT_ACT_TAG_USER_ID + ","
                    + " " + AppHelper.DB_USER_CONTACT_ACT_TAG_ACT_ID + ","
                    + " " + AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID
                    + " FROM " + AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE
                    + " WHERE " + AppHelper.DB_USER_CONTACT_ACT_TAG_USER_ID + " = '" + userId + "' "
                    + " AND " + AppHelper.DB_USER_CONTACT_ACT_TAG_TAGGED_USER_ID + " = '" + taggedUderId + "' ) ucat"
                    + " ON ucat." + AppHelper.DB_USER_ID + " = ua." + AppHelper.DB_USER_ID
                    + " AND ucat." + AppHelper.DB_USER_CONTACT_ACT_TAG_ACT_ID + " = ua." + AppHelper.DB_USER_ACTIVITY_ACT_ID
                    + " WHERE ua." + AppHelper.DB_USER_ID + " = '" + userId + "'";

            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }

        public ArrayList<Map> getUserActLocations(String userId, String actId) {

            String[] columns = { AppHelper.DB_USER_ACT_LOCATION_LOCATION, };

            String query = "SELECT uel." + AppHelper.DB_USER_ACT_LOCATION_LOCATION
                    + " FROM " + AppHelper.DB_USER_ACT_LOCATIONS_TABLE + " uel "
                    + " WHERE " + AppHelper.DB_USER_ACT_LOCATION_USER_ID + " = '" + userId + "' "
                    + " AND " + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + " = '" + actId + "'";

            System.out.println(LOGTAG + query);

            Cursor c = dataBaseImage.rawQuery(query, null);

            return cursorToMap(c, columns);

        }

    }
}
