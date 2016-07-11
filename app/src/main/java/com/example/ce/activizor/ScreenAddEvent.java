package com.example.ce.activizor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Tools.PopUpInvite;


public class ScreenAddEvent extends AppCompatActivity {

    private Context context = ScreenAddEvent.this;
    private String LOGTAG = "ScreenAddEvent: ";
    private ClassDataBaseImage db;
    private ClassServerInt serverInt;
    private PopUpInvite popUpInvite;

    EditText etActivity;
    EditText etDate;
    EditText etStartTime;
    EditText etLocation;
    CheckBox cbIsPublic;
    Button btnInviteFriends;
    Button btnAddEvent;
    Button btnCancel;

    ListView lvQuickList;
    EditText viewActiveEditText;
    ArrayList<Map> activityList;
    ArrayList<Map> dateList = new ArrayList<>();
    ArrayList<Map> timeList = new ArrayList<>();
    ArrayList<Map> locationList = new ArrayList<>();
    ListViewsArrayAdapter listViewsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverInt = new ClassServerInt(context);
        db = new ClassDataBaseImage(context);
        db.open();
        popUpInvite = new PopUpInvite(context, db);

        setActivitiesList();
        setDateList();
        setStartTimeList();

        etActivity = (EditText)findViewById(R.id.et_addevent_activity);
        etDate = (EditText)findViewById(R.id.et_addevent_date);
        etStartTime = (EditText)findViewById(R.id.et_addevent_start_time);
        etLocation =  (EditText)findViewById(R.id.et_addevent_location);
        lvQuickList = (ListView)findViewById(R.id.lv_addevent_quicklist);
        cbIsPublic = (CheckBox)findViewById(R.id.cb_addevent_ispublic);
        btnInviteFriends = (Button)findViewById(R.id.btn_addevent_invite);
        btnAddEvent = (Button)findViewById(R.id.btn_addevent_add);
        btnCancel = (Button)findViewById(R.id.btn_addevent_cancel);

        viewActiveEditText = etActivity;

        etActivity.setOnFocusChangeListener(editTextFocusListener);
        etDate.setOnFocusChangeListener(editTextFocusListener);
        etStartTime.setOnFocusChangeListener(editTextFocusListener);
        etLocation.setOnFocusChangeListener(editTextFocusListener);

