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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import Tools.AsyncFinishListener;


public class ClassServerInt {

    Context context;
    private AsyncFinishListener asyncFinishListener;
    private String LOGTAG = "DEBUG ClassServerInt: ";

    public ClassServerInt(Context c) {

        context = c;

    }


    public void setAsyncFinishListener(AsyncFinishListener asyncFinishListener) {

        System.out.println(LOGTAG + "setAsyncFinishListener");

        this.asyncFinishListener = asyncFinishListener;

    }


    public void newUser(Context context, String phpFile, String... userDetails) {

        String userName = userDetails[0];
        String userPw = userDetails[1];
        String userEmail = userDetails[2];

        String parameters = "user_name=" + userName + "&user_pw=" + userPw + "&user_email=" + userEmail;

        new QueryDb(context).execute(phpFile, parameters);

    }

    public void newUserAct(Context context, String phpFile, String... actDetails) {

        String userName = actDetails[0];
        String activity = actDetails[1];

        String parameters = "user_name=" + userName + "&activity_name=" + activity;

        new QueryDb(context).execute(phpFile, parameters);

    }

    public void newApt(Context context, String phpFile, String... aptDetails) {

        String userName = aptDetails[0];
        String activityName = aptDetails[1];
        String date = aptDetails[2];
        String startTime = aptDetails[3];
        String endTime = aptDetails[4];
        String location = aptDetails[5];

        String parameters = "user_name=" + userName +
                "&activity_name=" + activityName +
                "&date=" + date +
                "&start_time=" + startTime +
                "&end_time=" + endTime +
                "&location=" + location;

        new QueryDb(context).execute(phpFile, parameters);

    }

    public void getNewRowsFromDbAndSync(Context context, String phpFile, String... userDetails) {

        String table_name = userDetails[0];
        String key_name = userDetails[1];
        String key_list = userDetails[2];
        String return_ids = userDetails[3];

        String parameters = "table_name=" + table_name + "&key_name=" + key_name
                + "&key_list=" + key_list + "&return_ids=" + return_ids ;

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

        getNewRowsFromDbAndSync(context, AppHelper.PHP_GET_NEWROWS, tableName, keyName, keyList, return_ids);

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

                                String c = jsonObj.getString("all_ids");

                                asyncFinishListener.processFinished(context, true,
                                        jsonObj.getString("table_name"),
                                        jsonObj.getString("key_name"),
                                        jsonObj.getString("all_ids"));

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