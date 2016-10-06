package com.hackust.taxi.taxihitchhike;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cameron on 4/16/2016.
 */
public class AddEventTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
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

            Log.d("result", resultBuilder.toString());
            return (resultBuilder.toString().equals("success"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return false;
    }
}
