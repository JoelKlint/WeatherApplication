package com.klintelligence.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by firefox on 2018-03-20.
 */

public abstract class GetWundergroundData extends AsyncTask<URL, Void, JSONObject> {

    final String API_KEY = "12f650b9d99c55ee";
    final String BASE_URL = "http://api.wunderground.com/api/" + API_KEY;
    String QUERY_URL = "";

    public GetWundergroundData(String query) {
        QUERY_URL =  BASE_URL + query;
    }

    @Override
    protected JSONObject doInBackground(URL... urls) {
        Log.i("THE URL", QUERY_URL);
        URL url = null;
        try {
            url = new URL(QUERY_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());

            Scanner s = new Scanner(is).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    public abstract void handleResult(JSONObject result);

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        handleResult(jsonObject);

    }
}