        listViewsArrayAdapter = new ListViewsArrayAdapter(context, new ArrayList<Map>());
        listViewsArrayAdapter.updateActiveList(activityList);
        lvQuickList.setAdapter(listViewsArrayAdapter);
        lvQuickList.setOnItemClickListener(listViewListener);

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AppHelper.CL_MENU));
            }
        });

        btnInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpInvite.setInviteIds();

            }
        });

    }

    private void setActivitiesList(){

        this.activityList = db.dbQueries.getUserActivities(AppHelper.getUserId(context));

    }

    private void setDateList() {

        if (this.dateList != null) {this.dateList.clear();};
        SimpleDateFormat sdf = new SimpleDateFormat(AppHelper.DATEFORMAT);
        SimpleDateFormat sdf_dayName = new SimpleDateFormat(AppHelper.DATEFORMATWEEKDAYS);

        Calendar calOffset = Calendar.getInstance();
        Date today = new Date();
        calOffset.setTime(today);

        for (int i = 0; i < 8; i++) {

            String dateDes;
            Map<String, String> dateMap = new HashMap<>();
            if (i == 0) {
                dateDes = "Today";
            } else if (i == 1) {
                dateDes = "Tomorrow";
            } else {
                dateDes = sdf_dayName.format(calOffset.getTime());
            }

            dateMap.put("description", dateDes);
            dateMap.put("dateString", sdf.format(calOffset.getTime()));

            this.dateList.add(dateMap);
            calOffset.add(Calendar.DATE, 1);
        }
    }

    private void setStartTimeList() {

        if (this.timeList != null) {this.timeList.clear();};

        String allTImes[] = {"06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30",
                "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00",
                "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
                "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00",
                "23:30", "00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30",
                "04:00", "04:30", "05:00", "05:30", };

        for (String t : allTImes) {

            Map m = new HashMap();
            m.put(AppHelper.DB_EVENT_STARTTIME, t);
            this.timeList.add(m);

        }
    }

    public void setLocationList() {

        if (this.locationList != null) {this.locationList.clear();};
        String activity = etActivity.getText().toString();

        if (!activity.equals("")){

            this.locationList = db.dbQueries.getUserActLocations(AppHelper.getUserId(context),
                    db.dbQueries.getActIdByName(activity));

        }
    }


    View.OnFocusChangeListener editTextFocusListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            viewActiveEditText = (EditText) view;

            switch (view.getId()) {

                case R.id.et_addevent_activity:

                    listViewsArrayAdapter.updateActiveList(activityList);
                    listViewsArrayAdapter.notifyDataSetChanged();

                    break;

                case R.id.et_addevent_date:

                    listViewsArrayAdapter.updateActiveList(dateList);
                    listViewsArrayAdapter.notifyDataSetChanged();

                    break;

                case R.id.et_addevent_start_time:

                    listViewsArrayAdapter.updateActiveList(timeList);
                    listViewsArrayAdapter.notifyDataSetChanged();

                    break;

                case R.id.et_addevent_location:

                    setLocationList();
                    listViewsArrayAdapter.updateActiveList(locationList);
                    listViewsArrayAdapter.notifyDataSetChanged();

                    break;

            }
        }
    };

    AdapterView.OnItemClickListener listViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch (viewActiveEditText.getId()) {

                case R.id.et_addevent_activity:

                    viewActiveEditText.setText(activityList.get(position).get(
                            AppHelper.DB_ACTIVITY_NAME).toString());
                    etDate.requestFocus();
                    break;

                case R.id.et_addevent_date:

                    viewActiveEditText.setText(dateList.get(position).get(
                            "dateString").toString());
                    etStartTime.requestFocus();
                    break;

                case R.id.et_addevent_start_time:

                    viewActiveEditText.setText(timeList.get(position).get(
                            AppHelper.DB_EVENT_STARTTIME).toString());
                    etLocation.requestFocus();
                    break;

                case R.id.et_addevent_location:

                    viewActiveEditText.setText(locationList.get(position).get(
                            AppHelper.DB_USER_ACT_LOCATION_LOCATION).toString());

                    break;

            }
        }
    };


    private class ListViewsArrayAdapter extends ArrayAdapter {


        ArrayList<Map> activeListArrayAdapter;
        Context context;

        public ListViewsArrayAdapter(Context c, ArrayList<Map> activeListArrayAdapter) {

            super(c, 0, activeListArrayAdapter);
            context = c;
            this.activeListArrayAdapter = activeListArrayAdapter;

        }

        public void updateActiveList(ArrayList<Map> newList) {

            AppHelper.printArrayMap(newList);

            this.activeListArrayAdapter.clear();
            for (Map m : newList) {

                this.activeListArrayAdapter.add(m);

            }
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.aux_listview_simple_med, parent, false);

            }

            TextView tv = (TextView) convertView.findViewById(R.id.tv_social_custom_list_friends);

            String listField = "";

            switch (viewActiveEditText.getId()) {
                case R.id.et_addevent_activity:
                    listField = AppHelper.DB_ACTIVITY_NAME;
                    break;
                case R.id.et_addevent_date:
                    listField = "description";
                    break;
                case R.id.et_addevent_start_time:
                    listField = AppHelper.DB_EVENT_STARTTIME;
                    break;
                case R.id.et_addevent_location:
                    listField = AppHelper.DB_USER_ACT_LOCATION_LOCATION;
                    break;
                default:
                    throw new IllegalArgumentException(LOGTAG + "view id not matching");
            }

            if (!listField.equals("")) {
                tv.setText(activeListArrayAdapter.get(position).get(listField).toString());
            }

            return convertView;

        }
    }

    private void addEvent() {

        String username = AppHelper.getUserName(context);
        String activity = etActivity.getText().toString();
        String date = etDate.getText().toString();
        String startTime = etStartTime.getText().toString();
        String location = etLocation.getText().toString();
        String isPublic;
        String invitedIds = popUpInvite.getInviteIds();

        if (cbIsPublic.isChecked()) {
            isPublic = "1";
        } else {
            isPublic = "0";
        }

        serverInt.dbQueries.insertEvent(AppHelper.PHP_INSERT_EVENT, username, activity, date,
                startTime, "", location, isPublic, invitedIds);
        serverInt.syncTable(AppHelper.DB_EVENTS_TABLE, AppHelper.DB_EVENT_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_EVENTS_TABLE, AppHelper.DB_USER_EVENT_ID, true);

        if (!location.equals("")) {

            serverInt.dbQueries.insertUserActLocation(AppHelper.getUserId(context),
                    db.dbQueries.getActIdByName(activity), location);
            serverInt.syncTable(AppHelper.DB_USER_ACT_LOCATIONS_TABLE,
                    AppHelper.DB_USER_ACT_LOCATION_ID, true);

        }

        startActivity(new Intent(context, AppHelper.CL_MENU));

    }



}
