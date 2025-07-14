package com.example.appvehicular.uiapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.DATA.BDHelper
import com.example.appvehicular.R

import com.example.appvehicular.model.Ubicacion
import com.example.appvehicular.uiapp.UbicacionAdapter

class HistorialActivity : AppCompatActivity() {

    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var listView: ListView
    private lateinit var txtTitulo: TextView
    private var idVehiculo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        dbHelper = BDHelper(this)
        db = dbHelper.readableDatabase

        idVehiculo = intent.getIntExtra("id_vehiculo", 0)

        listView = findViewById(R.id.listViewHistorial)
        txtTitulo = findViewById(R.id.txtTituloHistorial)

        cargarHistorial()
    }

    private fun cargarHistorial() {
        val historial = mutableListOf<Ubicacion>()

        val cursor = db.rawQuery(
            "SELECT latitud, longitud, velocidad, estado, fecha FROM ubicaciones WHERE id_vehiculo = ? ORDER BY fecha DESC",
            arrayOf(idVehiculo.toString())
        )

        while (cursor.moveToNext()) {
            historial.add(
                Ubicacion(
                    latitud = cursor.getDouble(0),
                    longitud = cursor.getDouble(1),
                    velocidad = cursor.getDouble(2),
                    estado = cursor.getString(3),
                    fecha = cursor.getString(4)
                )
            )
        }
        cursor.close()

        listView.adapter = UbicacionAdapter(this, historial)
        txtTitulo.text = "Historial de ubicaciones (${historial.size})"
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
        dbHelper.close()
    }
}