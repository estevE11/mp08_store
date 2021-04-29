package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mp08_store.weather.WeatherData;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    private final int[] ids = {
        R.id.txt_weather_main,
        R.id.txt_location,
        R.id.txt_country,
        R.id.txt_temp,
        R.id.txt_temp_min,
        R.id.txt_temp_max,
        R.id.txt_humidity,
        R.id.txt_preassure,
        R.id.txt_windspeed,
        R.id.txt_ptemp,
        R.id.txt_visibility,
        R.id.txt_clouds,
        R.id.label_hum,
        R.id.label_preassure,
        R.id.label_windspeed,
        R.id.label_ptemp,
        R.id.label_visibility,
        R.id.label_clouds
    };
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
                setInfoVisible(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                setInfoVisible(false);
            }
        });
    }

    private void setInfoVisible(boolean visible) {
        findViewById(R.id.label_noinfo).setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        for(int id : this.ids) {
            findViewById(id).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
        findViewById(R.id.img_icon).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void fillData(WeatherData data) throws JSONException {
        ((TextView) findViewById(R.id.txt_weather_main)).setText(data.getMain());
        ((TextView) findViewById(R.id.txt_location)).setText(data.getLocation_name());
        ((TextView) findViewById(R.id.txt_country)).setText(data.getCountry());
        ((TextView) findViewById(R.id.txt_temp)).setText((int) data.getTemp() + "º");

        ((TextView) findViewById(R.id.txt_temp_min)).setText((int) data.getTemp_min() + "º");
        ((TextView) findViewById(R.id.txt_temp_max)).setText((int) data.getTemp_max() + "º");

        ((TextView) findViewById(R.id.txt_humidity)).setText((int) data.getHumidity() + "%");
        ((TextView) findViewById(R.id.txt_preassure)).setText((int) data.getPressure() + " hPa");

        ((TextView) findViewById(R.id.txt_windspeed)).setText((int) data.getWind_speed() + " km/h");
        ((TextView) findViewById(R.id.txt_ptemp)).setText((int) data.getFeels() + "º");

        ((TextView) findViewById(R.id.txt_visibility)).setText(data.getVisibility() + " km");
        ((TextView) findViewById(R.id.txt_clouds)).setText(data.getClouds()+"");

        Picasso.get().load(data.getIconUrl()).error(R.drawable.ic_baseline_error_outline_24).into(((ImageView) findViewById(R.id.img_icon)));
    }
}