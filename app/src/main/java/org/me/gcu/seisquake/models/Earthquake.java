/**
 *  NAME - IKEOLUWA AJIBOLA ODUKUDU
 *  MATRIC NO - S1702414
 **/

package org.me.gcu.seisquake.models;

import java.util.Date;

public class Earthquake {

    private String title, location, link, category;
    public Date pubDate;
    public int depth;
    public double latitude, longitude;
    public double magnitude;

    //constructors

    public Earthquake(String title, String location, String link, String category, int depth, Date pubDate,
                      double latitude, double longitude, double magnitude) {
        this.title = title;
        this.location = location;
        this.link = link;
        this.category = category;
        this.depth = depth;
        this.pubDate = pubDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.magnitude = magnitude;
    }

    public Earthquake() {}

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "title: " + title +
                ", location: " + location +
                ", link: " + link +
                ", category: " + category +
                ", depth: " + depth + " km" +
                ", pubDate: " + pubDate +
                ", latitude: " + latitude +
                ", longitude: " + longitude +
                ", magnitude: " + magnitude;
    }
}
