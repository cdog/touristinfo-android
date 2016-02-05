package com.touristinfo.touristinfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vladimir on 2/3/2016.
 */
public class InfoActivity extends Activity {
    private String logPrefix = "InfoActivity: ";
    private MySimpleArrayAdapter mySimpleArrayAdapter = null;
    public final ArrayList<LocationWeather> values = new ArrayList<>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Log.d(logPrefix, "The onCreate() event");

        Intent intent = getIntent();
        String locationName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_NAME);
        double coordinates[] = intent.getDoubleArrayExtra(MainActivity.EXTRA_MESSAGE_COORDINATES);
        String locationDescription = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_DESCRIPTION);

        values.clear();
        for (int i = 0; i < 7; i++)
            values.add(new LocationWeather());

        /* set location name */
        final TextView textViewName = (TextView) findViewById(R.id.locationName);
        textViewName.setText(locationName);

        /* set description */
        final TextView textViewDescription = (TextView) findViewById(R.id.locationDescription);
        textViewDescription.setText(locationDescription);

        /* set list */
        ArrayList<String> list = new ArrayList<>();
        for (LocationWeather lw : values)
            list.add("");

        final ListView listview = (ListView) findViewById(R.id.listView);
        mySimpleArrayAdapter = new MySimpleArrayAdapter(this, this, list);
        listview.setAdapter(mySimpleArrayAdapter);

        final String strURL = getResources().getString(R.string.server_name) + "/api/weather?lat=" + coordinates[0] + "&lon=" + coordinates[1];

        new LocationWeatherInfoGetter().execute(strURL);
    }

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(logPrefix, "The onStart() event");
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(logPrefix, "The onResume() event");
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(logPrefix, "The onPause() event");
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(logPrefix, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(logPrefix, "The onDestroy() event");
    }

    void getLocationWeatherInfo(String strURL) {
        JSONArray ja = HTTPHandler.makeRequest(strURL);
        if (ja == null) {
            /* TODO uncomment */
            //return;
            try {
                ja = new JSONArray("[{\"description\":\"sky is clear\",\"shortDescription\":\"Clear\",\"timeOfData\":1454662800,\"temperatureValues\":{\"day\":8.91,\"night\":8.91},\"icon\":\"http://openweathermap.org/img/w/01n.png\"},{\"description\":\"sky is clear\",\"shortDescription\":\"Clear\",\"timeOfData\":1454749200,\"temperatureValues\":{\"day\":22.32,\"night\":16.42},\"icon\":\"http://openweathermap.org/img/w/01d.png\"},{\"description\":\"sky is clear\",\"shortDescription\":\"Clear\",\"timeOfData\":1454835600,\"temperatureValues\":{\"day\":23.25,\"night\":8.29},\"icon\":\"http://openweathermap.org/img/w/01d.png\"},{\"description\":\"sky is clear\",\"shortDescription\":\"Clear\",\"timeOfData\":1454922000,\"temperatureValues\":{\"day\":19.9,\"night\":8.49},\"icon\":\"http://openweathermap.org/img/w/01d.png\"},{\"description\":\"sky is clear\",\"shortDescription\":\"Clear\",\"timeOfData\":1455008400,\"temperatureValues\":{\"day\":17.53,\"night\":7},\"icon\":\"http://openweathermap.org/img/w/01d.png\"},{\"description\":\"light rain\",\"shortDescription\":\"Rain\",\"timeOfData\":1455094800,\"temperatureValues\":{\"day\":22.76,\"night\":10.33},\"icon\":\"http://openweathermap.org/img/w/10d.png\"},{\"description\":\"sky is clear\",\"shortDescription\":\"Clear\",\"timeOfData\":1455181200,\"temperatureValues\":{\"day\":21.61,\"night\":6.73},\"icon\":\"http://openweathermap.org/img/w/01d.png\"}]");
            }
            catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        if (ja.length() == 7)
            try {
                for (int i = 0; i < 7; i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    values.get(i).date = jo.getString("timeOfData");
                    values.get(i).description = jo.getString("description");
                    values.get(i).dayTemperature = String.valueOf(jo.getJSONObject("temperatureValues").getDouble("day"));
                    values.get(i).nightTemperature = String.valueOf(jo.getJSONObject("temperatureValues").getDouble("night"));
                    values.get(i).iconURL = jo.getString("icon");

                    try {
                        URL newurl = new URL(jo.getString("icon"));
                        values.get(i).bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    }
                    catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private class LocationWeatherInfoGetter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            getLocationWeatherInfo(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mySimpleArrayAdapter != null)
                mySimpleArrayAdapter.notifyDataSetChanged();
        }
    }
}