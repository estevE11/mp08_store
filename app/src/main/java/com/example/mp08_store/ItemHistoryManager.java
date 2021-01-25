package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mp08_store.db.DBDatasource;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemHistoryManager extends AppCompatActivity {

    private DBDatasource db;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.db = new DBDatasource(this);

        final Bundle b = this.getIntent().getExtras();

        int type_n = b.getInt("type");
        this.type = type_n  == 0 ? "E" : "S";

        this.fillSpinner();
    }

    private void fillSpinner() {
        Spinner spinner_code = (Spinner)findViewById(R.id.spinner_code);
        String[] codes = this.db.getFullStoreCodes();
        ArrayList<String> codesList = new ArrayList<String>(Arrays.asList(codes));
        ArrayAdapter<String> codesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codesList);
        spinner_code.setAdapter(codesAdapter);
    }

    private void save() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_history_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_save:
                this.save();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}