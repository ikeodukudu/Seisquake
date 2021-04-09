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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchAdapter extends ArrayAdapter<Earthquake> {

    private Context context;
    int layoutResourceId;
    private List<Earthquake> data = null;

    public SearchAdapter(Context context, int layoutResourceId, List<Earthquake> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DateAndLocationHolder holder = null;

        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);

            holder = new DateAndLocationHolder();
            holder.location = (TextView) row.findViewById(R.id.searchLocationLabel);
            holder.date = (TextView) row.findViewById(R.id.searchDateLabel);

            row.setTag(holder);
        } else {
            holder = (DateAndLocationHolder) row.getTag();
        }

        Earthquake item = data.get(position);
        holder.location.setText(item.getLocation());
        Date pubDate = item.getPubDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(pubDate);
        holder.date.setText(date);
        return row;
    }

    static class DateAndLocationHolder {
        TextView location;
        TextView date;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
