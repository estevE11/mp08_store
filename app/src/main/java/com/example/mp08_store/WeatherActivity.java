package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mp08_store.weather.WeatherData;
import com.loopj.android.http.AsyncHttpResponseHandler;

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
                if(statusCode != 200) return;
                JSONObject data = null;
                String str = new String(responseBody);

                try {
                    data = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fillData(new WeatherData(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void fillData(WeatherData data) throws JSONException {;
        ((TextView) findViewById(R.id.txt_weather_main)).setText(data.getMain());
    }
}