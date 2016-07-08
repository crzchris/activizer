package com.example.ce.activizor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import Tools.AsyncFinishListener;


public class ClassServerInt {

    Context context;
    DbQueries dbQueries;

    private AsyncFinishListener asyncFinishListener;
    private String LOGTAG = "DEBUG ClassServerInt: ";

    public ClassServerInt(Context c) {

        context = c;
        dbQueries = new DbQueries();

    }


    public void setAsyncFinishListener(AsyncFinishListener asyncFinishListener) {

        System.out.println(LOGTAG + "setAsyncFinishListener");

        this.asyncFinishListener = asyncFinishListener;

    }


    public class DbQueries {


        public void insertUser(String phpFile, String userName, String userPw,
                               String userEmail) {

            String parameters = "user_name=" + userName + "&user_pw=" + userPw + "&user_email=" + userEmail;

            new QueryDb(context).execute(phpFile, parameters);

        }

        public void insertRows(String tableName, String columns, String values) {

            String parameters = "table_name=" + tableName + "&columns=" + columns + "&values=" + values;

            new QueryDb(context).execute(AppHelper.PHP_INSERT_ROW, parameters);


        }


        public void insertUserAct(String userName, String activity) {

            String parameters = "user_name=" + userName + "&activity_name=" + activity;

            new QueryDb(context).execute(AppHelper.PHP_INSERT_USER_ACT, parameters);

        }


        public void insertEvent(String phpFile, String userName, String activityName,
                                String date, String startTime, String endTime,
                                String location, String isPublic, String invitedIds) {


            String parameters = "user_name=" + userName +
                    "&activity_name=" + activityName +
                    "&date=" + date +
                    "&start_time=" + startTime +
                    "&end_time=" + endTime +
                    "&location=" + location +
                    "&is_public=" + isPublic +
                    "&invited_user_id_list=" + invitedIds;

            new QueryDb(context).execute(phpFile, parameters);

        }


        public void deleteByKey(String tableName, String keyName, String keyValue) {

            if (!keyValue.startsWith("('")) { keyValue = "('" + keyValue + "')"; };

            String parameters = "table_name=" + tableName + "&key_name=" + keyName
                    + "&key_value=" + keyValue;

            new QueryDb(context).execute(AppHelper.PHP_DELETE_BY_KEY, parameters);

        }


        public void insertUserEvent(String userId, String eventId) {

            String parameters = "table_name=" + AppHelper.DB_USER_EVENTS_TABLE
                    + "&user_id=" + userId + "&event_id=" + eventId;

            new QueryDb(context).execute(AppHelper.PHP_INSERT_USER_EVENT, parameters);

        }

        public void insertFriendInvite(String senderUserId,
                                       String recUserId) {

            String parameters = "sender_user_id=" + senderUserId + "&rec_user_id=" + recUserId
                    + "&is_friend_invite=true";

            new QueryDb(context).execute(AppHelper.PHP_INSERT_FRIEND_INVITE, parameters);

        }


        public void insertEventInvite(String senderUserId,
                                      String recUserId, String eventId) {

            String parameters = "sender_user_id=" + senderUserId + "&rec_user_id=" + recUserId
                    + "&event_id=" + eventId + "&is_friend_invite=false";

            new QueryDb(context).execute(AppHelper.PHP_INSERT_FRIEND_INVITE, parameters);

        }


        public void insertUserContact(String userId_1, String userId_2) {

            String parameters = "table_name=" + AppHelper.DB_USER_CONTACTS_TABLE +
                    "&user_id_1=" + userId_1 + "&user_id_2=" + userId_2;

            new QueryDb(context).execute(AppHelper.PHP_INSERT_USER_CONTACT, parameters);


        }


        public void insertEventComment(String userId, String eventId, String commentText) {

            try {

                commentText = URLEncoder.encode(commentText, "UTF-8");

            } catch  (UnsupportedEncodingException e) {

                e.printStackTrace();

            }

            String parameters = "table_name=" + AppHelper.DB_COMMENTS_TABLE +
                    "&user_id=" + userId + "&event_id=" + eventId + "&comment_text=" + commentText;

            new QueryDb(context).execute(AppHelper.PHP_INSERT_EVENT_COMMENT, parameters);

        }

        public void updateUserActivityTags(String taggeduserId, String actIds) {

            String parameters = "table_name=" + AppHelper.DB_USER_CONTACT_ACT_TAGS_TABLE
                    + "&user_id=" + AppHelper.getUserId(context)
                    + "&tagged_user_id=" + taggeduserId
                    + "&act_id_list=" + actIds;

            new QueryDb(context).execute(AppHelper.PHP_UPDATE_USER_ACT_TAGS, parameters);

        }
    }

