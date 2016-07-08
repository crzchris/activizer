package com.example.ce.activizor;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by CE on 5/3/2016.
 */
public class ScreenMenu extends ListActivity {

    private Context context = ScreenMenu.this;

    String menuItems[] = {"Calendar", "Social", "What's on", "Notifications", "Activities", "checkDB", "Sync", "Test"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppHelper.syncDb(context);
        setListAdapter(new ArrayAdapter<String>(ScreenMenu.this, android.R.layout.simple_list_item_1, menuItems));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);



        System.out.println("DEBUG:" + AppHelper.PROJECT + "." + menuItems[position]);

        switch (menuItems[position]) {

            case "Calendar":

                startActivity(new Intent(ScreenMenu.this, AppHelper.CL_CALENDAR));

                break;

            case "checkDB":

                ClassDataBaseImage db_check = new ClassDataBaseImage(this.context);
                db_check.open();

                Cursor c = db_check.dataBaseImage.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

                if (c.moveToFirst()) {
                    while ( !c.isAfterLast() ) {
                        Toast.makeText(ScreenMenu.this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                        c.moveToNext();
                    }
                }

                c.close();

                Cursor dbCursor =  db_check.dataBaseImage.query(AppHelper.DB_EVENTS_TABLE,
                        null, null, null, null, null, null);
                String[] columnNames = dbCursor.getColumnNames();

                for(String s : columnNames) {
                    System.out.println("DEBUG_DB: " + s);
                }

                dbCursor.close();
                db_check.close();

                break;

            case "Sync":

                AppHelper.syncDb(context);

                break;

            case "Test":

//                ClassDataBaseImage testDb = new ClassDataBaseImage(this);
//                testDb.open();
//                ArrayList<Map> actList = testDb.dbQueries.getCommentsByEvent("7");
//                testDb.close();
//
//                for (Map<String, String> map : actList) {
//
//                    for (Map.Entry<String,String> entry : map.entrySet()) {
//                        String key = entry.getKey();
//                        String value = entry.getValue();
//                        System.out.println("DEBUG: " + key + " " + value);
//                    }
//
//                }

//                for (String act :  actList) {
//
//                    System.out.println("DEBUG: " + act);
//
//                }

                break;

            case "Social":

                startActivity(new Intent(context, AppHelper.CL_SOCIAL));

                break;

            case "Notifications" :

                startActivity(new Intent(context, AppHelper.CL_NOTIFICATIONS));

                break;

            case "What's on" :

                startActivity(new Intent(context, AppHelper.CL_WHATSON));

                break;

            case "Activities":

                startActivity(new Intent(context, AppHelper.CL_ACTIVITIES));

        }
    }
}
