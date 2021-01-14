package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mp08_store.db.DBDatasource;

public class MainActivity extends AppCompatActivity {

    private DBDatasource db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DBDatasource(this);

        long id= this.db.addItem("T01", "Tornillo", "HARDWARE", 10f, 10);
        Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();

        Cursor c = this.db.getFullStore();

        int count = c.getCount();

        Toast.makeText(this, "" + count, Toast.LENGTH_SHORT).show();
    }
}
