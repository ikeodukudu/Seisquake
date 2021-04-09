/**
 *  NAME - IKEOLUWA AJIBOLA ODUKUDU
 *  MATRIC NO - S1702414
 **/

package org.me.gcu.seisquake.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.me.gcu.seisquake.R;
import org.me.gcu.seisquake.adapters.EarthquakeRSSAdapter;
import org.me.gcu.seisquake.adapters.SearchAdapter;
import org.me.gcu.seisquake.models.Earthquake;
import org.me.gcu.seisquake.viewmodel.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchMenuFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, View.OnClickListener, OnMapReadyCallback {//, SearchView.OnCloseListener {

    private static final String ARG_PARAM1 = "SearchFragment";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView displaySearchResults;
    private SearchView searchView;
    private SharedViewModel sharedViewModel;
    private ViewFlipper searchViewFlipper, resultViewFlipper;
    private ImageButton imageButton;
    private Button datePickerButton, maxDepthButton, minDepthButton, maxMagButton, clearButton;
    private TextView timeListItem, locationListItem, magListItem, latlongListItem, categoryListItem,
            depthListItem, maxDepthValue, minDepthValue, maxMagValue, noSearchString;

    private List<Earthquake> searchEarthquakeList;
    private EarthquakeRSSAdapter rssAdapter;
    private SearchAdapter searchAdapter;
    private MapView googleMapView;

    private GoogleMap googleMap;

    List<Earthquake> dateRangeEarthquakes;

    public SearchMenuFragment() {
        // Required empty public constructor
    }

    public static SearchMenuFragment newInstance(String tag) {
        SearchMenuFragment fragment = new SearchMenuFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_menu, container, false);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        displaySearchResults = (ListView) view.findViewById(R.id.searchEarthquakeListView);

        searchView = (SearchView) view.findViewById(R.id.mainSearchView);
        searchView.setOnQueryTextListener(this);

        noSearchString = (TextView) view.findViewById(R.id.noSearchString);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                System.out.println("does onClose work");
                getActivity().runOnUiThread(mRunnable);
                Log.e("MyTag", "Close search");
                return false;
            }
        });

        displaySearchResults.setOnItemClickListener(this);

        searchViewFlipper = (ViewFlipper) view.findViewById(R.id.searchViewFlipper);
        Log.e("MyTag", "initialised search view flipper");

        timeListItem = (TextView) view.findViewById(R.id.timeListItem);
        locationListItem = (TextView) view.findViewById(R.id.locationListItem);
        magListItem = (TextView) view.findViewById(R.id.magnitudeListItem);
        depthListItem  = (TextView) view.findViewById(R.id.depthListItem);
        latlongListItem = (TextView) view.findViewById(R.id.latLongListItem);
        categoryListItem = (TextView) view.findViewById(R.id.categoryListItem);

        imageButton = (ImageButton) view.findViewById(R.id.backButton);
        imageButton.setOnClickListener(this);

        datePickerButton = (Button) view.findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(this);

        resultViewFlipper = (ViewFlipper) view.findViewById(R.id.resultViewFlipper);

        maxDepthValue = (TextView) view.findViewById(R.id.maxDepthValue);
        maxDepthButton = (Button) view.findViewById(R.id.maxDepthButton);
        maxDepthButton.setOnClickListener(this);

        minDepthValue = (TextView) view.findViewById(R.id.minDepthValue);
        minDepthButton = (Button) view.findViewById(R.id.minDepthButton);
        minDepthButton.setOnClickListener(this);

        maxMagValue = (TextView) view.findViewById(R.id.maxMagValue);
        maxMagButton = (Button) view.findViewById(R.id.maxMagButton);
        maxMagButton.setOnClickListener(this);

        clearButton = (Button) view.findViewById(R.id.clearFilterButton);
        clearButton.setOnClickListener(this);

        googleMapView = (MapView) view.findViewById(R.id.searchListMapView);

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
    public boolean onQueryTextSubmit(String query) {

        System.out.println("Query is: " + query);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                sharedViewModel.getEarthquakes().observe(getActivity(), earthquakeList -> {
                    rssAdapter = new EarthquakeRSSAdapter
                            (getActivity().getBaseContext(), R.layout.location_strength_list, earthquakeList);

                    searchEarthquakeList = rssAdapter.filter(query,noSearchString);
                    searchAdapter = new SearchAdapter
                            (getActivity().getBaseContext(), R.layout.search_date_location_list, searchEarthquakeList);
                    displaySearchResults.setAdapter(searchAdapter);
                    resultViewFlipper.showPrevious();
                });
            }
        });
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    Runnable mRunnable = new Runnable() {
        public void run() {
            try {
                searchEarthquakeList.clear();
            }catch(NullPointerException e){
                Log.e("MyTag", "Search is empty");
            }
            finally {
                searchAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object itemAtPosition = displaySearchResults.getItemAtPosition(position);
        System.out.println(itemAtPosition.toString());
        sharedViewModel.getEarthquakes().observe(this, earthquakeList -> {
            System.out.println(earthquakeList);
            searchViewFlipper.showNext();
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
                    googleMapView.getMapAsync(this);
                    callMap(longitude,latitude,location);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                searchViewFlipper.showPrevious();
                Log.e("MyTag", "View Flipped");
                break;
            case R.id.clearFilterButton:
                resultViewFlipper.showPrevious();
                break;
            case R.id.datePickerButton:
                MaterialDatePicker.Builder<Pair<Long, Long>> pairBuilder = MaterialDatePicker.Builder.dateRangePicker();
                MaterialDatePicker<Pair<Long, Long>> picker = pairBuilder.build();
                picker.show(getActivity().getSupportFragmentManager(), picker.toString());
                picker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.e("DatePicker Activity", "Dialog was cancelled");
                    }
                });
                picker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("DatePicker Activity", "Dialog Negative Button was clicked");
                    }
                });

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Log.d("DatePicker Activity", "Date String = ${picker.headerText}::  Date epoch values::${selection.first}:: to :: ${selection.second}");
                        String test = picker.getHeaderText();
                        Long first = selection.first;
                        Long second = selection.second;
                        Log.e("MyTag - Get Header Text", test);
                        String message = "Date String = " + test + " :: Date epoch values " + first + " to " + second;
                        Log.e("MyTag", message);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                sharedViewModel.getEarthquakes().observe(getActivity(), earthquakeList -> {
                                    rssAdapter = new EarthquakeRSSAdapter
                                            (getActivity().getBaseContext(), R.layout.location_strength_list, earthquakeList);
                                    dateRangeEarthquakes = rssAdapter.filterDateRange(first, second);
                                    System.out.println(dateRangeEarthquakes);
                                    getMaxDepthValue(dateRangeEarthquakes);
                                    getMinDepthValue(dateRangeEarthquakes);
                                    getMaxMagnitudeValue(dateRangeEarthquakes);
                                    resultViewFlipper.showNext();
                                });
                            }
                        });
                    }
                });

                break;
            case R.id.maxMagButton:
                searchViewFlipper.showNext();
                Log.e("MyTag", "viewFlipper to show results");
                Log.e("MyTag", dateRangeEarthquakes.toString());
                List<Earthquake> maxMagEarthquake = getMaxMagnitudeValue(dateRangeEarthquakes);
                for (Earthquake earthquake : maxMagEarthquake){
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
                    googleMapView.getMapAsync(this);
                    callMap(longitude,latitude,location);
                }
                Log.e("MyTag", "magnitude button executed");
                break;

            case R.id.maxDepthButton:
                searchViewFlipper.showNext();
                List<Earthquake> maxDepthEarthquake = getMaxDepthValue(dateRangeEarthquakes);
                for (Earthquake earthquake : maxDepthEarthquake){
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
                    googleMapView.getMapAsync(this);
                    callMap(longitude,latitude,location);
                }
                Log.e("MyTag", "maximum depth button executed");
                break;

            case R.id.minDepthButton:
                searchViewFlipper.showNext();
                List<Earthquake> minDepthEarthquake = getMinDepthValue(dateRangeEarthquakes);
                for (Earthquake earthquake : minDepthEarthquake){
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
                    googleMapView.getMapAsync(this);
                    callMap(longitude,latitude,location);
                }
                Log.e("MyTag", "minimum depth button executed");
                break;

        }
    }

    public List<Earthquake> getMaxDepthValue(List<Earthquake> list){
        List<Integer> integerList = new ArrayList<>();
        List<Earthquake> newList = new ArrayList<>();
        int max = Integer.MIN_VALUE;
        String maxValue = "";
        String title ="";
        String link = "";
        String location = "";
        String category ="";
        Date pubDate = new Date();
        double magnitude = 0.0;
        double latitude = 0.0;
        double longitude = 0.0;
        int depth = 0;

        for (Earthquake earthquake : list) {
            Integer getDepth = (Integer) earthquake.getDepth();
            integerList.add(getDepth);

            for (int i = 0; i < integerList.size(); i++) {
                if (integerList.get(i) > max) {
                    max = integerList.get(i);
                    title = earthquake.getTitle();
                    depth = earthquake.getDepth();
                    link = earthquake.getLink();
                    location = earthquake.getLocation();
                    pubDate = earthquake.getPubDate();
                    category = earthquake.getCategory();
                    magnitude = earthquake.getMagnitude();
                    latitude = earthquake.getLatitude();
                    longitude = earthquake.getLongitude();
                }
            }

        }
        Earthquake max_earthquake = new Earthquake(title, location, link, category,
                depth, pubDate, latitude, longitude, magnitude);

        System.out.println("Location of Max Depth is " + location);

        maxValue = max + " km";
        System.out.println("Max Depth: " + maxValue);

        maxDepthValue.setText(maxValue);

        newList.add(max_earthquake);

        System.out.println("Size of list - " + newList.size());

        return newList;
    }

    public List<Earthquake> getMinDepthValue(List<Earthquake> list){
        List<Integer> integerList = new ArrayList<>();
        List<Earthquake> newList = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        String minValue = "";
        String title ="";
        String link = "";
        String location = "";
        String category ="";
        Date pubDate = new Date();
        double magnitude = 0.0;
        double latitude = 0.0;
        double longitude = 0.0;
        int depth = 0;

        for (Earthquake earthquake : list) {
            Integer getDepth = (Integer) earthquake.getDepth();
            integerList.add(getDepth);

            for (int i = 0; i < integerList.size(); i++) {
                if (integerList.get(i) < min) {
                    min = integerList.get(i);
                    title = earthquake.getTitle();
                    depth = earthquake.getDepth();
                    link = earthquake.getLink();
                    location = earthquake.getLocation();
                    pubDate = earthquake.getPubDate();
                    category = earthquake.getCategory();
                    magnitude = earthquake.getMagnitude();
                    latitude = earthquake.getLatitude();
                    longitude = earthquake.getLongitude();
                }
            }
        }
        Earthquake max_earthquake = new Earthquake(title, location, link, category,
                depth, pubDate, latitude, longitude, magnitude);

        System.out.println("Location of Min Depth is " + location);

        minValue = min + " km";
        System.out.println("Min Depth: " + minValue);

        minDepthValue.setText(minValue);

        newList.add(max_earthquake);

        System.out.println("Size of list - " + newList.size());

        return newList;
    }

    public List<Earthquake> getMaxMagnitudeValue(List<Earthquake> list){
        List<Double> doubleList = new ArrayList<>();
        List<Earthquake> newList = new ArrayList<>();

        double max = Double.MIN_VALUE;
        String maxValue = "";
        String title ="";
        String link = "";
        String location = "";
        String category ="";
        Date pubDate = new Date();
        double magnitude = 0.0;
        double latitude = 0.0;
        double longitude = 0.0;
        int depth = 0;

        for (Earthquake earthquake : list) {
            Double getMagnitude = (Double) earthquake.getMagnitude();
            doubleList.add(getMagnitude);

            for (int i = 0; i < doubleList.size(); i++) {
                if (doubleList.get(i) > max) {
                    max = doubleList.get(i);
                    title = earthquake.getTitle();
                    depth = earthquake.getDepth();
                    link = earthquake.getLink();
                    location = earthquake.getLocation();
                    pubDate = earthquake.getPubDate();
                    category = earthquake.getCategory();
                    magnitude = earthquake.getMagnitude();
                    latitude = earthquake.getLatitude();
                    longitude = earthquake.getLongitude();
                }
            }
        }

        Earthquake max_earthquake = new Earthquake(title, location, link, category,
                depth, pubDate, latitude, longitude, magnitude);

        System.out.println("Location of Largest Magnitude is " + location);

        maxValue = String.valueOf(max);
        System.out.println("Largest Magnitude: " + maxValue);

        maxMagValue.setText(maxValue);

        newList.add(max_earthquake);

        System.out.println("Size of list - " + newList.size());

        return newList;
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
        googleMapView.onDestroy();
    }

    public void callMap(double longitude, double latitude, String location){
        googleMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                LatLng place = new LatLng(latitude,longitude);
                googleMap.addMarker(new MarkerOptions().position(place).title(location));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.clear();
    }
}