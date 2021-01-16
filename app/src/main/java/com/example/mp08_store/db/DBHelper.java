package com.example.mp08_store.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {
    // database version
    private static final int database_VERSION = 1;

    // database name
    private static final String database_NAME = "store";

    public DBHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOLIST =
                "CREATE TABLE store ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "code TEXT UNIQUE NOT NULL," +
                        "description TEXT NOT NULL," +
                        "family TEXT CHECK(family='SOFTWARE' OR family='HARDWARE' OR family='ALTRES')," +
                        "price FLOAT(1000000, 2) NOT NULL," +
                        "stock INTEGER NOT NULL DEFAULT '0')";

        db.execSQL(CREATE_TODOLIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // De moment no fem res

    }
}
