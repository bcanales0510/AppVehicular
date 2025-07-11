package com.example.appvehicular.uiapp

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appvehicular.DATA.BDHelper
import com.example.appvehicular.R

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
        // Simular alertas (en una aplicación real, estas vendrían de la base de datos)
        val alertas = mutableListOf<String>()
        
        // Alertas simuladas
        alertas.add("🚨 Exceso de velocidad detectado - 85 km/h")
        alertas.add("⚠️ Bajo nivel de combustible - 15% restante")
        alertas.add("🔧 Mantenimiento próximo - Cambio de aceite en 500 km")
        alertas.add("🚗 Vehículo fuera de zona permitida")
        alertas.add("⏰ Tiempo de conducción excesivo - 4 horas continuas")
        alertas.add("🛑 Frenado brusco detectado")
        alertas.add("📱 Uso de teléfono detectado durante la conducción")
        alertas.add("🌡️ Temperatura del motor elevada")
        
        // Configurar adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, alertas)
        listViewAlertas.adapter = adapter
        
        // Actualizar título
        txtTitulo.text = "Alertas Activas (${alertas.size})"
        
        // Configurar click listener
        listViewAlertas.setOnItemClickListener { _, _, position, _ ->
            val alertaSeleccionada = alertas[position]
            mostrarDetalleAlerta(alertaSeleccionada)
        }
    }
    
    private fun mostrarDetalleAlerta(alerta: String) {
        val mensaje = when {
            alerta.contains("Exceso de velocidad") -> "Se detectó que el vehículo superó el límite de velocidad permitido. Se recomienda reducir la velocidad."
            alerta.contains("Bajo nivel de combustible") -> "El nivel de combustible está bajo. Se recomienda repostar pronto."
            alerta.contains("Mantenimiento próximo") -> "El vehículo necesita mantenimiento programado. Contacte con el taller."
            alerta.contains("fuera de zona") -> "El vehículo se encuentra fuera de la zona geográfica permitida."
            alerta.contains("Tiempo de conducción") -> "Se ha detectado un tiempo de conducción excesivo. Se recomienda tomar un descanso."
            alerta.contains("Frenado brusco") -> "Se detectó un frenado brusco. Revise las condiciones de la carretera."
            alerta.contains("Uso de teléfono") -> "Se detectó el uso del teléfono durante la conducción. Esto es peligroso."
            alerta.contains("Temperatura del motor") -> "La temperatura del motor está elevada. Revise el sistema de refrigeración."
            else -> "Detalle de la alerta no disponible."
        }
        
        android.app.AlertDialog.Builder(this)
            .setTitle("Detalle de Alerta")
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