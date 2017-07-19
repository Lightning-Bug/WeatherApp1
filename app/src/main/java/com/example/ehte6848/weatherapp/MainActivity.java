package com.example.ehte6848.weatherapp;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

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
import android.widget.Toast;

import static java.security.AccessController.getContext;

public class MainActivity extends Activity {
    public final static String ID_EXTRA = "com.example.ehte6848.weatherapp._ID";
    private TextView temp;
    RelativeLayout relativeLayout;
    private TextView condDescr;
    private TextView press;
    private TextView windSpeed;
    private EditText cityText;
    private SearchView searchView;

    LocationListener  locListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String city = "London,UK";
        Log.d("hii","i am created");

        cityText = (EditText) findViewById(R.id.editText2);
        temp = (TextView) findViewById(R.id.textView);
        condDescr = (TextView) findViewById(R.id.textView2);
        press = (TextView) findViewById(R.id.textView3);
        windSpeed = (TextView) findViewById(R.id.textView4);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayout.setVisibility(View.GONE);

    }

    public void process(View view) {
        if (cityText.getText().toString() != null) {
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(new String[]{cityText.getText().toString()});
        } else
            Toast.makeText(getApplicationContext(), "Please enter city name!", Toast.LENGTH_LONG).show();
    }

    /*   @Override
       public boolean onCreateOptionsMenu(Menu menu) {
           // Inflate the menu; this adds items to the action bar if it is present.
           getMenuInflater().inflate(R.menu.main, menu);
           return true;
       }*/
    public void pass(View view) {
        if (cityText.getText().toString() != null) {
            Intent i = new Intent(MainActivity.this, NextActivity.class);
            i.putExtra(ID_EXTRA, cityText.getText().toString());
            startActivity(i);
        } else
            Toast.makeText(getApplicationContext(), "Please enter city name!", Toast.LENGTH_LONG).show();

    }


    //fetch current location using LocationManager
   public void fetch(View view) {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


      Criteria searchProviderCriteria = new Criteria();

// Location Criteria
        {
            Log.d("hi","i am searching provider");
            searchProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
            searchProviderCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
            searchProviderCriteria.setCostAllowed(false);
        }
        String provider = locManager.getBestProvider(searchProviderCriteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("hlo","permission not granted");
            return;
        }
        Location loc = locManager.getLastKnownLocation(provider);
       Log.d("hey","permission granted");




        new LocationListener() {
            //  Log.i("hloo","i am inside loclistener");

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d("SwA", "Location changed!");
                String sLat = "" + location.getLatitude();
                String sLon = "" + location.getLongitude();
                Log.d("SwA", "Lat [" + sLat + "] - sLong [" + sLon + "]");

                LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locManager.removeUpdates(locListener);
                JSONSearchTask task = new JSONSearchTask();
                task.execute(new Location[]{location});
            }

        };
     /*  if ( loc == null || (SystemClock.elapsedRealtime() - loc.getTime()) &amp;gt;timeOut){
            // We request another update Location
            Log.d("SwA", "Request location");
            locManager.requestSingleUpdate(provider, locListener, null);
        }
         else{
            JSONSearchTask task = new JSONSearchTask();
            task.execute(new Location[]{loc});
        }*/
       JSONSearchTask task = new JSONSearchTask();
       task.execute(new Location[]{loc});

    }

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
            if (weather != null) {
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
                press.setText("" + weather.currentCondition.getPressure() + " hPa");
                windSpeed.setText("" + weather.wind.getSpeed() + " mps");
                relativeLayout.setVisibility(View.VISIBLE);
            } else {

                Toast.makeText(getApplicationContext(), "Invalid City or Country Name", Toast.LENGTH_LONG).show();
            }
        }
    }

    //JSONSearchTAsk
    private class JSONSearchTask extends AsyncTask<Location, Void, Weather> {

        @Override
        protected Weather doInBackground(Location... params) {
            Weather weather = new Weather();
            String data = ((new com.example.ehte6848.weatherapp.WeatherHttpClient2()).getWeatherData(params[0]));

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
            if (weather != null) {
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
                press.setText("" + weather.currentCondition.getPressure() + " hPa");
                windSpeed.setText("" + weather.wind.getSpeed() + " mps");
                relativeLayout.setVisibility(View.VISIBLE);
            } else {

                Toast.makeText(getApplicationContext(), "Invalid City or Country Name", Toast.LENGTH_LONG).show();
            }
        }

    }
}

