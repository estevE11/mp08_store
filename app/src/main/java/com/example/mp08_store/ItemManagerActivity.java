package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp08_store.db.DBDatasource;

import java.text.DecimalFormat;

public class ItemManagerActivity extends AppCompatActivity {

    private DBDatasource db;

    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manager);
        this.setTitle("Item manager");

        this.db = new DBDatasource(this);

        final Bundle b = this.getIntent().getExtras();

        this.editMode = true;
        int itemId = 0;
        try {
            itemId = b.getInt("_id");
        } catch (Exception e) {
            this.editMode = false;
        }

        ((Button) this.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        if(this.editMode) editItem();
        else createItem();
    }

    public void createItem() {
        Bundle values = this.getFormAndValidate();
        if(values == null) return;

        this.db.addItem(values.getString("code"), values.getString("desc"), values.getString("family"), values.getFloat("price"), values.getInt("stock"));
        this.openMainActivity();
    }

    public void editItem() {

    }

    private Bundle getFormAndValidate() {
        boolean insert = true;

        EditText input_code = ((EditText)findViewById(R.id.input_code));
        EditText input_desc = ((EditText)findViewById(R.id.input_desc));
        EditText input_stock = ((EditText)findViewById(R.id.input_stock));
        EditText input_price = ((EditText)findViewById(R.id.input_price));

        String desc = input_desc.getText().toString();
        String family = ((Spinner)findViewById(R.id.input_spinner_family)).getSelectedItem().toString();


        String price_val = input_price.getText().toString();
        if(price_val.isEmpty()) {
            input_price.setError("Price must contain a number!");
            insert = false;
        }

        String stock_val = input_stock.getText().toString();
        if(stock_val.isEmpty()) {
            input_stock.setError("Price must contain a number!");
            insert = false;
        }

        if(desc.isEmpty()) {
            input_desc.setError("Description must not be empty");
            insert = false;
        }

        String code = input_code.getText().toString();

        if(code.isEmpty()) {
            input_code.setError("Item code must not be empty");
            insert = false;
        } else {
            if(!this.checkUniqueCode(code)) {
                input_code.setError("Item code must be unique");
                insert = false;
            }
        }


        if(!insert) return null;
        int stock = Integer.parseInt(stock_val);
        float price = Float.parseFloat(price_val);

        if(stock < 0) {
            ((EditText)findViewById(R.id.input_stock)).setError("Stock must be 0 or higher");
            insert = false;
        }

        DecimalFormat df = new DecimalFormat("#.##");
        price = Float.parseFloat(df.format(price));

        if(family.equals("---")) family = null;

        if(!insert) return null;
        Bundle res = new Bundle();
        res.putString("code", code);
        res.putString("desc", desc);
        res.putFloat("price", price);
        res.putInt("stock", stock);
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
}