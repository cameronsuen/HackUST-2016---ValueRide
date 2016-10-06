package com.hackust.taxi.taxihitchhike;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import java.io.Console;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        AdapterView.OnItemClickListener,
        NoPlan.OnFragmentInteractionListener,
        Plan.onPlanSelectionListener,
        TimePickerFragment.TimePickerFragmentListener,
        QRCodeFragment.WebViewListener {

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private Location mLocation;
    private Marker mMarker;
    private Marker destMarker;
    private Marker guestMarker;

    private LatLng startCoord;
    private LatLng destCoord;

    private TimePicker picker;
    private AutoCompleteAdapter srcAdapter;
    private AutoCompleteAdapter destAdapter;

    private Polyline polyline;
    private String userid;

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    //private static final String API_KEY = "AIzaSyD5Sr_EOSgYJP9gUTxMwzO-RGJlX8iVaZc";
    private static final String API_KEY = "AIzaSyC06pGRJgqvndpib35XbOMXnKFv0PV6xs0";
    private ArrayList<LocationMetaData> resultList;

    private NoPlan frag;

    private String time;

    private ArrayList<RouteInfo> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        userid = getSharedPreferences("username", 0).getString("username", "null");


        buildGoogleApiClient();

        if (mClient != null) {
            Log.d("Connect", "Connect");
            mClient.connect();
        } else {
            buildAlertMessageNoGps();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*frag = (NoPlan) getSupportFragmentManager().findFragmentById(R.id.plan_column);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        ft.hide(frag);
        ft.commit();*/


        //picker = (TimePicker) findViewById(R.id.timePicker);

        /*picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Calendar cal = Calendar.getInstance();

                String minute = Integer.toString(i1);
                if (i1 < 10) { minute = "0" + minute; }

                int month_i = cal.get(Calendar.MONTH);
                String month;
                if (month_i < 10) {
                    month = "0" + Integer.toString(month_i);
                } else {
                    month = Integer.toString(month_i);
                }

                int day_i = cal.get(Calendar.DAY_OF_MONTH);
                String day;
                if (day_i < 10) {
                    day = "0" + Integer.toString(day_i);
                } else {
                    day = Integer.toString(day_i);
                }

                Log.d("Time", Integer.toString(i) + ":" + minute + ":00");
                Log.d("Date", Integer.toString(cal.get(Calendar.YEAR)) + "-" + month  + "-" + day);
            }
        });*/
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

        // Add a marker in Sydney and move the camera
        if (mLocation != null && mMap != null) {
            LatLng cLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(cLatLng)
                    .title("Starting point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cLatLng));
        }

    }



    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);

        if (mLocation != null && mMap != null) {
            startCoord = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(startCoord)
                    .title("Starting point"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startCoord, 14.0f));

        }

        AutoCompleteTextView inputSrc = (AutoCompleteTextView) findViewById(R.id.searchStart);
        AutoCompleteTextView inputDest = (AutoCompleteTextView) findViewById(R.id.searchDest);

        srcAdapter = new AutoCompleteAdapter(this, R.layout.list_item);
        destAdapter = new AutoCompleteAdapter(this, R.layout.list_item);

        inputSrc.setAdapter(srcAdapter);
        inputDest.setAdapter(destAdapter);

        inputSrc.setOnItemClickListener(this);
        inputDest.setOnItemClickListener(this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    public static ArrayList autocomplete(String input) {
        ArrayList result = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder query = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            query.append("?key=" + API_KEY);
            query.append("&components=country:hk");
            query.append("&input=" + URLEncoder.encode(input, "utf-8"));

            URL url = new URL(query.toString());
            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResult.toString());
            JSONArray jsonArr = jsonObj.getJSONArray("predictions");

            result = new ArrayList(jsonArr.length());
            for (int i = 0; i < jsonArr.length(); ++i) {
                String description = jsonArr.getJSONObject(i).getString("description");
                String id = jsonArr.getJSONObject(i).getString("place_id");
                LocationMetaData data = new LocationMetaData(id, description);
                result.add(data);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void setLatLng (String id, Adapter adapter) {
        StringBuilder jsonResult = new StringBuilder();
        try {
            Log.d("PlaceId", id);
            StringBuilder query = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            query.append("?key=" + API_KEY);
            query.append("&placeid=" + URLEncoder.encode(id, "utf-8"));

            jsonResult = new GetLatLngTask().execute(query.toString()).get();

        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            Log.d("ResultJSON", jsonResult.toString());
            JSONObject jsonObj = new JSONObject(jsonResult.toString());
            JSONObject jsonLoc = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");


            double lat = jsonLoc.getDouble("lat");
            double lng = jsonLoc.getDouble("lng");

            Log.d("Result", Double.toString(lat) + ", " + Double.toString(lng));

            LatLng cLatLng = new LatLng(lat, lng);

            if (adapter == srcAdapter) {
                startCoord = cLatLng;
                mMarker.remove();
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(cLatLng)
                        .title("Starting point"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cLatLng));
            } else {
                destCoord = cLatLng;
                Log.d("Dest called", "Dest called");
                if (destMarker != null) {
                    destMarker.remove();
                }
                destMarker = mMap.addMarker(new MarkerOptions()
                        .position(cLatLng)
                        .title("Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cLatLng));
            }

            if (destMarker != null && mMarker != null) {
                drawRoute(startCoord, destCoord);

                String url = "http://10.89.197.135/taxi/addEvent.php?userid="+userid+"&clat="+ startCoord.latitude + "&clng=" + startCoord.longitude
                        + "&dlat=" + destCoord.latitude + "&dlng=" + destCoord.longitude + "&time=" + time + "&date=2016-04-17";
                boolean result = new AddEventTask().execute(url).get();

                String matchURL = "http://10.89.197.135/taxi/matchpairsuggestion.php?userid="+userid+"&clat="+ startCoord.latitude + "&clng=" + startCoord.longitude
                        + "&dlat=" + destCoord.latitude + "&dlng=" + destCoord.longitude + "&time=" + time + "&date=2016-04-17";
                LatLng matchResult = new MatchEventTask().execute(matchURL).get();

                if (matchResult != null) {
                    Log.d("MatchResult", String.valueOf(matchResult.latitude));

                    guestMarker = mMap.addMarker(new MarkerOptions()
                            .position(matchResult)
                            .title("Guest"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(matchResult));

                    drawRoute(matchResult, destCoord);

                    DDTuple d1 = getDistance(startCoord, destCoord);
                    DDTuple d2 = getDistance(startCoord, matchResult);
                    DDTuple d3 = getDistance(matchResult, destCoord);

                    double price = Calculation.price_for_two(d1.getDistance(), d2.getDistance(), d3.getDistance(), 0, 0, false);
                    double distance = d3.getDistance();
                    double duration = d3.getDuration();

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Plan frag = new Plan();

                    Bundle bundle = new Bundle();
                    bundle.putDouble("Time", duration);
                    bundle.putDouble("Total_Cost", price);
                    bundle.putDouble("Cost", Calculation.fee_cal(distance)-price);
                    frag.setArguments(bundle);

                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.plan_layout, frag);
                    ft.show(frag);
                    ft.commit();
                    Log.d("Commit", "commit");

                } else {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    NoPlan frag = new NoPlan();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.plan_layout, frag);
                    ft.show(frag);
                    Log.d("NoPlan", "NoPlan");
                    ft.commit();
                }



            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        TimePicker picker = ((TimePickerFragment) dialog).getTime();
        String sHour, sMinute;
        int hour = picker.getCurrentHour();
        int minute = picker.getCurrentMinute();

        if (hour < 10) {
            sHour = "0" + Integer.toString(hour);
        } else {
            sHour = Integer.toString(hour);
        }

        if (minute < 10) {
            sMinute = "0" + Integer.toString(minute);
        } else {
            sMinute = Integer.toString(minute);
        }

        time = sHour + ":" + sMinute + ":00";
        Log.d("Time", time);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }


    public void drawRoute() {
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder query = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json");
            query.append("?key=" + API_KEY);
            query.append("&origin=" + URLEncoder.encode(Double.toString(startCoord.latitude), "utf-8"));
            query.append(",");
            query.append(Double.toString(startCoord.longitude));
            query.append("&destination=" + URLEncoder.encode(Double.toString(destCoord.latitude), "utf-8") + ",");
            query.append(Double.toString(destCoord.longitude));
            query.append("&sensor=false&mode=driving&alternatives=true");

            jsonResult = new GetLatLngTask().execute(query.toString()).get();



        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(jsonResult.toString());
            Log.d("Draw", json.toString());
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            JSONArray legArr = routes.getJSONArray("legs");
            long distance = 0;
            long duration = 0;

            for (int i = 0; i < legArr.length(); ++i) {
                JSONArray steps  = legArr.getJSONObject(i).getJSONArray("steps");
                for (int j = 0; j < steps.length(); ++j) {
                    distance += steps.getJSONObject(j).getJSONObject("distance").getLong("value");
                    duration += steps.getJSONObject(j).getJSONObject("duration").getLong("value");
                }
            }

            Log.d("duration", Long.toString(duration));
            Log.d("distance", Long.toString(distance));
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(15)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color

            );

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawRoute(LatLng startCoord, LatLng destCoord) {
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder query = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json");
            query.append("?key=" + API_KEY);
            query.append("&origin=" + URLEncoder.encode(Double.toString(startCoord.latitude), "utf-8"));
            query.append(",");
            query.append(Double.toString(startCoord.longitude));
            query.append("&destination=" + URLEncoder.encode(Double.toString(destCoord.latitude), "utf-8") + ",");
            query.append(Double.toString(destCoord.longitude));
            query.append("&sensor=false&mode=driving&alternatives=true");

            jsonResult = new GetLatLngTask().execute(query.toString()).get();



        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(jsonResult.toString());
            Log.d("Draw", json.toString());
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            JSONArray legArr = routes.getJSONArray("legs");
            long distance = 0;
            long duration = 0;

            for (int i = 0; i < legArr.length(); ++i) {
                JSONArray steps  = legArr.getJSONObject(i).getJSONArray("steps");
                for (int j = 0; j < steps.length(); ++j) {
                    distance += steps.getJSONObject(j).getJSONObject("distance").getLong("value");
                    duration += steps.getJSONObject(j).getJSONObject("duration").getLong("value");
                }
            }

            Log.d("duration", Long.toString(duration));
            Log.d("distance", Long.toString(distance));
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(15)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color

            );

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DDTuple getDistance(LatLng p1, LatLng p2) {
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder query = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json");
            query.append("?key=" + API_KEY);
            query.append("&origin=" + URLEncoder.encode(Double.toString(p1.latitude), "utf-8"));
            query.append(",");
            query.append(Double.toString(p1.longitude));
            query.append("&destination=" + URLEncoder.encode(Double.toString(p2.latitude), "utf-8") + ",");
            query.append(Double.toString(p2.longitude));
            query.append("&sensor=false&mode=driving&alternatives=true");

            jsonResult = new GetLatLngTask().execute(query.toString()).get();



        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(jsonResult.toString());
            Log.d("Draw", json.toString());
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONArray legArr = routes.getJSONArray("legs");
            long distance = 0;
            long duration = 0;

            for (int i = 0; i < legArr.length(); ++i) {
                JSONArray steps  = legArr.getJSONObject(i).getJSONArray("steps");
                for (int j = 0; j < steps.length(); ++j) {
                    distance += steps.getJSONObject(j).getJSONObject("distance").getLong("value");
                    duration += steps.getJSONObject(j).getJSONObject("duration").getLong("value");
                }
            }

            Log.d("duration", Long.toString(duration));
            Log.d("distance", Long.toString(distance));

            return new DDTuple(distance, duration);



        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        /*String str = (String) adapterView.getItemAtPosition(i);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();*/
        //setLatLng(srcAdapter.getId(i), adapterView.getAdapter());
        if (adapterView.getAdapter() == srcAdapter) {
            setLatLng(srcAdapter.getId(i), adapterView.getAdapter());
        } else {
            setLatLng(destAdapter.getId(i), adapterView.getAdapter());
        }

        Log.d("Clicked", "clicked");
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


    public void joinEvent(View v) {
        Intent intent = new Intent();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        QRCodeFragment frag = new QRCodeFragment();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.middle, frag);
        ft.show(frag);
        ft.commit();
        Log.d("Commit", "commit");
    }

    public void createEvent(View v) {
        /*Intent intent = new Intent();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        QRCodeFragment frag = new QRCodeFragment();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.middle, frag);
        ft.show(frag);
        ft.commit();
        Log.d("Commit", "commit");*/

        Intent intent = new Intent(this, FinalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public void onPlanSelected() {

    }

    @Override
    public void onClick() {

    }
}
