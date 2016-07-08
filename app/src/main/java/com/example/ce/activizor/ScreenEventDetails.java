package com.example.ce.activizor;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import Tools.AsyncFinishListener;

public class ScreenEventDetails extends AppCompatActivity {

    private String LogTag = "ScreenEventDetails: ";
    private Context context = ScreenEventDetails.this;
    private ClassServerInt serverInt;
    private ClassDataBaseImage db;
    private TextView tvActivityName;
    private TextView tvDate;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvLocation;
    private ListView lvParticipants;
    private ListView lvComments;
    private Button btnEdit;
    private Button btnNewComment;
    private Map event;
    private ArrayList<Map> participantList;
    private ArrayList<Map> commentList;

    ArrayAdapter adapterComments;
    ArrayAdapter adapterParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b;
        b = getIntent().getExtras();
        final String event_id = b.getString(AppHelper.BUNDLE_EVENT_ID);

        serverInt = new ClassServerInt(context);
        db = new ClassDataBaseImage(context);
        db.open();

        event = db.dbQueries.getEventById(event_id, false);

        participantList = new ArrayList<Map>();
        commentList = new ArrayList<Map>();

        tvActivityName = (TextView) findViewById(R.id.tv_eventDetails_activity_name);
        tvDate = (TextView) findViewById(R.id.tv_eventDetails_event_date);
        tvStartTime = (TextView) findViewById(R.id.tv_eventDetails_event_st);
        tvEndTime = (TextView) findViewById(R.id.tv_eventDetails_event_et);
        tvLocation = (TextView) findViewById(R.id.tv_eventDetails_event_loc);
        btnEdit = (Button) findViewById(R.id.btn_eventDetails_edit);

        tvActivityName.setText(event.get(AppHelper.DB_ACTIVITY_NAME).toString());
        tvDate.setText(event.get(AppHelper.DB_EVENT_DATE).toString());
        tvStartTime.setText(event.get(AppHelper.DB_EVENT_STARTTIME).toString());
        tvEndTime.setText(event.get(AppHelper.DB_EVENT_ENDTIME).toString());
        tvLocation.setText(event.get(AppHelper.DB_EVENT_LOCATION).toString());

        if (event.get(AppHelper.DB_EVENT_USER_ID).toString().equals(AppHelper.getUserId(context))) {

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO add edit
                    AppHelper.showToastMessage(context, "EDIT NEEDS TO BE IMPLEMENTED");

                }
            });

        } else {

                btnEdit.setVisibility(View.GONE);

        }



        this.setParticipantsFromDb();
        this.setCommentsFromDb();

        adapterParticipants = new ParticipantArrayAdapter(this, participantList) { };

        lvParticipants = (ListView) findViewById(R.id.lv_eventDetails_participants);
        lvParticipants.setAdapter(adapterParticipants);


        adapterComments = new CommentArrayAdapter(this, commentList) { };

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
                setCommentsFromDb();
                adapterComments.notifyDataSetChanged();

            }
        };

        lvComments = (ListView) findViewById(R.id.lv_eventDetails_comments);
        lvComments.setAdapter(adapterComments);
        lvComments.setLongClickable(true);

        btnNewComment = (Button)findViewById(R.id.btn_eventDetails_new_comment);
        btnNewComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ScreenEventDetails.this.getLayoutInflater();
                builder.setTitle("New comment");

                final View popupView = inflater.inflate(
                        R.layout.content_screen_event_details_popup_new_comment, null);

                builder.setView(popupView);

                final EditText etNewComment = (EditText)popupView.findViewById(
                        R.id.et_event_details_popup_new_comment_text);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String commentText = etNewComment.getText().toString();
                        serverInt.dbQueries.insertEventComment(AppHelper.getUserId(context),
                                event_id, commentText);

                        dialog.dismiss();

                        AppHelper.syncTableAndListen(context, AppHelper.DB_COMMENTS_TABLE,
                                AppHelper.DB_COMMENT_ID, updateListener);

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();;
                    }

                });

                AlertDialog b = builder.create();
                b.show();

            }
        });
    };


    private void setParticipantsFromDb(){

        if (participantList != null) {participantList.clear();};

        participantList.addAll(db.dbQueries.getParticipantsByEvent(event.get(AppHelper.DB_EVENT_ID).toString()));

    }


    private void setCommentsFromDb(){

        if (commentList != null) {commentList.clear();};

        commentList.addAll(db.dbQueries.getCommentsByEvent(event.get(AppHelper.DB_EVENT_ID).toString()));

    }


    private class CommentArrayAdapter extends ArrayAdapter {

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

                convertView = LayoutInflater.from(context).inflate(R.layout.content_screen_event_details_custom_listview_comments, parent, false);

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

    private class ParticipantArrayAdapter extends ArrayAdapter {

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

                convertView = LayoutInflater.from(context).inflate(R.layout.content_screen_event_details_custom_listview_participants, parent, false);

            }

            System.out.println(LogTag + position);

            TextView tvUserName = (TextView)convertView.findViewById(R.id.tv_event_details_custom_list_user_name);

            tvUserName.setText(participantList.get(position).get(AppHelper.DB_USER_NAME).toString());

            return convertView;

        }
    }

}