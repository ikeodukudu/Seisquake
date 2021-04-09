/**
  *  NAME - IKEOLUWA AJIBOLA ODUKUDU
  *  MATRIC NO - S1702414
 **/

package org.me.gcu.seisquake.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.seisquake.R;
import org.me.gcu.seisquake.adapters.EarthquakeRSSAdapter;
import org.me.gcu.seisquake.models.Earthquake;
import org.me.gcu.seisquake.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapViewFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final String ARG_PARAM1 = "MapFragment";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ViewSwitcher viewSwitcher;
    private ViewFlipper mapViewFlipper;
    private Button s1Button;
    private Button s2Button;
    private RadioGroup mapTypeGroup;
    private RadioButton normalViewButton;
    private RadioButton terrainViewButton;
    private RadioButton hybridViewButton;
    private RadioButton satelliteViewButton;
    private CheckBox panZoom;
    private MapView googleMapView;
    private ImageButton imageButton;
    private TextView timeListItem, locationListItem, magListItem, latlongListItem, categoryListItem,
            depthListItem;

    private GoogleMap googleMap;

    private SharedViewModel sharedViewModel;
    private EarthquakeRSSAdapter rssAdapter;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance(String tag) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tag);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        mapTypeGroup = (RadioGroup) view.findViewById(R.id.mapTypeGroup);
        normalViewButton = (RadioButton) view.findViewById(R.id.normalViewRadio);
        terrainViewButton = (RadioButton) view.findViewById(R.id.terrainViewRadio);
        hybridViewButton = (RadioButton) view.findViewById(R.id.hybridViewRadio);
        satelliteViewButton = (RadioButton) view.findViewById(R.id.satelliteViewRadio);
        panZoom = (CheckBox) view.findViewById(R.id.panZoom);

        Log.e(getActivity().getPackageName(), "just before avw");
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.vwSwitch);
        if (viewSwitcher == null)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Null ViewSwicther", Toast.LENGTH_LONG);
            Log.e(getActivity().getPackageName(), "null pointer");
        }
        s1Button = (Button) view.findViewById(R.id.mapScreenButton);
        s2Button = (Button) view.findViewById(R.id.mapScreenOptionsButton);
        s1Button.setOnClickListener(this);
        s2Button.setOnClickListener(this);
        normalViewButton.setOnClickListener(this);
        terrainViewButton.setOnClickListener(this);
        hybridViewButton.setOnClickListener(this);
        satelliteViewButton.setOnClickListener(this);
        normalViewButton.toggle();
        panZoom.setOnClickListener(this);

        // initialising mapview for google maps
        googleMapView = (MapView) view.findViewById(R.id.googleMapView);
        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }

        googleMapView.getMapAsync(this);

        mapViewFlipper = (ViewFlipper) view.findViewById(R.id.mapViewFlipper);
        timeListItem = (TextView) view.findViewById(R.id.timeListItem);
        locationListItem = (TextView) view.findViewById(R.id.locationListItem);
        magListItem = (TextView) view.findViewById(R.id.magnitudeListItem);
        depthListItem  = (TextView) view.findViewById(R.id.depthListItem);
        latlongListItem = (TextView) view.findViewById(R.id.latLongListItem);
        categoryListItem = (TextView) view.findViewById(R.id.categoryListItem);

        imageButton = (ImageButton) view.findViewById(R.id.backButton);
        imageButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        //add marker depending on earthquake magnitude
        sharedViewModel.getEarthquakes().observe(this, earthquakeList -> {
            for (Earthquake earthquake : earthquakeList){
                double longitude = earthquake.getLongitude();
                double latitude = earthquake.getLatitude();
                String location = earthquake.getLocation();
                double magnitude = earthquake.getMagnitude();
                LatLng place = new LatLng(latitude,longitude);
                String lat_lng = "Lat: " + latitude + ", Long: " + longitude;
                if(magnitude > 0.0 && magnitude < 1.0) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(place)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title(location)
                            .snippet(lat_lng)
                    );
                    marker.setTag(earthquake);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                    googleMap.setOnInfoWindowClickListener(this);
                }else if(magnitude >= 1.0 && magnitude < 2.0){
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(place)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .title(location)
                            .snippet(lat_lng)
                    );
                    marker.setTag(earthquake);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                    googleMap.setOnInfoWindowClickListener(this);
                }else if(magnitude >= 2.0 && magnitude < 4.0){
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(place)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title(location)
                            .snippet(lat_lng)
                    );
                    marker.setTag(earthquake);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
//                    marker.showInfoWindow();
                    googleMap.setOnInfoWindowClickListener(this);
                }
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        googleMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        googleMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        googleMapView.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        if (v == s1Button)
        {
            viewSwitcher.showNext();
        }
        else
        if (v == s2Button)
        {
            viewSwitcher.showPrevious();
        }
        else
        if (v == normalViewButton)
        {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else
        if (v == terrainViewButton)
        {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else
        if (v == hybridViewButton)
        {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else
        if (v == satelliteViewButton)
        {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }else if(v == imageButton){
            mapViewFlipper.showPrevious();
            Log.e("MyTag", "View Flipped");
        }

        if (panZoom.isChecked())
        {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        else
        {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object markerTag = marker.getTag();
        System.out.println(markerTag);
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Object markerTag = marker.getTag();

        System.out.println(markerTag);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                sharedViewModel.getEarthquakes().observe(getActivity(), earthquakeList -> {
                    System.out.println(earthquakeList);
                    mapViewFlipper.showNext();
                    for (Earthquake earthquake : earthquakeList) {
                        if (earthquake.equals(markerTag)) {
                            Date pubDate = earthquake.getPubDate();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                            String date = simpleDateFormat.format(pubDate);
                            timeListItem.setText(date);
                            String category = earthquake.getCategory();
                            categoryListItem.setText(category);
                            String location = earthquake.getLocation();
                            locationListItem.setText(location);
                            double magnitude = earthquake.getMagnitude();
                            magListItem.setText(String.valueOf(magnitude));
                            double latitude = earthquake.getLatitude();
                            double longitude = earthquake.getLongitude();
                            String lat_long = latitude + "," + longitude;
                            latlongListItem.setText(lat_long);
                            int depth = earthquake.getDepth();
                            String depthString = depth + " km";
                            depthListItem.setText(depthString);
                        }
                    }
                });
            }
        });
    }
}