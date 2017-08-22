package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.ConferenceDbHelper;

public class ConferencesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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
        String auditoriumCode = bundle.getString("auditoriumCode");

        this.setTitle(auditoriumName);

        this.arraySpinner = new String[]{
                "Jueves", "Viernes"
        };

        ArrayAdapter<String> adapterDays = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setAdapter(adapterDays);
        spinnerDays.setOnItemSelectedListener(this);

        if (isSecondDay()) {
            spinnerDays.setSelection(1);
        } else {
            spinnerDays.setSelection(0);
        }

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Cargando conferencias...");

        this.loadConferences(auditoriumCode);
    }

    private Context getActivity() {
        return this;
    }

    private void loadConferences(String auditoriumCode) {
        final ArrayList<Conference> conferences = this.getConferences(auditoriumCode);
        ConferenceAdapter adapter = new ConferenceAdapter(ConferencesActivity.this, conferences);
        lvConferences.setAdapter(adapter);

        lvConferences.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Conference conference = conferences.get(position);
                final Intent intent = new Intent(ConferencesActivity.this, ScannerActivity.class);

                try {

                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(conference.getTitle())
                            .setMessage("Quiere escanear credenciales de la charla " + conference.getTitle() + " en el horario " +
                                    "" + conference.getHour() + " ?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    intent.putExtra("ConferenceID", conference.getExternalId());
                                    intent.putExtra("ConferenceName", conference.getTitle());

                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                } catch (Exception e) {
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
            tvDate.setText(conference.getHour());

            return convertView;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Bundle bundle = getIntent().getExtras();
        String auditoriumCode = bundle.getString("auditoriumCode");

        this.loadConferences(auditoriumCode);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private ArrayList<Conference> getConferences(String auditoriumCode) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date firstDay = calendar.getTime();

        calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date secondDay = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String firstDayAsString = dateFormat.format(firstDay);
        String secondDayAsString = dateFormat.format(secondDay);

        ConferenceDbHelper conferenceDbHelper = ConferenceDbHelper.getInstance(this);

        ArrayList<Conference> conferences = new ArrayList<Conference>();

        try {
            Cursor cursor = conferenceDbHelper.getByAuditorium(auditoriumCode);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Conference conference = new Conference(cursor);
                cursor.moveToNext();

                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date conferenceDate = simpleDateFormat.parse(conference.getDate());
                String conferenceDayAsString = dateFormat.format(conferenceDate);

                if (spinnerDays.getSelectedItemPosition() == 0 && conferenceDayAsString.equals(firstDayAsString) ||
                        spinnerDays.getSelectedItemPosition() == 1 && conferenceDayAsString.equals(secondDayAsString)) {
                    conferences.add(conference);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

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
