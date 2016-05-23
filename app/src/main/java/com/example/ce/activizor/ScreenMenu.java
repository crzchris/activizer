package com.example.ce.activizor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by CE on 5/3/2016.
 */
public class ScreenMenu extends ListActivity {

    private AppHelper appHelper = new AppHelper();

//    String menuItems[] =  getResources().getStringArray(R.array.main_menu_items);
    String menuItems[] = {"ActList", "test", "Other Stuff"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(ScreenMenu.this, android.R.layout.simple_list_item_1, menuItems));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);



        System.out.println("DEBUG:" + appHelper.PROJECT + "." + menuItems[position]);

        switch (menuItems[position]) {

            case "ActList":

                startActivity(new Intent(ScreenMenu.this, appHelper.CL_ACTLIST));

                break;

            case "test":

                startActivity(new Intent(ScreenMenu.this, appHelper.CL_TEST));

                break;

            case "Other Stuff":

                break;

        }
    }
}
