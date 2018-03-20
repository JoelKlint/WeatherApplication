package com.klintelligence.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        getNewData();
    }

    private void getNewData() {
        /**
         *  Current forecast
         */
        new GetWundergroundData("/conditions/q/Daejeon.json") {
            @Override
            public void presentResult(JSONObject result) {
                try {
                    JSONObject observation = result.getJSONObject("current_observation");

                    String temp = observation.getString("temp_c");
                    String chanceOfRain = observation.getString("relative_humidity");
                    String cond = observation.getString("weather");

                    currentTempTextView.setText(temp + "°C");
                    currentRainProbTextView.setText(chanceOfRain + " chance of rain");
                    currentCondTextView.setText(cond);
                    currentCondImg.setImageResource(getImgResourceForCondition(cond));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute();

        /**
         *  Daily forecast
         */
        new GetWundergroundData("/forecast/q/Daejeon.json") {
            @Override
            public void presentResult(JSONObject result) {
                try {
                    JSONArray forecasts = result.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");

                    for(int i = 1; i < 4; i++) {
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
                    e.printStackTrace();
                }
            }
        }.execute();

        /**
         *  Hourly forecast
         */
        new GetWundergroundData("/hourly/q/Daejeon.json") {
            @Override
            public void presentResult(JSONObject result) {
                try {
                    JSONArray forecasts = result.getJSONArray("hourly_forecast");

                    for(int i = 1; i < 6; i++) {
                        JSONObject forecast = forecasts.getJSONObject(i-1);
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
                    e.printStackTrace();
                }
            }
        }.execute();

        /**
         *  Update the updated at display
         */
        Calendar calendar = Calendar.getInstance();
        String paddedHour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        String currentHour = paddedHour.substring(paddedHour.length() - 2);
        String paddedMinute = "0" + calendar.get(Calendar.MINUTE);
        String currentMinute = paddedMinute.substring(paddedHour.length() - 2);
        updatedTimeTextView.setText(currentHour + ":" + currentMinute);

    }

    private int getImgResourceForCondition(String condition) {
        Log.d("CONDITION", condition);
        switch (condition) {
            case "Overcast": {
                return R.drawable.icons8_partly_cloudy_day_96;
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
            case "Clear": {
                return R.drawable.icons8_summer_96;
            }
            default: {
                return -1;
            }
        }
    }
}
