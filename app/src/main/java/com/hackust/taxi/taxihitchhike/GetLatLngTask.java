package com.hackust.taxi.taxihitchhike;

import android.os.AsyncTask;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cameron on 4/16/2016.
 */
public class GetLatLngTask extends AsyncTask<String, Void, StringBuilder> {
    @Override
    protected StringBuilder doInBackground(String... urls) {
        HttpURLConnection conn = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            URL url = new URL(urls[0]);
            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return jsonResult;
    }
}
