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
        return dbR.query(STORE_TABLE_NAME, new String[]{STORE_CODE,STORE_DESCRIPTION,STORE_FAMILY,STORE_STOCK,STORE_PRICE},
                null, null,
                null, null, STORE_CODE);
    }

    public Cursor getFilteredStore(boolean desc, boolean inStock) {
        String optional = "";
        if(desc || inStock) {
            optional = "WHERE ";
            optional += (desc ? "description IS NOT NULL" : "");
            optional += (desc && inStock ? " OR " : "") + (inStock ? "stock < 1" : "");
        }
        return dbR.rawQuery("select code, description, family, price, stock from store " + (optional.length() > 0 ? optional : ""),null);
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
/*
    public void taskUpdate(long id, String title, String description, int level) {
        // Modifiquem els valors de las tasca amb clau primària "id"
        ContentValues values = new ContentValues();
        values.put(TODOLIST_TITLE, title);
        values.put(TODOLIST_DESCRIPCION, description);
        values.put(TODOLIST_LEVEL,level);
        values.put(TODOLIST_DONE,0);  // Forcem 0 pq si s'està creant la tasca no pot estar finalitzada

        dbW.update(table_TODOLIST,values, TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskDelete(long id) {
        // Eliminem la task amb clau primària "id"
        dbW.delete(table_TODOLIST,TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskPending(long id) {
        // Modifiquem al estat de pendent la task indicada
        ContentValues values = new ContentValues();
        values.put(TODOLIST_DONE,0);

        dbW.update(table_TODOLIST,values, TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskCompleted(long id) {
        // Modifiquem al estat de pendent la task indicada
        ContentValues values = new ContentValues();
        values.put(TODOLIST_DONE,1);

        dbW.update(table_TODOLIST,values, TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }*/

}
