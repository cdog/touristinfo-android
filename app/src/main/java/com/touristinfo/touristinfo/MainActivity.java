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
    public final static String EXTRA_MESSAGE_DESCRIPTION = "com.touristinfo.touristinfo.MESSAGE3";
    public final static String EXTRA_MESSAGE_COORDINATES = "com.touristinfo.touristinfo.MESSAGE2";

    private ArrayList<LocationInfo> locations = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private HashMap<Marker, Long> markers_click_times = new HashMap<>();
    private HashMap<String, String> nameToDescriptionMap = new HashMap<>();
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
            intent.putExtra(EXTRA_MESSAGE_DESCRIPTION, nameToDescriptionMap.get(marker.getTitle()));
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
                ja = new JSONArray("[{\"_id\":\"56b35fafaa1e8223485951e2\",\"name\":\"Cabana Colț\",\"location\":[22.741699,45.09679],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e3\",\"name\":\"Cabana Alpin\",\"location\":[23.378906,45.236217],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e4\",\"name\":\"Cabana Ghețar\",\"location\":[23.972168,45.274887],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e5\",\"name\":\"Cabana Brâna\",\"location\":[24.960938,45.305801],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e6\",\"name\":\"Camping Fereastra\",\"location\":[25.64209,45.305801],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e7\",\"name\":\"Refugiu Canion\",\"location\":[26.488037,45.690834],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e8\",\"name\":\"Cabana Creasta\",\"location\":[26.345215,46.027481],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951e9\",\"name\":\"Camping Padina\",\"location\":[26.433105,46.521076],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951ea\",\"name\":\"Camping Curmătura\",\"location\":[26.147461,47.15984],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951eb\",\"name\":\"Camping Cascada\",\"location\":[25.817871,47.331375],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951ec\",\"name\":\"Cabana Dolina\",\"location\":[25.653076,46.995239],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951ed\",\"name\":\"Cabana Peștera\",\"location\":[26.38916,46.91275],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951ee\",\"name\":\"Camping Cumpăna\",\"location\":[25.949707,46.822617],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951ef\",\"name\":\"Refugiu Coama\",\"location\":[25.993652,46.324173],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951f0\",\"name\":\"Camping Dorna\",\"location\":[26.136475,45.836452],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951f1\",\"name\":\"Camping Cheia\",\"location\":[25.894775,45.560219],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951f2\",\"name\":\"Cabana Izvor\",\"location\":[25.257568,45.383018],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951f3\",\"name\":\"Cabana Măgura\",\"location\":[24.0271,45.614037],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951f4\",\"name\":\"Refugiu Escalada\",\"location\":[23.686523,45.47554],\"__v\":0},{\"_id\":\"56b35fafaa1e8223485951f5\",\"name\":\"Camping Arcada\",\"location\":[23.049316,45.521744],\"__v\":0},{\"_id\":\"56b3b9dedc681811001ba548\",\"description\":\"Cabana Clujului ofera conditii de lux la pret redus.\",\"name\":\"Cabana Clujului\",\"location\":[23.642578125,46.769968433569815],\"__v\":0},{\"_id\":\"56b4a45d91857d11006d7b8e\",\"description\":\"Personal use\",\"name\":\"Bucharest\",\"location\":[26.103515625,44.402391829093915],\"__v\":0}]");
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
                String description = "N/A";
                try {
                    description = jo.getString("description");
                }
                catch (JSONException e) {
                }

                locations.add(new LocationInfo(name, description, latitude, longitude));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void refreshMarkers() {
        /* remove old markers */
        for (Marker marker : markers) {
            nameToDescriptionMap.remove(marker.getTitle());
            marker.remove();
        }
        markers.clear();
        markers_click_times.clear();

        /* add new markers */
        for (LocationInfo li : locations) {
            LatLng sydney = new LatLng(li.latitude, li.longitude);
            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(li.name));
            markers.add(marker);
            markers_click_times.put(marker, 0L);
            nameToDescriptionMap.put(li.name, li.description);
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
