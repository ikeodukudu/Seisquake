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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.seisquake.R;
import org.me.gcu.seisquake.adapters.EarthquakeRSSAdapter;
import org.me.gcu.seisquake.models.Earthquake;
import org.me.gcu.seisquake.parser.EarthquakeParser;
import org.me.gcu.seisquake.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimelineFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    public static final String TAG = "TimelineFragment";

    private static final String ARG_PARAM1 = "TimelineFragment";
    private static final String ARG_PARAM2 = "param2";

    private int mainViewFlipper = R.id.mainViewFlipper;

    private ListView displayLocationAndStrength;
    private ViewFlipper viewFlipper;
    private ImageButton imageButton;
    private TextView timeListItem, locationListItem, magListItem, latlongListItem, categoryListItem, depthListItem;
    private MapView googleMapView;

    private GoogleMap googleMap;

    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private EarthquakeRSSAdapter rssAdapter;
    private List<Earthquake> earthquakes = null;
    private EarthquakeParser earthquakeParser = new EarthquakeParser();

    private SharedViewModel sharedViewModel;

    private boolean isItemListClicked;
    private String ITEM_LIST_CLICKED = "Item List Clicked";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tag Parameter 1.
//     * @param  Parameter 2.
     * @return A new instance of fragment TimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimelineFragment newInstance(String tag) {
        TimelineFragment fragment = new TimelineFragment();
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
        if(savedInstanceState != null){
            mainViewFlipper = savedInstanceState.getInt("ViewFlipperId", R.id.mainViewFlipper);
            isItemListClicked = savedInstanceState.getBoolean(ITEM_LIST_CLICKED, true);
            Log.e("MyTag", "In onCreate - Timeline Fragment");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        setRetainInstance(true);
        outState.putInt("ViewFlipperId", mainViewFlipper);
        outState.putBoolean(ITEM_LIST_CLICKED, isItemListClicked);
        Log.e("MyTag", "In onsaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        displayLocationAndStrength = (ListView) view.findViewById(R.id.earthquakeListView);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.d("UI thread", "I am the UI thread - Adapter at work");
                sharedViewModel.getEarthquakes().observe(requireActivity(), earthquakeList -> {
                    // update UI
                    rssAdapter = new EarthquakeRSSAdapter
                            (getActivity().getApplicationContext(), R.layout.location_strength_list, earthquakeList);
                    displayLocationAndStrength.setAdapter(rssAdapter);
                });
            }
        });

        Log.e("MyTag", "after startButton");

        displayLocationAndStrength.setOnItemClickListener(this);


        viewFlipper = (ViewFlipper) view.findViewById(mainViewFlipper);
        Log.e("MyTag", "initialised main view flipper");

        timeListItem = (TextView) view.findViewById(R.id.timeListItem);
        locationListItem = (TextView) view.findViewById(R.id.locationListItem);
        magListItem = (TextView) view.findViewById(R.id.magnitudeListItem);
        latlongListItem = (TextView) view.findViewById(R.id.latLongListItem);
        categoryListItem = (TextView) view.findViewById(R.id.categoryListItem);
        depthListItem = (TextView) view.findViewById(R.id.depthListItem);

        imageButton = (ImageButton) view.findViewById(R.id.backButton);
        imageButton.setOnClickListener(this);

        googleMapView = (MapView) view.findViewById(R.id.timelineListMapView);

        googleMapView.onCreate(savedInstanceState);
        googleMapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.backButton) {
            viewFlipper.showPrevious();
            Log.e("MyTag", "View Flipped");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        isItemListClicked = true;

        Object itemAtPosition = displayLocationAndStrength.getItemAtPosition(position);
        System.out.println(itemAtPosition.toString());

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                sharedViewModel.getEarthquakes().observe(getActivity(), earthquakeList -> {
                    System.out.println(earthquakeList);
                    viewFlipper.showNext();
                    for (Earthquake earthquake : earthquakeList) {
                        if (earthquake.equals(itemAtPosition)) {
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
                            googleMapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap mMap) {
                                    googleMap = mMap;
                                    googleMap.clear();
                                    LatLng place = new LatLng(latitude, longitude);
                                    googleMap.addMarker(new MarkerOptions().position(place).title(location));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                                }
                            });
                        }
                    }
                });
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
//        googleMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}