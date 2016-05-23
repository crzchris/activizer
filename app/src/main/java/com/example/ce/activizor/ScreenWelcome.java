package com.example.ce.activizor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ScreenWelcome extends AppCompatActivity {

    private AppHelper appHelper = new AppHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Thread timer = new Thread(){
            public void run(){

                try{

                    sleep(1000);

                }

                catch (InterruptedException e){

                    e.printStackTrace();

                }

                finally{

                    Intent goTo = new Intent(ScreenWelcome.this, LoginScreen.class);
                    startActivity(goTo);

                }
            };
        };

        timer.start();



    }
}
