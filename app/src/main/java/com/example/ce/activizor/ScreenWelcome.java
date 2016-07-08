package com.example.ce.activizor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ScreenWelcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_welcome);

        Thread timer = new Thread(){
            public void run(){

                try{

                    sleep(1000);

                }

                catch (InterruptedException e){

                    e.printStackTrace();

                }

                finally{

                    Intent goTo = new Intent(ScreenWelcome.this, AppHelper.CL_LOGIN);
                    startActivity(goTo);

                }
            };
        };

        timer.start();



    }
}
