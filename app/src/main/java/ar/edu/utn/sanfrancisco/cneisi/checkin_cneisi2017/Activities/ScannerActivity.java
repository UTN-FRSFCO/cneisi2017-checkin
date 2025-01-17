package ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.client.android.BeepManager;


import org.json.JSONObject;

import java.io.IOException;

import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Scanner.BarcodeTrackerFactory;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Scanner.CameraSource;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Scanner.CameraSourcePreview;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Scanner.BarcodeTracker;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Persistence.AssistanceDbHelper;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Models.Assistance;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.R;
import ar.edu.utn.sanfrancisco.cneisi.checkin_cneisi2017.Services.ApiService;

public class ScannerActivity extends AppCompatActivity implements BarcodeTracker.BarcodeGraphicTrackerCallback {

    private static final String TAG = "Barcode-reader";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    private String lastText;
    private TextView tvAssistants;
    private TextView tvLastText;
    private int assistants;
    private int conferenceId;
    private AssistanceDbHelper assistanceDbHelper;
    private ApiService apiService;
    private BeepManager beepManager;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_scanner);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        beepManager = new BeepManager(this);
        beepManager.setBeepEnabled(true);
        beepManager.setVibrateEnabled(true);

        tvAssistants = (TextView) findViewById(R.id.tvAssistants);
        lastText = "";
        tvLastText = (TextView) findViewById(R.id.tvLastText);
        tvLastText.setText(lastText);

        Bundle bundle = getIntent().getExtras();
        String conferenceName = bundle.getString("ConferenceName");

        this.setTitle(conferenceName.toUpperCase());
        this.conferenceId = bundle.getInt("ConferenceID");

        this.assistanceDbHelper = AssistanceDbHelper.getInstance(this);
        try {
            assistants = assistanceDbHelper.getByConferenceIdCloud(conferenceId).getCount();
        } catch (Exception e)
        {
            assistants = 0;
            Log.e("ERORR", e.getMessage());
        }

        tvAssistants.setText(Integer.toString(assistants) + " ASISTENTES ESCANEADOS");

        assistanceDbHelper = AssistanceDbHelper.getInstance(this);
        apiService = new ApiService();

        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onDetectedQrCode(Barcode barcode) {
        String result = barcode.displayValue;

        if (barcode != null && !result.equals(lastText)) {
            lastText = barcode.displayValue;

            setText(tvLastText, lastText);

            beepManager.playBeepSoundAndVibrate();

            try {
                JSONObject assistantJson = new JSONObject(result);

                SharedPreferences catcherDetails = getSharedPreferences("catcher", MODE_PRIVATE);
                String catcherName = catcherDetails.getString("catcher_name", "");

                Assistance assistence = new Assistance(assistantJson, conferenceId, catcherName);

                new AddAssistanceTask().execute(assistence);

                assistants++;
                setText(tvAssistants, Integer.toString(assistants) + " ASISTENTES ESCANEADOS");
            } catch (Throwable t) {
                Log.e("ERROR", "Could not parse malformed JSON: \"" + result + "\"" + t.getMessage());
            }
        }
    }

    // Handles the requesting of the camera permission.
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    /**
     * Creates and starts the camera.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Low storage",
                        Toast.LENGTH_LONG).show();
                Log.w(TAG, "Low storage");
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(metrics.widthPixels, metrics.heightPixels)
                .setRequestedFps(24.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    // Restarts the camera
    @Override
    protected void onResume() {
        super.onResume();
        lastText = "";
        startCameraSource();
    }

    // Stops the camera
    @Override
    protected void onPause() {
        super.onPause();
        lastText = "";
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        lastText = "";
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage("No camera permission")
                .setPositiveButton("ok", listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class AddAssistanceTask extends AsyncTask<Assistance, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Assistance... assistances) {
            try {
                Assistance assistance = assistances[0];

                long assistanceId = assistanceDbHelper.saveAssistance(assistance);
                if (assistanceId > 0) {
                    assistance.setId((int) assistanceId);

                    int tries = 0;
                    boolean sent = false;

                    while(tries < 3 && !sent) {
                        sent = apiService.postAssistance(assistance);
                        tries++;
                    }

                    if (sent) {
                        assistance.setSent(true);
                        long updated = assistanceDbHelper.updateAssistance(assistance);

                        Log.i("actualizado", Long.toString(updated));
                    }

                    return sent;
                }

                return false;
            } catch (Exception e)
            {
                Log.e("ERROR GUARDANDO", e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i("INFO", "Assistance saved");
        }
    }

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
}
