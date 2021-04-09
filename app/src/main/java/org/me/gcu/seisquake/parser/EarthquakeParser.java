/**
 *  NAME - IKEOLUWA AJIBOLA ODUKUDU
 *  MATRIC NO - S1702414
 **/
package org.me.gcu.seisquake.parser;

import android.util.Log;

import org.me.gcu.seisquake.models.Earthquake;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EarthquakeParser {

    private List<Earthquake> earthquakeList = new ArrayList<>();
    private Earthquake earthquake;
    private String text;

    public List<Earthquake> retrieveEarthquakeList() {
        return earthquakeList;
    }

    // parse rss feed into a list
    public List<Earthquake> parse(InputStream in) {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(in, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            earthquake = new Earthquake();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // add employee object to list
                            earthquakeList.add(earthquake);
                        } else if (tagname.equalsIgnoreCase("title") && earthquake != null) {
                            earthquake.setTitle(text);
                            Log.e("MyTag - Title", earthquake.getTitle());
                        } else if (tagname.equalsIgnoreCase("description") && earthquake != null) {
                            Pattern pattern = Pattern.compile("\\s*:\\s*([^\\;]+).*\\s*:\\s*([^\\;]+).*\\s*:\\s*([^\\;]+).*\\s*:\\s*([^\\;]+).*\\s*:\\s*([^\\;]+)");
                            Matcher matcher = pattern.matcher(text);
                            if(matcher.find()){
                                //System.out.println(pattern);
                                String datetime = matcher.group(1);
                                String location = matcher.group(2);
                                String latlong = matcher.group(3);
                                String depth = matcher.group(4);
                                String depthSubString = depth.substring(0,1);
                                int depthInt = Integer.parseInt(depthSubString);
                                String mag = matcher.group(5);
                                double magnitude = Double.parseDouble(mag);
                                earthquake.setLocation(location);
                                earthquake.setMagnitude(magnitude);
                                earthquake.setDepth(depthInt);
                                Log.e("MyTag - Magnitude", String.valueOf(earthquake.getMagnitude()));
                                Log.e("MyTag - Depth", earthquake.getDepth() + " km");
                            }
                            Log.e("MyTag - Location", earthquake.getLocation());
                        } else if (tagname.equalsIgnoreCase("link") && earthquake != null) {
                            earthquake.setLink(text);
                            Log.e("MyTag - Link", earthquake.getLink());
                        } else if (tagname.equalsIgnoreCase("category") && earthquake != null) {
                            earthquake.setCategory(text);
                            Log.e("MyTag - Category", earthquake.getCategory());
                        } else if (tagname.equalsIgnoreCase("pubDate") && earthquake != null) {
                            DateFormat dateFormat;
                            dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
                            Date parseDate = dateFormat.parse(text);
                            earthquake.setPubDate(parseDate);
                            Log.e("MyTag - Date", earthquake.getPubDate().toString());
                        } else if (tagname.equalsIgnoreCase("lat") && earthquake != null) {
                            double lat = Double.parseDouble(text);
                            earthquake.setLatitude(lat);
                            Log.e("MyTag - Latitude", String.valueOf(earthquake.getLatitude()));
                        } else if (tagname.equalsIgnoreCase("long") && earthquake != null) {
                            double longitude = Double.parseDouble(text);
                            earthquake.setLongitude(longitude);
                            Log.e("MyTag - Longitude", String.valueOf(earthquake.getLongitude()));
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException | ParseException e) {
            e.printStackTrace();
        }
        return earthquakeList;
    }
}
