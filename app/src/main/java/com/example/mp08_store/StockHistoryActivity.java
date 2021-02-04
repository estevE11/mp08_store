package com.example.mp08_store;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.mp08_store.db.DBDatasource;

import java.text.NumberFormat;
import java.util.ArrayList;

import javax.sql.DataSource;

public class StockHistoryActivity extends AppCompatActivity {

    private DBDatasource db;
    private StockHistoryItemListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        final Bundle b = this.getIntent().getExtras();

        this.setTitle("Store stock movement history");

        this.db = new DBDatasource(this);

        Cursor c = this.db.getStockChangeHistoryByIds(b.getStringArrayList("selected"));
        this.listAdapter = new StockHistoryItemListAdapter(this, c);
        ListView lst = (ListView) this.findViewById(R.id.lv_stock_history);
        lst.setAdapter(this.listAdapter);
        /*lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });*/
    }
}

class StockHistoryItemListAdapter extends SimpleCursorAdapter {
    private Context conxtext;

    public StockHistoryItemListAdapter(Context context, Cursor c) {
        super(context, R.layout.layout_stock_history_lv_item, c,
                new String[]{"code", "day", "quantity"}, // from
                new int[]{R.id.text_code_sh, R.id.text_date, R.id.text_quantity},
                1); // to
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = super.getView(position, convertView, parent);

        Cursor c = (Cursor) getItem(position);

        char type = c.getString(4).charAt(0);
        item.findViewById(R.id.rect_type).setBackgroundColor(Color.parseColor(type == 'E' ? "#00bb00" : "#bb0000"));

        return(item);
    }
}