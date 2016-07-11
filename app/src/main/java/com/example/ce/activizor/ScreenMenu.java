package com.example.ce.activizor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


public class ScreenMenu extends AppCompatActivity {

    private Context context = ScreenMenu.this;

    Button btnCalendar;
    Button btnWhatson;
    Button btnFriends;
    Button btnNotifications;
    Button btnActivities;
    Button btnAddEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppHelper.syncDb(context);
        setContentView(R.layout.activity_screen_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnCalendar = (Button)findViewById(R.id.btn_screen_menu_cal);
        btnWhatson = (Button)findViewById(R.id.btn_screen_menu_whatson);
        btnFriends = (Button)findViewById(R.id.btn_screen_menu_friends);
        btnNotifications = (Button)findViewById(R.id.btn_screen_menu_notifications);
        btnActivities = (Button)findViewById(R.id.btn_screen_menu_act);
        btnAddEvent = (Button)findViewById(R.id.btn_screen_menu_addevent);

        btnCalendar.setOnClickListener(buttonHandler);
        btnWhatson.setOnClickListener(buttonHandler);
        btnFriends.setOnClickListener(buttonHandler);
        btnNotifications.setOnClickListener(buttonHandler);
        btnActivities.setOnClickListener(buttonHandler);
        btnAddEvent.setOnClickListener(buttonHandler);


        String xx ="CREATE TABLE " + AppHelper.DB_USER_ACT_LOCATIONS_TABLE + " ("
                + AppHelper.DB_USER_ACT_LOCATION_USER_ID + " INTEGER NOT NULL, "
                + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + " TEXT NOT NULL, "
                + AppHelper.DB_USER_ACT_LOCATION_LOCATION + " TEXT NOT NULL, "
                + "UNIQUE(" + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + ", "
                + AppHelper.DB_USER_ACT_LOCATION_ACT_ID + ", "
                + AppHelper.DB_USER_ACT_LOCATION_LOCATION + ") ON CONFLICT IGNORE)";

        System.out.println("ABCDE " + xx);

    }

    View.OnClickListener buttonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btn_screen_menu_cal:
                    startActivity(new Intent(ScreenMenu.this, AppHelper.CL_CALENDAR));
                    break;
                case R.id.btn_screen_menu_whatson:
                    startActivity(new Intent(context, AppHelper.CL_WHATSON));
                    break;
                case R.id.btn_screen_menu_friends:
                    startActivity(new Intent(context, AppHelper.CL_SOCIAL));
                    break;
                case R.id.btn_screen_menu_notifications:
                    startActivity(new Intent(context, AppHelper.CL_NOTIFICATIONS));
                    break;
                case R.id.btn_screen_menu_act:
                    startActivity(new Intent(context, AppHelper.CL_ACTIVITIES));
                    break;
                case R.id.btn_screen_menu_addevent:
                    startActivity(new Intent(context, AppHelper.CL_ADDEVENT));
                    break;

            }
        }
    };
}
