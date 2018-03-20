package com.klintelligence.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // TODO: Can I trust location services to ping me with an interval?

    private boolean locationAllowed, receivesLocationUpdates, deniedLocationAllowed;
    private LocationCallback locationListener;
    private LocationRequest request;

    TextView cityTextView,
            updatedTimeTextView,
            currentTempTextView,
            currentCondTextView,
            currentRainProbTextView;

    ImageView currentCondImg;

    HashMap<Integer, TextView>
            dailyForecastDayTextViews,
            dailyForecastTempTextViews,
            dailyForecastRainProbTextViews,
            hourlyForecastTimeTextViews,
            hourlyForecastTempViews,
            hourlyForecastRainProbViews;

    HashMap<Integer, ImageView> dailyForecastCondImageViews, hourlyForecastCondImageViews;

    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Get location provider
         */
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        prepareLocationTracking();

        /**
         * Get Views for current forecast
         */
        cityTextView = (TextView) findViewById(R.id.city);
        updatedTimeTextView = (TextView) findViewById(R.id.updatedTime);

        currentTempTextView = (TextView) findViewById(R.id.currentTemp);
        currentCondImg = (ImageView) findViewById(R.id.currentCondImg);
        currentCondTextView = (TextView) findViewById(R.id.currentCond);
        currentRainProbTextView = (TextView) findViewById(R.id.currentRainProb);

        /**
         * Get Views for hourly forecasts
         */
        hourlyForecastTimeTextViews = new HashMap<>();
        hourlyForecastTimeTextViews.put(1, (TextView) findViewById(R.id.hourOneTime));
        hourlyForecastTimeTextViews.put(2, (TextView) findViewById(R.id.hourTwoTime));
        hourlyForecastTimeTextViews.put(3, (TextView) findViewById(R.id.hourThreeTime));
        hourlyForecastTimeTextViews.put(4, (TextView) findViewById(R.id.hourFourTime));
        hourlyForecastTimeTextViews.put(5, (TextView) findViewById(R.id.hourFiveTime));

        hourlyForecastTempViews = new HashMap<>();
        hourlyForecastTempViews.put(1, (TextView) findViewById(R.id.hourOneTemp));
        hourlyForecastTempViews.put(2, (TextView) findViewById(R.id.hourTwoTemp));
        hourlyForecastTempViews.put(3, (TextView) findViewById(R.id.hourThreeTemp));
        hourlyForecastTempViews.put(4, (TextView) findViewById(R.id.hourFourTemp));
        hourlyForecastTempViews.put(5, (TextView) findViewById(R.id.hourFiveTemp));

        hourlyForecastRainProbViews = new HashMap<>();
        hourlyForecastRainProbViews.put(1, (TextView) findViewById(R.id.hourOneRainProb));
        hourlyForecastRainProbViews.put(2, (TextView) findViewById(R.id.hourTwoRainProb));
        hourlyForecastRainProbViews.put(3, (TextView) findViewById(R.id.hourThreeRainProb));
        hourlyForecastRainProbViews.put(4, (TextView) findViewById(R.id.hourFourRainProb));
        hourlyForecastRainProbViews.put(5, (TextView) findViewById(R.id.hourFiveRainProb));

        hourlyForecastCondImageViews = new HashMap<>();
        hourlyForecastCondImageViews.put(1, (ImageView) findViewById(R.id.hourOneCondImg));
        hourlyForecastCondImageViews.put(2, (ImageView) findViewById(R.id.hourTwoCondImg));
        hourlyForecastCondImageViews.put(3, (ImageView) findViewById(R.id.hourThreeCondImg));
        hourlyForecastCondImageViews.put(4, (ImageView) findViewById(R.id.hourFourCondImg));
        hourlyForecastCondImageViews.put(5, (ImageView) findViewById(R.id.hourFiveCondImg));

        /**
         * Get Views for daily forecasts
         */
        dailyForecastDayTextViews = new HashMap<>();
        dailyForecastDayTextViews.put(1, (TextView) findViewById(R.id.dayOneDay));
        dailyForecastDayTextViews.put(2, (TextView) findViewById(R.id.dayTwoDay));
        dailyForecastDayTextViews.put(3, (TextView) findViewById(R.id.dayThreeDay));

        dailyForecastTempTextViews = new HashMap<>();
        dailyForecastTempTextViews.put(1, (TextView) findViewById(R.id.dayOneTemp));
        dailyForecastTempTextViews.put(2, (TextView) findViewById(R.id.dayTwoTemp));
        dailyForecastTempTextViews.put(3, (TextView) findViewById(R.id.dayThreeTemp));

        dailyForecastRainProbTextViews = new HashMap<>();
        dailyForecastRainProbTextViews.put(1, (TextView) findViewById(R.id.dayOneRainProb));
        dailyForecastRainProbTextViews.put(2, (TextView) findViewById(R.id.dayTwoRainProb));
        dailyForecastRainProbTextViews.put(3, (TextView) findViewById(R.id.dayThreeRainProb));

        dailyForecastCondImageViews = new HashMap<>();
        dailyForecastCondImageViews.put(1, (ImageView) findViewById(R.id.dayOneCondImg));
        dailyForecastCondImageViews.put(2, (ImageView) findViewById(R.id.dayTwoCondImg));
        dailyForecastCondImageViews.put(3, (ImageView) findViewById(R.id.dayThreeCondImg));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Location Allowed", "" + locationAllowed);
        Log.d("ReceivesLocationUpdates", "" + receivesLocationUpdates);
        if (!deniedLocationAllowed && !locationAllowed) {
            askForLocationPermission();
        } else if (locationAllowed && !receivesLocationUpdates) {
            startLocationUpdates();
        }
        // Reset in the end of onResume, so it will ask the next time
        deniedLocationAllowed = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // TODO: Try not asking for permission, just start the location updating no matter what!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_REQUEST: {
                Log.d("GRANT RESULTS LENGTH", "" + grantResults.length);
                Log.d("GRANT RESULTS[0]", "" + grantResults[0]);
                Log.d("GRANT SUCCESS", "" + PackageManager.PERMISSION_GRANTED);
                Log.d("GRANT FAILURE", "" + PackageManager.PERMISSION_DENIED);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permissionsResult", "Location Access Granted");
                    locationAllowed = true;
                } else {
                    makeToast("Can not provide weather data without your location", Toast.LENGTH_SHORT);
                    Log.d("permissionsResult", "Location Access Denied");
                    deniedLocationAllowed = true;
                }
            }
        }
    }

    private void prepareLocationTracking() {
        locationAllowed = getHasLocationPermission();
        request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000 * 60 * 5);
        request.setFastestInterval(10000);
        locationListener = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                makeToast("Received new location", Toast.LENGTH_SHORT);

                if (locationResult == null) {
                    makeToast("Received null location", Toast.LENGTH_SHORT);
                    return;
                }
                //stopLocationUpdates();
                Location location = locationResult.getLastLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                geoLocate(latitude, longitude);
            }
        };
    }

    private void stopLocationUpdates() {
        if(receivesLocationUpdates) {
            receivesLocationUpdates = false;
            makeToast("Stopping location updates", Toast.LENGTH_SHORT);
            locationProviderClient.removeLocationUpdates(locationListener);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // TODO: Locations are never received, unless manually sent to emulator
        if(!receivesLocationUpdates) {
            receivesLocationUpdates = true;
            makeToast("Starting location updates", Toast.LENGTH_SHORT);
            locationProviderClient.requestLocationUpdates(request, locationListener, null);
        }
    }

    private void geoLocate(double latitude, double longitude) {
        String query = "/geolookup/q/" + latitude + "," + longitude + ".json";
        new GetWundergroundData(query) {
            @Override
            public void handleResult(JSONObject result) {
                try {
                    /*
                    String type = result.getJSONObject("location").getString("type");
                    if(!type.equals("CITY")) {
                        makeToast("Your location type is " + type + ", must be CITY", Toast.LENGTH_SHORT);
                        return;
                    }
                    */
                    String city = result.getJSONObject("location").getString("city");
                    cityTextView.setText(city);
                    updateWeatherInformation(city);
                } catch (JSONException e) {
                    makeToast("Error while parsing API response", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateWeatherInformation(String city) {
        updateCurrentForecast(city);
        updateDailyForecast(city);
        updateHourlyForecast(city);
        updateUpdatedAtTime();
    }

    private void updateUpdatedAtTime() {
        Calendar calendar = Calendar.getInstance();
        String paddedHour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        String currentHour = paddedHour.substring(paddedHour.length() - 2);
        String paddedMinute = "0" + calendar.get(Calendar.MINUTE);
        String currentMinute = paddedMinute.substring(paddedMinute.length() - 2);
        updatedTimeTextView.setText(currentHour + ":" + currentMinute);
    }

    private void updateHourlyForecast(String city) {
        String query = "/hourly/q/" + city + ".json";
        new GetWundergroundData(query) {
            @Override
            public void handleResult(JSONObject result) {
                try {
                    JSONArray forecasts = result.getJSONArray("hourly_forecast");

                    for (int i = 1; i < 6; i++) {
                        JSONObject forecast = forecasts.getJSONObject(i - 1);
                        String hour = forecast.getJSONObject("FCTTIME").getString("hour_padded");
                        String condition = forecast.getString("condition");
                        String temp = forecast.getJSONObject("temp").getString("metric");
                        String rainProb = forecast.getString("pop");

                        hourlyForecastTimeTextViews.get(i).setText(hour);
                        hourlyForecastTempViews.get(i).setText(temp + "°C");
                        hourlyForecastRainProbViews.get(i).setText(rainProb + "%");
                        hourlyForecastCondImageViews.get(i).setImageResource(getImgResourceForCondition(condition));
                    }
                } catch (JSONException e) {
                    makeToast("Error while parsing API response for hourly forecast", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateDailyForecast(String city) {
        String query = "/forecast/q/" + city + ".json";
        new GetWundergroundData(query) {
            @Override
            public void handleResult(JSONObject result) {
                try {
                    JSONArray forecasts = result.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");

                    for (int i = 1; i < 4; i++) {
                        JSONObject forecast = forecasts.getJSONObject(i);
                        String day = forecast.getJSONObject("date").getString("weekday");
                        String condition = forecast.getString("conditions");
                        String maxTemp = forecast.getJSONObject("high").getString("celsius");
                        String minTemp = forecast.getJSONObject("low").getString("celsius");
                        String rainProb = forecast.getString("pop");

                        dailyForecastDayTextViews.get(i).setText(day);
                        dailyForecastTempTextViews.get(i).setText(maxTemp + "°C / " + minTemp + "°C");
                        dailyForecastRainProbTextViews.get(i).setText(rainProb + "%");
                        dailyForecastCondImageViews.get(i).setImageResource(getImgResourceForCondition(condition));
                    }
                } catch (JSONException e) {
                    makeToast("Error while parsing API response for daily forecasst", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateCurrentForecast(String city) {
        String query = "/conditions/q/" + city + ".json";
        new GetWundergroundData(query) {
            @Override
            public void handleResult(JSONObject result) {
                try {
                    JSONObject observation = result.getJSONObject("current_observation");

                    String temp = observation.getString("temp_c");
                    // TODO: Get correct information for rain probability
                    String chanceOfRain = observation.getString("relative_humidity");
                    String cond = observation.getString("weather");

                    currentTempTextView.setText(temp + "°C");
                    currentRainProbTextView.setText(chanceOfRain + " chance of rain");
                    currentCondTextView.setText(cond);
                    currentCondImg.setImageResource(getImgResourceForCondition(cond));

                } catch (JSONException e) {
                    makeToast("Error while parsing API response for current forecast", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }

            }
        }.execute();
    }

    private int getImgResourceForCondition(String condition) {
        Log.d("CONDITION", condition);
        switch (condition) {
            case "Overcast": {
                return R.drawable.icons8_clouds_96;
            }
            case "Partly Cloudy": {
                return R.drawable.icons8_partly_cloudy_day_96;
            }
            case "Mostly Cloudy": {
                return R.drawable.icons8_partly_cloudy_day_96;
            }
            case "Snow": {
                return R.drawable.icons8_snow_96;
            }
            case "Snow Showers": {
                return R.drawable.icons8_light_snow_96;
            }
            case "Clear": {
                return R.drawable.icons8_summer_96;
            }
            case "Chance of Rain": {
                return R.drawable.icons8_rain_cloud_96;
            }
            case "Rain": {
                return R.drawable.icons8_rain_96;
            }
            default: {
                Log.d("Image For Condition", "Does not have image for condition: " + condition);
                return R.drawable.icons8_puzzled_96;
            }
        }
    }


    private void makeToast(String message, int duration) {
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }

    private void askForLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.LOCATION_PERMISSION_REQUEST);

    }

    private boolean getHasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
