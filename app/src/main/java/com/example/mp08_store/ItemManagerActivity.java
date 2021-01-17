package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ItemManagerActivity extends AppCompatActivity {

    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manager);
        this.setTitle("Item manager");

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
        String code = ((EditText)findViewById(R.id.input_code)).getText().toString();
        String desc = ((EditText)findViewById(R.id.input_desc)).getText().toString();
        float price = Float.parseFloat(((EditText)findViewById(R.id.input_price)).getText().toString());
        int stock = Integer.parseInt(((EditText)findViewById(R.id.input_stock)).getText().toString());
        int family = ((Spinner)findViewById(R.id.input_spinner_family)).getId();

        Toast.makeText(this, code + ", " + desc + ", " + price + ", " + stock + ", " + family, Toast.LENGTH_SHORT).show();
    }

    public void editItem() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }
}