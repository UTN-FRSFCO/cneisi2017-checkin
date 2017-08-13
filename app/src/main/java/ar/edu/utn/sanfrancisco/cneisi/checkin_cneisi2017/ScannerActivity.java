package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


import org.json.JSONObject;

import java.util.List;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceDbHelper;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services.ApiService;

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = ScannerActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private TextView tvAssistants;

    private int assistants;

    private int conferenceId;

    private AssistanceDbHelper assistanceDbHelper;
    private ApiService apiService;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            try {
                JSONObject assistantJson = new JSONObject(result.getText());

                Assistance assistence = new Assistance(assistantJson, conferenceId);

                new AddAssistanceTask().execute(assistence);

                assistants++;
                tvAssistants.setText(Integer.toString(assistants) + " Asistentes");
            } catch (Throwable t) {
                Log.e("ERROR", "Could not parse malformed JSON: \"" + result.getText() + "\"");
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        tvAssistants = (TextView) findViewById(R.id.tvAssistants);

        Bundle bundle = getIntent().getExtras();
        String conferenceName = bundle.getString("ConferenceName");

        this.setTitle(conferenceName);
        this.conferenceId = bundle.getInt("ConferenceId");

        assistants = 0;

        assistanceDbHelper = new AssistanceDbHelper(this);
        apiService = new ApiService();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(ScannerActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ScannerActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ScannerActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private class AddAssistanceTask extends AsyncTask<Assistance, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Assistance... assistances) {
            Assistance assistance = assistances[0];

            boolean saved = assistanceDbHelper.saveAssistance(assistance) > 0;
            if (saved) {
                boolean sent = apiService.postAssistance(assistance);

                if (sent) {
                    assistance.setSent(true);
                    assistanceDbHelper.saveAssistance(assistance);
                }

                return apiService.postAssistance(assistance);
            }

            return apiService.postAssistance(assistance);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i("INFO", "Assistance saved");
        }
    }
}
