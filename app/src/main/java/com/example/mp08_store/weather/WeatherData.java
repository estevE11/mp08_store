package com.example.mp08_store.weather;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {
    // Example: http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID={APIKEY}
    private static final String API_BASE = "http://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "f134d1f89188395b1741b751f2594c2e";

    private double lon, lat;
    private String main, description, icon;
    private String base;
    private double temp, feels, temp_max, temp_min, pressure, humidity;
    private int visibility;
    private double wind_speed, wind_deg, wind_gust;
    private int clouds;
    private int dt;
    private String country, location_name;
    private int country_id, location_id;
    private int timezone, sunrise, sunset;

    public WeatherData(JSONObject data) throws JSONException {
        this.location_name = (String) data.get("name");
        this.location_id = (int) data.get("id");

        JSONObject coords = (JSONObject) data.get("coord");
        this.lon = (double) coords.get("lon");
        this.lat = (double) coords.get("lat");

        JSONObject weather = (JSONObject) ((JSONArray) data.get("weather")).get(0);
        this.main = (String) weather.get("main");
        this.description = (String) weather.get("description");
        this.icon = (String) weather.get("icon");

        this.base = (String) data.get("base");
        this.visibility = (int) data.get("visibility");

        JSONObject main = (JSONObject) data.get("main");
        this.temp = ((double) main.get("temp")) - 273.15;
        this.feels = (double) main.get("feels_like") - 273.15;
        this.temp_min = ((double) main.get("temp_min")) - 273.15;
        this.temp_max = ((double) main.get("temp_max")) - 273.15;
        this.pressure = (int) main.get("pressure");
        this.humidity = (int) main.get("humidity");

        JSONObject wind = (JSONObject) data.get("wind");
        this.wind_speed = (double) wind.get("speed");
        this.wind_deg = (int) wind.get("deg");
        //this.wind_gust = (double) wind.get("gust");

        this.clouds = ((int) ((JSONObject) data.get("clouds")).get("all"));

        JSONObject sys = (JSONObject) data.get("sys");
        this.country_id = (int) sys.get("id");
        this.country = (String) sys.get("country");
        this.sunrise = (int) sys.get("sunrise");
        this.sunset = (int) sys.get("sunset");
    }

    public static void fromLocation(String location, AsyncHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0,10000);

        String url = API_BASE + "?q=" + location + "&APPID=" + API_KEY;
        client.get(url, handler);
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getIconUrl() {
        return "https://openweathermap.org/img/wn/" + this.icon + "@2x.png";
    }

    public String getBase() {
        return base;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeels() {
        return feels;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getVisibility() {
        return visibility;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public double getWind_deg() {
        return wind_deg;
    }

    public double getWind_gust() {
        return wind_gust;
    }

    public int getClouds() {
        return clouds;
    }

    public int getDt() {
        return dt;
    }

    public String getCountry() {
        return country;
    }

    public int getCountry_id() {
        return country_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public int getLocation_id() {
        return location_id;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getSunrise() {
        return sunrise;
    }

    public int getSunset() {
        return sunset;
    }
}
