package com.example.davidbuscholl.veranstalter.GUI.Activities.Veranstalter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.Entities.Event;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Token;
import com.example.davidbuscholl.veranstalter.Helpers.Validation;
import com.example.davidbuscholl.veranstalter.Helpers.ValidationRules;
import com.example.davidbuscholl.veranstalter.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VeranstaltungTreffenActivity extends AppCompatActivity {
    private Calendar myCalendar;
    private int eventId = -1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veranstaltung_treffen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        eventId = i.getIntExtra("event", -1);
        if (eventId == -1) {
            ServerErrorDialog.show(VeranstaltungDetailActivity.context, "Fehlerhafter Aufruf");
            finish();
            return;
        }

        final EditText dateInput = (EditText) findViewById(R.id.mAddDate);
        final EditText starttime = (EditText) findViewById(R.id.mAddStarttime);
        final EditText endtime = (EditText) findViewById(R.id.mAddEndtime);
        final RadioGroup radios = (RadioGroup) findViewById(R.id.mAddRepeatGroup);
        final EditText repeatInput = (EditText) findViewById(R.id.mAddRepeats);
        final Button button = (Button) findViewById(R.id.mAddButton);
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd. MM. yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

                dateInput.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(VeranstaltungTreffenActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        View.OnClickListener ocl = new View.OnClickListener() {
            EditText input;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (v.getId() == R.id.mAddStarttime) {
                    input = (EditText) findViewById(R.id.mAddStarttime);
                } else {
                    input = (EditText) findViewById(R.id.mAddEndtime);
                }
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(VeranstaltungTreffenActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hour = selectedHour < 10 ? "0" + String.valueOf(selectedHour) : String.valueOf(selectedHour);
                        String minute = selectedMinute < 10 ? "0" + String.valueOf(selectedMinute) : String.valueOf(selectedMinute);
                        input.setText(hour + ":" + minute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        };
        starttime.setOnClickListener(ocl);
        endtime.setOnClickListener(ocl);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateInput.getText().toString();
                String start = starttime.getText().toString();
                String end = endtime.getText().toString();
                String repeats = repeatInput.getText().toString();
                int radio = radios.indexOfChild(radios.findViewById(radios.getCheckedRadioButtonId()));
                HashMap<String, String> values = new HashMap<String, String>();
                values.put("date", date);
                values.put("starttime", start);
                values.put("endtime", end);

                Validation validation = new Validation();
                validation.check(values, ValidationRules.getMeetingRules());
                if (!validation.hasPassed()) {
                    String errors = "";
                    for (String s : validation.getErrors()) {
                        errors += s + "\n";
                    }
                    ServerErrorDialog.show(VeranstaltungTreffenActivity.this, errors);
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.GERMANY);
                Date startdate;
                Date enddate;
                try {
                    Calendar cal = Calendar.getInstance();
                    startdate = sdf.parse(date + " " + start);
                    enddate = sdf.parse(date + " " + end);
                    if (startdate.compareTo(enddate) > 0) {
                        cal.setTime(enddate);
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        enddate = cal.getTime();
                    }

                    ArrayList<Date> startdates = new ArrayList<Date>();
                    ArrayList<Date> enddates = new ArrayList<Date>();
                    startdates.add(startdate);
                    enddates.add(enddate);
                    if (repeats.length() > 0 && radio > 0) {
                        int num = Integer.parseInt(repeats);
                        switch(radio) {
                            case 1:
                                for(int i = 1; i <= num; i++) {
                                    cal.setTime(startdate);
                                    cal.add(Calendar.DAY_OF_MONTH, i);
                                    startdates.add(cal.getTime());
                                    cal.setTime(enddate);
                                    cal.add(Calendar.DAY_OF_MONTH, i);
                                    enddates.add(cal.getTime());
                                }
                                break;
                            case 2:
                                for(int i = 1; i <= num; i++) {
                                    cal.setTime(startdate);
                                    cal.add(Calendar.WEEK_OF_YEAR, i);
                                    startdates.add(cal.getTime());
                                    cal.setTime(enddate);
                                    cal.add(Calendar.WEEK_OF_YEAR, i);
                                    enddates.add(cal.getTime());
                                }
                                break;
                            case 3:
                                for(int i = 1; i <= num; i++) {
                                    cal.setTime(startdate);
                                    cal.add(Calendar.WEEK_OF_YEAR, i*2);
                                    startdates.add(cal.getTime());
                                    cal.setTime(enddate);
                                    cal.add(Calendar.WEEK_OF_YEAR, i*2);
                                    enddates.add(cal.getTime());
                                }
                                break;
                            case 4:
                                for(int i = 1; i <= num; i++) {
                                    cal.setTime(startdate);
                                    cal.add(Calendar.WEEK_OF_YEAR, i*4);
                                    startdates.add(cal.getTime());
                                    cal.setTime(enddate);
                                    cal.add(Calendar.WEEK_OF_YEAR, i*4);
                                    enddates.add(cal.getTime());
                                }
                                break;
                        }
                    }

                    final ProgressDialog progress = ProgressDialog.show(VeranstaltungTreffenActivity.this, "Ladevorgang", "Bitte warten...", true, false);

                    progress.show();
                    RequestQueue queue = Volley.newRequestQueue(VeranstaltungTreffenActivity.this);
                    String url = "http://37.221.196.48/thesis/public/meetings/create/" + eventId;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress.dismiss();
                            Log.d(this.toString(), response);
                            JSONObject ob = null;
                            try {
                                ob = new JSONObject(response);
                                if (ob.has("success")) {
                                    if (ob.getBoolean("success")) {
                                        Toast.makeText(VeranstaltungTreffenActivity.this,"Treffen hinzugefügt!",Toast.LENGTH_SHORT);
                                        VeranstaltungDetailActivity.reload();
                                        finish();
                                    } else {
                                        ServerErrorDialog.show(VeranstaltungTreffenActivity.this);
                                    }
                                } else {
                                    ServerErrorDialog.show(VeranstaltungTreffenActivity.this);
                                    ((Activity) VeranstaltungTreffenActivity.this).finish();
                                }
                            } catch (Exception e) {
                                ServerErrorDialog.show(VeranstaltungTreffenActivity.this);
                                e.printStackTrace();
                                ((Activity) VeranstaltungTreffenActivity.this).finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("token", Token.get(VeranstaltungTreffenActivity.this));
                            map.put("id", String.valueOf(eventId));
                            return map;
                        }
                    };
                    queue.add(stringRequest);

                } catch (ParseException e) {
                    ServerErrorDialog.show(VeranstaltungTreffenActivity.this, "Formatfehler! Bitte überprüfe deine Eingaben");
                }

            }
        });
    }


}