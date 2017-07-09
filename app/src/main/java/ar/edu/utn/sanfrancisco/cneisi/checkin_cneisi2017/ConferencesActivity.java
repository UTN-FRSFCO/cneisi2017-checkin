package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.models.Conference;

public class ConferencesActivity extends AppCompatActivity {
    TextView tvAuditoriumName;
    ListView lvConferences;

    ProgressDialog dialog;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferences);

        tvAuditoriumName = (TextView) findViewById(R.id.tvAuditoriumName);
        lvConferences = (ListView) findViewById(R.id.lvConferences);
        qrScan = new IntentIntegrator(this);

        Bundle bundle = getIntent().getExtras();
        String auditoriumName = bundle.getString("auditorium");

        tvAuditoriumName.setText(auditoriumName);

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
}
