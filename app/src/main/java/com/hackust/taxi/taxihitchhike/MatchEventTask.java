package com.hackust.taxi.taxihitchhike;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cameron on 4/17/2016.
 */
public class MatchEventTask extends AsyncTask<String, Void, LatLng> {
    @Override
    protected LatLng doInBackground(String... strings) {
        String success;
        HttpURLConnection conn = null;
        StringBuilder resultBuilder = new StringBuilder();
        try {
            URL url = new URL(strings[0]);
            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {
                resultBuilder.append(buff, 0, read);
            }

            if (resultBuilder.toString().isEmpty()) {
                return null;
            } else {
                JSONObject obj = new JSONObject(resultBuilder.toString());
                double dlat = Double.parseDouble(obj.getString("dlat"));
                double dlng = Double.parseDouble(obj.getString("dlng"));
                Log.d("Dlat", Double.toString(dlat));
                Log.d("Dlng", Double.toString(dlng));
                return new LatLng(dlat, dlng);

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }
}
