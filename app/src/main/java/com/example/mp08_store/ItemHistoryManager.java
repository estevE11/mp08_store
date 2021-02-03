package com.example.mp08_store;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mp08_store.db.DBDatasource;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemHistoryManager extends AppCompatActivity implements View.OnClickListener{

    private DBDatasource db;
    char type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.db = new DBDatasource(this);

        final Bundle b = this.getIntent().getExtras();

        int type_n = b.getInt("type");
        this.type = type_n == 0 ? 'E' : 'S';

        if (this.type == 'E') this.setTitle("Stock out");
        else this.setTitle("Stock in");

        this.fillSpinner();

        findViewById(R.id.input_date).setOnClickListener(this);
        findViewById(R.id.btn_stock_add).setOnClickListener(this);
        findViewById(R.id.btn_stock_rm).setOnClickListener(this);
    }

    private void fillSpinner() {
        Spinner spinner_code = (Spinner)findViewById(R.id.spinner_code);
        String[] codes = this.db.getFullStoreCodes();
        ArrayList<String> codesList = new ArrayList<String>(Arrays.asList(codes));
        ArrayAdapter<String> codesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, codesList);
        spinner_code.setAdapter(codesAdapter);
    }

    private void save() {
        Spinner spinner_code = (Spinner)findViewById(R.id.spinner_code);
        int id = spinner_code.getSelectedItemPosition();
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

    private void addQuantity(int q) {
        EditText ed_quant = (EditText) findViewById(R.id.input_quantity);
        int currStock = Integer.parseInt(String.valueOf(ed_quant.getText()));
        currStock += q;
        if(currStock < 0) currStock = 0;
        ed_quant.setText(currStock+"");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.input_date:
                final EditText dateInput = (EditText) findViewById(R.id.input_date);
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(ItemHistoryManager.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateInput.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btn_stock_add:
                this.addQuantity(1);
                break;
            case R.id.btn_stock_rm:
                this.addQuantity(-1);
                break;
        }
    }
}