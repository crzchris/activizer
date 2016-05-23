package com.example.ce.activizor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ScreenLogin extends AppCompatActivity {

    private Login mAuthTask = null;
    private AppHelper appHelper = new AppHelper();

    // UI references.
    private EditText etPassword;
    private EditText etUserName;
    private View viewProgress;
    private View viewLoginForm;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        etUserName = (EditText) findViewById(R.id.login_userName);
        etPassword = (EditText) findViewById(R.id.login_password);
        viewLoginForm = findViewById(R.id.login_form);
        viewProgress = findViewById(R.id.login_progress);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btnSignIn = (Button)findViewById(R.id.btn_login_sign_in);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(ScreenLogin.this);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                String userName = etUserName.getText().toString();
                editor.putString(AppHelper.SP_USERNAME, userName);
                editor.commit();

                attemptLogin();

            }
        });

        Button btnRegister = (Button)findViewById(R.id.btn_login_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ScreenLogin.this);
                LayoutInflater inflater = ScreenLogin.this.getLayoutInflater();
                builder.setTitle("Register");

                final View popupView = inflater.inflate(R.layout.activity_login_popup_newuser, null);
                builder.setView(popupView);

                final EditText etUserName = (EditText) popupView.findViewById(R.id.et_login_newuser_user_name);
                final EditText etUserEmail = (EditText) popupView.findViewById(R.id.et_login_newuser_user_email);
                final EditText etUserPw = (EditText) popupView.findViewById(R.id.et_login_newuser_user_password);
                final EditText etUserPwRepeat = (EditText) popupView.findViewById(R.id.et_login_newuser_user_password_repreat);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String userName = etUserName.getText().toString();
                        String userEmail = etUserEmail.getText().toString();
                        String userPw = etUserPw.getText().toString();
                        String userPwRp = etUserPwRepeat.getText().toString();

                        if (userPw.equals(userPwRp)) {

                            ServerInt servInt = new ServerInt();
                            servInt.newUser(ScreenLogin.this, AppHelper.PHP_INSERT_USER, userName, userPw, userEmail);

                            dialog.dismiss();

                        } else {

                            appHelper.showToastMessage(ScreenLogin.this, "passwords dont match");

                        }


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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
        String userPw = etPassword.getText().toString();
        String userName = etUserName.getText().toString();

        // Reset errors.
        etUserName.setError(null);
        etPassword.setError(null);


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(userPw)) {
            etPassword.setError("Password required");
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            etUserName.setError("UserName required");
            focusView = etUserName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            UserLoginServer uls = new UserLoginServer(userName, userPw);
            uls.login();
        }
    }

    public class UserLoginServer {

        private final String mUserName;
        private final String mUserPw;

        UserLoginServer(String userName, String userPw) {

            mUserName = userName;
            mUserPw = userPw;

        }

        private void login() {

            new Login(ScreenLogin.this).execute(mUserName, mUserPw);

        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            viewLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            viewLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            viewProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            viewLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class Login extends AsyncTask<String, Void, String> {

        private Context context;

        public Login(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {

            String userName = arg0[0];
            String userPw = arg0[1];

            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {

                data =  "?user_name=" + URLEncoder.encode(userName, "UTF-8");
                data += "&user_pw=" + URLEncoder.encode(userPw, "UTF-8");

                link = "http://10.0.2.2/phptest/login.php" + data;
                URL url = new URL(link);
                System.out.println("DEBUG LINK: " + link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();

                return result;

            } catch (Exception e) {
                System.out.println("DEBUG EXC: " +  e.getMessage());
                return "";
            }
        }

        @Override
        protected void onPostExecute(String jsonStr) {

            showProgress(false);
            boolean success = false;

            System.out.println("DEBUG POS_EX: " + jsonStr);
            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    int query_result = jsonObj.getInt("success");
                    if (query_result == 1) {
                        success = true;
                    } else if (query_result == 0) {
                        appHelper.showToastMessage(context, "user/pw bad");
                    } else {
                        appHelper.showToastMessage(context, "Something else");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    appHelper.showToastMessage(context, "Error parsing JSON data.");
                }
            } else {
                appHelper.showToastMessage(context, "Couldn't get any JSON data.");
            }

            if (success) {

                Intent goTo = new Intent(context, Menu.class);
                context.startActivity(goTo);

            } else {

                appHelper.showToastMessage(context, "Wrong userName/pw");

            }
        }
    }
}
