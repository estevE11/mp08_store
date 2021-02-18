package com.example.mp08_store;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class WeatherData {
    // Example: http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID={APIKEY}
    private static final String API_BASE = "http://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "f134d1f89188395b1741b751f2594c2e";

    public static void fromLocation(String location, AsyncHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0,10000);

        String url = API_BASE + "?q=" + location + "&APPID=" + API_KEY;
        client.get(url, handler);
    }

}
