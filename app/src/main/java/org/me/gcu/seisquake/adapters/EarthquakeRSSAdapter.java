/**
 *  NAME - IKEOLUWA AJIBOLA ODUKUDU
 *  MATRIC NO - S1702414
 **/
package org.me.gcu.seisquake.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.me.gcu.seisquake.R;
import org.me.gcu.seisquake.models.Earthquake;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarthquakeRSSAdapter extends ArrayAdapter<Earthquake> {
    private Context context;
    int layoutResourceId;
    private List<Earthquake> data = null;
    private ArrayList<Earthquake> earthquakeArrayList;

    public EarthquakeRSSAdapter(Context context, int layoutResourceId, List<Earthquake> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        LocationAndMagHolder holder = null;

        if (row == null) {
            //reusing the list item position
            row = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);

            holder = new LocationAndMagHolder();
            holder.location = (TextView) row.findViewById(R.id.locationLabel);
            holder.strength = (TextView) row.findViewById(R.id.magnitudeLabel);

            row.setTag(holder);
        } else {
            holder = (LocationAndMagHolder) row.getTag();
        }

        Earthquake item = data.get(position);
        holder.location.setText(item.getLocation());

        double magnitude = item.getMagnitude();
        if(magnitude > 0.0 && magnitude < 1.0) {
            holder.strength.setText("Very weak");
            holder.strength.setBackgroundColor(row.getResources().getColor(R.color.Green));
            holder.strength.setTextColor(row.getResources().getColor(R.color.white));
        } else if(magnitude >= 1.0 && magnitude < 2.0){
            holder.strength.setText("Weak");
            holder.strength.setBackgroundColor(row.getResources().getColor(R.color.SpringGreen));
            holder.strength.setTextColor(row.getResources().getColor(R.color.black));
        } else if(magnitude >= 2.0 && magnitude < 4.0){
            holder.strength.setText("Medium");
            holder.strength.setBackgroundColor(row.getResources().getColor(R.color.Orange));
            holder.strength.setTextColor(row.getResources().getColor(R.color.black));
        } else if(magnitude >= 4.0 && magnitude < 9.0){
            holder.strength.setText("Strong");
            holder.strength.setBackgroundColor(row.getResources().getColor(R.color.Tomato));
            holder.strength.setTextColor(row.getResources().getColor(R.color.white));
        }else if(magnitude >= 9.0 && magnitude <= 10.0){
            holder.strength.setText("Very Strong");
            holder.strength.setBackgroundColor(row.getResources().getColor(R.color.Red));
            holder.strength.setTextColor(row.getResources().getColor(R.color.white));
        }
        return row;
    }

    // Filter Class
    public List<Earthquake> filter(String charText, TextView textView) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.equals("january")){
            charText = "jan";
        }else if (charText.equals("february")){
            charText = "feb";
        }else if (charText.equals("march")){
            charText = "mar";
        }else if (charText.equals("april")){
            charText = "apr";
        }else if (charText.equals("may")){
            charText = "may";
        }else if (charText.equals("june")){
            charText = "jun";
        }else if (charText.equals("july")){
            charText = "jul";
        }else if (charText.equals("august")){
            charText = "aug";
        }else if (charText.equals("september")){
            charText = "sep";
        }else if (charText.equals("october")){
            charText = "oct";
        }else if (charText.equals("november")){
            charText = "nov";
        }else if (charText.equals("december")){
            charText = "dec";
        }
        earthquakeArrayList = new ArrayList<>();
        String text = "Please enter a month in the last 50 days";
        if (charText.length() == 0) {
            earthquakeArrayList.addAll(data);

        } else {
            for (Earthquake earthquake : data) {
                if (earthquake.getPubDate().toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    textView.setText("");
                    earthquakeArrayList.add(earthquake);}
//                }else{
//                    textView.setText(text);
//                }
            }
        }
        notifyDataSetChanged();

        return earthquakeArrayList;
    }

    public List<Earthquake> filterDateRange(Long startDateMs, Long endDateMs){
        earthquakeArrayList = new ArrayList<>();
        Date startDate = new Date(startDateMs);
        Date endDate = new Date(endDateMs);
        for (Earthquake earthquake : data){
            if(isWithinRange(earthquake.getPubDate(), startDate, endDate)){
                earthquakeArrayList.add(earthquake);
            }
        }
        return earthquakeArrayList;
    }

    boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }


    static class LocationAndMagHolder {
        TextView location;
        TextView strength;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}