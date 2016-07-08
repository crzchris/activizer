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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class ScreenSocial extends AppCompatActivity {

    private Context context;
    private String LogTag = "ScreenSocial: ";
    private ClassDataBaseImage db;
    private ClassServerInt serverInt;
    private ArrayList<Map> friendList;
    final private ArrayList<Map> actList = new ArrayList<>();
    private ArrayAdapter adapterFriends;
    private ArrayAdapter adapterTags;
    private Button btnInviteFriend;
    private ListView lvFriends;
    private ListView lvTags;
    private EditText etRecName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = ScreenSocial.this;

        setContentView(R.layout.activity_screen_social);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverInt = new ClassServerInt(context);
        db = new ClassDataBaseImage(context);
        db.open();

        this.setFriendsFromDB();
        btnInviteFriend = (Button)findViewById(R.id.btn_social_invite);
        setBtnInviteFriendListener();

        adapterFriends = new FriendsArrayAdapter(this, friendList) {};
        lvFriends = (ListView)findViewById(R.id.lv_social_friends);
        lvFriends.setAdapter(adapterFriends);
        setLvFriends();

    }


    private void setFriendsFromDB(){

        this.friendList = db.dbQueries.getFriendsByUserId(AppHelper.getUserId(context));

    }


    private void setBtnInviteFriendListener() {

        btnInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etRecName = (EditText)findViewById(R.id.et_social_invite);
                String recName = etRecName.getText().toString();

                String recUserId = db.dbQueries.getUserIdByName(recName);

                if (recUserId == null) {

                    AppHelper.showToastMessage(context, "User not found: " + recName);

                } else {

                    ClassServerInt serverInt = new ClassServerInt(context);
                    serverInt.dbQueries.insertFriendInvite(AppHelper.getUserId(context), recUserId);

                }
            }
        });


    }


    private void setLvFriends() {


        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ScreenSocial.this.getLayoutInflater();
                final String taggedUserId = friendList.get(position).get(AppHelper.DB_USER_ID).toString();

                setActList(db.dbQueries.getContactActTags(AppHelper.getUserId(context), taggedUserId));

                adapterTags = new ActTagsArrayAdapter(context, actList, taggedUserId);

                final View popupView = inflater.inflate(R.layout.aux_popup_listview, null);
                builder.setView(popupView);

                lvTags = (ListView) popupView.findViewById(R.id.lv_aux_popup_listview);
                lvTags.setAdapter(adapterTags);


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < actList.size(); i++) {
                            Map act = actList.get(i);

                            System.out.println(LogTag + "LOOP " + act.get(AppHelper.DB_ACTIVITY_NAME) + " " + act.get("is_tagged"));


                        }
                        String taggedAct = "";

                        for(int i=0;i<actList.size();i++){
                            Map act = actList.get(i);
                            if( act.get("is_tagged").toString().equals("true") ){
                                taggedAct += "'" + act.get(AppHelper.DB_ACTIVITY_ID) + "',";
                            }
                        }

                        if (taggedAct.equals("")) {taggedAct = "'',";};

                        taggedAct = "(" + taggedAct.substring(0, taggedAct.length() - 1) + ")";

                        System.out.println(LogTag + "taggedAct" + taggedAct);

                        serverInt.dbQueries.updateUserActivityTags(taggedUserId, taggedAct);
                        AppHelper.syncTable(context, AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE,
                                AppHelper.DB_USER_CONTACT_ACT_TAG_ID);

                        dialog.cancel();

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
        });
    }


    private void setActList(ArrayList<Map> activities) {

        this.actList.clear();
        this.actList.addAll(activities);

        for (Map act : this.actList) {

            System.out.println("SETTING ACTLIST " + act.get(AppHelper.DB_ACTIVITY_NAME).toString() + ", " + act.get("is_tagged").toString());

        }

    }


    private class FriendsArrayAdapter extends ArrayAdapter {


        ArrayList<Map> friendList;
        Context context;

        public FriendsArrayAdapter(Context c, ArrayList<Map> friendList) {

            super(c, 0, friendList);
            context = c;
            this.friendList = friendList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.content_screen_social_custom_listview_friends, parent, false);

            }

            TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_social_custom_list_friends);

            tvUserName.setText(friendList.get(position).get(AppHelper.DB_USER_NAME).toString());


            return convertView;

        }
    }


    private class ActTagsArrayAdapter extends ArrayAdapter {

        private Context context;

        ArrayList<Map> actList;

        public ActTagsArrayAdapter(Context c, ArrayList<Map> actList, String selectedContactUserId) {

            super(c, 0, actList);

            context = c;
            this.actList = actList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            System.out.println("getView called");
            CheckBox cbIsTagged;
            TextView tvActName;
            System.out.println (LogTag + position + " X ");
            Map currentAct = actList.get(position);

            System.out.println (LogTag + position + " A " + currentAct.get(AppHelper.DB_ACTIVITY_NAME).toString() + " " + currentAct.get("is_tagged").toString());
            System.out.println (LogTag + position + " B  " + currentAct.get(AppHelper.DB_ACTIVITY_NAME).toString() + " " + Boolean.valueOf(currentAct.get("is_tagged").toString()));

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.aux_checkbox_textview, parent, false);
                cbIsTagged = (CheckBox) convertView.findViewById(R.id.cb_aux_checkbox_textview);
                tvActName = (TextView) convertView.findViewById(R.id.tv_aux_checkbox_textview);
                convertView.setTag(new RowViewHolder(tvActName, cbIsTagged));

                cbIsTagged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Map<String, String> act = (Map<String, String>)buttonView.getTag();
                        act.put("is_tagged", Boolean.toString(isChecked));

                    }
                });

            } else {

                RowViewHolder rowHolder = (RowViewHolder) convertView.getTag();
                cbIsTagged = rowHolder.getCheckBox();
                tvActName = rowHolder.getTextView();

            }

            cbIsTagged.setTag(currentAct);
            cbIsTagged.setChecked(Boolean.valueOf(currentAct.get("is_tagged").toString()));
            tvActName.setText(currentAct.get(AppHelper.DB_ACTIVITY_NAME).toString());

            return convertView;

        }

    }


    private static class RowViewHolder {

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
