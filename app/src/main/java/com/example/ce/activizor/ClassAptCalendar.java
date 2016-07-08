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
    private static final String LOGTAG = "DEBUG ClassAptCalendar:";
    private ClassDataBaseImage db;
    private String ACTIVITY = "";
    private ArrayList<Map> eventList;
    private ArrayList<Map> inviteList;

    private Context context;

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
        db = new ClassDataBaseImage(context);
        db.open();


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

    public ArrayList<Map> getEventList() {

        return this.eventList;

    }

    public ArrayList<Map> getInviteList() {

        return this.inviteList;

    }


    public void setActivity(String act) {

        ACTIVITY = act;

    }


    private void initialize(Context context, AttributeSet attr) {

        db = new ClassDataBaseImage(context);
        db.open();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.content_screen_calendar, this);
        assignUiElements();
        assignClickHandlers();

    }


    private void assignUiElements() {

        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);

    }


    public void updateCalendar() {

        System.out.println(LOGTAG + "updateCalendar");

        HashSet<Date> eventDays = getEventDates();
        HashSet<Date> inviteDays = getInviteDates();
        updateCalendar(eventDays, inviteDays);

    }


    public void updateCalendar(HashSet<Date> eventDays, HashSet<Date> inviteDays) {

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

        grid.setAdapter(new CalendarAdapter(getContext(), cells, eventDays, inviteDays));

        txtDate.setText(sdf.format(currentDate.getTime()));

    }


    private class CalendarAdapter extends ArrayAdapter<Date> {

        private HashSet<Date> eventDays;
        private HashSet<Date> inviteDays;
        private LayoutInflater inflator;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays, HashSet<Date> inviteDays) {

            super(context, R.layout.content_screen_calendar_day, days);
            this.eventDays = eventDays;
            this.inviteDays = inviteDays;
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

                view = inflator.inflate(R.layout.content_screen_calendar_day, parent, false);

            }

            view.setBackgroundResource(0);

            if (eventDays != null) {

                for (Date eventDate : eventDays) {

                    calEvent.setTime(eventDate);

                    if (calEvent.get(Calendar.DAY_OF_MONTH) == day &&
                            calEvent.get(Calendar.MONTH) == month &&
                            calEvent.get(Calendar.YEAR) == year) {

                        view.setBackgroundResource(R.drawable.reminder);

                    }
                }
            }

            if (inviteDays != null) {

                for (Date inviteDate : inviteDays) {

                    calEvent.setTime(inviteDate);

                    if (calEvent.get(Calendar.DAY_OF_MONTH) == day &&
                            calEvent.get(Calendar.MONTH) == month &&
                            calEvent.get(Calendar.YEAR) == year) {

                        if (eventDays.contains(inviteDate)) {

                            view.setBackgroundResource(R.drawable.reminder_blue_yellow);

                        } else {

                            view.setBackgroundResource(R.drawable.reminder_yellow);

                        }
                    }
                }
            }

            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            if (month != currentDate.get(Calendar.MONTH) || year != currentDate.get(Calendar.YEAR))
            {
                ((TextView)view).setTextColor(getResources().getColor(R.color.grey_50));
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

    public void setCalendarEventsFromDb() {

        eventList = db.dbQueries.getEventsByDate("%", this.ACTIVITY, true);
        inviteList = db.dbQueries.getNotificationsEventsByUSerId(AppHelper.getUserId(context), this.ACTIVITY);

    }

    private HashSet<Date> getEventDates() {

        HashSet<Date> events = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppHelper.DATEFORMAT);

        for (Map event : eventList) {

            String dateString = event.get(AppHelper.DB_EVENT_DATE).toString();
            try {
                Date date = sdf.parse(dateString);
                events.add(date);
            } catch (ParseException e) {

                e.printStackTrace();

            }

        }

        return events;
    }


    private HashSet<Date> getInviteDates() {

        HashSet<Date> invites = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat(AppHelper.DATEFORMAT);

        for (Map invite : inviteList) {

            String dateString = invite.get(AppHelper.DB_EVENT_DATE).toString();
            try {
                Date date = sdf.parse(dateString);
                invites.add(date);
            } catch (ParseException e) {

                e.printStackTrace();

            }

        }

        return invites;
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
