package com.example.ce.activizor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ScreenEventDetails extends AppCompatActivity {

    private String LogTag = "ScreenEventDetails: ";
    private Context context = ScreenEventDetails.this;
    private TextView tvActivityName;
    private TextView tvDate;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvLocation;
    private ListView lvParticipants;
    private ListView lvComments;
    private Map event;
    private ArrayList<Map> participantList;
    private ArrayList<Map> commentList;

    ArrayAdapter adapterComments;
    ArrayAdapter adapterParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b;
        b = getIntent().getExtras();
        String event_id = b.getString(AppHelper.BUNDLE_EVENT_ID);

        ClassDataBaseImage db = new ClassDataBaseImage(context);
        db.open();
        event = db.getEventById(event_id);
        db.close();

        AppHelper.showToastMessage(context, event.get(AppHelper.DB_EVENT_DATE).toString());

        tvActivityName = (TextView) findViewById(R.id.tv_eventDetails_activity_name);
        tvDate = (TextView) findViewById(R.id.tv_eventDetails_event_date);
        tvStartTime = (TextView) findViewById(R.id.tv_eventDetails_event_st);
        tvEndTime = (TextView) findViewById(R.id.tv_eventDetails_event_et);
        tvLocation = (TextView) findViewById(R.id.tv_eventDetails_event_loc);

        tvActivityName.setText(event.get(AppHelper.DB_ACTIVITY_NAME).toString());
        tvDate.setText(event.get(AppHelper.DB_EVENT_DATE).toString());
        tvStartTime.setText(event.get(AppHelper.DB_EVENT_STARTTIME).toString());
        tvEndTime.setText(event.get(AppHelper.DB_EVENT_ENDTIME).toString());
        tvLocation.setText(event.get(AppHelper.DB_EVENT_LOCATION).toString());

        this.setParticipantsFromDb();
        this.setCommentsFromDb();

        adapterParticipants = new ParticipantArrayAdapter(this, participantList) {
        };

        ListView lvParticipants = (ListView) findViewById(R.id.lv_eventDetails_participants);
        lvParticipants.setAdapter(adapterParticipants);


        adapterComments = new CommentArrayAdapter(this, commentList) {
        };

        ListView lvComments = (ListView) findViewById(R.id.lv_eventDetails_comments);
        lvComments.setAdapter(adapterComments);
        lvComments.setLongClickable(true);

//        lvComments.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//                                           int pos, long id) {
//                // TODO Auto-generated method stub
//
//                Log.v("long clicked","pos: " + pos);
//
//                return true;
//            }
//        });

        lvComments.setOnLongClickListener(new AdapterView.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                AppHelper.showToastMessage(context, "XXXXXXXXXXX");
                System.out.println(LogTag + "LOOOOOOONG");
                return false;
            }
        });

//        lvComments.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AppHelper.showToastMessage(context, "XXXXXXXXXXX");
//                System.out.println(LogTag + "LOOOOOOONG");
//
//                return false;
//            }
//        });
//
//        lvComments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                AppHelper.showToastMessage(context, "ZZZZZZZZZZZZZZZZ");
//                System.out.println(LogTag + "ON ITEM CLICK");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


    };


    public void setParticipantsFromDb(){

        ClassDataBaseImage db = new ClassDataBaseImage(context);
        db.open();
        this.participantList = db.getParticipantsByEvent(event.get(AppHelper.DB_EVENT_ID).toString());
        db.close();

    }


    public void setCommentsFromDb(){

        ClassDataBaseImage db = new ClassDataBaseImage(context);
        db.open();
        this.commentList = db.getCommentsByEvent(event.get(AppHelper.DB_EVENT_ID).toString());
        db.close();

    }

    public class CommentArrayAdapter extends ArrayAdapter {

        ArrayList<Map> commentList;
        Context context;

        public CommentArrayAdapter(Context c, ArrayList<Map> commentList) {

            super(c, 0, commentList);
            context = c;
            this.commentList = commentList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.content_event_details_custom_listview_comments, parent, false);

            }

            TextView tvUserName = (TextView)convertView.findViewById(R.id.tv_event_details_custom_list_username);
            TextView tvCommentDate = (TextView)convertView.findViewById(R.id.tv_event_details_custom_list_comment_time);
            TextView tvComment = (TextView)convertView.findViewById(R.id.tv_event_details_custom_list_comment);

            tvUserName.setText(commentList.get(position).get(AppHelper.DB_USER_NAME).toString());
            tvCommentDate.setText(commentList.get(position).get(AppHelper.DB_COMMENT_TIME).toString());
            tvComment.setText(commentList.get(position).get(AppHelper.DB_COMMENT_TEXT).toString());

            return convertView;

        }
    }

    public class ParticipantArrayAdapter extends ArrayAdapter {

        ArrayList<Map> participantList;
        Context context;

        public ParticipantArrayAdapter(Context c, ArrayList<Map> participantList) {

            super(c, 0, participantList);
            context = c;
            this.participantList = participantList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.content_event_details_custom_listview_participants, parent, false);

            }

            System.out.println(LogTag + position);

            TextView tvUserName = (TextView)convertView.findViewById(R.id.tv_event_details_custom_list_user_name);

            tvUserName.setText(participantList.get(position).get(AppHelper.DB_USER_NAME).toString());

            return convertView;

        }
    }

}