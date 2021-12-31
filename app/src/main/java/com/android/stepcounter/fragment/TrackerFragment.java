package com.android.stepcounter.fragment;

import static com.android.stepcounter.sevices.SensorService.isGpsService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.stepcounter.R;
import com.android.stepcounter.activity.GPSStartActivity;
import com.android.stepcounter.sevices.SensorService;
import com.android.stepcounter.utils.StorageManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TrackerFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    View view;
    Spinner spTargetType, spType;
    TextView mTvtypevalue, mTvtypeDesc;
    LinearLayout mlladdwater, mllRemovewater, llremove;
    Button btnStart;
    String TargetType;
    double TargerDistance = 0;
    int TargetCalories = 0;
    Calendar calendar = Calendar.getInstance();
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    public TrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_training, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSharePreferanceData();
        init();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();
    }

    private void getSharePreferanceData() {
        TargerDistance = Double.parseDouble(StorageManager.getInstance().getTargetDistance());
        StorageManager.getInstance().getTargetDuration();
        TargetCalories = Integer.parseInt(StorageManager.getInstance().getTargetCalories());
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_view_map);
//                    supportMapFragment.getMapAsync((OnMapReadyCallback) getActivity());
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void init() {
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
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
                Intent i = new Intent(getActivity(), GPSStartActivity.class);
                i.putExtra("TargetType", TargetType);
                getActivity().startActivity(i);
                break;
        }
    }
}