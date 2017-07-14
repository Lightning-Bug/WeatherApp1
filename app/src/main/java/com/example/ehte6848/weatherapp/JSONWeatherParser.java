package com.example.ehte6848.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.ehte6848.weatherapp.Weather;




/**
 * Created by ehte6848 on 14-07-2017.
 */

public class JSONWeatherParser  {

    public static Weather getWeather(String data) throws JSONException {
        Weather weather = new Weather();

        //We create out JSONObject from data
        JSONObject jobj = new JSONObject(data);

        //few lines bunked
        // We get weather info (This is an array)
        JSONArray jArr = jobj.getJSONArray("weather");

        // we get only the first value
        JSONObject JSONWeather = jArr.getJSONObject(0);
        weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
        weather.currentCondition.setDescr(getString("description", JSONWeather));
        weather.currentCondition.setCondition(getString("main",JSONWeather));

        JSONObject mainObj = getObject("main",jobj);
        weather.currentCondition.setPressure(getInt("pressure", mainObj));
        weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weather.temperature.setTemp(getFloat("temp", mainObj));

        //wind
        JSONObject wObj = getObject("wind", jobj);
        weather.wind.setSpeed(getFloat("speed", wObj));

        return weather;


    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}
