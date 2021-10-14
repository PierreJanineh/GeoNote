package com.pierrejanineh.geonote.UI.navigation.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.pierrejanineh.geonote.Model.Note.NOTE_UID;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.pierrejanineh.geonote.Model.FirebaseManager;
import com.pierrejanineh.geonote.Model.Note;
import com.pierrejanineh.geonote.R;
import com.pierrejanineh.geonote.UI.navigation.AddEditNoteActivity;
import com.pierrejanineh.geonote.databinding.FragmentMapBinding;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String MAP_FUSED_LOCATION_PROVIDER = "MAP_FUSED_LOCATION_PROVIDER";
    private FragmentMapBinding binding;

    private GoogleMap map;
    private Boolean locationPermissionGranted = false;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeMap();

        getLocationPermission();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        googleMap.setOnMarkerClickListener(this);

        // Observe to Notes array changes and update Markers on map
        FirebaseManager.getInstance().getNotes().observe(this, notes -> {
            googleMap.clear();

            for (Note note : notes) {
                GeoPoint location = note.getLocation();

                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title(note.getTitle())
                                .snippet(note.getBody())
                );
                marker.setTag(note.getUid());
            }

            updateLocationUI();

            getDeviceLocation();
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent intent = new Intent(getContext(), AddEditNoteActivity.class);
        intent.putExtra(NOTE_UID, marker.getTag().toString());
        startActivity(intent);
        return false;
    }

    /**
     * Create Map
     */
    private void initializeMap() {
        if (isAdded()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLng(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude())));
                        }
                    } else {
                        Log.d(MAP_FUSED_LOCATION_PROVIDER, "Current location is null.");
                        Log.e(MAP_FUSED_LOCATION_PROVIDER, "Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e)  {
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
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            requestPermission(new String[]{
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
            });
        }
    }

    /**
     * Prompts permissions request and handles the result of the request for location permissions.
     */
    private void requestPermission(String[] permissions) {
        registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(
                    ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(
                    ACCESS_COARSE_LOCATION, false);

            locationPermissionGranted = fineLocationGranted && coarseLocationGranted;

            if (!locationPermissionGranted) {
                Toast.makeText(getActivity(), R.string.location_is_needed, Toast.LENGTH_LONG).show();
                return;
            }

            updateLocationUI();
            getDeviceLocation();
        }).launch(permissions);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}