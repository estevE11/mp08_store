package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mp08_store.db.DBDatasource;

import java.text.NumberFormat;

public class ItemManagerActivity extends AppCompatActivity {

    private DBDatasource db;

    private int itemId = 0;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.db = new DBDatasource(this);

        final Bundle b = this.getIntent().getExtras();

        this.editMode = true;
        try {
            this.itemId = b.getInt("_id");
        } catch (Exception e) {
            this.editMode = false;
        }

        if(this.editMode) {
            this.setTitle("Edit item");
            this.loadItemAndFill(this.itemId);
        } else {
            this.setTitle("Create item");
        }
    }

    private void save() {
        if(this.editMode) editItem(this.itemId);
        else createItem();
    }

    public void createItem() {
        Bundle values = this.getFormAndValidate();
        if(values == null) return;

        this.db.addItem(values.getString("code"), values.getString("desc"), values.getString("family"), values.getFloat("price"), values.getInt("stock"));
        this.openMainActivity();
    }

    public void editItem(int id) {
        Bundle values = this.getFormAndValidate();
        if(values == null) return;

        this.db.updateItem(id, values.getString("desc"), values.getString("family"), values.getFloat("price"), values.getInt("stock"));
        this.openMainActivity();
    }

    public void loadItemAndFill(int id) {
        Cursor c_item = this.db.getItem(id);
        c_item.moveToFirst();

        EditText input_code = ((EditText)findViewById(R.id.input_code));
        EditText input_desc = ((EditText)findViewById(R.id.input_desc));
        EditText input_price = ((EditText)findViewById(R.id.input_price));
        Spinner input_spinner_family = ((Spinner)findViewById(R.id.input_spinner_family));

        input_code.setText(c_item.getString(1));
        input_desc.setText(c_item.getString(2));
        input_price.setText(c_item.getString(4));

        input_code.setInputType(InputType.TYPE_NULL);

        String family = c_item.getString(3);
        if(family == null) return;
        if(family.equals("---")) return;
        for(int i = 0; i < input_spinner_family.getCount(); i++) {
            if(input_spinner_family.getItemAtPosition(i).toString().equals(family)) {
                input_spinner_family.setSelection(i);
                break;
            }
        }
    }

    private Bundle getFormAndValidate() {
        boolean insert = true;

        EditText input_code = ((EditText)findViewById(R.id.input_code));
        EditText input_desc = ((EditText)findViewById(R.id.input_desc));
        EditText input_stock = ((EditText)findViewById(R.id.input_stock));
        EditText input_price = ((EditText)findViewById(R.id.input_price));

        String desc = input_desc.getText().toString();
        String family = ((Spinner)findViewById(R.id.input_spinner_family)).getSelectedItem().toString();
        String code = input_code.getText().toString();
        String price_val = input_price.getText().toString();

        float price = 0;

        if(price_val.isEmpty()) {
            input_price.setError("Price must contain a number!");
            insert = false;
        } else {
            price = Float.parseFloat(price_val);
            if(price <= 0) {
                input_price.setError("Pice must be higher than 0");
                insert = false;
            } else if(price > 99999) {
                input_price.setError("Pice must not be higher than 99999");
                insert = false;
            }
        }

        if(desc.isEmpty()) {
            input_desc.setError("Description must not be empty");
            insert = false;
        }

        if(!editMode) {
            if (code.isEmpty()) {
                input_code.setError("Item code must not be empty");
                insert = false;
            } else {
                if (!this.checkUniqueCode(code)) {
                    input_code.setError("Item code must be unique");
                    insert = false;
                }
            }
        }

        if(!insert) return null;

        NumberFormat df = NumberFormat.getCurrencyInstance();
        price = Float.parseFloat(df.format(price).substring(1).replaceAll(",", ""));

        if(family.equals("---")) family = null;

        Bundle res = new Bundle();
        res.putString("code", code);
        res.putString("desc", desc);
        res.putFloat("price", price);
        res.putInt("stock", 0);
        res.putString("family", family);
        return res;
    }

    private boolean checkUniqueCode(String code) {
        String[] codes = this.db.getFullStoreCodes();
        for(String it : codes) {
            if(it.equals(code)) return false;
        }
        return true;
    }

    private void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_manager, menu);
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