package com.example.mp08_store;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.mp08_store.db.DBDatasource;
import com.example.mp08_store.utils.Date;

import java.util.ArrayList;

public class StockHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private DBDatasource db;
    private StockHistoryItemListAdapter listAdapter;
    private ArrayList<String> selected;
    private String orderBy = "desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        final Bundle b = this.getIntent().getExtras();

        this.setTitle("Stock movement history");

        this.db = new DBDatasource(this);

        this.selected = b.getStringArrayList("selected");
        Cursor c = this.db.getStockChangeHistoryByIds(this.selected, null, null, this.orderBy);
        this.listAdapter = new StockHistoryItemListAdapter(this, c);
        ListView lst = (ListView) this.findViewById(R.id.lv_stock_history);
        lst.setAdapter(this.listAdapter);
        /*lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });*/

        findViewById(R.id.input_date_start).setOnClickListener(this);
        findViewById(R.id.input_date_end).setOnClickListener(this);
    }

    private void load() {
        Date start_date = Date.parse(((EditText)findViewById(R.id.input_date_start)).getText().toString(), null);
        Date end_date = Date.parse(((EditText)findViewById(R.id.input_date_end)).getText().toString(), null);
        if(!start_date.isValid()) {
            start_date = null;
            end_date = null;
        } else if(!end_date.isValid()) {
            end_date = null;
        }

        Cursor c = this.db.getStockChangeHistoryByIds(this.selected, start_date, end_date, this.orderBy);
        this.listAdapter.changeCursor(c);
        this.listAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.input_date_start:
                this.openDatePicker(R.id.input_date_start);
                break;
            case R.id.input_date_end:
                this.openDatePicker(R.id.input_date_end);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openDatePicker(int id) {
        final EditText dateInput = (EditText) findViewById(id);
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateInput.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            load();
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_filter_rm:
                ((EditText)findViewById(R.id.input_date_start)).setText("--/--/----");
                ((EditText)findViewById(R.id.input_date_end)).setText("--/--/----");
                this.load();
                return false;
            case R.id.button_filter_date:
                if(this.orderBy.equals("desc")) this.orderBy = "asc";
                else if(this.orderBy.equals("asc")) this.orderBy = "desc";
                this.load();
                return false;
        }
        return super.onOptionsItemSelected(item);
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

        Date date = Date.parseSql(c.getString(2), null);
        ((TextView)item.findViewById(R.id.text_date)).setText(date.getDate(null));

        char type = c.getString(4).charAt(0);
        item.findViewById(R.id.rect_type).setBackgroundColor(Color.parseColor(type == 'E' ? "#00bb00" : "#bb0000"));

        return(item);
    }
}