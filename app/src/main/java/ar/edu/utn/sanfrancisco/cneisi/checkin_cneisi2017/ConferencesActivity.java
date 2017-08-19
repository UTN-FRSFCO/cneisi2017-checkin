package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Conference;

public class ConferencesActivity extends AppCompatActivity {
    TextView tvAuditoriumName;
    ListView lvConferences;
    Spinner spinnerDays;

    private String[] arraySpinner;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferences);

        tvAuditoriumName = (TextView) findViewById(R.id.tvAuditoriumName);
        lvConferences = (ListView) findViewById(R.id.lvConferences);
        spinnerDays = (Spinner) findViewById(R.id.spinnerDay);

        Bundle bundle = getIntent().getExtras();
        String auditoriumName = bundle.getString("auditorium");

        this.setTitle(auditoriumName);

        this.arraySpinner = new String[] {
                "Jueves", "Viernes"
        };

        ArrayAdapter<String> adapterDays = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setAdapter(adapterDays);

        if (isSecondDay()) {
            spinnerDays.setSelection(1);
        } else {
            spinnerDays.setSelection(0);
        }

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Cargando conferencias...");

        final ArrayList<Conference> conferences = this.getConferences();

        ConferenceAdapter adapter = new ConferenceAdapter(ConferencesActivity.this, conferences);
        lvConferences.setAdapter(adapter);

        lvConferences.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conference conference = conferences.get(position);
                Intent intent = new Intent(ConferencesActivity.this, ScannerActivity.class);

                try
                {
                    intent.putExtra("ConferenceID", conference.getId());
                    intent.putExtra("ConferenceName", conference.getTitle());

                    startActivity(intent);
                }catch (Exception e)
                {
                    Toast.makeText(ConferencesActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class ConferenceAdapter extends ArrayAdapter<Conference> {
        private ConferenceAdapter(Context context, ArrayList<Conference> conferences) {
            super(context, 0, conferences);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Conference conference = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_conference, parent, false);
            }

            TextView tvName = (TextView) convertView.findViewById(R.id.tvConferenceName);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvConferenceDate);

            tvName.setText(conference.getTitle());
            tvDate.setText(conference.getDate());

            return convertView;
        }
    }

    private ArrayList<Conference> getConferences() {
        Conference conference1 = new Conference();
        conference1.setId(1);
        conference1.setTitle("IBM - Watson");
        conference1.setDate("30/08/2017 16:00");

        Conference conference2 = new Conference();
        conference2.setId(2);
        conference2.setTitle("Asegurarte.com - Bla bla");
        conference2.setDate("30/08/2017 18:00");

        Conference conference3 = new Conference();
        conference3.setId(3);
        conference3.setTitle("Sergio Guzman - Chatbot");
        conference3.setDate("30/08/2017 19:00");

        ArrayList<Conference> conferences = new ArrayList<Conference>();
        conferences.add(conference1);
        conferences.add(conference2);
        conferences.add(conference3);

        return conferences;
    }

    private boolean isSecondDay() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date secondDay = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String secondDayAsString = dateFormat.format(secondDay);

        calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        String todayAsString = dateFormat.format(today);

        return secondDayAsString.equals(todayAsString);
    }
}
