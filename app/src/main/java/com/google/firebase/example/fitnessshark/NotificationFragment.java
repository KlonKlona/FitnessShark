package com.google.firebase.example.fitnessshark;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.example.fitnessshark.adapter.CalendarAdapter;

public class NotificationFragment extends Fragment {

    public GregorianCalendar month, itemmonth;// calendar instances.

    public CalendarAdapter adapter;// adapter instance
    public Handler handler;// for grabbing some event values for showing the dot
    // marker.
    public ArrayList<String> items; // container to store calendar items which
    // needs showing the event marker


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar,  container, false);

        Locale.setDefault( Locale.US );
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        items = new ArrayList<String>();
        adapter = new CalendarAdapter(getActivity(), month);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        RelativeLayout previous = (RelativeLayout) view.findViewById(R.id.previous);

        previous.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        RelativeLayout next = (RelativeLayout) view.findViewById(R.id.next);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                String selectedGridDate = CalendarAdapter.dayString
                        .get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                showToast(selectedGridDate);

            }
        });
        return view;
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

    }

    public void refreshCalendar() {
        TextView title = (TextView) getActivity().findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            items.clear();

            // Print dates of the current week
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            String itemvalue;
            for (int i = 0; i < 7; i++) {
                itemvalue = df.format(itemmonth.getTime());
                itemmonth.add(GregorianCalendar.DATE, 1);
                ArrayList<LocalDate> days = new ArrayList<>();
                days.add(YearMonth.now().atDay(1).with(DayOfWeek.MONDAY));
                days.add(YearMonth.now().atDay(1).with(DayOfWeek.WEDNESDAY));
                days.add(YearMonth.now().atDay(1).with(DayOfWeek.FRIDAY));
                items = (ArrayList<String>) weeksInCalendar(YearMonth.now(), days);
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<String> weeksInCalendar(YearMonth month, ArrayList<LocalDate> days) {
        List<String> firstDaysOfWeeks = new ArrayList<>();
        for (LocalDate day : days) {
            for (day = firstDayOfCalendar(month); stillInCalendar(month, day); day = day
                    .plusWeeks(1)) {
                firstDaysOfWeeks.add(day.toString());
            }
            for (day = thirdDayOfCalendar(month); stillInCalendar(month, day); day = day
                    .plusWeeks(1)) {
                firstDaysOfWeeks.add(day.toString());
            }
            for (day = fifthDayOfCalendar(month); stillInCalendar(month, day); day = day
                    .plusWeeks(1)) {
                firstDaysOfWeeks.add(day.toString());
            }
        }
        return firstDaysOfWeeks;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate firstDayOfCalendar(YearMonth month) {
        DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.MONDAY;
        return month.atDay(1).with(FIRST_DAY_OF_WEEK);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate thirdDayOfCalendar(YearMonth month) {
        DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.WEDNESDAY;
        return month.atDay(1).with(FIRST_DAY_OF_WEEK);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate fifthDayOfCalendar(YearMonth month) {
        DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.FRIDAY;
        return month.atDay(1).with(FIRST_DAY_OF_WEEK);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean stillInCalendar(YearMonth yearMonth, LocalDate day) {
        return !day.isAfter(yearMonth.atEndOfMonth());
    }
//
//    private NotificationViewModel mViewModel;
//
//    public static NotificationFragment newInstance() {
//        return new NotificationFragment();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.notification_fragment, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
//        // TODO: Use the ViewModel
//    }

}