    public void getNewRowsFromDbAndSync(String phpFile, String tableName,
                                        String keyName, String keyList, String returnIds) {

        String parameters = "table_name=" + tableName + "&key_name=" + keyName
                + "&key_list=" + keyList + "&return_ids=" + returnIds ;

        new DbSync(context).execute(phpFile, parameters);

    }


    public void syncTable(String tableName, String keyName, Boolean deleteRows) {

        String keyList = "('',";
        String return_ids;
        ClassDataBaseImage db = new ClassDataBaseImage(this.context);
        db.open();
        String query = "SELECT " + keyName + " FROM " + tableName;
        Cursor c = db.dataBaseImage.rawQuery(query, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            keyList += "'" + c.getString(0) + "',";

        }

        c.close();

        keyList = keyList.substring(0,keyList.length()-1) + ")";

        db.close();

        if (deleteRows) {
            return_ids = "true";
        } else {
            return_ids = "false";
        }

        getNewRowsFromDbAndSync(AppHelper.PHP_SYNC_DB, tableName, keyName, keyList, return_ids);

    }


    public class QueryDb extends AsyncTask<String, Void, String> {

        protected Context context;

        public QueryDb(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... parametersArray) {

            String phpFile = parametersArray[0];
            String parameters = parametersArray[1];

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;

            System.out.println("DEBUG phpFile: " + phpFile);
            System.out.println("DEBUG parameters: " + parameters);

            try {
                url = new URL(AppHelper.SERVER_URL + phpFile);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {

                    sb.append(line + "\n");

                }

                response = sb.toString();
                isr.close();
                reader.close();
                return response;

            } catch (IOException e) {

                return e.toString();

            }
        }

        protected void onPostExecute(String jsonStr) {

            System.out.println(LOGTAG + "JSON STRING: " + jsonStr);
            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int query_result = jsonObj.getInt("success");
                    if (query_result == 1) {
                        System.out.println(LOGTAG + jsonObj.getString("success"));
                    } else if (query_result == 0) {
                        System.out.println(LOGTAG + jsonObj.getString("error"));
                    } else {
                        System.out.println(LOGTAG + "Something unexpected");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(LOGTAG + "No JSON data");;
            }
        }
    }


    public class DbSync extends QueryDb {


        public DbSync(Context context) {
            super(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            System.out.println(LOGTAG + "JSON STRING: " + jsonStr);
            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int query_success = jsonObj.getInt("success");

                    if (query_success == 1) {

                        if(jsonObj.has("rows")) {

                            JSONObject jsonNewRows = jsonObj.getJSONObject("rows");
                            String tableName = jsonObj.getString("table_name");

                            Iterator<?> iteratorRowKeys = jsonNewRows.keys();

                            while (iteratorRowKeys.hasNext()) {

                                String key = (String) iteratorRowKeys.next();
                                JSONObject jsonRow = jsonNewRows.getJSONObject(key);
                                System.out.println(jsonNewRows.get(key));

                                Iterator<?> iteratorColumns = jsonRow.keys();
                                ContentValues cv = new ContentValues();

                                while (iteratorColumns.hasNext()) {

                                    String col = (String) iteratorColumns.next();
                                    cv.put(col, jsonRow.getString(col));
                                }

                                ClassDataBaseImage entry = new ClassDataBaseImage(context);

                                entry.open();

                                entry.addEntry(cv, tableName);

                                entry.close();

                            }

                        } else {

                            System.out.println(LOGTAG + "No new rows");

                        }

                        if (asyncFinishListener != null) {

                            System.out.println(LOGTAG + "LISTENER SET");

                            if(jsonObj.has("all_ids")) {

                                String allIds =  jsonObj.getString("all_ids");
                                if (allIds.equals("")) {allIds = "('')";}

                                asyncFinishListener.processFinished(context, true,
                                        jsonObj.getString("table_name"),
                                        jsonObj.getString("key_name"),
                                        allIds);

                            } else {

                                asyncFinishListener.processFinished(true);

                            }

                        } else {

                            System.out.println(LOGTAG + "LISTENER NOT SET");

                        }

                    } else if (query_success == 0) {
                        System.out.println(LOGTAG + jsonObj.getString("error"));
                    } else {
                        System.out.println(LOGTAG + "Something unexpected");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(LOGTAG + "No JSON data");
            }
        }
    }
}