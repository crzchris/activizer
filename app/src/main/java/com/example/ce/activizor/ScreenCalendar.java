package com.example.ce.activizor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import Tools.AsyncFinishListener;

/**
 * Created by CE on 17/4/2016.
 */
public class ScreenCalendar extends AppCompatActivity {

    private Context context = ScreenCalendar.this;
    private static final String LOGTAG = "DEBUG ScreenCalendar: ";
    ArrayList<String> eventList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ClassAptCalendar cv;
    Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        selectedDate =  Calendar.getInstance().getTime();

        //TODO FIX
//        Bundle b = getIntent().getExtras();
//        final String currentAct = b.getString(AppHelper.BUNDLE_ACTIVITYNAME);
        final String currentAct = "climbing";

        cv = (ClassAptCalendar)findViewById(R.id.test_calendar_view);
        cv.setActivity(currentAct);
        cv.setEventsFromDb();
        cv.updateCalendar();

        // assign event handler
        cv.setEventHandler(new ClassAptCalendar.EventHandler() {

            @Override
            public void onDaySelect(Date date) {

                selectedDate = date;
                createList(date);

            }

            @Override
            public void onDayLongPress(Date date) {

                Calendar clickDate = Calendar.getInstance();
                clickDate.setTime(date);
                selectedDate = clickDate.getTime();

                DateFormat df = SimpleDateFormat.getDateInstance();
                AppHelper.showToastMessage(context, df.format(date));

                CharSequence options[] = new CharSequence[]{"Add Event"};

                final String dateString = AppHelper.datePiecesToString(clickDate.get(Calendar.YEAR),
                        clickDate.get(Calendar.MONTH) + 1, clickDate.get(Calendar.DAY_OF_MONTH));

                System.out.println(LOGTAG + " dateString " + dateString);

                AlertDialog.Builder builder = new AlertDialog.Builder(ScreenCalendar.this);
                builder.setTitle("ScreenAppointments");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        switch (position) {

                            case 0:

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = ScreenCalendar.this.getLayoutInflater();
                                builder.setTitle("Add Activity Appointment");
                                builder.setMessage(currentAct);

                                final View popupView = inflater.inflate(R.layout.activity_appointments_popup_add_activity, null);
                                builder.setView(popupView);

                                final EditText etDate = (EditText) popupView.findViewById(R.id.et_appointment_add_date);
                                final EditText etStart = (EditText) popupView.findViewById(R.id.et_appointment_add_start_time);
                                final EditText etEnd = (EditText) popupView.findViewById(R.id.et_appointment_add_end_time);
                                final EditText etLoc = (EditText) popupView.findViewById(R.id.et_appointment_add_location);
                                etDate.setText(dateString);

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        addEvent(currentAct, etDate.getText().toString(), etStart.getText().toString(), etEnd.getText().toString(), etLoc.getText().toString());
                                        cv.setEventsFromDb();
                                        cv.updateCalendar();
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog b = builder.create();
                                b.show();

                        }
                    }
                });

                builder.show();

            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventList);

        ListView lv = (ListView)findViewById(R.id.lv_cal_items);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Bundle storage = new Bundle();
                String txt = ((TextView) v).getText().toString();
                storage.putString(AppHelper.BUNDLE_ACTIVITYNAME, txt);

                Intent goTo = new Intent(context, AppHelper.CL_APPT_DETAILS);
                goTo.putExtras(storage);
                startActivity(goTo);

            }
        });


    }

    public void createList(Date date) {

        String eventString = "";

        this.eventList.clear();

        SimpleDateFormat sdf = AppHelper.SDF_DEFAULT;

        for (Map event : cv.getEventList()) {
            String dateString = event.get(AppHelper.DB_EVENT_DATE).toString();
            String cellDateString = sdf.format(date).toString();

            if(dateString.equals(cellDateString)) {

                try {

                    eventString = sdf.parse(dateString).toString() + ", "
                            + event.get(AppHelper.DB_EVENT_STARTTIME).toString() + ", "
                            + event.get(AppHelper.DB_ACTIVITY_NAME).toString() + ", "
                            + event.get(AppHelper.DB_EVENT_LOCATION).toString();

                    this.eventList.add(eventString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        this.adapter.notifyDataSetChanged();
    }


    private void addEvent(String activity, String date, String startTime, String endTime, String location) {

        String username = AppHelper.getUserName(context);

        ClassServerInt serverInt = new ClassServerInt(context);
        serverInt.setAsyncFinishListener(new AsyncFinishListener() {
            @Override
            public void processFinished(Boolean isFinished) {

                cv.setEventsFromDb();
                cv.updateCalendar();
                createList(selectedDate);
            }
        });

        serverInt.newApt(context, AppHelper.PHP_INSERT_EVENT, username, activity, date,
                startTime, endTime, location);
        serverInt.syncTable(AppHelper.DB_EVENTS_TABLE, AppHelper.DB_EVENT_ID);
        serverInt.syncTable(AppHelper.DB_USER_EVENTS_TABLE, AppHelper.DB_USER_EVENTS_USER_EVENT_ID);

    }
}
