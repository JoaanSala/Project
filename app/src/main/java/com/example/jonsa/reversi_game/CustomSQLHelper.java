package com.example.jonsa.reversi_game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomSQLHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE LOG " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " alias TEXT, " +
            " mida INT, " +
            " date DATETIME, " +
            " timed BOOL, " +
            " black INT, " +
            " white INT, " +
            " time_left INT, " +
            " result TEXT )";

    public CustomSQLHelper(Context contexto, String nombre,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creacion de la tabla
        db.execSQL(sqlCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior,
                          int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS LOG");
        db.execSQL(sqlCreate);
    }
}
