package com.example.mp08_store;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DBDatasource db;
    private ItemListAdapter listAdapter;

    private String searchFilter = "";
    private boolean stockFilter = false;
    private boolean inSelection = false;
    private ArrayList<String> selected = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Megastore");
        setContentView(R.layout.activity_main);

        this.db = new DBDatasource(this);

        Cursor c = this.db.getFilteredStore("", false);
        this.listAdapter = new ItemListAdapter(this, c);
        ListView lst = (ListView) this.findViewById(R.id.lv_items);
        lst.setAdapter(this.listAdapter);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!inSelection) openEditItemActivity((int) id);
                else {
                    toggleSelected((int) id);
                    if (selected.size() < 1) {
                        inSelection = false;
                        invalidateOptionsMenu();
                    }
                }
            }
        });

        lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!inSelection) {
                    selected = new ArrayList<String>();
                    toggleSelected((int) id);
                    inSelection = true;
                    invalidateOptionsMenu();
                }
                return true;
            }
        });

        findViewById(R.id.img_btn_filter_remove).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.btn_stock_in).setOnClickListener(this);
        findViewById(R.id.btn_stock_out).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void load() {
        String filter_info = "All";
        boolean hasSearch = !this.searchFilter.isEmpty();
        if (hasSearch || this.stockFilter) {
            filter_info = (hasSearch ? '"' + this.searchFilter + '"' : "") + (this.stockFilter ? ((hasSearch ? " and " : "") + "Out of stock") : "");
        }
        ((TextView) this.findViewById(R.id.text_filters)).setText(filter_info);
        this.load(this.searchFilter, this.stockFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void load(String desc, boolean stock) {
        Cursor c = this.db.getFilteredStore(desc, stock);
        Bundle b = new Bundle();
        b.putStringArrayList("selected", this.selected);
        c.setExtras(b);

        findViewById(R.id.img_btn_filter_remove).setVisibility((!desc.isEmpty() || stock) ? View.VISIBLE : View.INVISIBLE);

        this.listAdapter.changeCursor(c);
        this.listAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void toggleSelected(int id) {
        String str_id = "id" + id;
        for (int i = 0; i < this.selected.size(); i++) {
            String it = this.selected.get(i);
            if (it.equals(str_id)) {
                this.selected.remove(i);
                this.load();
                return;
            }
        }
        this.selected.add(str_id);
        this.load();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (this.inSelection) {
            menu.findItem(R.id.button_delete).setVisible(true);
        } else {
            menu.findItem(R.id.button_delete).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_filter:
                stockFilter = !stockFilter;
                this.turnOffSelectedMode();
                load();
                return false;
            case R.id.button_search:
                this.openDialogSearch();
                this.turnOffSelectedMode();
                return false;
            case R.id.button_filter_rm:
                this.openStockHistotyActivity();
                return false;
            case R.id.button_delete:
                this.openDeleteAlert();
                return false;
            case R.id.button_weather:
                this.openWeatherActivity();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCreateItemActivity() {
        Intent intent = new Intent(getApplicationContext(), ItemManagerActivity.class);
        startActivity(intent);
    }

    private void openWeatherActivity() {
        Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
        startActivity(intent);
    }

    private void openEditItemActivity(int id) {
        Bundle b = new Bundle();
        b.putInt("_id", id);
        Intent intent = new Intent(getApplicationContext(), ItemManagerActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openHistoryManagerActivity(int type) {
        Bundle b = new Bundle();
        b.putInt("type", type);
        Intent intent = new Intent(getApplicationContext(), ItemHistoryManager.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    private void openStockHistotyActivity() {
        Intent intent = new Intent(getApplicationContext(), StockHistoryActivity.class);
        Bundle b = new Bundle();
        b.putStringArrayList("selected", this.selected);
        intent.putExtras(b);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void turnOffSelectedMode() {
        this.inSelection = false;
        this.selected = new ArrayList<String>();
        invalidateOptionsMenu();
        this.load();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void deleteSelectedItems() {
        for (String it : this.selected) {
            int id = Integer.parseInt(it.substring(2));
            this.db.deleteItem(id);
        }
        this.turnOffSelectedMode();
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
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int whichButton) {
                String val = "";
                try {
                    val = edtValor.getText().toString();
                    searchFilter = val;
                    load();
                } catch (Exception e) {
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

    private void openDeleteAlert() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteSelectedItems();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("All selected items will be deleted").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @SuppressLint("RestrictedApi")
    private void toggleActionButtonMenu() {
        FloatingActionButton btn_create = (FloatingActionButton) findViewById(R.id.btn_create);
        if(btn_create.getVisibility() == View.VISIBLE) {
            this.setActionButtonMenuVisibility(View.INVISIBLE);
        } else {
            this.setActionButtonMenuVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("RestrictedApi")
    private void setActionButtonMenuVisibility(int val) {
        FloatingActionButton btn_create = (FloatingActionButton) findViewById(R.id.btn_create);
        FloatingActionButton btn_stock_in = (FloatingActionButton) findViewById(R.id.btn_stock_in);
        FloatingActionButton btn_stock_out = (FloatingActionButton) findViewById(R.id.btn_stock_out);
        TextView label_create = (TextView) findViewById(R.id.label_create);
        TextView label_stock_in = (TextView) findViewById(R.id.label_stock_in);
        TextView label_stock_out = (TextView) findViewById(R.id.label_stock_out);
        btn_create.setVisibility(val);
        btn_stock_in.setVisibility(val);
        btn_stock_out.setVisibility(val);
        label_create.setVisibility(val);
        label_stock_in.setVisibility(val);
        label_stock_out.setVisibility(val);
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_filter_remove:
                this.stockFilter = false;
                this.searchFilter = "";
                this.load();
                break;
            case R.id.btn_add:
                this.toggleActionButtonMenu();
                break;
            case R.id.btn_create:
                this.openCreateItemActivity();
                this.toggleActionButtonMenu();
                break;
            case R.id.btn_stock_in:
                this.openHistoryManagerActivity(0);
                this.toggleActionButtonMenu();
                break;
            case R.id.btn_stock_out:
                this.openHistoryManagerActivity(1);
                this.toggleActionButtonMenu();
                break;
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

        NumberFormat df = NumberFormat.getCurrencyInstance();
        float final_price = Float.parseFloat(df.format(price*1.21).substring(1).replaceAll(",", ""));
        text_price_final.setText(this.formatPrice(final_price) + "€");

        TextView text_price = (TextView)item.findViewById(R.id.text_price);
        float formatted_price = Float.parseFloat(df.format(price).substring(1).replaceAll(",", ""));
        text_price.setText(this.formatPrice(formatted_price) + "€");

        int id = c.getInt(0);
        boolean selected = false;
        Bundle b = c.getExtras();
        ArrayList<String> selectedList = b.getStringArrayList("selected");
        if(selectedList != null) {
            for (String it : selectedList) {
                if (it.equals("id" + id)) {
                    selected = true;
                    break;
                }
            }
        }

        if(selected) {
            item.setBackgroundColor(Color.parseColor("#ccccff"));
        } else if(c.getFloat(5) > 0) {
            item.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            item.setBackgroundColor(Color.parseColor("#ffcccc"));
        }
        return(item);
    }

    private String formatPrice(float price) {
        String[] str_price = String.valueOf(price).split("\\.");
        if(str_price.length < 2) return str_price[0] + ".00";
        else return price + (str_price[1].length() == 1 ? "0" : "");
    }
}
