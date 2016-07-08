package com.example.ce.activizor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import Tools.AsyncFinishListener;

public class ScreenActivities extends AppCompatActivity {

    String LOGTAG = "ScreenActivities: ";
    Context context = ScreenActivities.this ;
    EditText etNewAct;
    Button btnNewAct;
    ListView lvAllAct;
    ClassDataBaseImage db;
    ClassServerInt serverInt;
    ArrayList<Map> userActList;
    ArrayList<String> userActStringList = new ArrayList<>();
    ArrayAdapter<String> activityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverInt = new ClassServerInt(context);
        db = new ClassDataBaseImage(context);
        db.open();

        etNewAct = (EditText)findViewById(R.id.et_activity_new_activity);
        btnNewAct = (Button)findViewById(R.id.btn_activity_new_activity);
        lvAllAct = (ListView)findViewById(R.id.lv_activity_all_activities);

        btnNewAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newActivityText;
                newActivityText = etNewAct.getText().toString();

                if (! newActivityText.equals("")) {

                    insertUserActivity(newActivityText);

                }

            }
        });

        setUserActStringList();
        activityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userActStringList) {};

        lvAllAct.setAdapter(activityAdapter);
        lvAllAct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Map currentAct = userActList.get(position);

                CharSequence options[] = new CharSequence[]{"Delete Activity"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        switch (position) {

                            case 0:

                                deleteUserActivity(currentAct);

                        }
                    }
                });

                builder.show();
                return false;
            };
        });


    }

    private void setUserActStringList(){

        userActStringList.clear();
        userActList = db.dbQueries.getUserActivities(AppHelper.getUserId(context));

        for (Map act : userActList) {

            userActStringList.add(act.get(AppHelper.DB_ACTIVITY_NAME).toString());

        }
    }

    private void insertUserActivity(String actName) {

        serverInt.setAsyncFinishListener(new AsyncFinishListener() {
            @Override
            public void processFinished(Boolean isFinished) {

                System.out.println(LOGTAG + "processFinished + Simple");
                System.exit(0);

            }

            @Override
            public void processFinished(Context context, Boolean isFinished, String tableName,
                                        String keyName, String goodIds) {

                System.out.println(LOGTAG + "processFinished + multivars");

                setUserActStringList();
                activityAdapter.notifyDataSetChanged();

            }
        });

        serverInt.dbQueries.insertUserAct(AppHelper.getUserName(context), actName.toLowerCase());
        serverInt.syncTable(AppHelper.DB_ACTIVITY_TABLE, AppHelper.DB_ACTIVITY_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_ACTIVITY_TABLE, AppHelper.DB_USER_ACTIVITY_ID, true);


    }

    private void deleteUserActivity(Map currentAct) {

        serverInt.setAsyncFinishListener(new AsyncFinishListener() {
            @Override
            public void processFinished(Boolean isFinished) {

                System.out.println(LOGTAG + "processFinished + Simple");
                System.exit(0);

            }

            @Override
            public void processFinished(Context context, Boolean isFinished, String tableName,
                                        String keyName, String goodIds) {

                System.out.println(LOGTAG + "processFinished + multivars");

                setUserActStringList();
                activityAdapter.notifyDataSetChanged();

            }
        });

        serverInt.dbQueries.deleteByKey(AppHelper.DB_USER_ACTIVITY_TABLE,
                "(" + AppHelper.DB_USER_ACTIVITY_ACT_ID + ","
                        + AppHelper.DB_USER_ACTIVITY_USER_ID + ")",
                "('" + currentAct.get(
                        AppHelper.DB_USER_ACTIVITY_ACT_ID).toString() + "',"
                        + "'" + AppHelper.getUserId(context) + "')");

        serverInt.syncTable(AppHelper.DB_ACTIVITY_TABLE, AppHelper.DB_ACTIVITY_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_ACTIVITY_TABLE, AppHelper.DB_USER_ACTIVITY_ID, true);

    }


}
