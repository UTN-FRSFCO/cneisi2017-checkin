package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Auditorium;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services.ApiService;

public class MainActivity extends AppCompatActivity {
    private ListView lvAuditoriums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvAuditoriums = (ListView) findViewById(R.id.lvAuditoriums);

        final ArrayList<Auditorium> auditoriums = this.getAuditoriums();

        AuditoriumAdapter adapter = new AuditoriumAdapter(MainActivity.this, auditoriums);
        lvAuditoriums.setAdapter(adapter);

        lvAuditoriums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Auditorium auditorium = auditoriums.get(position);
                Intent intent = new Intent(MainActivity.this, ConferencesActivity.class);

                try
                {
                    intent.putExtra("auditorium", auditorium.getName());
                    intent.putExtra("auditoriumCode", auditorium.getCode());

                    startActivity(intent);
                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ApiService apiService = new ApiService();
        apiService.getConferences(this);
    }

    public class AuditoriumAdapter extends ArrayAdapter<Auditorium> {
        public AuditoriumAdapter(Context context, ArrayList<Auditorium> auditoriums) {
            super(context, 0, auditoriums);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Auditorium auditorium = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_auditorium, parent, false);
            }

            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);

            tvName.setText(auditorium.getName());

            return convertView;
        }
    }

    private ArrayList<Auditorium> getAuditoriums() {
        Auditorium assemblyHall = new Auditorium();
        assemblyHall.setId(1);
        assemblyHall.setName("Salón de actos");
        assemblyHall.setCode("auditorium_1");

        Auditorium fourthLevelA = new Auditorium();
        fourthLevelA.setId(2);
        fourthLevelA.setName("Cuarto nivel A");
        fourthLevelA.setCode("auditorium_2");

        Auditorium fourthLevelB = new Auditorium();
        fourthLevelB.setId(3);
        fourthLevelB.setName("Cuarto nivel B");
        fourthLevelB.setCode("auditorium_3");

        ArrayList<Auditorium> auditoriums = new ArrayList<Auditorium>();
        auditoriums.add(assemblyHall);
        auditoriums.add(fourthLevelA);
        auditoriums.add(fourthLevelB);

        return auditoriums;
    }
}
