package com.example.ce.activizor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by CE on 17/4/2016.
 */
public class ClassAptCalendar extends LinearLayout {


    private static final String DATE_FORMAT = "MMM yyyy";
    private static final String LOGTAG = "DEBUG CTC:";
    private String ACTIVITY = "";
    private String USERNAME = "";
    private ArrayList<Map> aptList;

    private Context context;

//    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;


    public ClassAptCalendar(Context c) {

        super(c);
        context = c;

    }


    public ArrayList<Map> getAppointmentList() {

        return this.aptList;

    }


    public ClassAptCalendar(Context c, AttributeSet attrs) {

        super(c, attrs);
        context = c;
        initialize(context, attrs);

    }


    public ClassAptCalendar(Context c, AttributeSet attrs, int defStyleAttr) {

        super(c, attrs, defStyleAttr);
        context = c;
        initialize(c, attrs);

    }


    public void setActivity(String act) {

        ACTIVITY = act;

    }

    public void setUserName(String user) {

        USERNAME = user;

    }


    private void initialize(Context context, AttributeSet attr) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.content_appointment_calendar, this);
        assignUiElements();
        assignClickHandlers();

    }


    private void assignUiElements() {

//        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);

    }


    public void updateCalendar() {

        HashSet<Date> events = getEventDates();
        updateCalendar(events);

    }


    public void updateCalendar(HashSet<Date> events) {

        ArrayList<Date> cells = new ArrayList<>();

        Calendar calendar = (Calendar)currentDate.clone();

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 2;


        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (cells.size() < 42) {

            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        txtDate.setText(sdf.format(currentDate.getTime()));

    }


    private class CalendarAdapter extends ArrayAdapter<Date> {

        private HashSet<Date> eventDays;
        private LayoutInflater inflator;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays) {

            super(context, R.layout.content_calendar_day, days);
            this.eventDays = eventDays;
            inflator = LayoutInflater.from(context);

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            Calendar calendar = Calendar.getInstance();
            Calendar calEvent = Calendar.getInstance();
            Calendar calToday = Calendar.getInstance();

            Date today = new Date();
            calToday.setTime(today);

            Date date = getItem(position);
            calendar.setTime(date);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            if (view == null) {

                view = inflator.inflate(R.layout.content_calendar_day, parent, false);

            }

            view.setBackgroundResource(0);

            if (eventDays != null) {

                for (Date eventDate : eventDays) {

                    calEvent.setTime(eventDate);

                    System.out.println(LOGTAG + " SDF " + AppHelper.SDF_DEFAULT.format(calEvent.getTime()));

                    if (calEvent.get(Calendar.DAY_OF_MONTH) == day &&
                            calEvent.get(Calendar.MONTH) == month &&
                            calEvent.get(Calendar.YEAR) == year) {

                        view.setBackgroundResource(R.drawable.reminder);

                    }
                }
            }

            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            if (month != currentDate.get(Calendar.MONTH) || year != currentDate.get(Calendar.YEAR))
            {
                ((TextView)view).setTextColor(getResources().getColor(R.color.greyed_out));
            }
            if (day == calToday.get(Calendar.DAY_OF_MONTH) && month == calToday.get(Calendar.MONTH)
                    && year == calToday.get(Calendar.YEAR))
            {
                ((TextView) view).setTypeface(null, Typeface.BOLD);
                ((TextView)view).setTextColor(getResources().getColor(R.color.today));
            }

            ((TextView)view).setText(String.valueOf(date.getDate()));

            return view;
        }
    }

    public void setAppointmentsFromDB() {

        System.out.println(LOGTAG + "getAppointments");


        DataBase info = new DataBase(context);
        info.open();
        aptList = info.getAppointmentsByDate(this.USERNAME, "%", this.ACTIVITY);
        info.close();

    }

    private HashSet<Date> getEventDates() {

        HashSet<Date> events = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppHelper.DATEFORMAT);

        for (Map apt : aptList) {

            String dateString = apt.get(DataBase.DATE).toString();
            try {
                Date date = sdf.parse(dateString);
              events.add(date);
            } catch (ParseException e) {

                //TODO add exception

            }

        }

        return events;
    }


    private void assignClickHandlers()
    {

        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View cell, int position, long id) {

                if (eventHandler != null) {

                    eventHandler.onDaySelect((Date) parent.getItemAtPosition(position));

                }

            }

        });


        // long-pressing a day
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                eventHandler.onDayLongPress((Date)parent.getItemAtPosition(position));
                return true;
            }
        });
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler) {

        this.eventHandler = eventHandler;

    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {

        void onDayLongPress(Date date);

        void onDaySelect(Date date);

    }
}
