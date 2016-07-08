package com.example.ce.activizor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
    ClassDataBaseImage db;
    ArrayList<String> eventList = new ArrayList<String>();
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> markerList = new ArrayList<>();
    ArrayList<String> activityList = new ArrayList<String>();
    ArrayAdapter<String> eventsAdapter;
    ArrayAdapter<String> activityAdapter;
    ClassAptCalendar cv;
    Date selectedDate;
    String currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_calendar);
        selectedDate =  Calendar.getInstance().getTime();
        db = new ClassDataBaseImage(context);
        db.open();

        // Spinner dropdown
        activityList = new ArrayList<>();
        currentActivity = "all";
        this.setActivityList();
        Spinner dropdownActivity = (Spinner)findViewById(R.id.sp_calendar_spinner_activity);

        activityAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, activityList);

        dropdownActivity.setAdapter(activityAdapter);
        dropdownActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currentActivity = parent.getItemAtPosition(position).toString();
                refreshDataAndUpdateCalendar();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Calendar view
        cv = (ClassAptCalendar)findViewById(R.id.custom_calendar_view);
        refreshDataAndUpdateCalendar();

        cv.setEventHandler(new ClassAptCalendar.EventHandler() {

            @Override
            public void onDaySelect(Date date) {

                selectedDate = date;
                createList(date);

            }

            @Override
            public void onDayLongPress(Date date) {

                final Calendar clickDate = Calendar.getInstance();
                clickDate.setTime(date);
                selectedDate = clickDate.getTime();

                CharSequence options[] = new CharSequence[]{"Add Event"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        switch (position) {

                            case 0:

                                new PopUpAddEvent(clickDate);

                        }
                    }
                });

                builder.show();

            }
        });

        eventsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventList) {};

        ListView lv = (ListView)findViewById(R.id.lv_cal_items);
        lv.setAdapter(eventsAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Bundle storage = new Bundle();
                String event_id = idList.get(position);
                storage.putString(AppHelper.BUNDLE_EVENT_ID, event_id);

                Intent goTo = new Intent(context, AppHelper.CL_EVENT_DETAILS);
                goTo.putExtras(storage);
                startActivity(goTo);

            }
        });
    }

    private void setActivityList() {

        activityList.clear();
        activityList.add("all");
        ArrayList<Map> activities = db.dbQueries.getUserActivities(AppHelper.getUserId(context));
        for (Map act : activities) {
            activityList.add(act.get(AppHelper.DB_ACTIVITY_NAME).toString());
        }

    }


    private void refreshDataAndUpdateCalendar() {

        cv.setActivity(currentActivity);
        cv.setCalendarEventsFromDb();
        cv.updateCalendar();

    }


    public void createList(Date date) {

        this.eventList.clear();
        this.idList.clear();
        this.markerList.clear();

        fillList(date, cv.getEventList(), "event");
        fillList(date, cv.getInviteList(), "invite");

        this.eventsAdapter.notifyDataSetChanged();
    }


    private void fillList(Date date, ArrayList<Map> list, String marker){

        SimpleDateFormat sdf = AppHelper.SDF_DEFAULT;
        String eventString = "";

        for (Map event : list) {
            String dateString = event.get(AppHelper.DB_EVENT_DATE).toString();
            String cellDateString = sdf.format(date).toString();

            if(dateString.equals(cellDateString)) {

                    eventString = event.get(AppHelper.DB_ACTIVITY_NAME).toString( ) + ", "
                            + cellDateString + ", "
                            + event.get(AppHelper.DB_EVENT_STARTTIME).toString() + ", "
                            + event.get(AppHelper.DB_EVENT_LOCATION).toString();

                    this.eventList.add(eventString);
                    this.idList.add(event.get(AppHelper.DB_EVENT_ID).toString());
                    this.markerList.add(marker);

            }
        }

    }


    private void addEvent(String activity, String date, String startTime, String endTime,
                          String location, String isPublic, String invitedIds) {

        String username = AppHelper.getUserName(context);

        ClassServerInt serverInt = new ClassServerInt(context);
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
                cv.setCalendarEventsFromDb();
                cv.updateCalendar();
                createList(selectedDate);

            }
        });

        serverInt.dbQueries.insertEvent(AppHelper.PHP_INSERT_EVENT, username, activity, date,
                startTime, endTime, location, isPublic, invitedIds);
        serverInt.syncTable(AppHelper.DB_EVENTS_TABLE, AppHelper.DB_EVENT_ID, true);
        serverInt.syncTable(AppHelper.DB_USER_EVENTS_TABLE, AppHelper.DB_USER_EVENT_ID, true);

    }


    class PopUpAddEvent {

        PopUpAddEvent(Calendar clickDate) {

            final String isPublic;
            final PopUpInvite popUpInvite = new PopUpInvite();

            final ArrayList<String>  activityListNoAll = activityList;
            if (activityListNoAll.get(0).equals("all")) { activityListNoAll.remove(0);}

            final String dateString = AppHelper.datePiecesToString(clickDate.get(Calendar.YEAR),
                    clickDate.get(Calendar.MONTH) + 1, clickDate.get(Calendar.DAY_OF_MONTH));



            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = ScreenCalendar.this.getLayoutInflater();
            builder.setTitle("Add Event");

            final View popupView = inflater.inflate(R.layout.content_screen_calendar_popup_add_event, null);
            builder.setView(popupView);

            final Spinner spAct = (Spinner) popupView.findViewById(R.id.sp_calendar_add_event_activity);
            final EditText etDate = (EditText) popupView.findViewById(R.id.et_calendar_add_event_date);
            final EditText etStart = (EditText) popupView.findViewById(R.id.et_calendar_add_event_start_time);
            final EditText etEnd = (EditText) popupView.findViewById(R.id.et_calendar_add_event_end_time);
            final EditText etLoc = (EditText) popupView.findViewById(R.id.et_calendar_add_event_location);
            final CheckBox cbIsPublic = (CheckBox) popupView.findViewById(R.id.cb_calendar_add_event_ispublic);
            final Button btnInvite = (Button) popupView.findViewById(R.id.btn_calendar_add_event_invite);

            if (cbIsPublic.isChecked()) {
                isPublic = "1";
            } else {
                isPublic = "0";
            }

            activityAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_dropdown_item, activityListNoAll);

            spAct.setAdapter(activityAdapter);

            etDate.setText(dateString);

            if (!currentActivity.equals("all")) {

                for (int i = 0; i < spAct.getCount(); i++) {
                    if (spAct.getItemAtPosition(i).toString().equals(currentActivity)) {
                        spAct.setSelection(i);
                        break;
                    }
                }
            }

            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println(LOGTAG + "POPUPINVITE CALLED");

                    popUpInvite.setInviteIds();

                }
            });

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    addEvent(spAct.getSelectedItem().toString(),
                            etDate.getText().toString(),
                            etStart.getText().toString(),
                            etEnd.getText().toString(),
                            etLoc.getText().toString(),
                            isPublic,
                            popUpInvite.getInviteIds());

                    cv.setCalendarEventsFromDb();
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



    class PopUpInvite {

        ListView lvTags;
        InviteTagsAdapter adapterTags;
        String inviteIds = "('')";


        public void setInviteIds() {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = ScreenCalendar.this.getLayoutInflater();

            final ArrayList<Map> tagList = getTags();

            adapterTags = new InviteTagsAdapter(context, tagList);

            final View popupView = inflater.inflate(R.layout.aux_popup_listview, null);
            builder.setView(popupView);

            lvTags = (ListView) popupView.findViewById(R.id.lv_aux_popup_listview);
            lvTags.setAdapter(adapterTags);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String ids = "";

                    for (Map tag : tagList) {

                        System.out.println(LOGTAG + tag.get(AppHelper.DB_USER_NAME).toString() + " " + tag.get("is_tagged").toString());

                        if (tag.get("is_tagged").equals("true")) {

                            ids += "'" + tag.get(AppHelper.DB_USER_ID).toString() + "',";

                        }
                    }

                    inviteIds = "(" + ids.substring(0,ids.length()-1) + ")";
                    System.out.println(LOGTAG + inviteIds);
                    dialog.cancel();

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();

                }
            });

            AlertDialog b = builder.create();
            b.show();

        }

        private ArrayList<Map> getTags(){

            return db.dbQueries.getFriendsByUserId(AppHelper.getUserId(context));

        }

        public String getInviteIds() {

            return this.inviteIds;

        }

        private class InviteTagsAdapter extends ArrayAdapter {

            private Context context;

            ArrayList<Map> tagList;

            public InviteTagsAdapter(Context c, ArrayList<Map> tagList) {

                super(c, 0, tagList);

                context = c;
                this.tagList = tagList;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                System.out.println("getView called");
                CheckBox cbIsTagged;
                TextView tvTagName;
                Map<String, String> currentTag = tagList.get(position);
                currentTag.put("is_tagged", "false");

                if (convertView == null) {

                    convertView = LayoutInflater.from(context).inflate(R.layout.aux_checkbox_textview, parent, false);
                    cbIsTagged = (CheckBox) convertView.findViewById(R.id.cb_aux_checkbox_textview);
                    tvTagName = (TextView) convertView.findViewById(R.id.tv_aux_checkbox_textview);
                    convertView.setTag(new RowViewHolder(tvTagName, cbIsTagged));

                    cbIsTagged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            Map<String, String> tag = (Map<String, String>)buttonView.getTag();
                            tag.put("is_tagged", Boolean.toString(isChecked));

                        }
                    });

                } else {

                    RowViewHolder rowHolder = (RowViewHolder) convertView.getTag();
                    cbIsTagged = rowHolder.getCheckBox();
                    tvTagName = rowHolder.getTextView();

                }

                cbIsTagged.setTag(currentTag);
                tvTagName.setText(currentTag.get(AppHelper.DB_USER_NAME).toString());

                return convertView;

            }

            private class RowViewHolder {

                private TextView tvActName;
                private CheckBox cbIsTagged;

                public RowViewHolder(TextView tvActName, CheckBox cbIsTagged) {

                    this.tvActName = tvActName;
                    this.cbIsTagged = cbIsTagged;


                }

                public CheckBox getCheckBox() {

                    return this.cbIsTagged;

                }

                public TextView getTextView() {

                    return this.tvActName;

                }


    }

        }

    }

}
