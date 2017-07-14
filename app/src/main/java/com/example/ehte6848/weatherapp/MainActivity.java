package com.example.ehte6848.weatherapp;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//2 imports missing

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends Activity {

    private TextView temp;
    RelativeLayout relativeLayout;
    private TextView condDescr;
    private TextView press;
    private TextView windSpeed;
    private EditText cityText;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String city= "London,UK";

        cityText= (EditText) findViewById(R.id.editText2);
        temp = (TextView) findViewById(R.id.textView);
        condDescr = (TextView) findViewById(R.id.textView2);
        press = (TextView) findViewById(R.id.textView3);
        windSpeed = (TextView) findViewById(R.id.textView4);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{"Chennai,In"});
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new com.example.ehte6848.weatherapp.WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            //relativeLayout.setVisibility(View.VISIBLE);

            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "C");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");


        }
    }
    }

