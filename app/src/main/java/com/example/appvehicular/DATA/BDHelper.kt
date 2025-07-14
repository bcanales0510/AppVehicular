package com.example.appvehicular.DATA
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BDHelper (context: Context):SQLiteOpenHelper (
    context, DATABASE_NAME, null, DATABASE_VERSION
){
    companion object {
        const val DATABASE_NAME = "appvehicular.db"
        const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE IF NOT EXISTS alertas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_vehiculo INTEGER,
            tipo TEXT,
            descripcion TEXT,
            fecha TEXT,
            latitud REAL,
            longitud REAL
        )
    """.trimIndent())

        db.execSQL(
            """
        CREATE TABLE vehiculos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            placa TEXT NOT NULL UNIQUE,
            marca TEXT,
            modelo TEXT
        );
        """
        )

        db.execSQL(
            """
        CREATE TABLE usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario TEXT NOT NULL UNIQUE,
            clave TEXT NOT NULL,
            id_vehiculo INTEGER,
            FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id)
        );
        """
        )

        db.execSQL(
            """
        CREATE TABLE ubicaciones (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_vehiculo INTEGER,
            latitud REAL,
            longitud REAL,
            velocidad REAL,
            estado TEXT,
            fecha TEXT,
            FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id)
        );
        """
        )

        db.execSQL(
            """
        CREATE TABLE mantenimientos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_vehiculo INTEGER,
            descripcion TEXT,
            fecha TEXT,
            kilometraje INTEGER,
            FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id)
        );
        """
        )
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS alertas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_vehiculo INTEGER,
                tipo TEXT,
                descripcion TEXT,
                fecha TEXT,
                latitud REAL,
                longitud REAL
            );
        """.trimIndent())
        }
    }


}
