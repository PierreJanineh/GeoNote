package com.pierrejanineh.geonote.UI.navigation;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.pierrejanineh.geonote.Model.Note.NOTE_UID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.pierrejanineh.geonote.Model.FirebaseManager;
import com.pierrejanineh.geonote.Model.Listeners.LocationListener;
import com.pierrejanineh.geonote.Model.Listeners.NoteReadyListener;
import com.pierrejanineh.geonote.Model.Note;
import com.pierrejanineh.geonote.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private final FirebaseManager firebase = FirebaseManager.getInstance();

    private DatePicker date;
    private EditText title;
    private EditText body;
    private Button addBtn;
    private ProgressBar progressBar;

    private LinearLayoutCompat editBtnsLyot;
    private Button saveBtn;
    private Button deleteBtn;

    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        addBtn = findViewById(R.id.addNoteBtn);
        progressBar = findViewById(R.id.progressBar);
        editBtnsLyot = findViewById(R.id.editButtonsLayout);
        saveBtn = findViewById(R.id.saveNoteBtn);
        deleteBtn = findViewById(R.id.deleteNoteBtn);

        getLocationPermission();

        switchBetweenAddEdit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Go Back when Back button is clicked.
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Switches between Add screen and Edit screen according to Intent Extras.
     * If Intent has StringExtra(NOTE_UID) -> Note's Uid has been passed -> Screen is Edit.
     */
    private void switchBetweenAddEdit() {
        String uid = getIntent().getStringExtra(NOTE_UID);
        if (uid != null) {
            Note note = Arrays.stream(firebase.getNotes().getValue()).filter(note1 -> uid.equals(note1.getUid())).findFirst().get();

            Calendar cal = Calendar.getInstance();
            cal.setTime(note.getDate());

            date.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
            title.setText(note.getTitle());
            body.setText(note.getBody());

            addBtn.setVisibility(View.GONE);
            editBtnsLyot.setVisibility(View.VISIBLE);

            saveBtn.setOnClickListener(view -> {
                prepareNote(note1 -> {
                    note1.setUid(uid);
                    setNoteOnFirebaseManager(note1, true);
                });
            });

            deleteBtn.setOnClickListener(view -> {
                deleteNoteOnFirebaseManager(note);
            });
        }

        addBtn.setOnClickListener(view -> {
            prepareNote(note -> {
                setNoteOnFirebaseManager(note, false);
            });
        });
    }

    /**
     * Handle deleting note on FirebaseManager and display a Toast for Success/Failure.
     * @param note Note to be deleted.
     */
    private void deleteNoteOnFirebaseManager(Note note){
        firebase.deleteNoteOnDB(note, unused -> {
            Toast.makeText(this, R.string.note_deletion_success, Toast.LENGTH_LONG).show();
            finish();
        }, e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Add/Update Note on FirebaseManager and display a Toast for Success/Failure.
     * @param note Note to be Added/Updated.
     * @param isUpdate true for Update | false for Addition.
     */
    private void setNoteOnFirebaseManager(Note note, boolean isUpdate){
        firebase.setNoteOnDB(note, unused -> {
            Toast.makeText(this, isUpdate ? R.string.note_update_success : R.string.note_addition_success, Toast.LENGTH_LONG).show();
            finish();
        }, e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Create a Note instance and get Current Location to pass NoteReadyListener.OnReady Callback with Note instance.
     * @param listener NoteReadyListener to continue with DB.
     */
    private void prepareNote(NoteReadyListener listener) {
        progressBar.setVisibility(View.VISIBLE);

        Date dateO = getDate(date.getYear(), date.getMonth(), date.getDayOfMonth());
        String titleS = title.getText().toString();
        String bodyS = body.getText().toString();

        getLocation(location -> {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            Note note = new Note(titleS, bodyS, geoPoint, dateO);
            listener.OnReady(note);
        });
    }

    /**
     * Get Current Device Location, Throws an exception if fails.
     * @param listener LocationListener for handling onComplete CallBack.
     */
    private void getLocation(LocationListener listener){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted){
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        listener.onSuccess(task.getResult());
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        } catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION
                    }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
    }

    /**
     * Gets Date instance.
     * @param year Year in numbers.
     * @param month Month of Year in numbers.
     * @param day Day of Month in numbers.
     * @return Date instance.
     */
    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}