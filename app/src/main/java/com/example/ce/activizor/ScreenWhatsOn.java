package com.example.ce.activizor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ScreenWhatsOn extends AppCompatActivity {

    Context context = ScreenWhatsOn.this;
    String LOGTAG = "ScreenWhatsOn: ";

    ClassDataBaseImage db;
    TextView tvActivity;
    TextView tvDate;
    TextView tvStartTime;
    TextView tvLocation;
    Button btnReject;
    Button btnMaybe;
    Button btnAccept;
    ArrayList<Map> eventList;
    Map currentEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_whats_on);

        db = new ClassDataBaseImage(context);
        db.open();

        tvActivity = (TextView)findViewById(R.id.tv_whats_on_activity);
        tvDate = (TextView)findViewById(R.id.tv_whats_on_date);
        tvStartTime = (TextView)findViewById(R.id.tv_whats_on_start_time);
        tvLocation = (TextView)findViewById(R.id.tv_whats_on_location);

        btnReject = (Button)findViewById(R.id.btn_whats_on_reject);
        btnMaybe = (Button)findViewById(R.id.btn_whats_on_maybe);
        btnAccept = (Button)findViewById(R.id.btn_whats_on_accept);

        setEventIdList();
        updateEvent();


        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateEvent();

            }
        });


    }

    private void updateEvent() {

        try {

            currentEvent = eventList.remove(0);

            tvActivity.setText(currentEvent.get(AppHelper.DB_ACTIVITY_NAME).toString());
            tvDate.setText(currentEvent.get(AppHelper.DB_EVENT_DATE).toString());
            tvStartTime.setText(currentEvent.get(AppHelper.DB_EVENT_STARTTIME).toString());
            tvLocation.setText(currentEvent.get(AppHelper.DB_EVENT_LOCATION).toString());

        } catch (IndexOutOfBoundsException e) {

            setEmpty();
        }
    }

    private void setEmpty(){

        tvActivity.setText("Nothing going on here");
        tvDate.setText("");
        tvStartTime.setText("");
        tvLocation.setText("");

    }


    private void setEventIdList() {

        ArrayList<Map> all_events = db.dbQueries.getPublicEvents();
        ArrayList<Map> temp_map = new ArrayList<>();

        for (Map e : all_events) {

            if (e.get(AppHelper.DB_EVENT_USER_ID).toString().equals(AppHelper.SP_USERID)) {

                temp_map.add(e);

            }
        }

        this.eventList =  temp_map;

    }
}
