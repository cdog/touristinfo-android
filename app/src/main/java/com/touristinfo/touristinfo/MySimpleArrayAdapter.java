package com.touristinfo.touristinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vladimir on 2/3/2016.
 */
public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> values;
    private InfoActivity infoActivity;

    public MySimpleArrayAdapter(InfoActivity infoActivity, Context context, ArrayList<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.infoActivity = infoActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < 0 || position >= 7)
            return convertView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = (convertView != null ? convertView : inflater.inflate(R.layout.list_entry, parent, false));

        if (!infoActivity.values.get(position).date.equals("Date: N/A")) {
            Date date = new Date(((long) Integer.parseInt(infoActivity.values.get(position).date) * 1000));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateWithoutTime = null;
            try {
                dateWithoutTime = sdf.parse(sdf.format(date));
                ((TextView) rowView.findViewById(R.id.Date)).setText(sdf.format(date).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ((TextView) rowView.findViewById(R.id.Description)).setText(infoActivity.values.get(position).description);
        ((TextView) rowView.findViewById(R.id.Day)).setText("Day: " + infoActivity.values.get(position).dayTemperature);
        ((TextView) rowView.findViewById(R.id.Night)).setText("Night: " + infoActivity.values.get(position).nightTemperature);
        if (infoActivity.values.get(position).bitmap != null)
            ((ImageView) rowView.findViewById(R.id.icon)).setImageBitmap(infoActivity.values.get(position).bitmap);
        else
            ((ImageView) rowView.findViewById(R.id.icon)).setImageResource(R.mipmap.ic_launcher);

        return rowView;
    }
}
