package com.example.mp08_store.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

public class DBDatasource {
    public static final String STORE_TABLE_NAME = "store";
    public static final String STORE_CODE = "code";
    public static final String STORE_DESCRIPTION = "description";
    public static final String STORE_FAMILY = "family";
    public static final String STORE_PRICE = "price";
    public static final String STORE_STOCK = "stock";

    private DBHelper dbHelper;
    private SQLiteDatabase dbW, dbR;

    // CONSTRUCTOR
    public DBDatasource(Context ctx) {
        // En el constructor directament obro la comunicació amb la base de dades
        this.dbHelper = new DBHelper(ctx);

        // amés també construeixo dos databases un per llegir i l'altre per alterar
        this.open();
    }

    // DESTRUCTOR
    protected void finalize () {
        // Cerramos los databases
        this.dbW.close();
        this.dbR.close();
    }

    private void open() {
        this.dbW = this.dbHelper.getWritableDatabase();
        this.dbR = this.dbHelper.getReadableDatabase();
    }

    // ******************
    // Funcions que retornen cursors de todoList
    // ******************
    public Cursor getFullStore() {
        // Retorem totes les tasques
        return dbR.query(STORE_TABLE_NAME, new String[]{"_id", STORE_CODE,STORE_DESCRIPTION,STORE_FAMILY,STORE_STOCK,STORE_PRICE},
                null, null,
                null, null, STORE_CODE);
    }

    public String[] getFullStoreCodes() {
        // Retorem totes les tasques
        Cursor c = dbR.query(STORE_TABLE_NAME, new String[]{"_id", STORE_CODE},
                null, null,
                null, null, STORE_CODE);

        c.moveToFirst();

        String[] cursors = new String[c.getCount()];
        for(int i = 0; i < cursors.length; i++) {
            cursors[i] = c.getString(1);
            c.moveToNext();
        }

        return cursors;
    }

    public Cursor getFilteredStore(String search, boolean stock) {
        String extra = "";
        boolean desc = !search.isEmpty();
        if(!search.isEmpty() || stock) {
            extra = " where " + (desc ? "description like '%" + search + "%'" : "") + (stock ? ( desc? " and " : "") + "stock < 1" : "");
        }
        return dbR.rawQuery("select _id, code, description, family, price, stock from store" + extra,null);
    }

    public Cursor getItem(int id) {
        /*Cursor c = dbR.query(STORE_TABLE_NAME, new String[]{"_id", STORE_CODE,STORE_DESCRIPTION,STORE_FAMILY,STORE_STOCK,STORE_PRICE},
                "_id=?", new String[]{String.valueOf(id)},
                null, null, STORE_CODE);*/
        return dbR.rawQuery("select _id, code, description, family, price, stock from store where _id=" + (id),null);
    }

    public int getStockById(int id) {
        Cursor c = dbR.rawQuery("select stock from store where _id=" + (id),null);
        c.moveToFirst();
        int stock = c.getInt(0);
        return stock;
    }

    public int getStockByCode(String code) {
        Cursor c = dbR.rawQuery("select stock from store where code='" + (code) + "'",null);
        c.moveToFirst();
        int stock = c.getInt(0);
        return stock;
    }

    public Cursor getStockChangeHistory() {
        return dbR.query("movements", new String[]{"_id", "code","day","quantity","type"},
                null, null,
                null, null, STORE_CODE);
    }

    public Cursor getStockChangeHistoryByCode(String code) {
        return dbR.rawQuery("select * from movements where code='" + code + "'",null);
    }

    // ******************
    // Funciones de manipualación de datos
    // ******************

    public long addItem(String code,  String desc, String family, float price, int stock) {
        // Creem una nova tasca i retornem el id crear per si el necessiten
        ContentValues values = new ContentValues();
        values.put("code", code);
        values.put("description", desc);
        values.put("family", family);
        values.put("price", price);
        values.put("stock", stock);

        return dbW.insert("store",null,values);
    }

    public void updateItem(int id, String desc, String family, float price, int stock) {
        // Creem una nova tasca i retornem el id crear per si el necessiten
        ContentValues values = new ContentValues();
        values.put("description", desc);
        values.put("family", family);
        values.put("price", price);
        values.put("stock", stock);

        dbW.update("store", values, "_id=?", new String[] { String.valueOf(id) });
    }

    public void deleteItem(int id) {
        dbW.delete("store", "_id=?", new String[] { String.valueOf(id) });
    }

    public long insertStockChange(String code, String date, int stockDiff, char type) {
        ContentValues values = new ContentValues();
        values.put("code", code);
        values.put("day", date);
        values.put("quantity", date);
        values.put("type", String.valueOf(type));

        long res = dbW.insert("movements",null,values);

        this.changeStock(code, type == 'E' ? stockDiff : stockDiff*-1);

        return res;
    }

    public void changeStock(String code, int stockDiff) {
        int currStock = this.getStockByCode(code);
        ContentValues values = new ContentValues();
        values.put("stock", currStock + stockDiff);

        dbW.update("store", values, "code=?", new String[] { code });
    }
}
