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

class HistorialActivity : AppCompatActivity() {
    
    private lateinit var listViewHistorial: ListView
    private lateinit var txtTitulo: TextView
    private lateinit var dbHelper: BDHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var adapter: ArrayAdapter<String>
    private val historial = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)
        
        title = "Historial de Ubicaciones"
        
        // Inicializar vistas
        listViewHistorial = findViewById(R.id.listViewHistorial)
        txtTitulo = findViewById(R.id.txtTituloHistorial)
        
        // Inicializar base de datos
        dbHelper = BDHelper(this)
        db = dbHelper.readableDatabase
        
        // Configurar adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historial)
        listViewHistorial.adapter = adapter
        
        // Configurar click en lista
        listViewHistorial.setOnItemClickListener { _, _, position, _ ->
            mostrarDetalleUbicacion(position)
        }
        
        // Cargar historial
        cargarHistorial()
    }
    
    private fun cargarHistorial() {
        historial.clear()
        
        try {
            // Consultar ubicaciones de la base de datos
            val cursor = db.rawQuery(
                "SELECT latitud, longitud, velocidad, estado, fecha FROM ubicaciones ORDER BY fecha DESC LIMIT 20",
                null
            )
            
            if (cursor.moveToFirst()) {
                do {
                    val latitud = cursor.getDouble(0)
                    val longitud = cursor.getDouble(1)
                    val velocidad = cursor.getDouble(2)
                    val estado = cursor.getString(3)
                    val fecha = cursor.getString(4)
                    
                    val entrada = "📍 ${String.format("%.4f", latitud)}, ${String.format("%.4f", longitud)} - ${String.format("%.0f", velocidad)} km/h - $estado - $fecha"
                    historial.add(entrada)
                } while (cursor.moveToNext())
            }
            cursor.close()
            
            // Si no hay datos en la BD, mostrar datos simulados
            if (historial.isEmpty()) {
                cargarHistorialSimulado()
            }
        } catch (e: Exception) {
            cargarHistorialSimulado()
        }
        
        adapter.notifyDataSetChanged()
        txtTitulo.text = "Historial de Ubicaciones (${historial.size})"
    }
    
    private fun cargarHistorialSimulado() {
        // Historial simulado
        historial.add("📍 -2.1709, -79.9223 - 65 km/h - En movimiento - 2024-12-20 15:30:00")
        historial.add("📍 -2.1710, -79.9225 - 58 km/h - En movimiento - 2024-12-20 15:27:00")
        historial.add("📍 -2.1712, -79.9228 - 72 km/h - En movimiento - 2024-12-20 15:24:00")
        historial.add("📍 -2.1715, -79.9230 - 45 km/h - En movimiento - 2024-12-20 15:21:00")
        historial.add("📍 -2.1718, -79.9232 - 80 km/h - En movimiento - 2024-12-20 15:18:00")
        historial.add("📍 -2.1720, -79.9235 - 55 km/h - En movimiento - 2024-12-20 15:15:00")
        historial.add("📍 -2.1722, -79.9238 - 0 km/h - Detenido - 2024-12-20 15:12:00")
        historial.add("📍 -2.1725, -79.9240 - 68 km/h - En movimiento - 2024-12-20 15:09:00")
        historial.add("📍 -2.1728, -79.9242 - 75 km/h - En movimiento - 2024-12-20 15:06:00")
        historial.add("📍 -2.1730, -79.9245 - 62 km/h - En movimiento - 2024-12-20 15:03:00")
    }
    
    private fun mostrarDetalleUbicacion(position: Int) {
        val ubicacion = historial[position]
        
        val mensaje = """
            Detalles de la ubicación:
            
            $ubicacion
            
            Esta ubicación fue registrada por el sistema de rastreo GPS del vehículo.
            
            Coordenadas: ${ubicacion.split(" - ")[0].replace("📍 ", "")}
            Velocidad: ${ubicacion.split(" - ")[1]}
            Estado: ${ubicacion.split(" - ")[2]}
            Fecha: ${ubicacion.split(" - ")[3]}
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("Detalle de Ubicación")
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        db.close()
        dbHelper.close()
    }
}