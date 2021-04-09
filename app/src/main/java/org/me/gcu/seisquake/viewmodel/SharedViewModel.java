/**
 *  NAME - IKEOLUWA AJIBOLA ODUKUDU
 *  MATRIC NO - S1702414
 **/
package org.me.gcu.seisquake.viewmodel;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.seisquake.models.Earthquake;
import org.me.gcu.seisquake.parser.EarthquakeParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    // view model to share data

    private MutableLiveData<List<Earthquake>> earthquakes;
    private EarthquakeParser earthquakeParser = new EarthquakeParser();
    private List<Earthquake> earthquakeList = new ArrayList<>();
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    private String TAG = SharedViewModel.class.getSimpleName();

    public LiveData<List<Earthquake>> getEarthquakes(){
        if (earthquakes == null) {
            earthquakes = new MutableLiveData<List<Earthquake>>();
            loadEarthquakes();
        }
        return earthquakes;
    }

    private void loadEarthquakes(){
        new GetRSSData(urlSource).execute();
    }

    private class GetRSSData extends AsyncTask<Void, Void, Void> {

        private String url;

        URL aurl;
        URLConnection yc;
        InputStream inputStream;
        BufferedReader in = null;
        String inputLine = "";

        public GetRSSData(String aurl) {
            url = aurl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.e("MyTag", "doInBackground start");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                inputStream = yc.getInputStream();

                Log.e("MyTag", "after ready");

                earthquakeList = earthquakeParser.parse(inputStream);
                System.out.println("Number of Earthquakes: "+ earthquakeList.size());
                inputStream.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }
            Log.e("MyTag", "doInBackground completed");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.e("MyTag", "onPostExecute start");

            earthquakes.setValue(earthquakeList);

            Log.e("MyTag", "onPostExecute completed");
            super.onPostExecute(result);
        }
    }
}