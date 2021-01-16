package com.example.mp08_store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp08_store.db.DBDatasource;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private DBDatasource db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Buenas");
        setContentView(R.layout.activity_main);

        this.db = new DBDatasource(this);

        long id = this.db.addItem("B02", "Boligrafo Bic", "ALTRES", .5f, -1);

        Cursor c = this.db.getFilteredStore("", false);
        ItemListAdapter listAdapter = new ItemListAdapter(this, c);
        ListView lst = (ListView)this.findViewById(R.id.lv_items);
        lst.setAdapter(listAdapter);
    }

    private void load(String desc, boolean stock) {
        Cursor c = this.db.getFilteredStore(desc, stock);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

class ItemListAdapter extends SimpleCursorAdapter {
    private Context conxtext;

    public ItemListAdapter(Context context, Cursor c) {
        super(context, R.layout.layout_main_lv_item, c,
                new String[]{"code", "description", "family", "price", "stock"}, // from
                new int[]{R.id.text_code, R.id.text_desc, R.id.text_family, R.id.text_price, R.id.text_stock},
                1); // to
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = super.getView(position, convertView, parent);

        Cursor c = (Cursor) getItem(position);

        TextView edt = (TextView)item.findViewById(R.id.text_price);
        edt.setText(c.getString(4) + "€");

        item.setBackgroundColor(Color.parseColor("#ffffff"));

        return(item);
    }
}
