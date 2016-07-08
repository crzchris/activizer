package com.example.ce.activizor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import Tools.AsyncFinishListener;

public class ScreenNotifications extends AppCompatActivity {

    Context context;
    String LogTag = "ScreenNotifications :";
    private ArrayList<Map> notificationList;
    ArrayAdapter adapterNotifications;
    ClassDataBaseImage db;
    ClassServerInt serverInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = ScreenNotifications.this;
        notificationList= new ArrayList<Map>();
        serverInt = new ClassServerInt(context);
        db = new ClassDataBaseImage(context);
        db.open();

        this.setNotificationsFromDb();

        adapterNotifications = new NotificationsArrayAdapter(this, notificationList) {};
        ListView lvFriends = (ListView)findViewById(R.id.lv_notifications_notifications);
        lvFriends.setAdapter(adapterNotifications);

    }


    private void setNotificationsFromDb(){

        if (notificationList != null) {notificationList.clear();};

        ArrayList<Map> friendInvitations = db.dbQueries.getNotificationsFriendsByUserId(AppHelper.getUserId(context));
        ArrayList<Map> eventInvitations = db.dbQueries.getNotificationsEventsByUSerId(AppHelper.getUserId(context), "%");
        notificationList.addAll(friendInvitations);
        notificationList.addAll(eventInvitations);

    }

    private class NotificationsArrayAdapter extends ArrayAdapter {

        ArrayList<Map> notificationList;
        Context context;

        public NotificationsArrayAdapter(Context c, ArrayList<Map> notificationList) {

            super(c, 0, notificationList);
            context = c;
            this.notificationList = notificationList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Map invite = notificationList.get(position);
            String inviteType;

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.content_screen_notifications_custom_listview_notifications, parent, false);

            }

            TextView tvNotificationType = (TextView)convertView.findViewById(R.id.tv_notifications_type);
            TextView tvNotificationDes = (TextView)convertView.findViewById(R.id.tv_notifications_descriptions);
            Button btnAccept = (Button)convertView.findViewById(R.id.btn_notifications_accept);
            Button btnReject = (Button)convertView.findViewById(R.id.btn_notifications_reject);
            Button btnGoto = (Button)convertView.findViewById(R.id.btn_notifications_goto);

            if (invite.containsKey(AppHelper.DB_NOT_INV_FRIEND_SENDER_USER_ID)) {

                inviteType = "Friend";

            } else{

                inviteType = "Event";

            }

            tvNotificationType.setText(inviteType);

            final AsyncFinishListener updateListener = new AsyncFinishListener() {

                @Override
                public void processFinished(Boolean isFinished) { }

                public void processFinished(Context context, Boolean isFinished, String tableName,
                                            String keyName, String goodIds) {

                    AppHelper.showToastMessage(context, "Sync Finished + delete");

                    System.out.println(LogTag + "processFinished + delete");
                    ClassDataBaseImage db = new ClassDataBaseImage(context);
                    db.open();
                    db.deleteByExcludedId(tableName, keyName, goodIds);
                    setNotificationsFromDb();
                    adapterNotifications.notifyDataSetChanged();

                }
            };

            switch (inviteType) {

                case "Friend":

                    tvNotificationDes.setText(db.dbQueries.getUserNameById(
                            invite.get(AppHelper.DB_NOT_INV_FRIEND_SENDER_USER_ID).toString()));

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            serverInt.dbQueries.insertUserContact(AppHelper.getUserId(context),
                                    invite.get(AppHelper.DB_NOT_INV_FRIEND_SENDER_USER_ID).toString());

                            serverInt.dbQueries.deleteByKey(AppHelper.DB_NOT_INV_FRIEND_TABLE,
                                    AppHelper.DB_NOT_INV_FRIEND_ID,
                                    invite.get(AppHelper.DB_NOT_INV_FRIEND_ID).toString());

                            AppHelper.syncTableAndListen(context,
                                    AppHelper.DB_NOT_INV_FRIEND_TABLE, AppHelper.DB_NOT_INV_FRIEND_ID,
                                    updateListener);

                            AppHelper.syncTable(context, AppHelper.DB_USER_CONTACTS_TABLE,
                                    AppHelper.DB_USER_CONTACT_ID);
                        }
                    });

                    btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            serverInt.dbQueries.deleteByKey(AppHelper.DB_NOT_INV_FRIEND_TABLE,
                                    AppHelper.DB_NOT_INV_FRIEND_ID,
                                    invite.get(AppHelper.DB_NOT_INV_FRIEND_ID).toString());

                            AppHelper.syncTableAndListen(context,
                                    AppHelper.DB_NOT_INV_FRIEND_TABLE, AppHelper.DB_NOT_INV_FRIEND_ID,
                                    updateListener);

                        }
                    });

                    btnGoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                       }
                    });

                    break;

                case "Event":

                    Map event = db.dbQueries.getEventById(
                            invite.get(AppHelper.DB_NOT_INV_EVENT_EVENT_ID).toString(), false);

                    String eventDescription =  event.get(AppHelper.DB_ACTIVITY_NAME).toString() + "\n"
                            + event.get(AppHelper.DB_EVENT_DATE).toString() + "\n"
                            + event.get(AppHelper.DB_EVENT_STARTTIME).toString() + "\n"
                            + event.get(AppHelper.DB_EVENT_LOCATION).toString();

                    tvNotificationDes.setText(eventDescription);

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            serverInt.dbQueries.insertUserEvent(AppHelper.getUserId(context),
                                    invite.get(AppHelper.DB_NOT_INV_EVENT_EVENT_ID).toString());

                            serverInt.dbQueries.deleteByKey(AppHelper.DB_NOT_INV_EVENT_TABLE,
                                    AppHelper.DB_NOT_INV_EVENT_ID,
                                    invite.get(AppHelper.DB_NOT_INV_EVENT_ID).toString());

                            AppHelper.syncTableAndListen(context,
                                    AppHelper.DB_NOT_INV_EVENT_TABLE, AppHelper.DB_NOT_INV_EVENT_ID,
                                    updateListener);

                            AppHelper.syncTable(context, AppHelper.DB_USER_EVENTS_TABLE,
                                    AppHelper.DB_USER_EVENT_ID);
                        }
                    });

                    btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            serverInt.dbQueries.deleteByKey(AppHelper.DB_NOT_INV_EVENT_TABLE,
                                    AppHelper.DB_NOT_INV_EVENT_ID,
                                    invite.get(AppHelper.DB_NOT_INV_EVENT_ID).toString());

                            AppHelper.syncTableAndListen(context,
                                    AppHelper.DB_NOT_INV_EVENT_TABLE, AppHelper.DB_NOT_INV_EVENT_ID,
                                    updateListener);

                        }
                    });

                    btnGoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bundle storage = new Bundle();
                            String event_id = invite.get(AppHelper.DB_NOT_INV_EVENT_EVENT_ID).toString();
                            storage.putString(AppHelper.BUNDLE_EVENT_ID, event_id);

                            Intent goTo = new Intent(context, AppHelper.CL_EVENT_DETAILS);
                            goTo.putExtras(storage);
                            startActivity(goTo);
                        }
                    });

                    break;

                }

           return convertView;

        }
    }

}