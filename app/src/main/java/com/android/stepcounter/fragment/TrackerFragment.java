package com.android.stepcounter.fragment;

import static com.android.stepcounter.sevices.SensorService.IsServiceStart;
import static com.android.stepcounter.sevices.SensorService.IsTargetType;
import static com.android.stepcounter.sevices.SensorService.IsTimerStart;
import static com.android.stepcounter.sevices.SensorService.isGpsService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.GPSStartActivity;
import com.android.stepcounter.sevices.LocationBgService;
import com.android.stepcounter.sevices.SensorService;
import com.android.stepcounter.utils.StorageManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TrackerFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    View view;
    Spinner spTargetType, spType;
    TextView mTvtypevalue, mTvtypeDesc;
    LinearLayout mlladdwater, mllRemovewater, llremove;
    Button btnStart;
    String TargetType;
    double TargerDistance = 0;
    int TargetCalories = 0;
    Calendar calendar = Calendar.getInstance();

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_training, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSharePreferanceData();
        init();

    }

    private void getSharePreferanceData() {
        TargerDistance = Double.parseDouble(StorageManager.getInstance().getTargetDistance());
        StorageManager.getInstance().getTargetDuration();
        TargetCalories = Integer.parseInt(StorageManager.getInstance().getTargetCalories());
    }

    @SuppressLint("MissingPermission")
    private void init() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        mlladdwater = view.findViewById(R.id.lladdwater);
        mllRemovewater = view.findViewById(R.id.llRemovewater);
        llremove = view.findViewById(R.id.llremove);

        btnStart = view.findViewById(R.id.btnStart);
        spTargetType = view.findViewById(R.id.spTargetType);
        spType = view.findViewById(R.id.spType);

        mTvtypevalue = view.findViewById(R.id.typevalue);
        mTvtypeDesc = view.findViewById(R.id.typeDesc);

        ArrayList<String> TargetList = new ArrayList<String>();
        TargetList.add("Target Distance");
        TargetList.add("Target Duration");
        TargetList.add("Target Calories");
        TargetList.add("Open Target");

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, TargetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTargetType.setAdapter(adapter);
        spTargetType.setSelection(StorageManager.getInstance().getTargetType());

        spTargetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                mTvtypeDesc.setVisibility(View.VISIBLE);
                mlladdwater.setVisibility(View.VISIBLE);
                mllRemovewater.setVisibility(View.VISIBLE);
                if (value.equals("Target Distance")) {
                    mTvtypevalue.setText(StorageManager.getInstance().getTargetDistance());
                    mTvtypeDesc.setText("Mile");
                    TargetType = "Target Distance";
                } else if (value.equals("Target Duration")) {

                    calendar.clear();
                    calendar.set(Calendar.MINUTE, Integer.parseInt(StorageManager.getInstance().getTargetDuration()));

                    mTvtypevalue.setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE));
                    mTvtypeDesc.setText("Hour : Minutes");
                    TargetType = "Target Duration";
                } else if (value.equals("Target Calories")) {
                    mTvtypevalue.setText(StorageManager.getInstance().getTargetCalories());
                    mTvtypeDesc.setText("Kcal");
                    TargetType = "Target Calories";
                } else if (value.equals("Open Target")) {
                    mTvtypevalue.setText("Free");
                    mTvtypeDesc.setVisibility(View.GONE);
                    mlladdwater.setVisibility(View.GONE);
                    mllRemovewater.setVisibility(View.GONE);
                    TargetType = "Open Target";

                }

//                Toast.makeText(getActivity(), adapterView.getItemIdAtPosition(i) + "", Toast.LENGTH_SHORT).show();
                StorageManager.getInstance().setTargetType((int) adapterView.getItemIdAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayList<String> TypeList = new ArrayList<String>();
        TypeList.add("Walk");
        TypeList.add("Run");
        TypeList.add("Ride");

        ArrayAdapter arrayAdapterr = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, TypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(arrayAdapterr);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                StorageManager.getInstance().setStepType(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mlladdwater.setOnClickListener(this);
        mllRemovewater.setOnClickListener(this);
        btnStart.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lladdwater:
                llremove.setVisibility(View.VISIBLE);
                if (TargetType.equals("Target Distance")) {
                    TargerDistance = TargerDistance + 0.1;
                    String formattedNumber = String.format(Locale.US, "%.1f", TargerDistance);
                    mTvtypevalue.setText(formattedNumber);
                    StorageManager.getInstance().setTargetDistance(formattedNumber);
                } else if (TargetType.equals("Target Duration")) {
                    calendar.set(Calendar.MINUTE, Integer.parseInt(StorageManager.getInstance().getTargetDuration()) + 1);
                    mTvtypevalue.setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE));
                    StorageManager.getInstance().setTargetDuration(String.valueOf(calendar.get(Calendar.MINUTE)));
                } else if (TargetType.equals("Target Calories")) {
                    TargetCalories = TargetCalories + 50;
                    mTvtypevalue.setText(TargetCalories + "");
                    StorageManager.getInstance().setTargetCalories(TargetCalories + "");
                }
                break;
            case R.id.llRemovewater:
                if (TargetType.equals("Target Distance")) {
                    TargerDistance = TargerDistance - 0.1;
                    if (TargerDistance > 0) {
                        String formattedNumber = String.format(Locale.US, "%.1f", TargerDistance);
                        mTvtypevalue.setText(formattedNumber);
                        StorageManager.getInstance().setTargetDistance(formattedNumber);
                    } else {
                        TargerDistance = 0;
                        mTvtypevalue.setText(0.0 + "");
                        llremove.setVisibility(View.GONE);
                        StorageManager.getInstance().setTargetDistance(TargerDistance + "");
                    }

                } else if (TargetType.equals("Target Duration")) {
                    calendar.set(Calendar.MINUTE, Integer.parseInt(StorageManager.getInstance().getTargetDuration()) - 1);
                    mTvtypevalue.setText(calendar.get(Calendar.HOUR) + " : " + calendar.get(Calendar.MINUTE));
                    StorageManager.getInstance().setTargetDuration(String.valueOf(calendar.get(Calendar.MINUTE)));
                } else if (TargetType.equals("Target Calories")) {
                    TargetCalories = TargetCalories - 50;
                    if (TargetCalories > 0) {
                        mTvtypevalue.setText(TargetCalories + "");
                    } else {
                        TargetCalories = 0;
                        mTvtypevalue.setText(0 + "");
                        llremove.setVisibility(View.GONE);
                    }
                    StorageManager.getInstance().setTargetCalories(TargetCalories + "");
                }
                break;
            case R.id.btnStart:
                if (!StorageManager.getInstance().getIsStepService()) {
                    Intent i = new Intent(getActivity(), SensorService.class);
                    getActivity().startService(i);
                    StorageManager.getInstance().setIsStepService(true);
                }
                isGpsService = true;
                IsTimerStart = true;
                IsServiceStart = true;
                IsTargetType = TargetType;

                Intent i = new Intent(getActivity(), GPSStartActivity.class);
                getActivity().startActivity(i);

                Intent intent = new Intent(getActivity(), LocationBgService.class);
                getActivity().startService(intent);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        @SuppressLint("MissingPermission") Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getActivity(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}