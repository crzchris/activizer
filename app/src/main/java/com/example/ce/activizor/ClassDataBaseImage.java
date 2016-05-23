package com.example.ce.activizor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CE on 17/5/2016.
 */
public class ClassDataBaseImage {

    private static final int DATABASE_VERSION = 0;
    private String DATABASE_NAME = "ServerImage';
    private DbHelper myHelper;
    private Context myContext;
    private SQLiteDatabase myDataBase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + USERNAME + " TEXT NOT NULL, "
                    + ACTIVITY_NAME + " TEXT NOT NULL, "
                    + "UNIQUE(" + ACTIVITY_NAME + ") ON CONFLICT IGNORE);"
            );

            db.execSQL("CREATE TABLE " + DATABASE_TABLE_APT + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + USERNAME + " TEXT NOT NULL, "
                    + ADMIN + " INTEGER, "
                    + ACTIVITY_NAME + " TEXT NOT NULL, "
                    + DATE + " DATE NOT NULL, "
                    + STARTTIME + " TIME NOT NULL, "
                    + ENDTIME + " TIME, "
                    + LOCATION + " TEXT,"
                    + APT_MAINID + " INTEGER,"
                    + ISDELETED + " INTEGER,"
                    + LAST_UPDATE + " DATETIME);"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_APT);
            onCreate(db);

        }

    }


}
