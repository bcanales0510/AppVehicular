package com.example.appvehicular.uiapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.DATA.BDHelper
import com.example.appvehicular.R
import com.example.appvehicular.model.Alerta


class AlertasActivity : AppCompatActivity() {
    
    private lateinit var listViewAlertas: ListView
    private lateinit var txtTitulo: TextView
    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alertas)
        
        title = "Alertas del Vehículo"
        
        // Inicializar vistas
        listViewAlertas = findViewById(R.id.listViewAlertas)
        txtTitulo = findViewById(R.id.txtTituloAlertas)
        
        // Inicializar base de datos
        dbHelper = BDHelper(this)
        db = dbHelper.readableDatabase
        
        // Cargar alertas
        cargarAlertas()
    }

    private fun cargarAlertas() {
        val alertas = mutableListOf<Alerta>()

        val cursor = db.rawQuery(
            "SELECT id_vehiculo, tipo, descripcion, fecha, latitud, longitud FROM alertas WHERE id_vehiculo = ?",
            arrayOf("1")
        )

        while (cursor.moveToNext()) {
            alertas.add(
                Alerta(
                    idVehiculo = cursor.getInt(0),
                    tipo = cursor.getString(0),
                    descripcion = cursor.getString(1),
                    fecha = cursor.getString(2),
                    latitud = cursor.getDouble(3),
                    longitud = cursor.getDouble(4)
                )
            )
        }

        cursor.close()

        val adapter = AlertaAdapter(this, alertas)
        listViewAlertas.adapter = adapter

        txtTitulo.text = "Alertas registradas (${alertas.size})"

        listViewAlertas.setOnItemClickListener { _, _, position, _ ->
            val alertaSeleccionada = alertas[position]
            mostrarDetalleAlerta(alertaSeleccionada)
        }
    }


    private fun mostrarDetalleAlerta(alerta: Alerta) {
        val mensaje = """
        Tipo: ${alerta.tipo}
        Descripción: ${alerta.descripcion}
        Fecha: ${alerta.fecha}
        Ubicación: ${alerta.latitud}, ${alerta.longitud}
    """.trimIndent()

        android.app.AlertDialog.Builder(this)
            .setTitle("Detalle de la Alerta")
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        db.close()
        dbHelper.close()
    }
}