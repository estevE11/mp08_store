package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        findViewById(R.id.btn_search_weather).setOnClickListener(this);
    }

    public void onClick(View v) {
        String loc = ((TextView) findViewById(R.id.input_location)).getText().toString();
        WeatherData.fromLocation(loc, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject data = null;
                String str = new String(responseBody);

                try {
                    data = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fillData(((JSONObject)((JSONArray) data.get("weather")).get(0)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void fillData(JSONObject data) throws JSONException {
        String main = (String) data.get("main");
        ((TextView) findViewById(R.id.txt_weather_main)).setText(main);
    }
}