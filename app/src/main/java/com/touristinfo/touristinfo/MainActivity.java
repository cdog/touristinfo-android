package com.touristinfo.touristinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {
    public final static String EXTRA_MESSAGE_NAME = "com.touristinfo.touristinfo.MESSAGE";
    public final static String EXTRA_MESSAGE_COORDINATES = "com.touristinfo.touristinfo.MESSAGE2";

    private ArrayList<LocationInfo> locations = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private HashMap<Marker, Long> markers_click_times = new HashMap<>();
    private GoogleMap mMap;
    private Marker visibleMarker;
    private Marker candidateVisibleMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* configure searchview */
        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Enter location");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                searchView.setQuery("", false);
                Log.d("MainActivity: ", "loading locations " + query);
                new LocationGetter().execute(query);
                return false;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        refreshMarkers();
    }

    public boolean onMarkerClick(Marker marker) {
        long now = System.currentTimeMillis();
        long last_clicked = markers_click_times.get(marker);
        Log.d("MainActivity: ", "marker click " + marker.getTitle());

        if (now - last_clicked < 1000) {
            /* Double click */
            Log.d("MainActivity: ", "double click");
            double coordinates[] = new double[]{marker.getPosition().latitude, marker.getPosition().longitude};
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra(EXTRA_MESSAGE_NAME, marker.getTitle());
            intent.putExtra(EXTRA_MESSAGE_COORDINATES, coordinates);
            startActivity(intent);
        }
        else {
            /* Single click */
            marker.showInfoWindow();
            /* this shit doesn't work as it should
            candidateVisibleMarker = marker;
            Handler handler = new Handler();
            handler.post(new Runnable() {
                public void run() {
                    if (!candidateVisibleMarker.isInfoWindowShown()) {
                        Log.d("MainActivity: ", "info shown");
                        candidateVisibleMarker.hideInfoWindow();
                        visibleMarker = null;
                    } else {
                        Log.d("MainActivity: ", "info not shown");
                        if (visibleMarker != null)
                            visibleMarker.hideInfoWindow();
                        visibleMarker = candidateVisibleMarker;
                        candidateVisibleMarker.showInfoWindow();
                    }
                }
            });
            */
        }

        markers_click_times.put(marker, now);
        return false;
    }

    void loadLocations(String regex) {
        locations.clear();
        final String strURL = getResources().getString(R.string.server_name) + "/ajax/places";
        JSONArray ja = HTTPHandler.makeRequest(strURL);
        if (ja == null) {
            /* TODO rework */
            //return;
            try {
                ja = new JSONArray("[{\"id\":1,\"name\":\"Cabana Colț\",\"latitude\":45.09679,\"longitude\":22.741699,\"description\":null},{\"id\":2,\"name\":\"Cabana Alpin\",\"latitude\":45.236217,\"longitude\":23.378906,\"description\":null},{\"id\":3,\"name\":\"Cabana Ghețar\",\"latitude\":45.274887,\"longitude\":23.972168,\"description\":null},{\"id\":4,\"name\":\"Cabana Brâna\",\"latitude\":45.305801,\"longitude\":24.960938,\"description\":null},{\"id\":5,\"name\":\"Camping Fereastra\",\"latitude\":45.305801,\"longitude\":25.64209,\"description\":null},{\"id\":6,\"name\":\"Refugiu Canion\",\"latitude\":45.690834,\"longitude\":26.488037,\"description\":null},{\"id\":7,\"name\":\"Cabana Creasta\",\"latitude\":46.027481,\"longitude\":26.345215,\"description\":null},{\"id\":8,\"name\":\"Camping Padina\",\"latitude\":46.521076,\"longitude\":26.433105,\"description\":null},{\"id\":9,\"name\":\"Camping Curmătura\",\"latitude\":47.15984,\"longitude\":26.147461,\"description\":null},{\"id\":10,\"name\":\"Camping Cascada\",\"latitude\":47.331375,\"longitude\":25.817871,\"description\":null},{\"id\":11,\"name\":\"Cabana Dolina\",\"latitude\":46.995239,\"longitude\":25.653076,\"description\":null},{\"id\":12,\"name\":\"Cabana Peștera\",\"latitude\":46.91275,\"longitude\":26.38916,\"description\":null},{\"id\":13,\"name\":\"Camping Cumpăna\",\"latitude\":46.822617,\"longitude\":25.949707,\"description\":null},{\"id\":14,\"name\":\"Refugiu Coama\",\"latitude\":46.324173,\"longitude\":25.993652,\"description\":null},{\"id\":15,\"name\":\"Camping Dorna\",\"latitude\":45.836452,\"longitude\":26.136475,\"description\":null},{\"id\":16,\"name\":\"Camping Cheia\",\"latitude\":45.560219,\"longitude\":25.894775,\"description\":null},{\"id\":17,\"name\":\"Cabana Izvor\",\"latitude\":45.383018,\"longitude\":25.257568,\"description\":null},{\"id\":18,\"name\":\"Cabana Măgura\",\"latitude\":45.614037,\"longitude\":24.0271,\"description\":null},{\"id\":19,\"name\":\"Refugiu Escalada\",\"latitude\":45.47554,\"longitude\":23.686523,\"description\":null},{\"id\":20,\"name\":\"Camping Arcada\",\"latitude\":45.521744,\"longitude\":23.049316,\"description\":null}]");
            }
            catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

        int length = ja.length();

        try {
            for (int i = 0; i < length; i++) {
                JSONObject jo = ja.getJSONObject(i);
                String name = jo.getString("name");
                System.out.println("Comp: |" + name + "| vs |" + regex + "|");
                // TODO enable filter
                if (regex.compareTo("*") != 0 && !name.contains(regex))
                    continue;
                JSONArray location = jo.getJSONArray("location");
                double latitude = location.getDouble(1);
                double longitude = location.getDouble(0);
                //String description = jo.getString("description");
                String description = jo.getString("__v");
                System.out.println(name + " " + description + " " + latitude + " " + longitude);
                locations.add(new LocationInfo(name, description, latitude, longitude));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void refreshMarkers() {
        /* remove old markers */
        for (Marker m : markers)
            m.remove();

        markers.clear();
        markers_click_times.clear();

        System.out.println("locations size = " + locations.size());
        /* add new markers */
        for (LocationInfo li : locations) {
            LatLng sydney = new LatLng(li.latitude, li.longitude);
            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(li.name));
            markers.add(marker);
            markers_click_times.put(marker, 0L);
        }

        /* center map */
        if (!markers.isEmpty()) {
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (Marker marker : markers)
                boundsBuilder.include(marker.getPosition());
            LatLngBounds bounds = boundsBuilder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mMap.moveCamera(cameraUpdate);
        }
    }

    private class LocationGetter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            loadLocations(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            refreshMarkers();
        }
    }
}
