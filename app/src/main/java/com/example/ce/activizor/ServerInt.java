package com.example.ce.activizor;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerInt {

    public void newUser(Context context, String phpFile, String... userDetails) {

        String userName = userDetails[0];
        String userPw = userDetails[1];
        String userEmail = userDetails[2];

        String parameters = "user_name=" + userName + "&user_pw=" + userPw + "&user_email=" + userEmail;

        new InsertIntoDb(context).execute(phpFile, parameters);

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

        new InsertIntoDb(context).execute(phpFile, parameters);

    }

    public class InsertIntoDb extends AsyncTask<String, Void, String> {

        private Context context;

        public InsertIntoDb(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... parametersArray) {

            String phpFile = parametersArray[0];
            String parameters = parametersArray[1];

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;

//            System.out.println(parameters);

            try {
                url = new URL("http://10.0.2.2/phptest/" + phpFile);
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
                System.out.println("DEBUG: Message from Server: \n" + response);
                isr.close();
                reader.close();
                return "DEBUG: Message from Server: \n" + response;

            } catch (IOException e) {
                // Error
                return e.toString();
            }
        }

        protected void onPostExecute(String jsonStr) {

            System.out.println("DEBUG: " + jsonStr);

        }

    }
}