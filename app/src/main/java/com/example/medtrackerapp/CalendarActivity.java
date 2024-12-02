package com.example.medtrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView textView;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        textView = (TextView) findViewById(R.id.date_view);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date = (month + 1) + "/" + day + "/" + year;
                textView.setText(date);

            }


        });

    }
}
