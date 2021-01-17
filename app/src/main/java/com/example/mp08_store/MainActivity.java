package com.example.mp08_store;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp08_store.db.DBDatasource;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DBDatasource db;
    private ItemListAdapter listAdapter;

    private String searchFilter = "";
    private boolean stockFilter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Megastore");
        setContentView(R.layout.activity_main);

        this.db = new DBDatasource(this);

        Cursor c = this.db.getFilteredStore("", false);
        this.listAdapter = new ItemListAdapter(this, c);
        ListView lst = (ListView)this.findViewById(R.id.lv_items);
        lst.setAdapter(this.listAdapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditItemActivity(position);
            }
        });

        findViewById(R.id.img_btn_filter_remove).setOnClickListener(this);
    }

    private void load() {
        String filter_info = "All";
        boolean hasSearch = !this.searchFilter.isEmpty();
        if(hasSearch || this.stockFilter) {
            filter_info = (hasSearch ? '"' + this.searchFilter + '"' : "") + (this.stockFilter ? ((hasSearch ? " and " : "") + "Out of stock") : "");
        }
        ((TextView)this.findViewById(R.id.text_filters)).setText(filter_info);
        this.load(this.searchFilter, this.stockFilter);
    }

    private void load(String desc, boolean stock) {
        Cursor c = this.db.getFilteredStore(desc, stock);
        this.listAdapter.changeCursor(c);
        this.listAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_filter:
                stockFilter = !stockFilter;
                load();
                return false;
            case R.id.button_search:
                this.openDialogSearch();
                return false;
            case R.id.button_save:
                this.openCreateItemActivity();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCreateItemActivity() {
        Intent intent = new Intent(getApplicationContext(), ItemManagerActivity.class);
        startActivity(intent);
    }

    private void openEditItemActivity(int id) {
        Bundle b = new Bundle();
        b.putInt("_id", id);
        Intent intent = new Intent(getApplicationContext(), ItemManagerActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openDialogSearch() {
        AlertDialog ad;

        ad = new AlertDialog.Builder(this).create();
        ad.setTitle("");
        ad.setMessage("Search filter");

        // Ahora forzamos que aparezca el editText
        final EditText edtValor = new EditText(this);
        edtValor.setText(this.searchFilter);
        ad.setView(edtValor);

        ad.setButton(AlertDialog.BUTTON_POSITIVE, "Insert", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String val = "";
                try {
                    val = edtValor.getText().toString();
                    searchFilter = val;
                    load();
                } catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "Input needs to be a number!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // no fem res.
            }
        });
        ad.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.img_btn_filter_remove) {
            this.stockFilter = false;
            this.searchFilter = "";
            this.load();
        }
    }
}

class ItemListAdapter extends SimpleCursorAdapter {
    private Context conxtext;

    public ItemListAdapter(Context context, Cursor c) {
        super(context, R.layout.layout_main_lv_item, c,
                new String[]{"code", "description", "family", "price", "stock"}, // from
                new int[]{R.id.text_code, R.id.text_desc, R.id.text_family, R.id.text_price_final, R.id.text_stock},
                1); // to
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = super.getView(position, convertView, parent);

        Cursor c = (Cursor) getItem(position);

        float price = Float.parseFloat(c.getString(4));
        TextView text_price_final = (TextView)item.findViewById(R.id.text_price_final);

        DecimalFormat df = new DecimalFormat("#.##");
        float final_price = Float.parseFloat(df.format(price*1.21));
        text_price_final.setText(final_price + "€");

        TextView text_price = (TextView)item.findViewById(R.id.text_price);
        text_price.setText(price + "€");

        if(c.getFloat(5) > 0) {
            item.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            item.setBackgroundColor(Color.parseColor("#ffcccc"));
        }
        return(item);
    }
}
